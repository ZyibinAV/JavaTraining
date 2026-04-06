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
│   ├── source/          # JSON question sources (temporary until DB migration)
│   ├── util/            # Utility classes
│   └── validation/      # Input validation logic
├── src/main/resources/
│   ├── hibernate.cfg.xml  # Hibernate ORM configuration
│   ├── log4j2.xml         # Log4j2 configuration
│   └── questions/         # JSON question files (temporary)
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

- **Purpose**: Central configuration class that initializes repository beans
- **Status**: **🔄 NEEDS COMPLETE REWRITE** - current implementation has critical issues
- **Current State**:
    - Uses concrete classes instead of proper dependency injection ❌
    - Hard-coded repository instantiation ❌
    - No configuration management ❌
    - Missing proper interface-based design ❌
- **Critical Issues**: 
    - Class needs complete redesign for proper DI pattern
    - Should use interfaces and proper configuration management
    - Current implementation is not production-ready
- **Required Changes**: Complete rewrite needed with proper dependency injection

#### `AppInitListener.java`

- **Purpose**: ServletContextListener for application startup
- **Responsibilities**:
    - Initializes all repositories and stores them in ServletContext
    - Provides dependency injection for servlets
    - Logs initialization status
- **Current Issue**: Resolved - ApplicationConfig fully updated

### 1.1. ApplicationConfig - Critical Issues

#### Current State (ACTUAL):
```java
@Getter
public class ApplicationConfig {
    private final UserRepository userRepository;                    // ❌ Interface but concrete instantiation
    private final TestResultRepository testResultRepository;      // ❌ Interface but concrete instantiation  
    private final QuestionRepository questionRepository;          // ❌ Interface but concrete instantiation
    
    public ApplicationConfig() {
        this.userRepository = new HibernateUserRepository();       // ❌ Hard-coded instantiation
        this.testResultRepository = new HibernateTestResultRepository(); // ❌ Hard-coded instantiation
        this.questionRepository = new HibernateQuestionRepository(); // ❌ Hard-coded instantiation
    }
}
```

#### Critical Issues:
- ❌ **No proper dependency injection**
- ❌ **Hard-coded repository instantiation**
- ❌ **Not production-ready**
- ❌ **Missing configuration management**
- ❌ **Violates DI principles**

#### Required Changes:
- 🔄 **Complete rewrite needed**
- 🔄 **Implement proper dependency injection**
- 🔄 **Add configuration management**
- 🔄 **Use factory or framework-based DI**

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
    public Optional<Question> findById(Long id) { /* ✅ Implemented */ }
    
    @Override
    public List<Question> findAll() { /* ✅ Implemented */ }
    
    @Override
    public void save(Question question) { /* ✅ Implemented with transaction management */ }
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

### 3. Repository Layer (Migration Status: ✅ COMPLETED)

#### Current Implementation Status

**Hibernate Repositories (✅ Implemented):**

- **HibernateUserRepository** - Fully implemented with Session API
    - CRUD operations with transaction management
    - HQL queries for username lookup
    - Error handling and logging

- **HibernateTestResultRepository** - Fully implemented
    - Implements TestResultRepository interface
    - HQL queries with user relationship filtering
    - Transaction management and logging

- **HibernateQuestionRepository** - Fully implemented
    - ✅ Implements QuestionRepository interface
    - ✅ Complete CRUD operations (getQuestions, findById, findAll, save)
    - ✅ Proper method signature: getQuestions(Topic topic)
    - ✅ Transaction management and error handling
    - Added method existsByTextAndTopic(String questionText, Topic topic)
    - Used for data migration duplicate protection
  
#### Repository Layer Status: **FULLY MIGRATED TO HIBERNATE**

All repositories:
- ✅ Use interface-based design
- ✅ Use Hibernate Session API
- ✅ Support complete CRUD operations
- ✅ Include proper transaction management
- ✅ Ready for production use

**⚠️ CRITICAL CONFIGURATION ISSUE:**
ApplicationConfig needs complete rewrite - currently uses hard-coded instantiation instead of proper dependency injection.

**Legacy Repositories (Status):**

- **QuestionRepository.defaultRepository()** - JSON-based (completely replaced)
- **InMemoryUserRepository** - Completely replaced
- **InMemoryTestResultRepository** - Completely replaced

#### Migration Status: **COMPLETE**

All repository implementations successfully migrated to Hibernate with proper interface pattern.

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
- Uses QuestionRepository interface for loading questions ✅
- Loads Topic entities via TopicLoader instead of manual creation ✅
- Fully integrated with Hibernate-based question loading ✅
- `QuestionServlet` - Displays questions and handles answers
- `ResultServlet` - Shows test results
- `LoginServlet` / `LogoutServlet` - Authentication
- `RegistrationServlet` - User registration
- `ProfileServlet` / `ProfileEditServer` - User profile management
- `HomeServlet` - Main dashboard
- `AvatarUploadServlet` / `AvatarSelectServlet` - Avatar management
- `TestSettingServlet` - Test configuration

