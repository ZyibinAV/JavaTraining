# JavaTraining Application Documentation

## Overview

**JavaTraining** is a web-based Java learning and testing platform built with Java Servlets, JSP, and Maven. The
application provides interactive Java programming tests, user management, and administrative features for tracking
learning progress.

## Technology Stack

- **Java Version**: 21
- **Framework**: Java Servlets with JSP
- **Build Tool**: Maven 3.x
- **Parent Framework**: Spring Boot 3.3.5 (for dependency management)
- **Logging**: SLF4J + Log4j2
- **Testing**: JUnit 5.10.2 + Mockito 5.8.0
- **JSON Processing**: Jackson 2.17.0
- **Database**: PostgreSQL (migration completed)
- **ORM**: Hibernate ORM
- **Persistence API**: Jakarta Persistence
- **Connection Pool**: HikariCP
- **Template Engine**: JSP with JSTL
- **Code Coverage**: JaCoCo

## Project Structure

- JSON question files removed from runtime resources
- Application fully uses database as source of truth
- Migration tools located in tools.migration package
```
JavaTraining/
├── docker/
│   └── postgres/   
│       └── init.sql
├── src/main/java/com/homeapp/javatraining/
│   ├── config/          # Application configuration and initialization  
│   │   ├── ApplicationConfig.java
│   │   ├── AppInitListener.java
│   │   └── hibernate/   # Hibernate infrastructure
│   │       └── HibernateUtil.java
│   ├── controllers/     # HTTP request handlers (Servlets)
│   ├── dto/             # Data Transfer Objects
│   ├── exception/       # Custom exception classes
│   ├── filter/          # Servlet filters for authentication and authorization
│   ├── handler/         # Request handling utilities
│   ├── model/           # JPA entities and domain models
│   ├── repository/      # Data access layer (in-memory + migration to Hibernate)
│   ├── service/         # Business logic layer
│   ├── session/         # Session management utilities
│   ├── tools/migration/ # Migration and import tools (JSON → DB)
│   ├── util/            # Utility classes
│   └── validation/      # Input validation logic
├── src/main/resources/
│   ├── hibernate.cfg.xml  # Hibernate ORM configuration
│   ├── log4j2.xml         # Log4j2 configuration
│   └── 
├── src/main/webapp/
│   ├── WEB-INF/
│   │   └── jsp/        # JSP view files
│   │       ├── admin/  # Admin-specific pages
│   │       └── common/ # Common UI components
│   ├── css/            # Stylesheets
│   ├── js/             # JavaScript files
│   └── uploads/        # File upload directory (avatars)
├── pom.xml
├── docker-compose.yml
└── [documentation files]
```

## Core Components

### 1. Configuration Layer

#### `ApplicationConfig.java`
- ApplicationConfig acts as a manual DI container
- Uses factory methods for dependency creation
- Centralizes repository and service wiring
- Still framework-free (no Spring)
- **Purpose**: Central configuration class that initializes repository and service beans
- **Status**: **✅ FUNCTIONAL - Manual DI Implementation**
- **Current State**:
    - ✅ Uses factory methods for dependency creation
    - ✅ All repositories injected via interfaces
    - ✅ Services created with proper constructor injection
    - ✅ Centralized dependency management
- **Implementation Pattern**:
    - Manual dependency injection with factory methods
    - Interface-based repository injection
    - Service layer properly wired with constructor injection
    - Framework-free approach (intentional design choice)
- QuestionService now depends on QuestionRepository
- QuestionService created via factory method with repository injection

#### `AppInitListener.java`

- **Purpose**: ServletContextListener for application startup
- **Responsibilities**:
    - Initializes ApplicationConfig
    - Stores all beans in ServletContext for servlet injection
    - Provides dependency injection for servlets
    - Logs initialization status
- **Status**: ✅ **FUNCTIONAL**

### 1.1. ApplicationConfig - Manual DI Implementation

