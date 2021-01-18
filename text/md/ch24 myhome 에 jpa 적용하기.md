### myhome 에 jpa 적용하기

**build.gradle**

```groovy
plugins {
	id 'org.springframework.boot' version '2.3.7.RELEASE'
	id 'io.spring.dependency-management' version '1.0.10.RELEASE'
	id 'java'
	id 'war'
}

group = 'com.megait'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
	implementation group: 'nz.net.ultraq.thymeleaf', name: 'thymeleaf-layout-dialect'

	// lombok
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	// h2
	runtimeOnly 'com.h2database:h2'

	// data-jpa
	implementation group: 'org.springframework.boot', name: 'spring-boot-starter-data-jpa', version: '2.3.7.RELEASE'

	// validation
	implementation 'org.springframework.boot:spring-boot-starter-validation'
}

test {
	useJUnitPlatform()
}

```



**application.yml**

```yaml
spring:
    datasource:
        url: jdbc:h2:tcp://127.0.0.1/~/test
        username: sa
        password:
        driver-class-name: org.h2.Driver

    jpa:
        hibernate:
            ddl-auto: create
        properties:
            hibernate:
                format_sql: true
#                show_sql: true
logging.level:
        org.hibernate.SQL: debug

```



**com.megait.myhome.domain 패키지에 User.java (클래스) 추가**

```java
package com.megait.myhome.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue
    @Column(name="user_id")
    private Long id; // PK

    private String username; // 회원 id

    private String password; // 회원 비밀번호

}

```



**com.megait.myhome.service 패키지에 UserService  클래스 추가** 

```java
package com.megait.myhome.service;

import com.megait.myhome.domain.User;
import com.megait.myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean signup(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.insert(user);
    }
}

```



**com.megait.myhome.repository에 UserRepository 클래스 추가 **

```java
package com.megait.myhome.repository;

import com.megait.myhome.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    public Long insert(User user){
        em.persist(user);
        return user.getId();
    }
}

```