**Admin Controllers** (`/admin/`):

- `AdminServlet` - Admin dashboard
- `AdminUserServlet` - User management interface
- `AdminBlockUserServlet` - User blocking operations
- `AdminChangeRoleServlet` - Role management
- `AdminStatisticsServlet` - System statistics

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

- **Data Migration**: JSON questions still need to be migrated to PostgreSQL (infrastructure ready)
- **Security**: SHA-256 hashing without modern security features
- **Single Instance**: Not distributed-ready

### Scalability Notes

- Repository pattern fully implemented with database integration ✅
- Service layer supports business logic expansion
- Filter-based security is extensible
- Session management could be externalized
- JPA entities enable future scaling with proper caching

### Migration Progress

The project has **successfully completed database migration** with all components fully functional:

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
- HibernateUserRepository: ✅ Implemented and working
- HibernateTestResultRepository: ✅ Implemented and working
- HibernateQuestionRepository: ✅ Implemented with complete CRUD operations

**Phase 4 - Configuration Integration (🔄 INCOMPLETE):**
- ApplicationConfig needs complete rewrite ❌
- Hard-coded repository instantiation ❌
- Missing proper dependency injection ❌
- Repository pattern implemented but configuration broken ❌

**Phase 5 - Data Migration (⏳ Ready):**
- QuestionMigrationRunner implemented for JSON → PostgreSQL migration
- FileQuestionSource adapted for entity compatibility
- Ready for data migration execution

**Migration Status: 🔄 INCOMPLETE - Configuration Issues**

**Critical Components Status:**
- ✅ Repository pattern fully implemented
- ❌ ApplicationConfig needs complete rewrite
- ✅ Interface-based dependency injection (repositories)
- ❌ Configuration management broken
- ✅ Complete CRUD operations for all entities
- ❌ Hard-coded instantiation instead of proper DI
- ✅ Transaction management and error handling
- ❌ Not production-ready due to configuration issues

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
- ✅ Database schema designed
- ✅ All JPA entities implemented
- ✅ Hibernate infrastructure configured
- ✅ Docker environment ready
- ✅ All repositories implemented with interface pattern
- ❌ ApplicationConfig needs complete rewrite

**Critical Issues:**
- ❌ ApplicationConfig uses hard-coded instantiation
- ❌ No proper dependency injection
- ❌ Configuration management missing
- ❌ Not production-ready

**In Progress:**
- 🔄 Data migration from JSON to PostgreSQL (infrastructure ready)
- 🔄 Performance optimization and testing

**Next Steps:**
- 🔄 Complete rewrite of ApplicationConfig with proper DI
- 🔄 Implement configuration management
- Execute data migration using QuestionMigrationRunner
- Remove JSON-based storage components
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

### Immediate Improvements (Post-Migration)

1. **🔄 CRITICAL: Rewrite ApplicationConfig**: Implement proper dependency injection
2. **Complete Database Migration**: Finish implementing proper configuration management
3. **Enhanced Security**: Add BCrypt password hashing, CSRF protection, rate limiting
4. **Performance Optimization**: Add database indexes, configure fetch strategies
5. **API Layer**: RESTful endpoints for mobile clients
6. **Caching**: Redis for session management and query caching

### Long-term Features

1. **Microservices**: Split into user, test, and admin services
2. **Real-time Features**: WebSocket for live test updates
3. **Analytics**: Advanced reporting and insights
4. **Multi-language Support**: Internationalization
5. **Cloud Deployment**: Kubernetes configuration
6. **Advanced Testing**: Adaptive testing algorithms

### Technical Debt

- Remove JSON-based question storage
- Implement proper transaction management
- Add comprehensive integration tests
- Upgrade to Spring Boot for full framework benefits

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

*This documentation is continuously updated to reflect the current state of the JavaTraining project. The project has **completed database migration** but has **critical configuration issues** that need to be addressed. Please refer to MIGRATION_LOG.md and DB_MIGRATION_PLAN.md for detailed migration history.*

*Last updated: 2026-04-06 (Configuration issues identified - ApplicationConfig needs complete rewrite)*

## Immediate Action Required

**🔄 CONFIGURATION REWRITE NEEDED**

**Critical Issues Identified:**
1. ❌ ApplicationConfig needs complete rewrite
2. ❌ Hard-coded repository instantiation
3. ❌ No proper dependency injection
4. ❌ Configuration management missing

**Current Status:**
- Repository layer fully functional with PostgreSQL backend
- All repositories successfully migrated to Hibernate
- ❌ Configuration layer broken - not production-ready
- Ready for data migration after configuration fix

**Priority Actions:**
1. 🔄 Complete rewrite of ApplicationConfig
2. 🔄 Implement proper dependency injection pattern
3. 🔄 Add configuration management
4. 🔄 Test integration after configuration fix
