# AOP (Aspect Oriented Programming)

- 관점지향 프로그래밍
- 흩어진 관심사(Crosscutting Concerns) 혹은 관심의 분리(Separation of Concerns)
- OOP 를 보완하는 보조자 역할
- 자바 AOP 구현체 : AspectJ, JBossAOP, SpringAOP
- 코드 중복을 줄이고 적은 코드 수정으로 전체 변경을 가능하게 함.
- 핵심 관점(메인 비지니스 로직) + 횡단관점(로깅, 트랜잭션, 보안, 인증 등)



# AOP 용어

1. Joinpoint : 작업이 투입되어야 하는 '합류점'

2. Advice : 투입될 작업

3. Target : 투입할 곳 (핵심관점)

4. Pointcut : Target 클래스와 Advice가 결합(Weaving) 될 때 정의되는 결합 규칙

   ​	예) A 클래스의 aa()의 시작 전에 B.bb()를 실행해줘!

   ​		A : Target

   ​		B.bb() : Advice

   ​		before aa()  : Before => joinpoint , aa() => pointcut

5. Aspect : advice + pointcut. (모듈; 어디에 무엇을 적용할 지) 
6. Weaving : advice 에 joinpoint 감싸는 과정 ==> 이 작업을 aop 구현체가 한다.





# AOP 적용 방법

- 컴파일
- 클래스 로드 
- 런타임 <-- Spring AOP 가 사용하는 방법





# Spring AOP 

- Weaving 후 생성된 객체를 Proxy 라고 함.
- 실제 Target이 호출될 때는 Target 자체가 실행되는 것이 아닌 Proxy 객체가 호출됨.
- AOP 설정 방법
  - Proxy 기반 <-- Spring AOP
  - XML 기반 <-- aspectJ 
- 스프링 빈에만 적용 가능
- 자유도는 AspectJ 보다는 낮음. 최소한의 AOP 를 적용





# 직접 프록시 패턴 만들어 보기

```java
package com.megait.aop;

public interface SomeService {
    void createSomething();

    void publishSomething();

    void deleteSomething();
}

```



```java
package com.megait.aop;

import org.springframework.stereotype.Service;

@Service  
public class SomeServiceImpl implements SomeService { // 주인공!
    @Override
    public void createSomething() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Created something.");

    }

    @Override
    public void publishSomething() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Published something.");
    }

    @Override
    public void deleteSomething(){
        System.out.println("Deleted something.");
    }
}

```

이 클래스를 건드리지 않고 `createSomething()`과 `publishSomething()`의 실행 시간을 측정하고 싶다.



```java
package com.megait.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class ProxySomeService implements SomeService {

    @Autowired
    SomeService someService;

    @Override
    public void createSomething() {
        long begin = System.currentTimeMillis();
        someService.createSomething();
        System.out.println(System.currentTimeMillis() - begin);
    }

    @Override
    public void publishSomething() {
        long begin = System.currentTimeMillis();
        someService.publishSomething();
        System.out.println(System.currentTimeMillis() - begin);
    }

    @Override
    public void deleteSomething() {
        someService.deleteSomething();
    }
}
```



```java
package com.megait.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MyRunner implements ApplicationRunner {
    @Autowired
    SomeService someService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        someService.createSomething();
        someService.publishSomething();
        someService.deleteSomething();
    }
}

```



```java
package com.megait.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AopApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AopApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);

	}

}
```



> [결과]
>
> Created something.
> 1013
> Published something.
> 2015
> Deleted something.



## 직접 만들 수는 있지만...

매번 프록시를 작성해야 함..

여러 클래스의 여러 메서드에 적용하려면..

복잡한 객체 관계 사이에서는...

등등의 문제가 있음.



## 그래서 등장한 것이 스프링 AOP

- 스프링 IoC 컨테이너가 제공하는 기반 시설과 Dynamic 프록시를 사용하여 여러 복잡한 문제 해결.

