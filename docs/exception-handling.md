# Exception Handling Documentation

## Overview

The SACCO Management System uses centralized exception handling through Spring Boot's `@ControllerAdvice`.

The purpose of the global exception handler is to provide:

- consistent API error responses
- readable client-side error messages
- centralized exception handling logic
- proper HTTP status codes

---

# Global Exception Handler

File:

```java
GlobalExceptionHandler.java
```

This class catches exceptions thrown across controllers/services and converts them into standardized API responses.

Example response format:

```json
{
  "success": false,
  "message": "Member not found",
  "data": null
}
```

---

# Custom Exceptions

## ResourceNotFoundException

Thrown when a requested resource does not exist.

Examples:

- Member not found
- Loan application not found
- Contribution not found

HTTP Status:

```text
404 NOT FOUND
```

---

## BadRequestException

Thrown when request input is invalid.

Examples:

- Loan amount is negative
- Contribution amount is zero
- Invalid request payload

HTTP Status:

```text
400 BAD REQUEST
```

---

## DuplicateResourceException

Thrown when duplicate data is detected.

Examples:

- Duplicate membership number
- Duplicate national ID
- Duplicate email

HTTP Status:

```text
409 CONFLICT
```

---

## UnauthorizedException

Thrown when authentication fails.

Examples:

- Missing token
- Invalid JWT token
- Expired token

HTTP Status:

```text
401 UNAUTHORIZED
```

---

## ForbiddenException

Thrown when user lacks permission.

Examples:

- Member tries approving loan
- Non-admin accesses admin-only endpoint

HTTP Status:

```text
403 FORBIDDEN
```

---

## DataIntegrityViolationException

Spring-provided exception.

Thrown when database constraints are violated.

Examples:

- Foreign key violation
- Unique constraint violation
- Null constraint violation

HTTP Status:

```text
409 CONFLICT
```

---

## Generic Exception Fallback

Fallback handler for uncaught exceptions.

Purpose:

- prevent application crashes from leaking internal stack traces
- provide safe generic error response

Response:

```json
{
  "success": false,
  "message": "Something went wrong",
  "data": null
}
```

HTTP Status:

```text
500 INTERNAL SERVER ERROR
```

---

# Response Format

All exception responses use:

```java
ApiResponse<T>
```

Structure:

```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

Fields:

| Field | Type | Description |
|---|---:|---:|
| success | Boolean | request success status |
| message | String | error description |
| data | Object | response payload, null for errors |

---