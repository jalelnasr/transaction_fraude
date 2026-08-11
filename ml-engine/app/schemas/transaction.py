from datetime import datetime
from typing import Optional

from pydantic import BaseModel


class TransactionEvent(BaseModel):
    transactionId: str
    amount: float
    currency: Optional[str] = None
    sourceAccountId: Optional[str] = None
    destinationAccountId: Optional[str] = None
    channel: Optional[str] = None
    country: Optional[str] = None
    city: Optional[str] = None
    timestamp: Optional[datetime] = None
    customerId: Optional[str] = None
    sourceBalanceBefore: Optional[float] = None
    sourceBalanceAfter: Optional[float] = None
    destinationBalanceBefore: Optional[float] = None
    destinationBalanceAfter: Optional[float] = None
