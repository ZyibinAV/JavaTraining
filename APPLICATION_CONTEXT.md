# JavaTraining Application Documentation

## Overview

**JavaTraining** is a web-based Java learning and testing platform built with Java Servlets, JSP, and Maven. The application provides interactive Java programming tests, user management, and administrative features for tracking learning progress.

## Technology Stack

- **Java Version**: 21
- **Framework**: Java Servlets with JSP
- **Build Tool**: Maven 3.x
- **Parent Framework**: Spring Boot 3.3.5 (for dependency management)
- **Logging**: SLF4J + Log4j2
- **Testing**: JUnit 5.10.2 + Mockito 5.8.0
- **JSON Processing**: Jackson 2.17.0
- **Template Engine**: JSP with JSTL
- **Code Coverage**: JaCoCo

## Project Structure

```
src/main/java/com/homeapp/javatraining/
├── config/          # Application configuration and initialization
├── controllers/     # HTTP request handlers (Servlets)
├── dto/            # Data Transfer Objects
├── exception/      # Custom exception classes
├── filter/         # Servlet filters for authentication and authorization
├── handler/        # Request handling utilities
├── model/          # Domain entities and enums
├── repository/     # Data access layer (in-memory implementations)
├── service/        # Business logic layer
├── session/        # Session management utilities
├── source/         # Data source configurations
├── util/           # Utility classes
└── validation/     # Input validation logic

src/main/webapp/
├── WEB-INF/
│   └── jsp/        # JSP view files
│       ├── admin/  # Admin-specific pages
│       └── common/ # Common UI components
├── css/            # Stylesheets
├── js/             # JavaScript files
└── uploads/        # File upload directory (avatars)
```

## Core Components

### 1. Configuration Layer

#### `ApplicationConfig.java`
- **Purpose**: Central configuration class that initializes repository beans
- **Repositories Created**:
  - `InMemoryUserRepository` - User data management
  - `InMemoryTestResultRepository` - Test result storage
  - `QuestionRepository.defaultRepository()` - Question data source

#### `AppInitListener.java`
- **Purpose**: ServletContextListener for application startup
- **Responsibilities**:
  - Initializes all repositories and stores them in ServletContext
  - Provides dependency injection for servlets
  - Logs initialization status

### 2. Data Models

#### Core Entities

**`User.java`**
- **Fields**: id, username, passwordHash, email, nickname, about, avatarPath, role, createdAt, blocked
- **Roles**: USER, ADMIN (enum)
- **Features**: Profile management, role assignment, blocking functionality

**`Question.java`**
- **Fields**: questionText, answers (List<String>), correctAnswerIndex
- **Format**: JSON-serializable with Jackson annotations
- **Usage**: Multiple choice questions for tests

**`TestResult.java`**
- **Purpose**: Stores test completion data and scores
- **Fields**: userId, testDate, score, topic, questionCount

**`InterviewState.java`**
- **Purpose**: Manages active test session state
- **Fields**: selectedTopics, questions, currentQuestionIndex, answers

#### Enums

**`Topic.java`**
- **Available Topics**:
  - JAVA_SYNTAX ("java-syntax", "Java Syntax")
  - JAVA_CORE ("java-core", "Java Core")
  - JAVA_CONCURRENCY ("java-concurrency", "Java Concurrency")
  - SERVLETS ("servlets", "Сервлеты")
  - MAVEN ("maven", "Maven")
  - JUNIT ("junit5", "JUnit 5")
  - MOCKITO ("mockito", "Mockito")
  - LOGGING ("logging", "Logging")

**`Role.java`**
- **Values**: USER, ADMIN

### 3. Repository Layer (In-Memory)

#### Interface Pattern
All repositories follow interface-implementation pattern:

**`UserRepository.java`** → `InMemoryUserRepository.java`
**`TestResultRepository.java`** → `InMemoryTestResultRepository.java`
**`QuestionRepository.java`** → Static factory method

#### Key Features
- Thread-safe operations using ConcurrentHashMap
- Auto-incrementing IDs
- In-memory data storage (no external database)
- CRUD operations for all entities

### 4. Service Layer

#### Authentication & Authorization

**`AuthenticationService.java`**
- **Purpose**: User login validation
- **Features**:
  - Password hashing with SHA-256
  - User blocking checks
  - Credential validation
  - Custom exceptions for auth failures

**`RegistrationService.java`**
- **Purpose**: New user registration
- **Features**: Input validation, duplicate checking

#### Business Logic Services

**`QuestionService.java`**
- **Purpose**: Question management and retrieval
- **Features**: Topic-based filtering, random selection

**`UserService.java`** / `UserServiceImpl.java`
- **Purpose**: User profile management
- **Features**: Profile updates, avatar management

**`AdminUserService.java`**
- **Purpose**: Administrative user management
- **Features**: Role changes, user blocking/unblocking

**`UserStatisticsService.java`** / `UserTestStatisticsService.java`
- **Purpose**: Test result analytics and reporting

### 5. Controller Layer (Servlets)

#### Base Architecture

**`BaseServlet.java`**
- **Purpose**: Abstract base class for all servlets
- **Features**:
  - Dependency injection from ServletContext
  - Common authentication methods
  - Session management utilities
  - Logging setup

#### Core Controllers

**User-Facing Servlets**:
- `StartServlet` - Initiates test sessions
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
- **Purpose**: Topic enumeration utilities
- **Features**: Code-to-display-name mapping

**`ValidationFactory.java`**
- **Purpose**: Validation object creation
- **Pattern**: Factory pattern for validators

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
HTTP Request → Filter → Servlet → Service → Repository → Memory
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
- **In-Memory Storage**: Data lost on application restart
- **No Database**: Not production-ready for persistent data
- **Single Instance**: Not distributed-ready
- **Limited Security**: Basic authentication only

### Scalability Notes
- Repository pattern allows easy database integration
- Service layer supports business logic expansion
- Filter-based security is extensible
- Session management could be externalized

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

### Immediate Improvements
1. **Database Integration**: Replace in-memory repositories
2. **Enhanced Security**: Add CSRF protection, rate limiting
3. **API Layer**: RESTful endpoints for mobile clients
4. **Caching**: Redis for session management

### Long-term Features
1. **Microservices**: Split into user, test, and admin services
2. **Real-time Features**: WebSocket for live test updates
3. **Analytics**: Advanced reporting and insights
4. **Multi-language Support**: Internationalization

## AI Development Context

This application serves as an excellent learning platform for:
- **Servlet-based Architecture**: Traditional Java web development
- **Layered Architecture**: Clear separation of concerns
- **In-memory Data Patterns**: Repository pattern implementation
- **Security Implementation**: Authentication and authorization
- **Maven Project Structure**: Standard Java project organization

### Code Patterns to Learn
- **Factory Pattern**: ValidationFactory
- **Template Method**: BaseServlet abstract class
- **Repository Pattern**: Data access abstraction
- **Filter Chain**: Security implementation
- **Service Layer**: Business logic separation

### Extension Points for AI Development
- **New Question Types**: Expand beyond multiple choice
- **Adaptive Testing**: Difficulty-based question selection
- **Learning Paths**: Personalized curriculum generation
- **Performance Analytics**: Machine learning for improvement suggestions
- **Integration APIs**: Connect with external learning platforms

---

*This documentation is designed to be continuously updated by AI assistants working on this codebase. Please add new findings, architectural decisions, and implementation details as they are discovered.*
