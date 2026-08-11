import pandas as pd

from app.features.transaction_features import is_night_hour_from_datetime
from app.models.model_loader import get_model
from app.schemas.transaction import TransactionEvent


def build_feature_row(transaction: TransactionEvent) -> pd.DataFrame:
    return pd.DataFrame([{
        "channel": transaction.channel or "UNKNOWN",
        "amount": transaction.amount,
        "sourceBalanceBefore": transaction.sourceBalanceBefore or 0.0,
        "destinationBalanceBefore": transaction.destinationBalanceBefore or 0.0,
        "is_night_hour": is_night_hour_from_datetime(transaction.timestamp),
    }])


def predict_fraud_score(transaction: TransactionEvent) -> tuple[float, pd.DataFrame]:
    model = get_model()
    feature_row = build_feature_row(transaction)
    fraud_probability = model.predict_proba(feature_row)[0, 1]
    return float(fraud_probability), feature_row
