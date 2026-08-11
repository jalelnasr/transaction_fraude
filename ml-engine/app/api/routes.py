from fastapi import APIRouter

from app.models.model_loader import get_model_metadata

router = APIRouter()


@router.get("/actuator/health")
def health():
    return {"status": "UP"}


@router.get("/api/models/current")
def current_model():
    return get_model_metadata()
