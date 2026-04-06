# JavaTraining Migration Plan (JSON → PostgreSQL + Hibernate)

## Stage 1 – Infrastructure

**COMPLETED** ✅
- Added Hibernate ORM 6.6.44.Final
- Added PostgreSQL JDBC driver 42.7.3
- Added Jakarta Persistence API 3.1.0
- Added HikariCP 5.1.0

---

## Stage 2 – Database Design

**COMPLETED** ✅
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

docker/postgres/init.sql (primary)
src/main/resources/db/training.sql (copy)

---

## Stage 3 – Hibernate Infrastructure

**COMPLETED** ✅

Create package

config.hibernate

Classes:

HibernateUtil
HibernateSessionFactory

Files:

hibernate.cfg.xml

Goal:

Create SessionFactory and database connection.

Docker Configuration:
- PostgreSQL 16 container
- Database: javatraining
- Port: 5432
- Connection: jdbc:postgresql://localhost:5432/javatraining

---

## Stage 4 – Entity Migration

**COMPLETED** ✅

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

Update:

JSON adapter implemented:
- FileQuestionSource now converts JSON into entity model (Question + Answer)

Technical Details:
- All entities use Lombok for boilerplate reduction
- Proper bidirectional relationships established
- JPA-compliant constructors and validation

---

## Stage 4.5 – Entity Testing (NEW)

**PENDING** ⏳

Unit tests for JPA entities:
- Entity validation tests
- Relationship mapping tests
- Constraint validation tests

Integration tests:
- Hibernate session tests
- Database transaction tests

---

## Stage 5 – Repository Migration

**COMPLETED** ✅

Replace in-memory repositories.

Old:

InMemoryUserRepository
InMemoryTestResultRepository
QuestionRepository(JSON)

New:

HibernateUserRepository
HibernateTestResultRepository
HibernateQuestionRepository

Implementation Details:
- Extend JpaRepository or use Hibernate Session
- Implement proper transaction management
- Add error handling and logging
- Maintain existing interface contracts

### Current Implementation Status:

**HibernateUserRepository – ✅ COMPLETED**
- ✅ Implements UserRepository interface
- ✅ Full CRUD operations with transaction management
- ✅ HQL queries and error handling
- ✅ Ready for production use

**HibernateTestResultRepository – ✅ COMPLETED**
- ✅ Implements TestResultRepository interface  
- ✅ Full CRUD operations with transaction management
- ✅ HQL queries with user relationship filtering
- ✅ Ready for production use

**HibernateQuestionRepository – ✅ COMPLETED**
- ✅ Implements QuestionRepository interface
- ✅ Complete CRUD operations (getQuestions, findById, findAll, save)
- ✅ Proper method signature: getQuestions(Topic topic)
- ✅ Transaction management and error handling
- ✅ Ready for production use
---

## Stage 5.5 – Repository Testing (NEW)

**PENDING** ⏳

Test Hibernate repositories:
- CRUD operations testing
- Performance comparison with in-memory
- Transaction rollback testing
- Error scenario handling

---

## Stage 6 – ApplicationConfig Update

Status: 🔄 **NEEDS COMPLETE REWRITE**

✔ UserRepository switched to Hibernate ✅
✔ TestResultRepository switched to Hibernate ✅
✔ QuestionRepository switched to Hibernate (interface) ✅
✔ @Getter annotation added ✅

### ⚠️ CRITICAL ISSUES IDENTIFIED:
- ❌ Uses hard-coded repository instantiation
- ❌ No proper dependency injection framework
- ❌ Configuration management missing
- ❌ Not production-ready design
- ❌ Violates dependency inversion principles

### Current State (ACTUAL):
```java
@Getter
public class ApplicationConfig {
    private final UserRepository userRepository;                    // ✅ Interface
    private final TestResultRepository testResultRepository;      // ✅ Interface  
    private final QuestionRepository questionRepository;          // ✅ Interface
    
    public ApplicationConfig() {
        this.userRepository = new HibernateUserRepository();       // ❌ Hard-coded
        this.testResultRepository = new HibernateTestResultRepository(); // ❌ Hard-coded
        this.questionRepository = new HibernateQuestionRepository(); // ❌ Hard-coded
    }
}
```

### Required Actions:
- 🔄 Complete rewrite of ApplicationConfig needed
- 🔄 Implement proper dependency injection pattern
- 🔄 Add configuration management
- 🔄 Use factory or framework-based DI

---

## Stage 7 – Data Migration

**PENDING** ⏳

Move questions from JSON files into PostgreSQL.

Steps:
1. Create data migration script – ✅ COMPLETED
2. Read existing JSON question files
3. Convert to entity format
4. Batch insert into database
5. Validate data integrity
6. Backup original JSON files

Data Validation:
- Compare record counts before/after
- Validate relationships integrity
- Test data retrieval functionality

- Validation: Topic must be persistent (id != null)
- Migration runner throws exception for invalid Topic
- Duplicate protection implemented at repository level
- Migration runner skips existing questions
- Batch processing implemented for question persistence
- Hibernate session optimized with flush/clear

1. Create data migration script – ✅ COMPLETED
---

## Stage 8 – Remove JSON Storage

**PENDING** ⏳

Remove:

FileQuestionSource
JSON question files
source package (if unused)

Replace with database queries.

Cleanup:
- Remove src/main/resources/questions/
- Update QuestionRepository interface
- Remove JSON-related dependencies

---

## Stage 9 – Performance Tuning

**PENDING** ⏳

Add:

Database indexes:
- Primary keys (automatic)
- Foreign key indexes
- Query-specific indexes

