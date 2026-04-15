# JavaTraining - Pull Request Guide

## Project Overview

JavaTraining is a web-based Java training and testing platform built with Spring Boot, Hibernate ORM, and PostgreSQL. The application allows users to register, take tests on various Java topics, track their progress, and administrators to manage users and test content.

## Technology Stack

### Core Technologies
- **Java**: 21
- **Spring Boot**: 3.3.5 (parent POM, manual DI - no Spring MVC)
- **Hibernate ORM**: 6.6.44.Final
- **PostgreSQL**: 42.7.3 (database driver)
- **HikariCP**: 5.1.0 (connection pooling)

### Build & Test Tools
- **Maven**: Build automation
- **JUnit Jupiter**: 5.10.2 (testing framework)
- **Mockito**: 5.8.0 (mocking framework)
- **AssertJ**: 3.24.2 (assertions)
- **H2 Database**: 2.2.224 (in-memory database for testing)
- **Jacoco**: 0.8.11 (code coverage)

### Additional Libraries
- **Lombok**: 1.18.34 (code generation)
- **Jackson**: 2.17.0 (JSON processing)
- **Log4j2**: Logging framework
- **Jakarta Servlet API**: For web layer
- **JSTL**: JSP Standard Tag Library

### Infrastructure
- **Docker**: PostgreSQL 16 container
- **Packaging**: WAR (Web Application Archive)

## Architecture

### Design Patterns
- **Repository Pattern**: Interface-based repositories with Hibernate implementations
- **Service Layer**: Business logic separation
- **Manual Dependency Injection**: Custom DI container via ApplicationConfig
- **Servlet-based Controllers**: Traditional servlet pattern (not Spring MVC)
- **DTO Pattern**: Data transfer objects for statistics and API responses

### Layer Structure
```
├── config/           # Configuration and DI container
├── controllers/     # HTTP request handlers (Servlet-based)
├── service/         # Business logic layer
├── repository/      # Data access layer (interfaces + Hibernate)
├── model/           # JPA entities
├── dto/             # Data transfer objects
├── validation/      # Input validation
├── filter/          # Servlet filters (auth, admin)
├── handler/         # Request/error handlers
├── util/            # Utility classes
├── tools/migration/ # Data migration tools
└── session/         # Session management utilities
```

## Database Schema

### Entities
- **User**: User accounts with roles (USER/ADMIN), profiles, avatars
- **Topic**: Test topics (e.g., Java Core, Collections, Streams)
- **Question**: Test questions linked to topics
- **Answer**: Possible answers for questions
- **TestResult**: User test results with scores and timestamps
- **InterviewState**: Session state for active tests

### Relationships
- User 1:N TestResult
- Topic 1:N Question
- Question 1:N Answer
- User uses Role enum (USER, ADMIN)

## Project Structure

### Source Organization
```
src/
├── main/
│   ├── java/com/homeapp/javatraining/
│   │   ├── config/
│   │   │   ├── ApplicationConfig.java      # Manual DI container
│   │   │   ├── AppInitListener.java       # ServletContext init
│   │   │   └── hibernate/
│   │   │       └── HibernateUtil.java     # SessionFactory
│   │   ├── controllers/
│   │   │   ├── BaseServlet.java           # Base servlet with DI
│   │   │   ├── admin/                     # Admin controllers
│   │   │   ├── LoginServlet.java
│   │   │   ├── RegistrationServlet.java
│   │   │   ├── ProfileServlet.java
│   │   │   ├── QuestionServlet.java
│   │   │   ├── ResultServlet.java
│   │   │   └── ...
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── UserServiceImpl.java
│   │   │   ├── AuthenticationService.java
│   │   │   ├── QuestionService.java
│   │   │   └── ...
│   │   ├── repository/
│   │   │   ├── UserRepository.java        # Interface
│   │   │   ├── HibernateUserRepository.java # Implementation
│   │   │   ├── QuestionRepository.java
│   │   │   ├── HibernateQuestionRepository.java
│   │   │   └── ...
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Question.java
│   │   │   ├── Topic.java
│   │   │   └── ...
│   │   ├── dto/
│   │   ├── validation/
│   │   ├── filter/
│   │   ├── handler/
│   │   ├── util/
│   │   └── tools/migration/
│   ├── resources/
│   │   ├── hibernate.cfg.xml              # Hibernate config
│   │   ├── log4j2.xml                     # Logging config
│   │   └── questions/                     # JSON question files
│   │       ├── java-syntax.json
│   │       ├── java-core.json
│   │       ├── java-concurrency.json
│   │       ├── servlets.json
│   │       ├── maven.json
│   │       ├── junit5.json
│   │       ├── mockito.json
│   │       ├── logging.json
│   │       └── hibernate.json
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── jsp/                       # JSP pages
│       │   └── web.xml                    # Servlet config
│       ├── css/                           # Stylesheets
│       └── js/                            # JavaScript
└── test/
    └── java/com/homeapp/javatraining/
        ├── service/
        ├── model/
        ├── handler/
        ├── util/
        └── validation/
docker/
└── postgres/
    └── init.sql                           # Database initialization script
```

