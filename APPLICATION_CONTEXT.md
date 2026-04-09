# JavaTraining Application Context

## Project Overview

JavaTraining is a web-based testing application for Java knowledge assessment. The application allows users to register, take tests on various Java topics, and view their results. Administrators can view statistics on test results.

## Technology Stack

- **Java Version**: 21
- **Build Tool**: Maven
- **Web Framework**: Jakarta Servlet API (no Spring Framework)
- **Database**: PostgreSQL 16 (Dockerized)
- **ORM**: Hibernate 6.6.44.Final
- **Connection Pool**: HikariCP 5.1.0
- **Logging**: SLF4J + Log4j2
- **Testing**: JUnit 5, Mockito
- **Other**: Lombok, Jackson

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/homeapp/javatraining/
│   │       ├── config/              # Configuration and DI
│   │       ├── controllers/         # Servlets (web layer)
│   │       ├── model/              # JPA entities
│   │       ├── repository/         # Data access layer
│   │       ├── service/            # Business logic layer
│   │       ├── filter/             # Authentication filters
│   │       ├── handler/            # Error handling
│   │       ├── session/            # Session utilities
│   │       ├── util/               # Utilities
│   │       └── validation/         # Input validation
│   ├── resources/
│   │   ├── hibernate.cfg.xml      # Hibernate configuration
│   │   └── db/
│   └── webapp/
│       └── WEB-INF/
│           └── jsp/               # JSP views
└── test/
    └── java/
