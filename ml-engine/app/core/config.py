from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    kafka_bootstrap_servers: str = "localhost:9092"
    transactions_topic: str = "transactions"
    ml_scores_topic: str = "ml-scores"
    consumer_group_id: str = "ml-engine-group"
    model_version: str = "v1"

    class Config:
        env_prefix = "ML_ENGINE_"


settings = Settings()
