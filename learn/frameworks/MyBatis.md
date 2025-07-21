Okay, let's create a minimal Spring Boot application with MyBatis to manage a `User` object, expose a REST API for getting and setting users, and demonstrate a `core` like structure.

**Project Structure:**

```
my-mybatis-app/
├── src/main/java/com/example/mybatisapp/
│   ├── config/
│   │   └── MyBatisConfig.java          # MyBatis-Spring integration config (optional for basic cases)
│   ├── controller/
│   │   └── UserController.java         # REST API endpoints
│   ├── model/
│   │   └── User.java                   # User POJO (Entity)
│   ├── mapper/
│   │   └── UserMapper.java             # MyBatis Mapper Interface
│   └── MybatisAppApplication.java      # Spring Boot main class
├── src/main/resources/
│   ├── mapper/
│   │   └── UserMapper.xml              # MyBatis SQL Mapper XML
│   ├── application.properties          # Spring Boot configuration
└── pom.xml                             # Maven project file
```

-----

**Step 1: `pom.xml` (Maven Dependencies)**

This file defines your project and its dependencies.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.1</version> <relativePath/> </parent>
    <groupId>com.example</groupId>
    <artifactId>my-mybatis-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>my-mybatis-app</name>
    <description>Demo project for Spring Boot and MyBatis</description>

    <properties>
        <java.version>17</java.version> </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>3.0.3</version> </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

-----

**Step 2: `src/main/resources/application.properties`**

This configures our H2 in-memory database and tells MyBatis where to find the XML mappers.

```properties
# H2 Database Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# MyBatis Configuration
# Tells MyBatis where to find the XML mapper files
mybatis.mapper-locations=classpath*:mapper/*.xml

# Optionally, configure type aliases base package
mybatis.type-aliases-package=com.example.mybatisapp.model

# Logging (optional, useful for debugging SQL)
logging.level.com.example.mybatisapp.mapper=DEBUG
logging.level.org.mybatis.spring.transaction=DEBUG
```

-----

**Step 3: `src/main/java/com/example/mybatisapp/model/User.java` (User POJO)**

This is our simple data model.

```java
package com.example.mybatisapp.model;

public class User {
    private Long id;
    private String name;
    private String email;

    // Constructors
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
```

-----

**Step 4: `src/main/java/com/example/mybatisapp/mapper/UserMapper.java` (MyBatis Mapper Interface)**

This interface defines the operations for our `User` entity. `@Mapper` annotation makes it a Spring component and allows MyBatis to find it.

```java
package com.example.mybatisapp.mapper;

import com.example.mybatisapp.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper {

    // Example using annotations directly (for simple queries)
    @Select("SELECT id, name, email FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT id, name, email FROM users")
    List<User> findAll();

    // Example using XML mapping (for more complex queries or readability)
    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // Auto-populate ID after insert
    void insert(User user);

    // You could also have @Update, @Delete methods here or in XML
}
```

-----

**Step 5: `src/main/resources/mapper/UserMapper.xml` (MyBatis SQL Mapper XML)**

This XML file contains the actual SQL for the `UserMapper` interface. The `namespace` must match the fully qualified name of the mapper interface.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mybatisapp.mapper.UserMapper">

    <resultMap id="userResultMap" type="com.example.mybatisapp.model.User">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <result property="email" column="email"/>
    </resultMap>

    <select id="findById" parameterType="long" resultMap="userResultMap">
        SELECT id, name, email FROM users WHERE id = #{id}
    </select>

    <insert id="insert" parameterType="com.example.mybatisapp.model.User" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO users(name, email) VALUES(#{name}, #{email})
    </insert>

    </mapper>
```

-----

**Step 6: `src/main/java/com/example/mybatisapp/config/MyBatisConfig.java` (Optional MyBatis Config)**

For simple Spring Boot + MyBatis setups, this might not be strictly necessary as the starter auto-configures a lot. However, if you need custom `SqlSessionFactory` or `SqlSessionTemplate` configurations, you'd define them here.

```java
package com.example.mybatisapp.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
// Scans for MyBatis mapper interfaces in the specified package
@MapperScan("com.example.mybatisapp.mapper")
public class MyBatisConfig {

    // This bean is typically auto-configured by mybatis-spring-boot-starter,
    // but you'd define it manually if you need fine-grained control or custom properties.
    // @Bean
    // public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    //     SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    //     sessionFactory.setDataSource(dataSource);
    //     // Point to your mapper XMLs if not auto-detected by mybatis.mapper-locations
    //     // Resource[] mapperLocations = new PathMatchingResourcePatternResolver()
    //     //         .getResources("classpath*:mapper/**/*.xml");
    //     // sessionFactory.setMapperLocations(mapperLocations);
    //     sessionFactory.setTypeAliasesPackage("com.example.mybatisapp.model"); // Optional
    //     return sessionFactory.getObject();
    // }
}
```

-----

**Step 7: `src/main/java/com/example/mybatisapp/controller/UserController.java` (REST API Controller)**

This exposes the REST endpoints to interact with our User data.

```java
package com.example.mybatisapp.controller;

import com.example.mybatisapp.mapper.UserMapper;
import com.example.mybatisapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserMapper userMapper;

    @Autowired
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userMapper.insert(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userMapper.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
```

-----

**Step 8: `src/main/java/com/example/mybatisapp/MybatisAppApplication.java` (Main Spring Boot Application)**

The main class that runs the Spring Boot application. We'll also add a `CommandLineRunner` to initialize the H2 database schema.

```java
package com.example.mybatisapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class MybatisAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisAppApplication.class, args);
    }

    // This CommandLineRunner will execute after the application starts,
    // allowing us to create our 'users' table in the H2 in-memory database.
    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("DROP TABLE IF EXISTS users;"); // Drop if exists for clean start
            jdbcTemplate.execute("CREATE TABLE users (" +
                                 "id INT AUTO_INCREMENT PRIMARY KEY," +
                                 "name VARCHAR(255) NOT NULL," +
                                 "email VARCHAR(255) NOT NULL UNIQUE" +
                                 ");");
            System.out.println("User table created successfully!");
        };
    }
}
```

-----

**How to Run and Test:**

1.  **Save all files** in their respective locations as per the project structure.

2.  **Open a terminal** in the `my-mybatis-app` root directory.

3.  **Build the project:**

    ```bash
    mvn clean install
    ```

4.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

    You should see output indicating that Spring Boot has started on port 8080 (default).

5.  **Test the REST APIs using `curl` or Postman/Insomnia:**

      * **Create a User (POST):**

        ```bash
        curl -X POST http://localhost:8080/users \
        -H "Content-Type: application/json" \
        -d '{"name": "Alice Smith", "email": "alice@example.com"}'
        ```

        Expected Output: JSON representation of the created user (including generated ID) with HTTP Status 201 CREATED.

      * **Get All Users (GET):**

        ```bash
        curl http://localhost:8080/users
        ```

        Expected Output: JSON array containing the user(s) you've created.

      * **Get User by ID (GET):** (Replace `1` with the actual ID you got from the POST request)

        ```bash
        curl http://localhost:8080/users/1
        ```

        Expected Output: JSON representation of the user with ID 1.

      * **Access H2 Console (optional):**
        Open your browser and go to `http://localhost:8080/h2-console`.
        Use `jdbc:h2:mem:testdb` as JDBC URL, `sa` as username, and `password` as password. Click "Connect". You can then browse the `USERS` table and see the data.

