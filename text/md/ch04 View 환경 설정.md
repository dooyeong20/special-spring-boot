# View (Thymeleaf 템플릿 엔진) 설정

[thymeleaf 공식 사이트](https://www.thymeleaf.org/)

[thymeleaf 공식 튜토리얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)

[스프링 공식 튜토리얼](https://spring.io/guides/gs/serving-web-content/)
[스프링부트 메뉴얼](https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-templateengines)

thymeleaf 엔진으로 돌아가는 view 파일은 확장자가 `.html`이며, 

이들은 모두 `resources/templates/`**{view}**`.html` 여야 한다.

바꾸는 방법은 [Resolver](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#the-springstandard-dialect) 를 지정해주거나, 혹은 [application.properties, applicaton.yml](https://www.baeldung.com/spring-thymeleaf-template-directory)에서 설정할 수 있다.

###### application.yml 이라면

```yaml
spring:
  thymeleaf:
    prefix: classpath:/경로명/
    suffix: .html
```



##### application.properties 라면

```properties
spring.thymeleaf.prefix=classpath:/경로명/
spring.thymeleaf.prefix=.html
```

> 참고 : `classpath`는 `프로젝트:src.main.resources`다. 물론 이또한 변경 가능하지만 컨벤션이므로 그냥 냅두자.





# thymeleaf 사용해보기

##### src/main/java/com/practice/mymall 에 HelloController 클래스 추가

```java
package com.practice.mymall;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
```



| 참고) 모두 임포트 방법 : File –>> Settings –>> Editor –>> General –>> Auto Import –>> 해당 두개의 부분을 체크처리한다. |
| ------------------------------------------------------------ |
| ![image-20201222021120485](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201222021120485.png) |



##### templates에 hello.html 추가하기

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>Hello, Spring boot!</h1>
</body>
</html>
```



| 실행 후 결과 (http://localhost:8080/hello)                   |
| ------------------------------------------------------------ |
| ![image-20201222021524311](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201222021524311.png) |



thymeleaf도 jsp 와 같이 동적페이지다. thymeleaf 템플릿 엔진이 이를 동적으로 렌더링 해준다.

동적으로 attribute를 출력해보자.

##### HelloController 에 함수 추가

```java
@Controller
public class HelloController {

    ...

    @GetMapping("hello2")
    public String hello2(Model model) {
        model.addAttribute("name", "hong");
        return "hello";
    }
}
```



##### templates에 hello.html 수정

```html
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1 th:text="${name} + '님, 안녕하세요!'"></h1>
</body>
</html>
```

| http://localhost:8080/hello                                  |
| ------------------------------------------------------------ |
| ![image-20201222023800082](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201222023800082.png) |
| http://localhost:8080/hello2                                 |
| ![image-20201222023744640](C:\Users\Sera\AppData\Roaming\Typora\typora-user-images\image-20201222023744640.png) |



> 참고) 변경 사항 생길 때마다 서버 리로드 하기 : https://www.grepiu.com/post/48