#### Current State (ACTUAL):
```java
@Getter
public class ApplicationConfig {
    // ===== Repositories (Interfaces) =====
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;

    // ===== Services =====
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final QuestionService questionService;

    public ApplicationConfig() {
        this.userRepository = createUserRepository();
        this.testResultRepository = createTestResultRepository();
        this.questionRepository = createQuestionRepository();

        this.userService = createdUserService();
        this.authenticationService = createdAuthenticationService();
        this.registrationService = createdRegistrationService();
        this.questionService = createdQuestionService();
    }

    private UserRepository createUserRepository() {
        return new HibernateUserRepository();
    }

    private TestResultRepository createTestResultRepository() {
        return new HibernateTestResultRepository();
    }

    private QuestionRepository createQuestionRepository() {
        return new HibernateQuestionRepository();
    }
    // ... service factory methods
}
```

#### Implementation Details:
- ✅ **Factory Pattern** - Uses factory methods for object creation
- ✅ **Interface-Based Injection** - All repositories injected via interfaces
- ✅ **Service Layer DI** - Services created with constructor injection
- ✅ **Centralized Configuration** - All dependency wiring in one place
- ✅ **Framework-Free** - Intentional manual DI implementation

#### Design Choice:
The application uses manual dependency injection instead of full Spring Framework DI container. This is a valid architectural choice for:
- Learning purposes (understanding DI patterns)
- Lightweight applications
- Full control over dependency lifecycle
- Avoiding Spring complexity when not needed

#### Current HibernateQuestionRepository Implementation (ACTUAL):
```java
@Slf4j
public class HibernateQuestionRepository implements QuestionRepository {  // ✅ Interface implemented
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    
    @Override
    public List<Question> getQuestions(Topic topic) {  // ✅ Topic entity instead of String
        try (Session session = sessionFactory.openSession()) {
            List<Question> questions = session.createQuery(
                    "FROM Question q WHERE q.topic = :topic", Question.class)
                    .setParameter("topic", topic).list();
            return questions;
        }
    }
    
    @Override
    public Optional<Question> findById(Long id) { /* ✅ Fully implemented */ }
    
    @Override
    public List<Question> findAll() { /* ✅ Fully implemented */ }
    
    @Override
    public void save(Question question) { /* ✅ Fully implemented with transaction management */ }
    
    @Override
    public boolean existsByTextAndTopic(String questionText, Topic topic) { /* ✅ Implemented for duplicate protection */ }
    
    @Override
    public void saveAll(List<Question> questions) { /* ✅ Batch processing implemented */ }
}
```

### 2. Data Models

#### Core Entities

**`User.java`**

- **Fields**: id, username, passwordHash, email, nickname, about, avatarPath, role, createdAt, blocked
- **Roles**: USER, ADMIN (enum)
- **Features**: Profile management, role assignment, blocking functionality
**Important (Hibernate migration):**
- id is generated by database (@GeneratedValue)
- manual id assignment is not allowed
- constructor does not accept id

**`Question.java`**

- **Type**: JPA Entity with relationships
- **Fields**: id, questionText, topic (ManyToOne), correctAnswerIndex, answers (OneToMany)
- **Relationships**: Linked to Topic and Answer entities
- **Usage**: Multiple choice questions for tests
- **Migration**: Fully converted to JPA entity with Answer relationship

**`TestResult.java`**

- **Type**: JPA Entity
- **Fields**: id, user (ManyToOne), topic (ManyToOne), totalQuestions, correctAnswers, passed, finishedAt
- **Features**: Stores test completion data with user and topic relationships
- **Format**: Includes formatted date output method
- **Important (Hibernate migration):**
- TestResult now stores only one Topic (ManyToOne)
- Multi-topic tests are not supported at persistence level

**`InterviewState.java`**

- **Purpose**: Manages active test session state
- **Fields**: selectedTopics, questions, currentQuestionIndex, answers
- **Important:**
- InterviewState exposes question list via getQuestions()
- Used for result processing and persistence logic

#### JPA Entities

**`Topic.java`**

