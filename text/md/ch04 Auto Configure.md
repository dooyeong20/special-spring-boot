# Auto Configure

application 자바 파일(메인 메서드가 있는 java)에 가보면 다음과 같은 애너테이션이 있다.

| DemoApplication.java                                         |
| ------------------------------------------------------------ |
| ![image-20201213165531104](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201213165531104.png) |



이 애너테이션 소스파일에 들어가보면 또 다음과 같은 애너테이션들이 있다.

| SpringBootApplication.class                                  |
| ------------------------------------------------------------ |
| ![image-20201213165741807](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201213165741807.png) |



-  `@EnableAutoConfiguration`  (`@SpringBootApplication` 안에 숨어 있음)

- **Bean Scanning**(빈을 생성하여 등록하는 과정)은 **두 단계**로 나뉜다.

  - 1단계: `@ComponentScan`

  - 2단계: `@EnableAutoConfiguration`

    

## @ComponentScan

`@ComponentScan`이 설정된 클래스 위치를 기준으로, 하위 패키지의 모든`@Component` 클래스들을 찾아 Bean으로 등록한다. 

예)

com.demo.a

com.demo.b

com.demo.Application <= 이 클래스에 `@ComponentScan`이 있다면 a, b 패키지의 모든 `@Component` 클래스를 찾아 생성한다.



`@Component`는 다음 자식 애터네이션이 있다.

- `@Configuration` `@Repository` `@Service` `@Controller` `@RestController`

  

## @EnableAutoConfiguration

- spring.factories 에 기재되어있는 모든 클래스를 빈으로 생성한다.

  - 위치 : org.springframework.boot.autoconfigure.EnableAutoConfiguration

  | spring-boot-autoconfigure-X.X.X.jar                          |
  | ------------------------------------------------------------ |
  | ![image-20201213171333858](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201213171333858.png)<br />이 부분을 열어보면..<br />![image-20201213171539771](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201213171539771.png) |

  파일 내부에 # Auto Configure 부분이 있을 것이다. 

  이 부분에 기재된 클래스들은 모두 `@Configuraion` 애너테이션이 적용되어있는데, 이 클래스들이 모두 빈으로 등록되는 것은 아니다.

  `@ConditionalOnXxxYyyZzz`로 지정된 경우, 특정 조건에 부합해야 생성된다.

  예) 

  @ConditionalOnWebApplication(type = Type.SERVLET)
  @ConditionalOnClass(MessageDispatcherServlet.class)
  @ConditionalOnMissingBean(WsConfigurationSupport.class)







# 나만의 Auto Configure 만들기



#### 0. `maven project`생성

| ![image-20201213181429026](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201213181429026.png) |
| ------------------------------------------------------------ |
| ![image-20201213181435968](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201213181435968.png) |
| GroupId : me.xxx<br />ArtifactId : XXX-spring-boot-autoconfigure |

#### 1. `pom.xml`에 다음을 추가

```xml
... 
	<dependencies>
		...
        ...
        <!-- 의존성 2개 추가 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-autoconfigure</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-autoconfigure-processor</artifactId>
			<optional>true</optional>
		</dependency>
	</dependencies>

	<!-- dependencyManagement 추가 --> 
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-dependencies</artifactId>
				<version>2.0.3.RELEASE</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
</project>

```



#### 2. `me.xxx.xxx_spring_boot_autoconfigure`에 `Book.java` 추가

```java
package me.ssellu.sera_spring_boot_autoconfigure;

public class Book {
	private String title;
	private String publisher;
	private int price;
	// getters와 setters도 추가한다.
    // toString()도 오버라이드
}
```





#### 2. `me.xxx.xxx_spring_boot_autoconfigure`에 `BookConfiguration.java` 추가

```java
package me.ssellu.sera_spring_boot_autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookConfiguration {

	@Bean
	public Book book() {
		Book book = new Book();
		book.setTitle("재미있는 스프링 부트");
		book.setPublisher("자바출판사");
		book.setPrice(30000);
		return book;
	}
}
```



