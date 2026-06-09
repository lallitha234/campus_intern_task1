# Campus Intern Task 1 — Microservices API Monitoring System

4 Spring Boot microservices + AI-powered monitoring dashboard.

## Quick Start

**Double-click `start-all.ps1`** — it auto-detects Java, fixes the JAVA_HOME path,
and opens all 4 apps in separate windows.

Then open **http://localhost:9090**

---

## Projects

| Folder | API | Port |
|--------|-----|------|
| `url-shortener-api` | Shorten URLs | 8081 |
| `email-validation-api` | Validate emails | 8082 |
| `password-validation-api` | Check password strength | 8083 |
| `api-monitor` | Monitor all APIs + PDF report | 9090 |

---

## Run Individually (if start-all.ps1 fails)

Open PowerShell and run each in a **separate window**:

```powershell
# Fix JAVA_HOME first (run once)
$fso = New-Object -ComObject Scripting.FileSystemObject
$env:JAVA_HOME = $fso.GetFolder((Split-Path (Get-Command java).Source -Parent | Split-Path -Parent)).ShortPath

# Then in 4 separate windows:
cd url-shortener-api       ; .\mvnw.cmd spring-boot:run
cd email-validation-api    ; .\mvnw.cmd spring-boot:run
cd password-validation-api ; .\mvnw.cmd spring-boot:run
cd api-monitor             ; .\mvnw.cmd spring-boot:run
```

---

## API Endpoints

### URL Shortener (8081)
- `POST /api/shorten` — `{"originalUrl":"https://example.com"}`
- `GET /{code}` — redirects to original URL
- `GET /api/info/{code}` — URL details + click count

### Email Validation (8082)
- `POST /api/v1/email/validate` — `{"email":"test@gmail.com"}`
- `GET /api/v1/email/validate?email=test@gmail.com`
- `POST /api/v1/email/validate/bulk` — `{"emails":["a@b.com","c@d.com"]}`

### Password Validation (8083)
- `POST /api/v1/password/validate` — `{"password":"MyPass@123"}`
- `GET /api/v1/password/validate?password=MyPass@123`
- Returns: `valid`, `strength` (WEAK/MEDIUM/STRONG), `errors`, `suggestions`

### API Monitor (9090)
- `GET /` — Web dashboard UI
- `POST /api/monitor/check` — upload Excel → check all APIs
- `POST /api/monitor/report` — upload Excel → download PDF report
- `GET /api/monitor/template` — download sample Excel template

---

## Tech Stack
Java 17 · Spring Boot 3.2.1 · Apache POI · OpenPDF · Claude AI (optional)