- **Type**: JPA Entity (fully migrated from enum)
- **Fields**: id, code, displayName
- **Purpose**: Represents test topics stored in database
- **Status**: Migration complete - enum code commented out

Topics are no longer enum-based.
They must be loaded from database using Hibernate.
Topic.values() is no longer valid.

TopicUtils is deprecated and should not be used.
Topic data must be accessed directly via entity fields.

Topic.fromCode() is no longer valid.
Topic must be retrieved from database and used as entity.

**`Answer.java`**

- **Type**: JPA Entity
- **Fields**: id, answerText, answerIndex, question (ManyToOne)
- **Purpose**: Represents individual answer options for questions
- **Relationship**: Linked to Question entity

**`Role.java`**

- **Type**: Java Enum
- **Values**: USER, ADMIN
- **Purpose**: User role management

### 3. Repository Layer (Migration Status: ✅ FULLY COMPLETED)

#### Current Implementation Status

**Hibernate Repositories (✅ All Fully Implemented):**

- **HibernateUserRepository** - ✅ Production-ready
    - Complete CRUD operations with transaction management
    - HQL queries for username lookup and filtering
    - Comprehensive error handling and logging
    - Implements UserRepository interface

- **HibernateTestResultRepository** - ✅ Production-ready
    - Implements TestResultRepository interface
    - HQL queries with user relationship filtering
    - Transaction management and error handling
    - Complete CRUD operations

- **HibernateQuestionRepository** - ✅ Production-ready
    - ✅ Implements QuestionRepository interface
    - ✅ Complete CRUD operations (getQuestions, findById, findAll, save)
    - ✅ Proper method signature: getQuestions(Topic topic)
    - ✅ Transaction management and error handling
    - ✅ Duplicate protection: existsByTextAndTopic()
    - ✅ Batch processing: saveAll() with optimization
    - ✅ Migration-ready for JSON to PostgreSQL transfer
  
#### Repository Layer Status: **COMPLETELY MIGRATED TO HIBERNATE**

All repositories:
- ✅ Interface-based design properly implemented
- ✅ Hibernate Session API with proper resource management
- ✅ Complete CRUD operations for all entities
- ✅ Transaction management with rollback handling
- ✅ Production-ready error handling and logging
- ✅ Optimized batch processing capabilities

**Configuration Status:**
ApplicationConfig is **FUNCTIONAL** with manual DI implementation. All repositories and services are properly wired and injected via ServletContext.

**Legacy Repositories (Status):**

- **QuestionRepository.defaultRepository()** - ❌ JSON-based (completely replaced)
- **InMemoryUserRepository** - ❌ Completely replaced by HibernateUserRepository
- **InMemoryTestResultRepository** - ❌ Completely replaced by HibernateTestResultRepository

#### Migration Status: **✅ REPOSITORY LAYER COMPLETE**

All repository implementations successfully migrated to Hibernate with proper interface pattern.
**NOTE**: Repository layer is production-ready, configuration layer is functional with manual DI.

#### Interface Pattern

- **UserRepository.java** → **HibernateUserRepository.java** ✅ Complete
- **TestResultRepository.java** → **HibernateTestResultRepository.java** ✅ Complete
- **QuestionRepository.java** → **HibernateQuestionRepository.java** ✅ Complete

### DTO Layer

DTO classes are used as data containers for transferring information between layers.

**Rules:**
- DTO classes must not depend on entity lookup logic
- All required data must be passed into DTO constructors
- DTO must be constructed with all required data at creation time.
  Controllers and services are responsible for providing entity data.
### 4. Service Layer
- All services are created via ApplicationConfig
- No direct instantiation (new Service()) allowed
- Dependencies injected via constructors
- Handles question retrieval logic (topic-based loading, randomization, limiting)

#### TestResultService
- Handles result calculation and persistence
- Encapsulates TestResult creation logic
- Removes business logic from ResultServlet

#### AdminStatisticsService
- Aggregates system statistics
- Processes TestResult and User data
- Builds DTOs (UserStats, TopicStats)

#### Authentication & Authorization

**`AuthenticationService.java`** ✅

