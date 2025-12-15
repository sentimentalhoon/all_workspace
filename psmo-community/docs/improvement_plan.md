# Project Improvement Plan

## 1. Identified Issues

Based on the initial analysis of the `psmo-community` project, the following issues were identified:

### 🔴 Critical: Missing Test Environment

- **Issue**: The `backend/src/test` directory is empty.
- **Impact**: No automated verification for business logic or API endpoints. High risk of regression.
- **Action**: Initialize JUnit 5 and Ktor Test Host.

### 🟠 High: Missing OpenAPI Documentation

- **Issue**: No Swagger/OpenAPI UI configured.
- **Impact**: API is undocumented, making frontend integration and external usage difficult. Violates project rules.
- **Action**: Install `ktor-server-swagger` or `ktor-server-openapi` and configure Swagger UI.

### 🟡 Medium: Non-Standard Package Structure

- **Issue**: Kotlin source files are located directly in `src/main/kotlin` instead of `src/main/kotlin/com/psmo`.
- **Impact**: messy project structure, potential namespace conflicts, violates standard Java/Kotlin conventions.
- **Action**: Move all source files to `com.psmo` package structure.

## 2. Implementation Roadmap

We will address these issues in the following order:

### ✅ 1. Refactor Package Structure (Completed)

- [x] Move files to `package com.psmo`.
- [x] Update imports.
- [x] Verified build success.

### ✅ 2. Configure OpenAPI (Swagger) (Completed)

- [x] Add dependencies (`ktor-server-swagger`).
- [x] Configure `Install(Swagger)` in `Application.kt`.
- [x] Verified `/swagger` endpoint.

### ✅ 3. Setup Testing Environment (Completed)

- [x] Configuration `build.gradle.kts` for Testing (JUnit 5).
- [x] Create base test class `ApplicationTest.kt`.
- [x] Write a simple "Hello World" or Health Check test to verify the harness.
