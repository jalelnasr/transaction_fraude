import sys
import json
from pathlib import Path
from datetime import datetime, timezone

import pandas as pd
import joblib
from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, FunctionTransformer
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, confusion_matrix, roc_auc_score

PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.append(str(PROJECT_ROOT))

from app.features.transaction_features import (  # noqa: E402
    hour_of_day_from_step,
    compute_is_night_hour,
)

RAW_DATA_PATH = PROJECT_ROOT / "data" / "raw" / "AIML Dataset.csv"
MODEL_OUTPUT_PATH = PROJECT_ROOT / "data" / "models" / "fraud_model_v1.pkl"
METADATA_OUTPUT_PATH = PROJECT_ROOT / "data" / "models" / "fraud_model_v1.metadata.json"

NUMERIC_FEATURES = [
    "amount",
    "sourceBalanceBefore",
    "destinationBalanceBefore",
    "is_night_hour",
]
CATEGORICAL_FEATURES = ["channel"]
TARGET_COLUMN = "isFraud"

NON_FRAUD_SAMPLE_SIZE = 100_000
RANDOM_STATE = 42


def load_and_prepare_data() -> pd.DataFrame:
    print(f"Loading dataset from {RAW_DATA_PATH} (this can take a moment, ~6.3M rows)...")
    df = pd.read_csv(RAW_DATA_PATH)
    print(f"Full dataset shape: {df.shape}")

    df = df.rename(columns={
        "type": "channel",
        "oldbalanceOrg": "sourceBalanceBefore",
        "newbalanceOrig": "sourceBalanceAfter",
        "oldbalanceDest": "destinationBalanceBefore",
        "newbalanceDest": "destinationBalanceAfter",
    })

    fraud_rows = df[df[TARGET_COLUMN] == 1]
    non_fraud_rows = df[df[TARGET_COLUMN] == 0].sample(
        n=min(NON_FRAUD_SAMPLE_SIZE, (df[TARGET_COLUMN] == 0).sum()),
        random_state=RANDOM_STATE,
    )
    df_sampled = pd.concat([fraud_rows, non_fraud_rows]).sample(frac=1, random_state=RANDOM_STATE)
    print(f"Sampled dataset shape (all fraud + {NON_FRAUD_SAMPLE_SIZE} non-fraud): {df_sampled.shape}")
    print(f"Fraud ratio in sample: {df_sampled[TARGET_COLUMN].mean():.4f}")

    df_sampled["is_night_hour"] = compute_is_night_hour(hour_of_day_from_step(df_sampled["step"]))

    return df_sampled


def build_pipeline() -> Pipeline:
    preprocessor = ColumnTransformer(
        transformers=[
            ("categorical", OneHotEncoder(handle_unknown="ignore"), CATEGORICAL_FEATURES),
            ("numeric", "passthrough", NUMERIC_FEATURES),
        ]
    )

    model = RandomForestClassifier(
        n_estimators=300,
        max_depth=12,
        class_weight="balanced",
        random_state=RANDOM_STATE,
        n_jobs=-1,
    )

    return Pipeline(steps=[("preprocessor", preprocessor), ("classifier", model)])


def main():
    df = load_and_prepare_data()

    feature_columns = CATEGORICAL_FEATURES + NUMERIC_FEATURES
    X = df[feature_columns]
    y = df[TARGET_COLUMN]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, stratify=y, random_state=RANDOM_STATE
    )
    print(f"Train size: {len(X_train)} | Test size: {len(X_test)}")

    pipeline = build_pipeline()

    print("Training Random Forest model...")
    pipeline.fit(X_train, y_train)

    y_pred = pipeline.predict(X_test)
    y_proba = pipeline.predict_proba(X_test)[:, 1]

    print("\n=== Classification report (test set) ===")
    print(classification_report(y_test, y_pred, target_names=["Non-fraude", "Fraude"]))

    print("=== Confusion matrix ===")
    print(confusion_matrix(y_test, y_pred))

    auc = roc_auc_score(y_test, y_proba)
    print(f"\nROC AUC: {auc:.4f}")

    MODEL_OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    joblib.dump(pipeline, MODEL_OUTPUT_PATH)
    print(f"\nModel saved to {MODEL_OUTPUT_PATH}")

    metadata = {
        "model_version": "v1",
        "dataset": "AIML Dataset.csv (PaySim)",
        "trained_at": datetime.now(timezone.utc).isoformat(),
        "training_rows": len(X_train),
        "test_rows": len(X_test),
        "roc_auc": round(auc, 4),
        "features": {
            "numeric": NUMERIC_FEATURES,
            "categorical": CATEGORICAL_FEATURES,
        },
    }
    with open(METADATA_OUTPUT_PATH, "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2)
    print(f"Metadata saved to {METADATA_OUTPUT_PATH}")


if __name__ == "__main__":
    main()