- **Purpose**: User login validation
- **Features**:
    - Password hashing with SHA-256
    - User blocking checks
    - Credential validation
    - Custom exceptions for auth failures

**`RegistrationService.java`** ✅

- **Purpose**: New user registration
- **Features**: Input validation, duplicate checking

#### Business Logic Services

**`QuestionService.java`** ✅

- **Purpose**: Question management and retrieval
- **Features**: Topic-based filtering, random selection

**`UserService.java`** / `UserServiceImpl.java`** ✅

- **Purpose**: User profile management
- **Features**: Profile updates, avatar management

**`AdminUserService.java`** ✅

- **Purpose**: Administrative user management
- **Features**: Role changes, user blocking/unblocking

**`AvatarService.java`** ✅

- **Purpose**: Avatar management
- **Features**: Upload, selection, file handling

**`UserStatisticsService.java`** / `UserStatisticsServiceImpl.java`** ✅

- **Purpose**: Test result analytics and reporting

**`UserTestStatisticsService.java`** ✅

- **Purpose**: User-specific test statistics

### 5. Controller Layer (Servlets)

- Servlets DO NOT create services manually
- All services injected via BaseServlet from ServletContext
- **⚠ CURRENT STATUS**: Some servlets bypass service layer and require refactoring

#### ResultServlet
- Uses TestResultService instead of direct repository access
- No business logic inside servlet

#### StartServlet
- Uses QuestionService for loading questions
- Does not access repository directly
- Responsible only for request handling and session initialization

#### AdminStatisticsServlet
- Uses AdminStatisticsService
- No direct repository access
- No business logic inside servlet

#### Base Architecture

**`BaseServlet.java`**
- Uses QuestionRepository interface for question access (via ServletContext)
- **Purpose**: Abstract base class for all servlets
- **Features**:
    - Dependency injection from ServletContext
    - Common authentication methods
    - Session management utilities
    - Logging setup

#### Core Controllers

**User-Facing Servlets**:

- `StartServlet` - Initiates test sessions
  - ⚠ **ISSUE**: Directly uses questionRepository (violates service layer)
  - Comment: "⚠ Рабочий код ❗ Но нарушает слой Service ❗ И начнёт разваливаться при усложнении логики"
  - ✅ Uses TopicLoader correctly
  - ✅ Loads Topic entities instead of manual creation

- `QuestionServlet` - Displays questions and handles answers
  - ❌ **ISSUE**: Uses TopicUtils (deprecated - should use Topic entities directly)
  - ⚠ **ISSUE**: Has QuestionService field but doesn't initialize it in initializeSpecificServices
  - ⚠ **ISSUE**: Uses state.getTopicCodes() which may not exist in InterviewState

- `ResultServlet` - Shows test results
  - ⚠ **ISSUE**: Uses repository directly (testResultRepository.save) - should use service layer
  - Comment: "// доработать и упростить"
  - ✅ Uses Topic entity correctly

- `LoginServlet` / `LogoutServlet` - Authentication
  - ✅ Uses authenticationService correctly

- `RegistrationServlet` - User registration
  - ✅ Uses registrationService correctly
  - ⚠ **ISSUE**: Has UserServiceImpl import (should use interface UserService)

- `ProfileServlet` / `ProfileEditServer` - User profile management

- `HomeServlet` - Main dashboard

- `AvatarUploadServlet` / `AvatarSelectServlet` - Avatar management

- `TestSettingServlet` - Test configuration
  - ❌ **ISSUE**: Uses HibernateUtil directly (should use TopicLoader or service layer)
  - Bypasses repository/service layer

**Admin Controllers** (`/admin/`):

- `AdminServlet` - Admin dashboard

- `AdminUserServlet` - User management interface

- `AdminBlockUserServlet` - User blocking operations

- `AdminChangeRoleServlet` - Role management

- `AdminStatisticsServlet` - System statistics
  - ⚠ **ISSUE**: Uses repositories directly (testResultRepository, userRepository) - should use service layer
  - ✅ Already has fix for Topic entity usage