## Setup Instructions

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

### Local Development Setup

1. **Clone the repository** 
```bash
git clone <https://github.com/ZyibinAV/JavaTraining.git>
cd JavaTraining
```

2. **Start PostgreSQL database**
```bash
docker-compose up -d
```
This starts PostgreSQL 16 on port 5432 with database `javatraining`.

3. **Configure Hibernate**
Edit `src/main/resources/hibernate.cfg.xml` if needed:
- Database URL: `jdbc:postgresql://localhost:5432/javatraining`
- Username: `postgres`
- Password: `postgres`

4. **Build the project**
```bash
mvn clean install
```

5. **Run tests**
```bash
mvn test
```

6. **Deploy to servlet container**
```bash
mvn package
# Deploy target/JavaTraining-1.0-SNAPSHOT.war to Tomcat or similar
```

### Database Initialization

The database schema is automatically created when the PostgreSQL container starts via the `docker/postgres/init.sql` script. This script:

- Creates all required tables: `users`, `topics`, `questions`, `answers`, `test_results`
- Sets up foreign key constraints and indexes
- Inserts initial topic data (Java Syntax, Java Core, Concurrency, Servlets, Maven, JUnit 5, Mockito, Logging)

Hibernate is configured with `hibernate.hbm2ddl.auto=validate` to ensure the entity mappings match the database schema.

### JSON Question Files Format

The project includes JSON files with test questions located in `src/main/resources/questions/`. These files are used to populate the database with questions via the migration tool.

**File Naming Convention:**
- Files must be named `{topicCode}.json` (e.g., `java-syntax.json`, `java-core.json`)
- Topic codes must match the codes in the `topics` table (java-syntax, java-core, java-concurrency, servlets, maven, junit5, mockito, logging, hibernate)

**JSON Structure:**
```json
[
  {
    "questionText": "Question text here",
    "answers": [
      "Answer option 1",
      "Answer option 2",
      "Answer option 3"
    ],
    "correctAnswerIndex": 0
  }
]
```

**Field Descriptions:**
- `questionText`: String containing the question text
- `answers`: Array of strings with answer options (minimum 2 options)
- `correctAnswerIndex`: Integer (0-based index) indicating the correct answer

**Example:**
```json
[
  {
    "questionText": "Какой из следующих вариантов является правильным объявлением метода main?",
    "answers": [
      "public static void main(String[] args)",
      "static public void start(String args[])",
      "void Main(String[] arguments)"
    ],
    "correctAnswerIndex": 0
  }
]
```

**Adding New Questions:**
1. Create or edit the appropriate JSON file in `src/main/resources/questions/`
2. Follow the format specified above
3. Ensure the topic exists in the database
4. Run the migration tool to import questions

### Data Migration

The project includes tools to migrate questions from JSON files to PostgreSQL. The application comes with pre-configured JSON question files for all topics in `src/main/resources/questions/`.

**Migration Process:**

1. **Ensure database is running:**
```bash
docker-compose up -d
```

2. **Build the project:**
```bash
mvn clean compile
```

3. **Run data migration:**
```bash
java -cp target/classes com.homeapp.javatraining.tools.migration.DataMigrationExecutor
```

**Migration Details:**

- The migration tool (`DataMigrationExecutor`) loads questions from JSON files
- Each JSON file corresponds to a topic (e.g., `java-syntax.json` → topic with code `java-syntax`)
- Existing questions are detected and skipped (based on question text and topic)
- New questions are inserted with their answers
- The process validates questions before insertion

