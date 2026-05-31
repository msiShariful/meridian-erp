# Meridian ERP

A production-grade, modular **Enterprise Resource Planning** web application built with Spring Boot 3.4, Spring Modulith, Thymeleaf and Tailwind CSS. It bundles ten business modules — CRM, HRM, Inventory, E‑Commerce, Accounting, Procurement, Projects, Support, Reports and Settings — behind a single role-aware admin console, plus a public e‑commerce storefront.

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green) ![License](https://img.shields.io/badge/license-MIT-blue)

---

## ✨ Features

| Module | Highlights |
|---|---|
| **Dashboard** | Role-aware KPI cards, revenue trend (ApexCharts), sales-by-category donut, recent orders, activity feed, attendance summary, quick actions |
| **CRM** | Leads (table + drag‑and‑drop Kanban + scoring + convert‑to‑deal), Contacts, Companies, Deals pipeline (Kanban with stage totals), Activities, Campaigns with ROI |
| **HRM** | Employees (auto `EMP‑0001` IDs), Departments, Attendance, Leave (apply/approve workflow), Payroll (payslips), Performance reviews, Training |
| **Inventory** | Products (auto SKUs, stock-status badges), hierarchical Categories, Warehouses, Stock movements & adjustments, Suppliers |
| **E‑Commerce** | Orders (status workflow + printable invoice), Customers, Coupons, Review moderation, **public storefront** (catalog, session cart, coupons, checkout, order confirmation) |
| **Accounting** | Invoices (multi‑line, tax/discount, printable, payments), Payments, Expenses (approval workflow), Journal entries (balanced validation), Chart of Accounts, Budgets with variance |
| **Procurement** | Vendors (scorecards), Requisitions (approval workflow), Purchase Orders (multi‑line, printable, goods receipt), GRN, Vendor bills |
| **Projects** | Projects (progress bars + CSS Gantt), Tasks (drag‑and‑drop Kanban + list), Milestones, Comments, Timesheets |
| **Support** | Tickets (email‑style threads, SLA breach detection, priority/status), Knowledge Base (helpful voting), FAQ |
| **Reports** | Cross‑module analytics dashboard, custom report builder, **Excel (Apache POI)** & **PDF (OpenPDF)** export |
| **Settings** | Company profile, User management, Role permission matrix, Email (SMTP) config, Module toggles, System info, Backup |
| **Platform** | Spring Security (form login, 9 roles, granular permissions, method security), in‑app notifications, AOP audit logging, global exception handling, file uploads, custom error pages |

---

## 🧱 Tech Stack

- **Framework:** Spring Boot 3.4 · Spring Modulith 1.3
- **View:** Thymeleaf 3 + Layout Dialect · Tailwind CSS (CDN) · Alpine.js · ApexCharts · Heroicons
- **Data:** Spring Data JPA / Hibernate · **H2 (file‑based, persistent)**
- **Security:** Spring Security 6 (BCrypt, role + permission based, `@PreAuthorize`)
- **Build:** Maven · Java 21
- **Utilities:** Lombok · MapStruct · ModelMapper · Jakarta Bean Validation
- **Export:** Apache POI (Excel) · OpenPDF (PDF)
- **Testing:** JUnit 5 · Spring Security Test · MockMvc

---

## 🚀 Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+

### Run
```bash
mvn spring-boot:run
```
Then open <http://localhost:8080>. The H2 database is created at `./data/erpdb` and **persists across restarts**. Demo data is seeded automatically on first launch.

### Build a jar
```bash
mvn clean package
java -jar target/meridian-erp.jar
```

### Run tests
```bash
mvn test
```

---

## 🔐 Demo Accounts

All accounts use the password **`Admin@1234`**.

| Email | Role |
|---|---|
| `admin@erp.com` | Super Administrator (full access) |
| `tanvir.admin@erp.com` | Administrator |
| `nusrat.hr@erp.com` | HR Manager |
| `rakib.sales@erp.com` | Sales Manager |
| `farzana.acc@erp.com` | Accountant |
| `imran.inv@erp.com` | Inventory Manager |
| `sadia.support@erp.com` | Support Agent |
| `mehedi.emp@erp.com` | Employee |

Each role sees only the modules its permissions allow. The public storefront is at **`/shop`** (no login required).

---

## 🗂️ Project Structure

```
src/main/java/com/erp/
├── ErpApplication.java
├── config/         # Security, MVC, auditing, global model advice
├── common/         # BaseEntity, enums, exceptions, file storage
├── core/           # User, Role, Permission, auth, profile
├── dashboard/      # Aggregated KPI dashboard
├── crm/ hrm/ inventory/ ecommerce/ accounting/
├── procurement/ projects/ support/                 # business modules
├── reports/        # cross-module analytics + export
├── notifications/  # in-app notifications
├── audit/          # AOP audit logging + viewer
└── settings/       # company, users, roles, email, system
src/main/resources/
├── application.properties
├── templates/      # Thymeleaf views (layout, per-module, shop, errors)
└── static/         # app.css, app.js
```

Each module is a self-contained vertical slice (`entity` → `repository` → `service` → `controller` → templates) following the same conventions, with idempotent demo-data seeders that run on startup.

---

## 🛠️ Useful URLs

- App: `/` → `/dashboard`
- Storefront: `/shop`
- H2 console: `/h2-console` (JDBC URL `jdbc:h2:file:./data/erpdb`, user `sa`, no password)

---

## 📌 Notes

- **Open Session In View** is enabled because server‑rendered views traverse lazy associations.
- All identifiers are quoted globally so reserved words (`value`, `order`, …) are safe across modules.
- Email uses a mock transport by default; configure real SMTP under **Settings → Email**.
- Reset the database by deleting the `./data` directory; it will be recreated and reseeded on next start.

---

## 📄 License

MIT — built as a reference full-stack ERP implementation.