#### Current Servlet Layer Issues:

**Architectural Violations:**
1. Direct repository access in servlets (StartServlet, ResultServlet, AdminStatisticsServlet)
2. Deprecated TopicUtils usage (QuestionServlet)
3. Direct HibernateUtil usage (TestSettingServlet)
4. Service fields not properly initialized (QuestionServlet)
5. Service implementation imports instead of interfaces (RegistrationServlet)

**Required Refactoring:**
- Remove all direct repository access from servlets
- Remove deprecated TopicUtils usage
- Remove direct HibernateUtil usage
- Ensure proper service initialization in initializeSpecificServices()
- Use service interfaces only
- Enforce layered architecture: Servlet → Service → Repository → Database

#### Refactoring Priority: HIGH
- **Status**: Infrastructure complete, but servlet layer violates proper architecture
- **Impact**: Application works but not production-ready until refactored
- **Next Action**: Refactor servlets to use service layer properly

### 6. Security Layer

#### Filters

**`AuthFilter.java`**

- **Purpose**: Authentication requirement enforcement
- **Protected Paths**: `/profile`, `/admin/*`, `/result`, `/question`
- **Features**: Session validation, redirect to login

**`AdminFilter.java`**

- **Purpose**: Admin-only access control
- **Protected Paths**: `/admin/*`
- **Features**: Role validation, unauthorized access handling

#### Security Features

- Password hashing with SHA-256
- Session-based authentication
- Role-based authorization
- User blocking functionality
- Input validation and sanitization

### 7. Utility Classes

**`PasswordUtil.java`**

- **Purpose**: Password hashing operations
- **Algorithm**: SHA-256 with UTF-8 encoding
- **Format**: Hexadecimal output

**`TopicUtils.java`**

- **Purpose**: Topic enumeration utilities (deprecated)
- **Status**: Topic enum replaced with JPA entities
- **Note**: Use TopicLoader for database access instead

**`TopicLoader.java`**

- **Purpose**: Database access for Topic entities
- **Features**: 
    - loadAllTopics() - loads all topics from database
    - findByCode(String code) - finds topic by code
- **Usage**: Replacement for enum-based Topic access
- **Implementation**: Uses Hibernate Session API

**`QuestionMigrationRunner.java`**

- **Purpose**: Data migration from JSON to PostgreSQL
- **Features**:
    - Load questions from JSON via FileQuestionSource
    - Convert to JPA entities (Question + Answer)
    - Persist using QuestionRepository
- **Usage**: One-time migration tool for transferring JSON data to database
- Important constraints:
- QuestionMigrationRunner requires Topic entities loaded from database
- Usage of transient Topic (new Topic()) is запрещено
- Status update:

- QuestionMigrationRunner is NOT removed after Stage 7
- It is retained as a reusable data import tool

Usage:

- Can be used for:
    - initial migration (completed)
    - future bulk imports (JSON → DB)
    - test data seeding

Constraints:

- Uses FileQuestionSource ONLY as import mechanism
- MUST NOT be used in runtime application flow
- MUST NOT be used by services or controllers

Classification:

- One-time migration tool → converted to reusable import utility

**`ValidationFactory.java`**

- **Purpose**: Validation object creation
- **Pattern**: Factory pattern for validators
- **Features**: Creates type-specific validators for entities

### 8. Exception Handling

**`AuthenticationException.java`**

- **Types**: User not found, invalid credentials, user blocked
- **Purpose**: Authentication failure scenarios

**`ValidationException.java`**

- **Purpose**: Input validation failures
- **Features**: Field-specific error messages

### 9. Session Management

**`SessionUtils.java`**

- **Purpose**: Session state management
- **Features**:
    - Interview state storage
    - User session tracking
    - Session cleanup utilities


TestResult no longer contains userId and topicCode.
All access must go through:
- testResult.getUser()
- testResult.getTopic()
- 
## Application Flow

### User Journey