#### 3. `src/main`에 `resources` 폴더 추가

#### 4. `src/main/resources` 에 `META-INF` 폴더 추가

#### 5. `META-INF`에 `spring.factories` 파일 추가

| spring.factories                                             |
| ------------------------------------------------------------ |
| org.springframework.boot.autoconfigure.EnableAutoConfiguration= \ me.ssellu.sera_spring_boot_autoconfigure.BookConfiguration |
| 두번째 줄에 BookConfiguration 의 풀네임(FQCN)을 기재할 것    |



#### 6. 프로젝트 우클릭 > Run As > `maven install`

| 결과                                                         |
| ------------------------------------------------------------ |
| [INFO] ------------------------------------------------------------------------<br/>[INFO] BUILD SUCCESS<br/>[INFO] ------------------------------------------------------------------------ |

이렇게 하면 나만의 자동설정이 만들어졌다.

이것을 다른 프로젝트에서 활용해보자.



## 나만의 Auto Configure를 새로운 프로젝트에 적용하기

#### 0. 새 spring boot 프로젝트 생성

#### 1. 새 프로젝트의 `pom.xml` 에 다음을 추가 

앞서 작성한 메이븐 프로젝트의 `pom.xml` 에 기재된 `groupId`, `artifactId`, `version`를 복사하여 이를 적용할 새 프로젝트의 `pom.xml`의 `<dependency>`부분에 추가해주면 된다.

```xml
	<?xml version="1.0" encoding="UTF-8"?>
... (중략)
	<dependencies>
		... (중략)
		<dependency>
			<groupId>com.example</groupId>
			<artifactId>demo</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
	</dependencies>
... (중략)
```



#### 2. demoApplication.java 의 메인 메서드를 조금 수정 (필수 X)

```java
package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import me.ssellu.sera_spring_boot_autoconfigure.Book;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
        
        // 조금 빠른 실행을 위해 Web 서비스 타입이 아닌 일반 타입으로 설정
		application.setWebApplicationType(WebApplicationType.NONE); 
        
        
		application.run(args);
	}
}
```

#### 3. BookRunner.java 추가

```java
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import me.ssellu.sera_spring_boot_autoconfigure.Book;

@Component
public class BookRunner implements ApplicationRunner{
	
	@Autowired
	Book book;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println(book);
	}
}
```

book  필드가 `@Autowird`되었기 때문에 Autoconfigure 측에서 bean으로 지정된 Book 빈이 생성되어 이 곳에 등록된다.



#### 4. 결과 확인 

| console                                                      |
| ------------------------------------------------------------ |
| ....<br />Book [title=재미있는 스프링 부트, publisher=자바출판사, price=30000] |



## 문제점

그런데 Auto configure를 이렇게 하면 다음 문제가 발생한다.

Demo 앱 측에서 자신이 원하는 값으로 Book 빈을 새롭게 등록하고자한다고 가정해보자.

즉, '재미있는 스프링 부트' 책 말고, '어려운 스프링 부트' 로 말이다.

#### 1. demoApplication.java 수정

```java
package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import me.ssellu.sera_spring_boot_autoconfigure.Book;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}
	
    ////////////////////// 이 부분을 추가한다. ///////////////////////
	@Bean
	public Book book() {
		Book book = new Book();
		book.setTitle("어려운 스프링 부트");
		book.setPublisher("자바출판사");
		book.setPrice(35000);
		return book;
	}
	////////////////////////////////////////////////////////////////
}
```

#### 2. 결과 확인 

| console                                                      |
| ------------------------------------------------------------ |
| ***************************<br/>APPLICATION FAILED TO START<br/>*************************** |

로그는 다음과 같다.

Description:

The bean 'book', defined in class path resource [me/ssellu/sera_spring_boot_autoconfigure/BookConfiguration.class], could not be registered. A bean with that name has already been defined in com.example.demo.DemoApplication and overriding is disabled.

Action:

Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true