**Available Question Files:**
- `java-syntax.json` - Java syntax questions
- `java-core.json` - Java core concepts
- `java-concurrency.json` - Java concurrency
- `servlets.json` - Servlet API
- `maven.json` - Maven build tool
- `junit5.json` - JUnit 5 testing
- `mockito.json` - Mockito mocking
- `logging.json` - Logging frameworks
- `hibernate.json` - Hibernate ORM

**Requirements:**
- PostgreSQL container must be running
- Topics must exist in database (created by `init.sql`)
- JSON files must be properly formatted (see JSON Question Files Format section)
- Database connection must be configured in `hibernate.cfg.xml`

**Troubleshooting:**
- If migration fails with "File not found", ensure JSON files are in `src/main/resources/questions/`
- If "No topics found" error occurs, verify `init.sql` was executed and topics exist in database
- Check logs for detailed error messages

## Running Tests

### Test Execution
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceImplTest

# Run tests with coverage report
mvn test jacoco:report
```

### Test Coverage
Coverage reports are generated in `target/site/jacoco/index.html`.

### Test Structure
- **Unit Tests**: Service layer with mocked repositories
- **Model Tests**: Entity behavior and validation
- **Handler Tests**: Request/error handling
- **Util Tests**: Utility functions
- **Validation Tests**: Input validation logic

## Key Features

### User Features
- User registration with automatic first-user admin assignment
- Login/logout with session management
- Profile editing (nickname, about, avatar)
- Avatar upload/selection
- Test taking with topic selection
- Test results tracking
- User statistics

### Admin Features
- User management (block/unblock, role changes)
- Test statistics and analytics
- Topic and question management
- User statistics viewing

### Security
- SHA-256 password hashing
- Role-based access control (USER/ADMIN)
- Authentication filter for protected routes
- Admin filter for admin-only routes
- Session-based authentication

## Configuration Files

### hibernate.cfg.xml
- Database connection settings
- Hibernate dialect
- SQL logging
- HBM2DDL auto mode (validate)
- HikariCP connection pool settings
- Entity mappings

### log4j2.xml
- Separate loggers for different layers (repository, service, controller)
- Rolling file appenders
- Console logging
- Log pattern configuration

### docker-compose.yml
- PostgreSQL 16 container configuration
- Volume mounts for data persistence
- Init script mounting

## Pull Request Guidelines

### Before Submitting PR

1. **Run full test suite**
```bash
mvn clean test
```

2. **Check test coverage**
```bash
mvn test jacoco:report
# Review target/site/jacoco/index.html
```

3. **Code style**
- Follow existing code style
- Use Lombok annotations appropriately
- Maintain consistent naming conventions

4. **Documentation**
- Update relevant documentation
- Add JavaDoc for public methods
- Update README if needed

### PR Description Template

```markdown
## Description
[Brief description of changes]

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] All tests passing
- [ ] Manual testing performed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
```

### Code Review Focus Areas

1. **Repository Layer**
- Interface adherence
- Proper session management
- Transaction handling
- Query optimization

2. **Service Layer**
- Business logic correctness
- Proper validation
- Error handling
- Dependency injection

3. **Controller Layer**
- Request/response handling
- Session management
- Security checks
- Error handling

4. **Model Layer**
- JPA annotations correctness
- Relationship mappings
- Validation annotations
- Equals/hashCode implementations

## Common Issues and Solutions

### Database Connection Issues
- Ensure Docker container is running: `docker-compose ps`
- Check connection parameters in hibernate.cfg.xml
- Verify PostgreSQL is accessible on port 5432

### Test Failures
- Ensure H2 database dependency is available for tests
- Check mock configurations in test classes
- Verify test data setup

### Build Issues
- Ensure Java 21 is installed: `java -version`
- Check Maven version: `mvn -version`
- Clean and rebuild: `mvn clean install`

## Development Workflow

1. Create feature branch from main
2. Implement changes with tests
3. Run full test suite
4. Check code coverage
5. Submit pull request with description
6. Address review feedback
7. Merge after approval

## Contact & Support

For questions about this project or pull request process, contact the maintainers or refer to project documentation.

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Hibernate Documentation](https://docs.jboss.org/hibernate/orm/6.6/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