This setup provides a complete, basic Spring Boot application using MyBatis for data persistence and a REST API for interaction, demonstrating a clear separation of concerns in its folder structure.

### What is `resultMap` in MyBatis?

In MyBatis, a `<resultMap>` is a powerful XML element used to define **how the data retrieved from a database query maps to a Java object (POJO)**. It's a fundamental concept for controlling the data hydration process in MyBatis.

Let's break down your example:

```xml
<resultMap id="userResultMap" type="com.example.mybatisapp.model.User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="email" column="email"/>
</resultMap>
```

  * **`id="userResultMap"`**: This is a **unique identifier** for this `resultMap`. You'll reference this ID in your `<select>` statements (e.g., `resultMap="userResultMap"`) to tell MyBatis which mapping definition to use.
  * **`type="com.example.mybatisapp.model.User"`**: This specifies the **fully qualified class name of the Java POJO** (your `User` model class) that the database row will be mapped into.
  * **`<id property="id" column="id"/>`**:
      * This element maps the **primary key** column of the database table.
      * `property="id"`: Refers to the **field name** (`id`) in your `com.example.mybatisapp.model.User` Java class.
      * `column="id"`: Refers to the **column name** (`id`) in your database table.
  * **`<result property="name" column="name"/>`**:
      * This element maps a **non-primary key column** of the database table.
      * `property="name"`: Refers to the `name` field in your Java class.
      * `column="name"`: Refers to the `name` column in your database table.
  * **`<result property="email" column="email"/>`**:
      * Similar to above, maps the `email` column to the `email` property.

### Why use `resultMap`?

1.  **Column-to-Property Name Mismatch:**
      * The most common reason. If your database column names (`user_id`, `user_name`, `user_email`) do not exactly match your Java POJO's property names (`id`, `name`, `email`), `resultMap` is essential to explicitly define these mappings. Without it, MyBatis wouldn't know how to fill your `User` object correctly.
2.  **Complex Mappings (Joins, Nested Objects, Collections):**
      * `resultMap` is crucial for handling more complex scenarios:
          * **One-to-One Relationships:** Mapping joined columns from two tables into separate objects (e.g., User and Profile objects).
          * **One-to-Many Relationships:** Mapping multiple rows from a child table into a collection within a parent object (e.g., a User with a List of Orders).
          * **Discriminator:** Handling polymorphic types (different sub-classes based on a column value).
3.  **Clarity and Readability:**
      * Even when column names match property names, explicitly defining the mapping in a `resultMap` can improve the clarity and maintainability of your SQL mappers, especially for large or complex queries.
4.  **Performance Optimization:**
      * By explicitly defining the mapping, MyBatis can often optimize the hydration process, though for simple cases, auto-mapping is very efficient.

While MyBatis can often **auto-map** simple cases where column names directly match Java property names (e.g., `SELECT id, name, email FROM users` will map to `User` if it has `id`, `name`, `email` properties), `resultMap` provides **explicit, robust, and flexible control** over how your SQL query results are transformed into Java objects. It's a cornerstone of MyBatis's flexibility and power.