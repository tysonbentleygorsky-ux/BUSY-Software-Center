CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE app_users (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), email TEXT NOT NULL UNIQUE,
 display_name TEXT NOT NULL, role TEXT NOT NULL CHECK(role IN ('STUDENT','TEACHER','IT_ADMIN')),
 password_hash TEXT, active BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE software (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), name VARCHAR(100) NOT NULL,
 publisher VARCHAR(100) NOT NULL, description VARCHAR(1000) NOT NULL,
 category VARCHAR(80) NOT NULL, status TEXT NOT NULL DEFAULT 'DRAFT'
 CHECK(status IN ('DRAFT','PUBLISHED','ARCHIVED')),
 created_by UUID REFERENCES app_users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE software_releases (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), software_id UUID NOT NULL REFERENCES software(id) ON DELETE CASCADE,
 version VARCHAR(50) NOT NULL, platform TEXT NOT NULL CHECK(platform IN ('WINDOWS','MACOS','LINUX')),
 installer_filename TEXT NOT NULL, storage_key TEXT NOT NULL, sha256 CHAR(64) NOT NULL,
 size_bytes BIGINT NOT NULL CHECK(size_bytes>0), status TEXT NOT NULL DEFAULT 'DRAFT'
 CHECK(status IN ('DRAFT','PUBLISHED','RETIRED')), created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 UNIQUE(software_id,version,platform)
);
CREATE TABLE software_requests (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), requester_id UUID NOT NULL REFERENCES app_users(id),
 software_name VARCHAR(100) NOT NULL, educational_purpose VARCHAR(2000) NOT NULL, vendor_url TEXT,
 status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','IN_REVIEW','APPROVED','REJECTED')),
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE approval_events (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), request_id UUID NOT NULL REFERENCES software_requests(id) ON DELETE CASCADE,
 reviewer_id UUID NOT NULL REFERENCES app_users(id), decision TEXT NOT NULL
 CHECK(decision IN ('APPROVED','REJECTED','COMMENTED')), comment VARCHAR(2000),
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE audit_events (
 id BIGSERIAL PRIMARY KEY, actor_id UUID REFERENCES app_users(id), event_type VARCHAR(100) NOT NULL,
 entity_type VARCHAR(80) NOT NULL, entity_id TEXT, details JSONB NOT NULL DEFAULT '{}'::jsonb,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_software_status ON software(status);
CREATE INDEX idx_release_software ON software_releases(software_id);
CREATE INDEX idx_request_queue ON software_requests(status,created_at DESC);
CREATE INDEX idx_audit_time ON audit_events(created_at DESC);
