from datetime import datetime, timezone
from typing import List

from pydantic import BaseModel, Field

from app.schemas.influential_feature import InfluentialFeature


class MLScoreEvent(BaseModel):
    transactionId: str
    fraudScore: float
    modelVersion: str
    influentialFeatures: List[InfluentialFeature] = Field(default_factory=list)
    evaluatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
