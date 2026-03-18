# Migration Log – JavaTraining (JSON → PostgreSQL + Hibernate)

## STEP 1 – Hibernate Infrastructure Added

Date: 2026-03-15

Changes:
- Added Hibernate ORM 6.4.4.Final
- Added Jakarta Persistence API 3.1.0
- Added PostgreSQL JDBC Driver 42.7.3
- Added HikariCP connection pool 5.1.0

File modified:
- pom.xml

Result:
Project builds successfully with new dependencies.
Application behavior unchanged.

---

## STEP 2 – Database Schema Designed

Date: 2026-03-15

Goal:
Create relational database schema for PostgreSQL compatible with Hibernate ORM.

New file created:
src/main/resources/db/training.sql

Tables defined:

users
topics
questions
answers
test_results

Relationships:

User 1 --- * TestResult
Topic 1 --- * Question
Question 1 --- * Answer
Topic 1 --- * TestResult

Design decisions:

1. answers stored in separate table
   instead of List<String> in Question.

2. topics stored as database table
   instead of Java enum only.

3. test_results references:
    - user_id
    - topic_id

Reason:
Prepare proper relational model before entity migration.

Application state:
Application still uses JSON and InMemory repositories.
Database is not connected yet.

## STEP 3 – Hibernate Infrastructure

Date: 2026-03-15

Changes:

Created Hibernate configuration.

New files:

src/main/resources/hibernate.cfg.xml
src/main/java/com/homeapp/javatraining/config/hibernate/HibernateUtil.java

Features:

- SessionFactory singleton
- PostgreSQL connection configuration
- SQL logging enabled
- Schema validation mode enabled

Database configuration:

jdbc:postgresql://localhost:5432/javatraining

Next step:

## STEP 4 – First Hibernate Entity

Date: 2026-03-15

Refactored User model to JPA entity.

Changes:
- Added @Entity and @Table
- Added JPA annotations
- Replaced manual id with @GeneratedValue
- Introduced Lombok to reduce boilerplate
- Added protected no-args constructor
- User entity constructor updated:
- removed manual id assignment
- database now generates identifiers

Notes:
User is now mapped to PostgreSQL table "users".

## STEP 4.2 – Topic Entity Migration

Date: 2026-03-17

Changes:
- Replaced Topic enum with JPA entity
- Added mapping to "topics" table
- Introduced id, code, displayName fields
- Removed enum-based logic (values, fromCode)

Impact:
- Topic is now stored in database
- Requires repository-based access instead of enum usage

Next step:
Refactor Question entity with Answer relationship
## STEP 4.3 – JSON Adapter for Question/Answer

Date: 2026-03-18

Changes:
- Updated FileQuestionSource:
    - removed direct Jackson mapping to Question
    - introduced manual mapping from JSON to Question + Answer entities

Details:
- answers now created as Answer entities
- answerIndex assigned based on position
- bidirectional relation set (Answer → Question)

Impact:
- JSON source now compatible with new domain model
- prepares system for HibernateQuestionRepository

Next step:
Start implementation of HibernateQuestionRepository

## STEP 5 – PostgreSQL Docker Setup

Docker container with PostgreSQL 16 created.

Database: javatraining
Port: 5432
User: postgres

Tables created from SQL schema:

users
topics
questions
answers
test_results

Database is running in Docker and accessible from application.

## STEP 6 – Database Alignment and Docker Integration

Date: 2026-03-17

Changes:
- Unified database schema into single source: training.sql
- Updated docker-compose to use schema.sql for initialization
- Removed duplicate SQL files
- Added initial data for topics table

Reason:
Ensure consistency between Hibernate validation and database initialization.

Notes:
Topic enum removed from codebase.
Topics are now fully managed by database.