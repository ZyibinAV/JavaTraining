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

STEP 4 – Convert model classes to Hibernate entities.