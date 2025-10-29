# Microscope — Step‑by‑Step To‑Do (with checkboxes)

A detailed, incremental execution plan. Each step lists **done criteria** and suggested commands.

> Tip: check items as you complete them.

---

## Phase 0 — Bootstrap & Infrastructure
- [ ] Create repo structure and baseline `README.md`, `microscope.md`.
- [ ] Write base `.env` with ports, credentials (dev-only).
- [ ] Add `deploy/docker-compose.yml` with: PostgreSQL, Redis, Kafka+ZooKeeper, Mosquitto, NGINX.
- [ ] Add volumes and basic healthchecks for all infra containers.
- [ ] Seed PostgreSQL with `registered_people` table and sample rows.
  - **Done when:** `docker compose up` starts all infra and `psql` shows seeded data.

## Phase 1 — Service Skeletons
- [ ] Scaffold Spring Boot services: `core-operational-backend`, `entrance-cockpit-backend`, `cache-loader-backend` (Java 21).
- [ ] Scaffold Node.js service: `telemetry-messaging-backend`.
- [ ] Add Dockerfiles for each service and connect to compose network.
  - **Done when:** `docker compose build` succeeds and services start with HEALTHY status.

## Phase 2 — Messaging Contracts
- [ ] Define MQTT topics:
  - `iot/entrance/badge` (badge scans)
  - `iot/entrance/decision` (authorize/deny)
- [ ] Define Kafka topics:
  - `attempt-logs`
  - `entrance-logs`
- [ ] Document payload JSON schemas in `/docs/messaging`.
  - **Done when:** Topics auto-create or init script provisions them and schemas are documented.

## Phase 3 — IoT Mocks
- [ ] Implement **badge-sensor-mock** (Node): publishes badge scans periodically or via CLI.
- [ ] Implement **door-lock-mock** (Node): subscribes to `iot/entrance/decision` and logs OPEN/DENIED.
- [ ] Containerize both mocks and add to compose (optional: toggle via profiles).
  - **Done when:** Running the sensor mock produces console output in the lock mock.

## Phase 4 — MQTT ↔ Kafka Bridge
- [ ] Implement `telemetry-messaging-backend`:
  - Subscribe to `iot/entrance/badge` (MQTT).
  - Produce normalized event to Kafka `attempt-logs`.
- [ ] (Optional) Also consume `iot/entrance/decision` and produce to `entrance-logs`.
  - **Done when:** Kafka topics receive events visible via `kafka-console-consumer`.

## Phase 5 — Core Operational Logic
- [ ] Implement Redis-first lookup for `badge_id`, fallback to PostgreSQL.
- [ ] Decision policy: allow if user exists and `is_active=true` (simple rule).
- [ ] Publish decision to `iot/entrance/decision` (MQTT).
- [ ] Also produce structured event to Kafka `entrance-logs` when authorized.
  - **Done when:** A badge scan results in a decision event and corresponding Kafka records.

## Phase 6 — Cache Loader
- [ ] Build `cache-loader-backend` scheduled job:
  - Poll DB every N seconds/minutes; write `{badge_id → user record}` into Redis.
  - Evict deleted/disabled users.
  - **Done when:** Disabling a user in DB reflects in Redis after the next sync.

## Phase 7 — Cockpit Backend & UI
- [ ] **entrance-cockpit-backend**:
  - Kafka consumer: stream `attempt-logs` & `entrance-logs`.
  - WebSocket endpoint: push live updates to UI.
  - REST: manual authorize/deny endpoint → publish MQTT decision.
- [ ] **entrance-cockpit-front**:
  - Simple HTML/CSS/JS page; connect WebSocket; render live table of events; buttons for manual actions.
  - **Done when:** UI updates in < 2s after a badge scan; manual authorize triggers lock mock OPEN.

## Phase 8 — NGINX Gateway
- [ ] Configure TLS (self-signed for dev), routing to cockpit-backend, static UI, and WebSocket proxy.
- [ ] Add rate limits and request logs.
  - **Done when:** All UI/API traffic goes through NGINX and WebSockets work via proxy.

## Phase 9 — Observability (optional but recommended)
- [ ] Add Prometheus exporters (JVM, Node), scrape configs, and Grafana dashboards.
- [ ] Add Loki + promtail for logs; create basic logs dashboard.
  - **Done when:** Dashboards show Kafka lag, MQTT message rate, JVM heap, and NGINX metrics.

## Phase 10 — CI/CD & Testing
- [ ] GitHub Actions workflow:
  - Build Java and Node services.
  - Run unit tests.
  - Build/push Docker images (if configured).
  - Spin `docker compose` for light integration tests.
- [ ] Java: Testcontainers integration tests for Redis/Postgres/Kafka.
- [ ] Contract tests for message payloads.
  - **Done when:** CI passes end‑to‑end on every PR.

## Phase 11 — Security & Hardening (dev-level)
- [ ] Secrets via `.env`/Docker secrets (no hardcoded creds).
- [ ] TLS between NGINX and backends (optional mTLS internally).
- [ ] Minimal RBAC for cockpit admin actions.
  - **Done when:** Secrets are not in repo; TLS is enforced at gateway; admin path protected.

---

## Commands Cheatsheet (examples)

```bash
# Compose up
docker compose -f deploy/docker-compose.yml up -d

# Kafka: read from topic
docker exec -it kafka   kafka-console-consumer --bootstrap-server localhost:9092   --topic attempt-logs --from-beginning

# Publish a test badge (MQTT)
docker exec -it mosquitto   mosquitto_pub -h mosquitto -t iot/entrance/badge -m '{"badge_id":"A12345","timestamp":"2025-01-01T00:00:00Z"}'
```

---

## Backlog (future ideas)
- Multi-door / multi-site support (topic partitioning by door/site).
- Avro/Schema Registry for Kafka.
- Graceful degradation when Redis/Postgres unavailable.
- Audit UI with filters and CSV export.
