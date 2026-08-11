from pydantic import BaseModel


class InfluentialFeature(BaseModel):
    featureName: str
    importanceScore: float