- 동적 프록시: 동적으로 프록시 객체 생성하는 방법
   - 자바가 제공하는 방법은 인터페이스 기반 프록시 생성.
   -  CGlib은 클래스 기반 프록시도 지원.

	- 스프링 IoC: 기존 빈을 대체하는 동적 프록시 빈을 만들어 등록 시켜준다.

- 클라이언트 코드 변경 없음.			

- [AbstractAutoProxyCreator](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.html) implements [BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html)



# build.gradle

```groovy
// TODO 1
implementation 'org.springframework.boot:spring-boot-starter-aop'
```



# PerfAspect 

```java
package com.megait.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// TODO 2
@Component
@Aspect
public class PerfAspect {

    // Advice (해야 할 일)

    // TODO 3
    // @Around("execution(* com.megait..*.*Service.*(..))") // Pointcut 정의.

    // TODO 5
    // @Around("@annotation(PerLogging)")

    // TODO 8
    @Around("bean(someServiceImpl)")
    public Object logPerf(ProceedingJoinPoint pjp) throws  Throwable{
        // ProceedingJoinPoint : Advice가 적용되는 대상 (createSomething(), publishSomething() 자체)
        long begin = System.currentTimeMillis();
        Object retVal = pjp.proceed(); // 메서드 실행 후 결과 받기
        System.out.println(System.currentTimeMillis() - begin);
        return retVal;

    }
}
```



# SomeServiceImpl

```java
package com.megait.aop;

import org.springframework.stereotype.Service;

@Service
public class SomeServiceImpl implements SomeService {
    @PerLogging  // TODO 6
    @Override
    public void createSomething() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Created something.");

    }

    @PerLogging  // TODO 7
    @Override
    public void publishSomething() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Published something.");
    }


    @Override
    public void deleteSomething(){
        System.out.println("Deleted something.");
    }
}
```



## Aspect 정의

- `@Aspect`
- `@Component`도 당연히 선언해야 함



## Pointcut 정의

- `@Pointcut(표현식)`
- 주요 표현식
  - execution
  - @anntation
  - bean
- 포인트컷 조합식
  - &&, ||, !



## Advice 정의

- @Before
- @AfterRunning
- @AfterThrowing
- @Around

<p><strong></strong><br><span style="color: rgb(0, 0, 0);font-size:10pt;" _foo="color: rgb(0, 0, 0); font-size: 10pt;">• execution(public void set*(..)) : 리턴 타입이 void이고 메서드 이름이 set으로 시작하고, 파라미터가 0개 이상인 메서드 호출.</span><br><span style="color: rgb(0, 0, 0);font-size:10pt;" _foo="color: rgb(0, 0, 0); font-size: 10pt;">• execution(* com.oracleclub.core..()) : com.oracleclub.core 패키지의 파라미터가 없는 모든 메서드 호출.</span><br><span style="color: rgb(0, 0, 0);font-size:10pt;" _foo="color: rgb(0, 0, 0); font-size: 10pt;">• execution(* com.oracleclub.core...(..)) : com.oracleclub.core 패키지 및 하위 패키지에 있는 파라미터가 0개 이상인 메서드 호출.</span><br><span style="color: rgb(0, 0, 0);font-size:10pt;" _foo="color: rgb(0, 0, 0); font-size: 10pt;">• execution(Integer com.oracleclub.core.AtricleBO.write(..)) : 리턴 타입이 Integer인 AtricleBO 인터페이스의 write() 메서드 호출.</span><br><span style="color: rgb(0, 0, 0);font-size:10pt;" _foo="color: rgb(0, 0, 0); font-size: 10pt;">• execution(* get*( * )) : 이름이 get으로 시작하고 1개의 파라미터를 갖는 메서드 호출.</span><br><span style="color: rgb(0, 0, 0);font-size:10pt;" _foo="color: rgb(0, 0, 0); font-size: 10pt;">• execution(* get*(.)) : get으로 시작하고 2개의 파라미터를 갖는 메서드 호출.</span></p>

https://www.google.com/url?sa=D&q=https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html%23aop-pointcuts&ust=1611861900000000&usg=AOvVaw2VJjO9JgTA566_0Gzcq_73&hl=ko