# SecurityConfig

```java
 .mvcMatchers("/",
                        "/login",
                        "/signup",
                        "/check-email",
                        "/check-email-token",
                        "/change-password",
                        "/reset-password",
                        "/store/**") // 이 부분 추가
                .permitAll()

```

참고) 인증을 거친 회원만 상세보기를 허용하고 싶다면 `SecurityConfig`는 수정하지 않아도 됨.



# MainController

url 핸들링 메서드 추가

```java
@GetMapping("/store/detail")
public String detail(Long id, Model model){
    Item item = itemService.getItem(id);
    model.addAttribute("item", item);
    return "view/store/detail";
}
```





# detail.html

/templates/view/store/detail.html 

```html

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">
<div layout:fragment="content" class="container">
    <div class="row justify-content-center py-5">
        <img th:src="${item.imageUrl}">
<!--        <i class="fas fa-heart"></i>-->
<!--        <i class="far fa-heart"></i>-->
    </div>
    <div class="row justify-content-center py-5">
        <h2 class="justify-content-center" th:text="${item.name}"></h2>
    </div>
    <div class="row justify-content-center">
        <span class="lead" th:text="${#numbers.formatInteger(item.price, 3, 'COMMA')}"></span>
        <span class="lead"> 원</span>
    </div>
    <hr class = "col-12 my-4"/>
    <div class="row justify-content-center">
        <div class="pb-5 row">
            <button class="btn btn-dark col-12 mx-1 my-2">장바구니 담기</button>
            <button class="btn btn-dark col-12 mx-1 my-21">찜하기</button>
        </div>
    </div>
    </div>

</html>
```

