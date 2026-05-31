# Contributing to Meridian ERP

Thanks for your interest in improving Meridian ERP! Contributions of all kinds are welcome — bug fixes, new features, documentation, and ideas.

## Getting set up

1. **Fork** the repository and clone your fork.
2. Make sure you have **JDK 21** and **Maven 3.9+** installed.
3. Run the app: `mvn spring-boot:run` and sign in at <http://localhost:8080> with `admin@erp.com` / `Admin@1234`.
4. Run the test suite before pushing: `mvn test`.

## Project conventions

Meridian ERP is organised as independent vertical slices — one package per module (`com.erp.<module>`) following `entity → repository → service → controller → templates`. When adding to or creating a module, mirror the existing ones (CRM is the reference). A few rules that keep the build healthy:

- **Entities** extend `com.erp.common.entity.BaseEntity` (UUID id + JPA auditing) and use Lombok builders with `@Builder.Default` on initialised fields.
- **Controllers** keep business logic in services. Constrain id path variables to UUIDs: `@GetMapping("/{id:[0-9a-fA-F-]{36}}")` so literal routes (`/new`, `/save`) are never shadowed.
- **Security**: gate views with `@PreAuthorize("hasAuthority('MODULE_VIEW')")` and mutations with `MODULE_EDIT`.
- **Templates** use the layout dialect (`layout:decorate="~{layout/base}"`) and the shared fragments in `layout/components.html` (`badge`, `emptyState`, `pagination`, `listToolbar`, `breadcrumb`).
- **Pagination & search** on every list page (`Pageable`, default 20/page).

## Branch & commit style

- Create a feature branch: `git checkout -b feat/<short-name>`.
- Use [Conventional Commits](https://www.conventionalcommits.org/): `feat(crm): …`, `fix(hrm): …`, `docs: …`, `refactor: …`, `test: …`, `chore: …`.
- Keep one logical change per commit where practical.

## Pull requests

1. Ensure `mvn clean verify` passes.
2. Describe **what** changed and **why**; include screenshots for UI changes.
3. Reference any related issue (`Closes #123`).

## Reporting bugs

Open an issue using the bug-report template with steps to reproduce, expected vs. actual behaviour, and your environment (OS, JDK version).

By contributing you agree that your contributions are licensed under the project's [MIT License](LICENSE).