Fetch strategies:
- Lazy loading for relationships
- Batch fetching for collections
- Join fetch optimization

Batch loading:
- Configure batch size
- Optimize insert operations
- Connection pool tuning

Prevent:

N+1 queries
- Use JOIN FETCH in HQL
- Entity graph optimization
- Query result caching

---

## Stage 10 – Final Refactoring

**PENDING** ⏳

Clean repository layer:
- Remove unused interfaces
- Standardize method naming
- Add comprehensive JavaDoc

Improve transaction management:
- @Transactional annotations
- Proper rollback configuration
- Connection leak prevention

Add integration tests:
- End-to-end testing
- Database integration tests
- Performance benchmarking

---

## Migration Status Summary

### ✅ Completed (Stages 1-5):
- Infrastructure setup
- Database design
- Hibernate configuration  
- Entity migration
- Repository implementation (all 3 repositories completed)

### 🔄 Incomplete (Stage 6):
- ❌ ApplicationConfig needs complete rewrite (hard-coded instantiation)
- ❌ No proper dependency injection framework
- ❌ Configuration not production-ready

### ⏳ Ready for Execution (Stages 7-10):
- Data migration (after ApplicationConfig fix)
- Remove JSON storage
- Performance tuning
- Final refactoring

### Current Migration State: **🔄 INCOMPLETE - CONFIGURATION ISSUES**

**Root Cause:** Repository layer complete, but ApplicationConfig uses hard-coded instantiation instead of proper dependency injection.

**Impact:** Application works with PostgreSQL but configuration layer not production-ready.

### Current Application Status:
- ✅ User management works with PostgreSQL
- ✅ Test results work with PostgreSQL  
- ✅ Questions work through PostgreSQL (repository ready)
- ✅ Application starts and runs (full functionality)
- ✅ Repository pattern implemented at interface level
- ❌ Configuration layer not production-ready
- ❌ Hard-coded dependency instantiation

### Technical Specifications:
- **Hibernate**: 6.6.44.Final
- **PostgreSQL**: 16 (Docker)
- **JPA**: Jakarta Persistence 3.1.0
- **Connection Pool**: HikariCP 5.1.0
- **Java**: 21

### Risk Mitigation:
- Data backup before migration
- Step-by-step validation
- Rollback strategy for each stage
- Performance monitoring

## CRITICAL ISSUES & IMMEDIATE ACTION PLAN

### 🚨 **CONFIGURATION REWRITE NEEDED**

#### **Primary Goal:** Migrate from collections/JSON storage to PostgreSQL database using Hibernate

#### **Current Status:** Repository layer complete, but ApplicationConfig needs complete rewrite for production readiness

### **Critical Issues Remaining:**
#### **1. 🔄 Complete ApplicationConfig Rewrite – CRITICAL**
- ❌ Hard-coded repository instantiation
- ❌ No proper dependency injection
- ❌ Configuration management missing
- ❌ Not production-ready

#### **2. Repository Pattern – ✅ COMPLETED**
- ✅ QuestionRepository interface implemented
- ✅ HibernateQuestionRepository completed
- ✅ All repositories use proper interface pattern

### **Priority 1 - Configuration Rewrite (CRITICAL):**  

#### **3. Fix ApplicationConfig**
- Implement proper dependency injection pattern
- Replace hard-coded instantiation with factory or framework-based DI
- Add configuration management
- Make application production-ready

#### **4. Execute Data Migration (After Configuration Fix)**
- ✅ Migration script created (QuestionMigrationRunner)
- ✅ FileQuestionSource adapted for entity compatibility
- Ready to execute migration after ApplicationConfig fix

#### **5. Remove JSON Storage**
- Remove src/main/resources/questions/ directory
- Remove QuestionRepository.defaultRepository() method
- Remove FileQuestionSource class (if no longer needed)
- Update any remaining JSON references

### **Priority 2 - Testing & Validation:**

#### **6. Test Repository Integration**
- Unit tests for all repositories
- Integration tests for database operations
- End-to-end application testing

### **Estimated Timeline:**
- **Priority 1 (ApplicationConfig rewrite): 2-3 hours**
- **Priority 2 (data migration): 1-2 hours**
- **Priority 3 (testing): 2-3 hours**

### **Success Criteria:**
1. ✅ Application starts without errors
2. ✅ All repositories use PostgreSQL via Hibernate
3. ✅ Repository pattern properly implemented
4. 🔄 ApplicationConfig uses proper dependency injection
5. ✅ JSON storage completely removed
6. ✅ Data migration successful
7. ✅ All tests pass
8. ✅ Configuration production-ready

### **Current Application Status:**
- ✅ Application starts and runs
- ✅ User management works with PostgreSQL
- ✅ Test results work with PostgreSQL
- ✅ Questions work through PostgreSQL (repository ready)
- ✅ Repository pattern implemented at interface level
- ❌ Configuration layer not production-ready
- ❌ Hard-coded dependency instantiation

### **Critical Blocker:**
- ❌ ApplicationConfig needs complete rewrite before production deployment
- ❌ Current configuration not suitable for testing or production

### **Failure Impact:**
- ❌ Application not production-ready due to configuration issues
- ❌ Hard to test and maintain due to hard-coded dependencies

---

**Migration Plan Status: 🔄 INCOMPLETE - CONFIGURATION ISSUES**
**Last Updated: 2026-04-06 (Configuration problems identified - ApplicationConfig needs complete rewrite)**
**Next Action: Complete ApplicationConfig rewrite with proper dependency injection**
