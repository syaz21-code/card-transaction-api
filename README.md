# Card Transaction Management REST API

Java Spring Boot assessment project for a fictional Cards IT transaction workflow. All records are demonstrations only; no real cardholder or payment credentials are stored.

## Stack

- Java 21, Spring Boot 4.1.x, Maven wrapper
- Spring Web MVC, Spring Data JPA, Hibernate, Jakarta Bean Validation
- Microsoft SQL Server Express (`TESTDB`), Microsoft JDBC driver
- Third-party reference exchange rates: [Frankfurter](https://frankfurter.dev/)
- Postman Collection 2.1 JSON

## Prerequisites

1. Install JDK 21 and Microsoft SQL Server (local `SQLEXPRESS` instance).
2. Create `TESTDB`, enable TCP/IP on localhost port 1433, and create a dedicated SQL login mapped to `TESTDB` with suitable development privileges. Do not use `sa` for the application.
3. Install Git; Postman desktop is recommended for testing.
4. This application currently uses Hibernate `ddl-auto=update` for development and creates its transaction table on startup. Do not use automatic schema updates in production.

## Run on Windows PowerShell

From the project root:

```powershell
$env:DB_USERNAME = 'card_app_user'
$secret = Read-Host 'Enter local TESTDB password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secret).Password
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

The app starts at `http://localhost:8080`. The local DB settings are configured in `src/main/resources/application.properties` using environment variables. Keep credentials out of version control. Its development JDBC URL uses `trustServerCertificate=true` for the local test SQL Server only; production requires valid certificate verification.

## API reference

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/transactions` | Create a fictional transaction (201) |
| GET | `/api/transactions/{id}` | Get transaction by ID (200/404) |
| PUT | `/api/transactions/{id}` | Update transaction (200/404) |
| GET | `/api/transactions?page=0` | Paginated list; fixed page size 10 |
| GET | `/api/exchange-rates/USD/MYR` | Fetch reference rate via Frankfurter |

Create request example:

```json
{"merchantName":"Demo Supermarket","amount":125.50,"currency":"MYR"}
```

Update request example:

```json
{"merchantName":"Updated Merchant","amount":250.75,"currency":"MYR","status":"APPROVED"}
```

Statuses accepted by the demo API: `PENDING`, `APPROVED`, `DECLINED`, `CANCELLED`. Dates, generated IDs and live reference exchange rates vary. Exchange rates are **not** real-time card settlement rates.

## Import and run Postman tests

1. Open Postman, select **Import**, and select `postman/Card-Transaction-API.postman_collection.json`.
2. Set collection variable `baseUrl` to `http://localhost:8080` if using another port.
3. Run **Create transaction** first; its test saves `transactionId` for GET and PUT.
4. Run the remaining requests or collection folders, verifying status codes, response fields, and pagination.
5. The exchange-rate endpoint requires external network access and Frankfurter availability.

The exported collection deliberately includes only fictional demo input data and no credentials.

## Logging

The `/api/` filter emits REQUEST and RESPONSE entries with correlation ID, method, path, status, elapsed time and allowed summary fields into `logs/card-transaction-api.log`. Log files are ignored by Git. Review generated logs locally after making POST, GET, PUT and exchange-rate requests. Headers and raw sensitive bodies are not intentionally logged.

## Database / transaction behavior

The app maps `CardTransaction` to the `card_transactions` SQL Server table using Hibernate. INSERT and UPDATE services use `@Transactional`. GET by ID and paginated GET use `@Transactional(readOnly = true)`. Pagination is zero-based with exactly 10 records per page (except a last partial page).

## Testing and build

```powershell
.\mvnw.cmd clean package
```

Manual API checks are provided in the Postman Collection, including negative cases. Run the full test suite before submission and report only tests you actually executed. A passing Maven build does not by itself prove every endpoint works.

## Repository checklist

- Ensure the GitHub repository is public and all intended code is pushed.
- Ensure no passwords, tokens, `.env` files, logs, or compiled output are committed.
- Keep `postman/Card-Transaction-API.postman_collection.json` in the repository.
- Verify a fresh clone can build and can connect to a local `TESTDB` after the reviewer configures SQL Server.

## Scope and limitations

Educational demonstration only; no real card processing, authentication, payment authorization, audit-grade event storage, production-grade migration process, or PCI-compliant handling. Exchange rate availability depends on the external provider.