```

## Architecture

### Layered Architecture

The application follows a strict layered architecture:

```
Servlet Layer (Controllers)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (PostgreSQL via Hibernate)
```

### Dependency Injection

The application uses **manual dependency injection** (no Spring Framework):

- **ApplicationConfig**: Manual DI container with factory methods
- **AppInitListener**: Initializes beans and registers them in ServletContext
- **BaseServlet**: Abstract base class for all servlets with common dependency injection
- **Interface-based injection**: All repositories and services injected via interfaces

### Design Patterns

- **Repository Pattern**: Interface-based repository implementations
- **Service Layer Pattern**: Business logic encapsulated in service classes
- **Factory Pattern**: Factory methods in ApplicationConfig for bean creation
- **DTO Pattern**: Data Transfer Objects for inter-layer communication

## Core Components

### Configuration Layer

**ApplicationConfig.java**
- Manual DI container
- Creates and wires all repositories and services
- Uses factory methods for dependency creation
- All dependencies injected via interfaces

**AppInitListener.java**
- ServletContextListener for application initialization
- Registers all beans in ServletContext
- Initializes Hibernate SessionFactory

**HibernateUtil.java**
- Singleton for Hibernate SessionFactory
- Provides database connection management

### Entity Layer (JPA)

**User.java**
- User account information
- Roles: USER, ADMIN
- Authentication data (password hash)
- Profile information (nickname, about, avatar)

**Topic.java**
- Test topics (previously enum, now database entity)
- Code and display name
- One-to-many relationship with Question

**Question.java**
- Test questions
- Many-to-one relationship with Topic
- One-to-many relationship with Answer
- Correct answer index

**Answer.java**
- Answer options for questions
- Many-to-one relationship with Question

**TestResult.java**
- Test completion results
- Many-to-one relationships with User and Topic
- Score and pass/fail status
- Timestamp

**InterviewState.java**
- Non-persistent class for active test session state
- Holds selected topics and questions
- Tracks current question index and score
- Uses Set<Topic> instead of Set<String> (refactored)

### Repository Layer

**UserRepository / HibernateUserRepository**
- User CRUD operations
- Find by username, ID
- Transaction management with rollback

**QuestionRepository / HibernateQuestionRepository**
- Question CRUD operations
- Get questions by topic
- Batch processing for migration
- Duplicate protection

**TestResultRepository / HibernateTestResultRepository**
- Test result CRUD operations
- Find by user ID
- Transaction management with rollback

### Service Layer

**UserService / UserServiceImpl**
- User registration logic
- Role assignment
- Duplicate username check
- Password hashing

**AuthenticationService**
- User authentication
- Password validation
- User blocking checks
- Custom exceptions for auth failures

**RegistrationService**
- User registration orchestration
- Input validation
- Delegates to UserService

**QuestionService**
- Question retrieval logic
- Random question selection by topics
- Answer processing and validation
- Interview state management

**TestResultService**
- Test result processing
- Score calculation
- Pass/fail determination
- Result persistence

**AdminStatisticsService**
- Aggregates system statistics
- User statistics
- Topic statistics
- Test completion metrics

### Controller Layer (Servlets)

**BaseServlet**
- Abstract base class for all servlets
- Common dependency injection from ServletContext
- Session management utilities
- User authentication helpers

**StartServlet**
- Initiates test sessions
- Loads selected topics via TopicLoader
- Fetches random questions via QuestionService
- Creates InterviewState with Topic entities

**QuestionServlet**
- Displays current question
- Handles answer submission
- Uses Topic entities for display (no TopicUtils)
- QuestionService injected via BaseServlet

**ResultServlet**
- Displays test results
- Processes and saves results via TestResultService
- Shows score and pass/fail status

**TestSettingServlet**
- Test configuration page
- Loads topics via TopicLoader (no direct HibernateUtil)
- Displays available topics for selection

**RegistrationServlet**
- User registration form
- Uses RegistrationService
- Uses UserService interface (not implementation)

**LoginServlet**
- User authentication
- Uses AuthenticationService

**AdminStatisticsServlet**
- Admin statistics page
- Uses AdminStatisticsService
- Displays system-wide statistics

### Utilities

**TopicLoader**
- Loads Topic entities from database
- Finds topic by code
- Replaces deprecated TopicUtils

**TopicUtils (DEPRECATED)**
- Legacy utility for topic code to display name mapping
- Marked deprecated, replaced by Topic entity usage
- Still exists but not used in current code

**QuestionValidator**
- Validates question data
- Used by QuestionService

**SessionUtils**
- Session management utilities
- Interview state storage/retrieval

## Migration Status

### ✅ COMPLETED MIGRATIONS

**Phase 1: Infrastructure**
- Hibernate ORM 6.6.44.Final added
- PostgreSQL JDBC Driver 42.7.3 added
- Jakarta Persistence API 3.1.0 added
- HikariCP 5.1.0 added

**Phase 2: Database Design**
- PostgreSQL schema created
- Docker container configured
- Tables: users, topics, questions, answers, test_results
- Relationships established

**Phase 3: Entity Migration**
- All model classes converted to JPA entities
- Topic enum replaced with database entity
- Proper bidirectional relationships established
- JPA-compliant constructors

**Phase 4: Repository Implementation**
- HibernateUserRepository implemented
- HibernateTestResultRepository implemented
- HibernateQuestionRepository implemented
- All repositories use interface pattern
- Transaction management with rollback
- Batch processing and duplicate protection

**Phase 5: Application Configuration**
- ApplicationConfig with factory methods
- Manual DI implementation
- Interface-based dependency injection
- Service layer DI integration
- Servlet layer DI integration

**Phase 6: Critical Fixes (Priority 0)**
- TestResultService injection fixed (2026-04-09)
- AdminStatisticsService injection fixed (2026-04-09)
- Database initialization fixed (DELETE FROM topics; removed)

**Phase 7: Servlet Layer Refactoring (Priority 1)**
- QuestionServlet: TopicUtils removed, Topic entities used
- InterviewState: Refactored to Set<Topic> instead of Set<String>
- StartServlet: Topic entities passed to InterviewState
- TestSettingServlet: HibernateUtil removed, TopicLoader used
- BaseServlet: Concrete implementation import removed
- RegistrationServlet: Interface import used
- TestResult: Unused import removed

### ⏳ PENDING MIGRATIONS

**Phase 8: Data Migration (Priority 3)**
- JSON questions to PostgreSQL migration
- QuestionMigrationRunner ready for execution
- Data integrity validation required

**Phase 9: Testing (Priority 4)**
- Unit tests for repositories
- Integration tests for database operations
- End-to-end testing

**Phase 10: Optimization (Priority 5)**
- Database indexes
- Fetch strategies optimization
- Connection pool tuning

## Known Issues (RESOLVED)

All critical issues have been resolved:

1. ✅ TestResultService injection fixed
2. ✅ AdminStatisticsService injection fixed
3. ✅ Database initialization DELETE FROM topics; removed
4. ✅ TopicUtils usage removed from servlets
5. ✅ InterviewState refactored to use Topic entities
6. ✅ Direct HibernateUtil usage removed from servlets
7. ✅ Concrete implementation imports removed

## Current Application Status

**Status**: ✅ **READY FOR TESTING**

**Migration Status**: ✅ **INFRASTRUCTURE COMPLETE - READY FOR DATA MIGRATION**

**Critical Issues**: ✅ **ALL RESOLVED**

**Architectural Issues**: ✅ **ALL RESOLVED**

**Production Readiness**: ⚠️ **REQUIRES TESTING AND DATA MIGRATION**

### What Works

- ✅ Application starts without errors
- ✅ User management with PostgreSQL
- ✅ Test results with PostgreSQL
- ✅ Question repository with PostgreSQL
- ✅ Repository pattern with interfaces
- ✅ Manual DI with factory methods
- ✅ Service layer with constructor injection
- ✅ Servlet layer with dependency injection
- ✅ Proper layered architecture (Servlet → Service → Repository → Database)
- ✅ No deprecated utilities in servlets
- ✅ No direct Hibernate usage in servlets
- ✅ No direct repository access in servlets

### What Needs Completion

- ✅ Data migration from JSON to PostgreSQL (completed 2026-04-09)
- ⏳ Unit and integration tests (integration tests created but require proper SessionFactory setup)
- ⏳ End-to-end testing (requires servlet container deployment)
- ⏳ Performance optimization

## Next Steps

1. ✅ **Execute data migration** (JSON → PostgreSQL using QuestionMigrationRunner) - COMPLETED 2026-04-09
2. ⏳ **Fix integration test setup** (SessionFactory lifecycle management for proper test isolation)
3. ⏳ **Perform end-to-end testing** (requires servlet container deployment - Tomcat/Jetty)
4. ⏳ **Optimize performance** (indexes, fetch strategies, connection pool)

## Design Decisions

### Manual DI vs Spring Framework

The application intentionally uses manual dependency injection instead of Spring Framework:

**Rationale:**
- Learning purposes (understanding DI patterns)
- Lightweight application
- Full control over dependency lifecycle
- Avoid Spring complexity when not needed

**Trade-offs:**
- More boilerplate code
- Manual dependency management
- No automatic AOP/transactions

### Topic Entity vs Enum

Topic was migrated from enum to database entity:

**Rationale:**
- Dynamic topic management
- Database-driven topic list
- Easier to add new topics
- Consistent with other entities

**Impact:**
- Requires repository-based access
- TopicLoader utility for database access
- InterviewState refactored to use Topic entities

## Dependencies

Key Maven dependencies:
- spring-boot-starter-parent 3.2.5 (parent only)
- Hibernate ORM 6.6.44.Final
- PostgreSQL JDBC Driver 42.7.3
- Jakarta Persistence API 3.1.0
- HikariCP 5.1.0
- Lombok
- Jakarta Servlet API 6.0.0
- JUnit 5
- Mockito
- SLF4J + Log4j2

## Database Configuration

**Connection**: jdbc:postgresql://localhost:5432/javatraining
**Schema**: docker/postgres/init.sql
**Hibernate Config**: src/main/resources/hibernate.cfg.xml
**Docker**: docker-compose.yml

## Security

- Password hashing: SHA-256
- Session-based authentication
- Role-based access control (USER, ADMIN)
- Servlet filters for authentication and authorization
- User blocking functionality

## Logging

- SLF4J facade
- Log4j2 implementation
- SQL logging enabled in Hibernate
- Request/response logging in servlets

## Testing Strategy

- Unit tests for services and repositories
- Integration tests for database operations
- End-to-end tests for user flows
- Mock objects for dependencies (Mockito)

---

**Last Updated**: 2026-04-09
**Status**: Infrastructure complete, critical issues resolved, ready for data migration and testing