이렇게 에러가 난 이유는 [링크](#AutoConfigure) 에서 설명한 컴포넌트 스캔 순서에 의한 것이다.

> **Bean Scanning**(빈을 생성하여 등록하는 과정)은 **두 단계**로 나뉜다.
>
> - 1단계: `@ComponentScan`
> - 2단계: `@EnableAutoConfiguration`

DemoApplication 의 `@Bean`으로 등록되었던 book()에 의해 1차적으로 빈이 생성된 후 

외부 프로젝트 출신의   '재미있는 스프링 부트' 책이 이후에 등록되는 부분에서 오버라이드 disabled 에러가 난 것이다.

만약 에러가 안 났다해도 자동 생성된 빈은 우리가 기대한 '어려운 스프링 부트'도 아니었을 것이다.

이 오류를 해결해보자.

## 문제 해결

### 방법1) 

#### 1. me.xxx.xxx_spring_boot_autoconfigure의 `BookConfiguration.java` 를 수정

```java
package me.ssellu.sera_spring_boot_autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookConfiguration {

	@Bean
	@ConditionalOnMissingBean // 이 부분을 추가한다.
	public Book book() {
		Book book = new Book();
		book.setTitle("재미있는 스프링 부트");
		book.setPublisher("자바출판사");
		book.setPrice(30000);
		return book;
	}
}
```

#### 2. autpconfigure 프로젝트를 maven clean 후 maven install 로 재 빌드 

이때 Versioning 을 해주면 좋다. (1.0.1 이런 식으로)

#### 3. demo 스프링부트 프로젝트를 재 실행 

에러가 나는 경우 book 을 import 한 부분을 삭제 후 재 import 해본다. (이클립스 리로드 버그가 있다.)

#### 4. 결과 확인

| Console                                                      |
| ------------------------------------------------------------ |
| Book [title=어려운 스프링 부트, publisher=자바출판사, price=35000] |

### 방법2) applicaton.properties 에 속성 넣어주기 

#### 1. Auto Configure 프로젝트의 `BookProperties.java` 추가

```java
package me.ssellu.sera_spring_boot_autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mybook")
public class BookProperties {
	private String title;
	private String publisher;
	private int price;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
```



#### 2. Auto Configure 프로젝트의 `BookConfiguration.java` 의 `book()` 수정

```java
package me.ssellu.sera_spring_boot_autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BookProperties.class)
public class BookConfiguration {

    ///////// 이 부분 수정 /////////////////////////////
	@Bean
	@ConditionalOnMissingBean
	public Book book(BookProperties properties) {
		Book book = new Book();
		book.setTitle(properties.getTitle());
		book.setPublisher(properties.getPublisher());
		book.setPrice(properties.getPrice());
		return book;
	}
	///////////////////////////////////////////////////
}
```



#### 3. Auto Configure 프로젝트 `maven install `



#### 2. demo 스프링 부트 프로젝트의 `application.properties` 에 다음을 추가 

`application.properties`가 없으면 src/main/resources 에 파일 생성

| application.properties                                       |
| ------------------------------------------------------------ |
| mybook.title=my third book<br/>mybook.publisher=Java's book<br/>mybook.price = 10000 |

문자열 표기(따옴표)는 쓰지 않는다.

자동으로 프로퍼티에 대해 setter가 실행될 것이다.

prefix (mybook)은 임의로 지정한다. 단, mybook과 Bool 빈을 매핑하는 java 파일을 만들어야 한다. (Auto configure 프로젝트에)

필드명이 두 단어 이상인 경우, 예를 들어 minPrice 라면 **mybook.min-price**, **mybook.minPrice** 두 표기 방식 모두 사용 가능하다.

`application.properties`에 빈 속성을 추가했으니 메인의 `@Bean`으로 등록해둔 `book()`은 삭제하면 된다.

#### 3. demo 스프링 부트 프로젝트의 `DemoApplication.java` 의 book() 은 삭제 

#### 4. 실행

| console                                                      |
| ------------------------------------------------------------ |
| Book [title=my third book, publisher=Java's book, price=10000] |