1. **Registration/Landing** - User creates account or logs in
2. **Test Configuration** - Select topics and question count
3. **Test Session** - Sequential question answering
4. **Results Display** - Score and performance metrics
5. **Profile Management** - Update personal information and avatar

### Admin Journey

1. **Admin Dashboard** - Overview of system statistics
2. **User Management** - View, block, unblock users
3. **Role Management** - Assign/revoke admin privileges
4. **Statistics** - System usage and performance analytics

## Data Flow Architecture

```
HTTP Request → Filter → Servlet → Service → Repository → (Memory | Database)
                ↓
            JSP View ← Model Data ← Service Response
```

## Key Features

### Testing System

- **Multi-topic Support**: 8 different Java topics
- **Configurable Tests**: User selects topics and question count
- **Random Question Selection**: Shuffled question order
- **Session State Management**: Maintains test progress
- **Immediate Results**: Real-time score calculation

### User Management

- **Profile Customization**: Nickname, about section, avatar
- **Avatar System**: Upload and select avatars
- **Role-based Access**: User and admin roles
- **Account Blocking**: Admin can block/unblock users

### Administrative Features

- **User Statistics**: Test performance tracking
- **System Analytics**: Usage statistics and metrics
- **User Administration**: Bulk user operations
- **Role Management**: Admin privilege assignment

## Development Considerations

### Current Limitations

- **Servlet Layer Architecture**: Some servlets bypass service layer and use repositories directly (HIGH PRIORITY)
- **Deprecated Utilities**: TopicUtils still used in QuestionServlet (needs removal)
- **Direct Hibernate Usage**: TestSettingServlet uses HibernateUtil directly (bypasses layers)
- **Data Migration**: JSON questions still need to be migrated to PostgreSQL (infrastructure ready)
- **Security**: SHA-256 hashing without modern security features
- **Single Instance**: Not distributed-ready

### Scalability Notes

- Repository pattern fully implemented with database integration ✅
- Service layer supports business logic expansion
- Filter-based security is extensible
- Session management could be externalized
- JPA entities enable future scaling with proper caching
- ⚠ Servlet layer needs refactoring to enforce proper layered architecture

### Migration Progress

The project has **successfully completed database migration** to PostgreSQL with Hibernate:

**Phase 1 - Infrastructure (✅ Complete):**
- Added Hibernate ORM 6.6.44.Final
- Added PostgreSQL JDBC Driver 42.7.3
- Added HikariCP connection pool
- Configured Hibernate session factory

**Phase 2 - Entity Migration (✅ Complete):**
- All model classes converted to JPA entities
- Database schema designed and implemented
- Relationships properly defined

**Phase 3 - Repository Implementation (✅ Complete):**
- HibernateUserRepository: ✅ Production-ready
- HibernateTestResultRepository: ✅ Production-ready
- HibernateQuestionRepository: ✅ Production-ready with complete CRUD + batch processing

**Phase 4 - Configuration Integration (✅ Complete):**
- ✅ ApplicationConfig implements manual DI with factory methods
- ✅ All repositories injected via interfaces
- ✅ Services created with constructor injection
- ✅ Centralized dependency management
- ✅ Functional manual DI implementation

**Phase 5 - Data Migration (⏸️ Blocked by Servlet Refactoring):**
- QuestionMigrationRunner implemented for JSON → PostgreSQL migration
- JsonQuestionImportSource adapted for entity compatibility
- Ready for data migration execution
- Duplicate protection and batch processing implemented

**Phase 6 - Servlet Layer Refactoring (⏸️ NEW PRIORITY):**
- ⚠ Some servlets bypass service layer and use repositories directly
- ⚠ Deprecated TopicUtils usage in QuestionServlet
- ⚠ Direct HibernateUtil usage in TestSettingServlet
- ⚠ Service fields not properly initialized in some servlets
- Required for proper layered architecture

**Migration Status: ✅ INFRASTRUCTURE COMPLETE - Servlet Refactoring Required**

