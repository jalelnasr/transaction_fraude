import logging
import threading

from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator

from app.api.routes import router
from app.kafka_handlers.consumer import run_consumer_loop
from app.models.model_loader import get_model

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s - %(message)s")
logger = logging.getLogger(__name__)

app = FastAPI(title="ML Engine", description="Machine Learning Engine - Fraud Scoring Service")
app.include_router(router)
Instrumentator().instrument(app).expose(app, endpoint="/actuator/prometheus")


@app.on_event("startup")
def startup_event():
    logger.info("Starting ML Engine...")
    get_model()  # fail fast if the model cannot be loaded

    consumer_thread = threading.Thread(target=run_consumer_loop, daemon=True)
    consumer_thread.start()
    logger.info("Kafka consumer thread started")
