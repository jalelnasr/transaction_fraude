import logging
from functools import lru_cache
from typing import List

import pandas as pd
import shap

from app.models.model_loader import get_model
from app.schemas.influential_feature import InfluentialFeature

logger = logging.getLogger(__name__)


@lru_cache(maxsize=1)
def get_explainer() -> shap.TreeExplainer:
    model = get_model()
    classifier = model.named_steps["classifier"]
    return shap.TreeExplainer(classifier)


def explain_prediction(feature_row: pd.DataFrame, top_n: int = 3) -> List[InfluentialFeature]:
    model = get_model()
    preprocessor = model.named_steps["preprocessor"]

    transformed = preprocessor.transform(feature_row)
    if hasattr(transformed, "toarray"):
        transformed = transformed.toarray()

    feature_names = preprocessor.get_feature_names_out()

    explainer = get_explainer()
    shap_values = explainer.shap_values(transformed)

    if isinstance(shap_values, list):
        values = shap_values[1][0]
    elif shap_values.ndim == 3:
        values = shap_values[0, :, 1]
    else:
        values = shap_values[0]

    scored = sorted(
        zip(feature_names, values),
        key=lambda pair: abs(pair[1]),
        reverse=True,
    )[:top_n]

    return [
        InfluentialFeature(featureName=str(name), importanceScore=round(float(score), 4))
        for name, score in scored
    ]
