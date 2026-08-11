import json
import logging
from functools import lru_cache
from pathlib import Path

import joblib

logger = logging.getLogger(__name__)

PROJECT_ROOT = Path(__file__).resolve().parents[2]
MODEL_PATH = PROJECT_ROOT / "data" / "models" / "fraud_model_v1.pkl"
METADATA_PATH = PROJECT_ROOT / "data" / "models" / "fraud_model_v1.metadata.json"


class ModelNotLoadedError(RuntimeError):
    pass


@lru_cache(maxsize=1)
def get_model():
    if not MODEL_PATH.exists():
        logger.error("Model file not found at %s", MODEL_PATH)
        raise ModelNotLoadedError(f"Model file not found at {MODEL_PATH}")

    logger.info("Loading fraud detection model from %s", MODEL_PATH)
    pipeline = joblib.load(MODEL_PATH)
    logger.info("Model loaded successfully")
    return pipeline


@lru_cache(maxsize=1)
def get_model_metadata() -> dict:
    if not METADATA_PATH.exists():
        return {"model_version": "unknown"}

    with open(METADATA_PATH, "r", encoding="utf-8") as f:
        return json.load(f)