**Critical Components Status:**
- ✅ Repository pattern fully implemented and production-ready
- ✅ ApplicationConfig is functional with manual DI
- ✅ Interface-based dependency injection (repositories and services)
- ✅ Configuration management centralized
- ✅ Complete CRUD operations for all entities
- ✅ Factory pattern for dependency creation
- ✅ Transaction management and error handling
- ⚠ Servlet layer has architectural violations
- ⚠ **NOT PRODUCTION-READY** until servlet refactoring completed

## Database Configuration

### PostgreSQL Setup

#### Docker Configuration
```yaml
# docker-compose.yml
services:
  postgres:
    image: postgres:16
    container_name: javatraining-postgres
    environment:
      POSTGRES_DB: javatraining
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker/postgres/init.sql:/docker-entrypoint-initdb.d/init.sql
```

#### Database Schema

Located in `docker/postgres/init.sql`:

```sql
users        (id, username, password_hash, email, nickname, about, avatar_path, role, created_at, blocked)
topics       (id, code, display_name)
questions    (id, topic_id, question_text, correct_answer_index)
answers      (id, question_id, answer_text, answer_index)
test_results (id, user_id, topic_id, total_questions, correct_answers, passed, finished_at)
```

#### Hibernate Configuration

Located in `src/main/resources/hibernate.cfg.xml`:
- **Driver**: PostgreSQL
- **Connection**: localhost:5432/javatraining
- **Dialect**: PostgreSQLDialect
- **Schema Strategy**: validate
- **Show SQL**: true (debug mode)
- **Pool Size**: 10 connections

### Migration Status

**Completed:**
- ✅ Database schema designed and implemented
- ✅ All JPA entities implemented and tested
- ✅ Hibernate infrastructure configured and working
- ✅ Docker environment ready with PostgreSQL 16
- ✅ All repositories implemented with interface pattern
- ✅ Batch processing and duplicate protection implemented
- ✅ ApplicationConfig with manual DI implementation
- ✅ Service layer with proper dependency injection

**Ready for Execution:**
- ⏸️ Data migration from JSON to PostgreSQL (infrastructure ready)
- ⏸️ Comprehensive testing of Hibernate repositories
- ⏸️ Performance optimization and tuning

**🔄 NEXT ACTIONS:**
- Execute data migration using QuestionMigrationRunner
- Remove JSON-based storage components (if any remain)
- Add comprehensive testing for Hibernate repositories
- Performance tuning and optimization

## Testing Strategy

### Unit Tests

- **Service Layer**: Business logic validation
- **Repository Layer**: Data operations
- **Utility Classes**: Helper function testing
- **Exception Handling**: Error scenarios

### Integration Testing

- **Servlet Controllers**: Request/response flow
- **Filter Chain**: Security enforcement
- **Session Management**: State persistence

### Code Coverage

- **JaCoCo Plugin**: Coverage reporting
- **Target**: High coverage for business logic
- **Exclusions**: Configuration and generated code

## Build and Deployment

### Maven Configuration

- **Packaging**: WAR file
- **Java Version**: 21
- **Parent**: Spring Boot for dependency management
- **Plugins**: Compiler, WAR, Surefire, JaCoCo

### Dependencies

- **Servlet API**: Jakarta Servlets
- **JSP/JSTL**: View layer technology
- **Jackson**: JSON processing
- **Logging**: SLF4J + Log4j2
- **Testing**: JUnit 5 + Mockito
- **Lombok**: Code generation

## Future Enhancement Opportunities

### Recommended Improvements

**Priority 1 - Servlet Layer Refactoring (CRITICAL):**
1. Refactor servlets to use service layer properly
2. Remove direct repository access from servlets
3. Remove deprecated TopicUtils usage
4. Remove direct HibernateUtil usage
5. Ensure proper service initialization
6. Enforce layered architecture

**Priority 2 - Data Migration:**
7. Execute Data Migration: Run QuestionMigrationRunner to migrate JSON questions to PostgreSQL

