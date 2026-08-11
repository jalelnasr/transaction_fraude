import json
import logging

from kafka import KafkaProducer

from app.core.config import settings
from app.schemas.prediction import MLScoreEvent

logger = logging.getLogger(__name__)

_producer: KafkaProducer | None = None


def get_producer() -> KafkaProducer:
    global _producer
    if _producer is None:
        _producer = KafkaProducer(
            bootstrap_servers=settings.kafka_bootstrap_servers,
            value_serializer=lambda v: json.dumps(v, default=str).encode("utf-8"),
            key_serializer=lambda k: k.encode("utf-8") if k else None,
        )
    return _producer


def publish_ml_score(score_event: MLScoreEvent) -> None:
    producer = get_producer()
    payload = score_event.model_dump(mode="json")
    producer.send(settings.ml_scores_topic, key=score_event.transactionId, value=payload)
    producer.flush()
    logger.info(
        "Published ML score %.4f for transaction %s",
        score_event.fraudScore,
        score_event.transactionId,
    )
