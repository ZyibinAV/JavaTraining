# JavaTraining Migration Plan (JSON → PostgreSQL + Hibernate)

## Stage 1 – Infrastructure

Completed
- Added Hibernate ORM
- Added PostgreSQL JDBC driver
- Added Jakarta Persistence API
- Added HikariCP

---

## Stage 2 – Database Design

Goal:
Design relational schema compatible with Hibernate.

Tables:

users
topics
questions
answers
test_results

Relationships:

User 1---* TestResult
Topic 1---* Question
Question 1---* Answer
Topic 1---* TestResult

Files created:

src/main/resources/db/schema.sql

---

## Stage 3 – Hibernate Infrastructure

Planned:

Create package

config.hibernate

Classes:

HibernateUtil
HibernateSessionFactory

Files:

hibernate.cfg.xml

Goal:

Create SessionFactory and database connection.

---

## Stage 4 – Entity Migration

Refactor model classes into JPA entities.

Entities:

User
Topic
Question
Answer
TestResult

Changes:

Add annotations:

@Entity
@Table
@Id
@GeneratedValue
@ManyToOne
@OneToMany
@JoinColumn

---

## Stage 5 – Repository Migration

Replace in-memory repositories.

Old:

InMemoryUserRepository
InMemoryTestResultRepository
QuestionRepository(JSON)

New:

HibernateUserRepository
HibernateTestResultRepository
HibernateQuestionRepository

---

## Stage 6 – ApplicationConfig Update

Replace repository initialization.

Before:

new InMemoryUserRepository()

After:

new HibernateUserRepository()

---

## Stage 7 – Data Migration

Move questions from JSON files into PostgreSQL.

Create import script or loader.

---

## Stage 8 – Remove JSON Storage

Remove:

FileQuestionSource
JSON question files

Replace with database queries.

---

## Stage 9 – Performance Tuning

Add:

indexes
fetch strategies
batch loading

Prevent:

N+1 queries

---

## Stage 10 – Final Refactoring

Clean repository layer
Improve transaction management
Add integration tests