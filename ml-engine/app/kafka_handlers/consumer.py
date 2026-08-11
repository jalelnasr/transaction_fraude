import json
import logging
import time

from kafka import KafkaConsumer
from kafka.errors import NoBrokersAvailable

from app.core.config import settings
from app.explainability.shap_explainer import explain_prediction
from app.kafka_handlers.producer import publish_ml_score
from app.models.fraud_model import predict_fraud_score
from app.schemas.prediction import MLScoreEvent
from app.schemas.transaction import TransactionEvent

logger = logging.getLogger(__name__)


def handle_transaction(raw_message: dict) -> None:
    transaction = TransactionEvent(**raw_message)
    logger.info("Received transaction %s for ML scoring", transaction.transactionId)

    fraud_score, feature_row = predict_fraud_score(transaction)
    influential_features = explain_prediction(feature_row)

    score_event = MLScoreEvent(
        transactionId=transaction.transactionId,
        fraudScore=fraud_score,
        modelVersion=settings.model_version,
        influentialFeatures=influential_features,
    )
    publish_ml_score(score_event)


def connect_with_retry(retry_delay_seconds: int = 5) -> KafkaConsumer:
    while True:
        try:
            return KafkaConsumer(
                settings.transactions_topic,
                bootstrap_servers=settings.kafka_bootstrap_servers,
                group_id=settings.consumer_group_id,
                value_deserializer=lambda v: json.loads(v.decode("utf-8")),
                auto_offset_reset="earliest",
            )
        except NoBrokersAvailable:
            logger.warning(
                "Kafka broker not reachable yet at %s, retrying in %ss...",
                settings.kafka_bootstrap_servers,
                retry_delay_seconds,
            )
            time.sleep(retry_delay_seconds)


def run_consumer_loop() -> None:
    while True:
        consumer = connect_with_retry()
        logger.info(
            "ml-engine consumer started, listening on topic '%s'",
            settings.transactions_topic,
        )

        try:
            for message in consumer:
                try:
                    handle_transaction(message.value)
                except Exception:
                    logger.exception("Failed to process transaction message: %s", message.value)
        except Exception:
            logger.exception("Kafka consumer loop crashed, reconnecting...")
            time.sleep(5)
