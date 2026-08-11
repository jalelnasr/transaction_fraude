from datetime import datetime
from typing import Optional

import pandas as pd

NIGHT_START_HOUR = 22
NIGHT_END_HOUR = 6


def compute_is_night_hour(hour_series: pd.Series) -> pd.Series:
    hour = hour_series.fillna(-1)
    return ((hour >= NIGHT_START_HOUR) | ((hour >= 0) & (hour < NIGHT_END_HOUR))).astype(int)


def is_night_hour_from_datetime(timestamp: Optional[datetime]) -> int:
    if timestamp is None:
        return 0
    hour = timestamp.hour
    return int(hour >= NIGHT_START_HOUR or hour < NIGHT_END_HOUR)


def hour_of_day_from_step(step_series: pd.Series) -> pd.Series:
    return step_series % 24


def compute_balance_error_source(amount: pd.Series, balance_before: pd.Series, balance_after: pd.Series) -> pd.Series:
    return (balance_after + amount - balance_before).fillna(0)


def compute_balance_error_destination(amount: pd.Series, balance_before: pd.Series, balance_after: pd.Series) -> pd.Series:
    return (balance_before + amount - balance_after).fillna(0)
