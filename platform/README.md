# BUSY School Software Center - shared school platform

This is an additive school-hosted architecture foundation.

## Services
- PostgreSQL 16 stores the shared software catalog, installer releases, teacher requests, approvals, and audit events.
- Java 21 / Spring Boot provides the API, JDBC catalog queries, and stateless Spring Security resource-server authorization.
- Python 3.12 / FastAPI provides read-only reports from PostgreSQL.
- Electron remains the desktop shell; connecting its current UI to this shared API is a follow-up integration task.

The new schema deliberately contains no device inventory, enrollment, remote-control, lock, or wipe functionality.

## Local development
1. Copy .env.example to .env and replace both placeholder secrets with long random values.
2. Run docker compose up --build from this directory.
3. Check localhost:8080/actuator/health, localhost:8080/api/health, and localhost:8090/health.

PostgreSQL runs the migration on first initialization of its data volume.

## Required before school production
- Configure and validate the school's Microsoft Entra ID/OIDC tenant, issuer, audience, and role/group claims. The sample issuer is not a completed school login integration.
- Implement user provisioning, request creation/review endpoints, approval comments, pagination, and school scoping.
- Store installer binaries in private object storage with malware scanning, checksums, immutable versioning, and short-lived authorized download URLs. Never execute uploaded installers on the server.
- Add rate limits, TLS/reverse proxy, backups and restore tests, secret management, monitoring, and a security review.
- Connect Electron to this shared API; the existing workspace chooser is not authentication.

## Existing implementation caveat
The current Electron/Express app still has local JSON workflows and legacy asset-inventory screens. This PR does not silently remove those screens. They must be removed and verified separately before claiming the entire desktop product is free of device-management features or deploying it to students.
