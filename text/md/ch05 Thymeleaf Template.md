# Thymeleaf 로 템플릿 만들기





## 1. 프로젝트 생성하기

| boot initializr 로 프로젝트 생성하기                         |
| ------------------------------------------------------------ |
| ![image-20201227174052264](C:\Users\issel\AppData\Roaming\Typora\typora-user-images\image-20201227174052264.png) |

- Gradle
- Java 11
- Package type - war
- Dependencies
  - Spring web
  - Thymeleaf





## 2.  build.gradle

```java
plugins {
	id 'org.springframework.boot' version '2.3.7.RELEASE'
	id 'io.spring.dependency-management' version '1.0.10.RELEASE'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
    
    // 이 부분 추가할 것!
	// https://mvnrepository.com/artifact/nz.net.ultraq.thymeleaf/thymeleaf-layout-dialect
	implementation group: 'nz.net.ultraq.thymeleaf', name: 'thymeleaf-layout-dialect'
	
}

test {
	useJUnitPlatform()
}
```





## 3. src/main 에 다음 파일을 추가

|                                                              |
| ------------------------------------------------------------ |
| ![image-20201227205053639](C:\Users\issel\AppData\Roaming\Typora\typora-user-images\image-20201227205053639.png) |



### Controller

```java
package com.template.test.TemplatePrac;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyController {

    @RequestMapping("/")
    public String index(){
        return "page/index";
    }

    @RequestMapping("/page1")
    public String page1(){
        return "page/page1";
    }

    @RequestMapping("/page2")
    public String page2(){
        return "page/page2";
    }
}
```





### src/main/resources/static/css/common/common.css 

모든 페이지에 적용할 공통 css 를 작성한다. 

```css
/*
모든 페이지에 적용할 공통 스타일
 */

*
{
    margin: 0;
    padding: 0;
    border: 0;
}

body
{
    font: 62.5%/1.5  "Lucida Grande", "Lucida Sans", Tahoma, Verdana, sans-serif;
    color: #000000;
    text-align:center;
}

#wrapper
{
    width:980px;
    text-align:left;
    margin-left:auto;
    margin-right:auto;
    background-color: #FFFFFF;
}

table
{
    border-spacing: 0;
    border-collapse: collapse;
}

td
{
    text-align: left;
    font-weight: normal;
}

h1
{
    font-size: 2.2em;
}

h2
{
    font-size: 2.0em;
}

h3
{
    font-size: 1.8em;
}

h4
{
    font-size: 1.6em;
}

h5
{
    font-size: 1.4em;
}

p
{
    font-size: 1.2em;
}
```





### src/main/resources/static/css/page/index.css

각 페이지에 적용할 CSS 가 있다면 스타일 파일을 생성한다.

```css
/*
 index.html 에만 적용할 스타일이 있다면...
 */
```



### src/main/resources/static/css/page/page1.css

```css
/*
 page1.html 에만 적용할 스타일이 있다면...
 */

body {
    background: aqua;
}
```



### src/main/resources/static/css/page/page2.css

```css
/*
 page2.html 에만 적용할 스타일이 있다면...
 */

body {
    background: tomato;
}
```



js 도 마찬가지로 작업한다.





## 4. templates/fragment 



### config.html

```html
<html lang="ko"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">

<!--configFragment 선언-->
<th:block th:fragment="configFragment">


    <!-- 이 영역에 공통으로 사용할 css, js library를 선언한다. -->
    <link rel="stylesheet" th:href="@{/css/common/common.css}" >
    <script th:src="@{/js/common/common.js}"></script>


    <!-- 공통으로 사용할 CDN 선언 -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>


    <!-- Content Page의 CSS fragment가 있다면 삽입될 위치-->
    <th:block layout:fragment="css"></th:block>

    <!-- Content Page의 script fragment가 있다면 삽입될 위치-->
    <th:block layout:fragment="script"></th:block>

</th:block>
</html>
```



### footer.html

```html
<html lang="ko"
      xmlns:th="http://www.thymeleaf.org">

<!--footerFragment 선언-->
<div th:fragment="footerFragment">
    <h1>이 부분은 footer</h1>
</div>

</html>
```



### header.html

```html
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

<div th:fragment="headerFragment">
    <h1>이 부분은 header</h1>
</div>
</html>
```



## 5. templates/layout



### common_layout.html

```html
<!DOCTYPE html>

<html lang="ko"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">


<meta charset="UTF-8" />
<title>Template Test</title>

<!-- config fragment 를 이곳에-->
<th:block th:replace="fragment/config :: configFragment" ></th:block>


<body>
<!-- header fragment 를 이곳에 -->
<th:block th:replace="fragment/header :: headerFragment"></th:block>

<!--
    content fragment 사용
    현재 layout을 사용하는 content fragment의 내용을 삽입한다.
-->
<th:block layout:fragment="content"></th:block>

<!-- footer fragment 를 이곳에-->
<th:block th:replace="fragment/footer :: footerFragment"></th:block>
</body>

</html>
```





## 6. templates/page

위에서 설정한 config, header, footer와 layout을 뷰에 적용해보자.



### index.html

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<!-- index.html 고유 CSS 추가 -->
<th:block layout:fragment="css">
    <link rel="stylesheet" th:href="@{/css/page/index.css}" >
</th:block>

<!-- index.html 고유 스크립트 추가 -->
<th:block layout:fragment="script">
    <script th:src="@{/js/page/index.js}"></script>
</th:block>

<div layout:fragment="content">
    <h1>이곳은 index</h1>
</div>



</html>
```

### page1.html

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<!-- index.html 고유 CSS 추가 -->
<th:block layout:fragment="css">
    <link rel="stylesheet" th:href="@{/css/page/page1.css}" >
</th:block>

<!-- index.html 고유 스크립트 추가 -->
<th:block layout:fragment="script">
    <script th:src="@{/js/page/page1.js}"></script>
</th:block>

<div layout:fragment="content">
    <h1>이곳은 page1</h1>
</div>



</html>
```

### page2.html

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<!-- index.html 고유 CSS 추가 -->
<th:block layout:fragment="css">
    <link rel="stylesheet" th:href="@{/css/page/page2.css}" >
</th:block>

<!-- index.html 고유 스크립트 추가 -->
<th:block layout:fragment="script">
    <script th:src="@{/js/page/page2.js}"></script>
</th:block>

<div layout:fragment="content">
    <h1>이곳은 page2</h1>
</div>



</html>
```





## 7. 결과 확인

| ![image-20201227210958721](C:\Users\issel\AppData\Roaming\Typora\typora-user-images\image-20201227210958721.png) |
| ------------------------------------------------------------ |
| ![image-20201227211011454](C:\Users\issel\AppData\Roaming\Typora\typora-user-images\image-20201227211011454.png) |
| ![image-20201227211024019](C:\Users\issel\AppData\Roaming\Typora\typora-user-images\image-20201227211024019.png) |

