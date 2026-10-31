"""Read-only reporting service for the BUSY school software platform."""
import os
from fastapi import FastAPI, HTTPException
from sqlalchemy import create_engine, text
app = FastAPI(title="BUSY Software Reports", version="1.0.0")
engine = create_engine(os.environ["DATABASE_URL"], pool_pre_ping=True)
@app.get("/health")
def health():
    try:
        with engine.connect() as conn: conn.execute(text("SELECT 1"))
        return {"status":"ok","service":"reports"}
    except Exception as exc:
        raise HTTPException(status_code=503, detail="Reporting database unavailable") from exc
@app.get("/reports/overview")
def overview():
    with engine.connect() as conn:
        total=conn.execute(text("SELECT count(*) FROM software")).scalar_one()
        published=conn.execute(text("SELECT count(*) FROM software WHERE status='PUBLISHED'")).scalar_one()
        pending=conn.execute(text("SELECT count(*) FROM software_requests WHERE status IN ('PENDING','IN_REVIEW')")).scalar_one()
        decisions=conn.execute(text("SELECT decision,count(*) AS total FROM approval_events WHERE created_at>=now()-interval '30 days' GROUP BY decision ORDER BY decision")).mappings().all()
        categories=conn.execute(text("SELECT category,count(*) AS total FROM software GROUP BY category ORDER BY total DESC LIMIT 12")).mappings().all()
    return {"software_total":total,"software_published":published,"requests_pending":pending,
            "approval_decisions_30d":list(decisions),"software_by_category":list(categories)}