**Priority 3 - Enhancements:**
8. **Enhanced Security**: Add BCrypt password hashing, CSRF protection, rate limiting
9. **Performance Optimization**: Add database indexes, configure fetch strategies
10. **API Layer**: RESTful endpoints for mobile clients
11. **Caching**: Redis for session management and query caching
12. **Comprehensive Testing**: Add integration tests for full application flow

### Long-term Features

1. **Microservices**: Split into user, test, and admin services
2. **Real-time Features**: WebSocket for live test updates
3. **Analytics**: Advanced reporting and insights
4. **Multi-language Support**: Internationalization
5. **Cloud Deployment**: Kubernetes configuration
6. **Advanced Testing**: Adaptive testing algorithms

### Technical Debt

- **HIGH PRIORITY**: Refactor servlet layer for proper architecture (direct repository access, deprecated utilities)
- Execute data migration from JSON to PostgreSQL (infrastructure ready, blocked by servlet refactoring)
- Add comprehensive integration tests
- Consider upgrading to full Spring Boot for additional framework benefits (optional)
- Remove any remaining JSON-based storage components after migration

## AI Development Context

This application serves as an excellent learning platform for:

- **Servlet-based Architecture**: Traditional Java web development
- **Layered Architecture**: Clear separation of concerns
- **Repository Pattern**: Data access abstraction with migration to JPA
- **Security Implementation**: Authentication and authorization
- **Maven Project Structure**: Standard Java project organization
- **Database Migration**: Real-world example of in-memory to database migration
- **JPA/Hibernate**: Modern ORM implementation
- **Docker**: Containerization and development environment setup

### Code Patterns to Learn

- **Factory Pattern**: ValidationFactory
- **Template Method**: BaseServlet abstract class
- **Repository Pattern**: Data access abstraction (both in-memory and JPA)
- **Filter Chain**: Security implementation
- **Service Layer**: Business logic separation
- **Entity Relationships**: JPA annotations and database design
- **Migration Patterns**: Step-by-step migration strategy

### Extension Points for AI Development

- **New Question Types**: Expand beyond multiple choice
- **Adaptive Testing**: Difficulty-based question selection
- **Learning Paths**: Personalized curriculum generation
- **Performance Analytics**: Machine learning for improvement suggestions
- **Integration APIs**: Connect with external learning platforms
- **Migration Automation**: Tools for database migration assistance
- **Testing Automation**: Generate test cases based on entity relationships

---

*This documentation is continuously updated to reflect the current state of JavaTraining project. The project has **completed infrastructure migration** to PostgreSQL with Hibernate. Repository layer is production-ready with manual DI implementation in ApplicationConfig. Please refer to MIGRATION_LOG.md and DB_MIGRATION_PLAN.md for detailed migration history.*

*Last updated: 2026-04-09 (Status: Infrastructure complete, servlet layer refactoring identified as new priority)*

## Current Status

**Project Status: ⚠ INFRASTRUCTURE COMPLETE - Servlet Refactoring Required**

**Completed Components:**
- ✅ Repository layer fully functional with PostgreSQL backend
- ✅ All repositories successfully migrated to Hibernate with interface pattern
- ✅ Complete CRUD operations with transaction management
- ✅ ApplicationConfig functional with manual DI implementation
- ✅ Service layer with proper dependency injection
- ✅ Docker environment with PostgreSQL 16

**Architectural Issues (NEW PRIORITY):**
- ⚠ Servlet layer has architectural violations (direct repository access)
- ⚠ Deprecated TopicUtils usage in QuestionServlet
- ⚠ Direct HibernateUtil usage in TestSettingServlet
- ⚠ Service fields not properly initialized in some servlets
- ⚠ Violates proper layered architecture

**Ready for Execution:**
- ⏸️ Servlet layer refactoring (HIGH PRIORITY)
- ⏸️ Data migration from JSON to PostgreSQL (blocked by servlet refactoring)
- ⏸️ Comprehensive integration testing

**Next Priority Actions:**
1. Refactor servlet layer for proper architecture (CRITICAL)
2. Execute data migration using QuestionMigrationRunner
3. Add comprehensive integration tests
4. Performance tuning and optimization
