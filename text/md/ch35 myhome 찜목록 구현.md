# 찜 목록 보기

`찜목록`버튼을 누르면

1. MemberRepository에서 Likes list를 가져온다.
2. Likes List에  들어있는 Item 목록을 가져온다.
3. imageUrl,  이름, 가격을 목록 형태로 출력한다.
4. 체크박스를 통해 원하는 아이템을 체크한다.
5. `장바구니에 추가` 버튼을 누르면 해당 아이템이 장바구니로 이동한다.



# head.html

찜목록의 `href` 수정

```html
...
<li class="nav-item">
    <a class="nav-link" th:href="@{/store/like-list}" sec:authorize="isAuthenticated()"><i class="fas fa-heart"></i></a>
</li>
...
```



# MainController

```java

	....

	private final MemberService memberService; // 추가


	...

    // 추가 
    @GetMapping("/store/like-list")
    public String likeList(@CurrentUser Member member, Model model){
        List<Item> likeList = memberService.getLikeList(member);
        model.addAttribute("likeList", likeList);

        return "view/store/like-list";
	}
```





# MemberService

메서드 추가

```java
public List<Item> getLikeList(Member member) {
    return memberRepository.findByEmail(member.getEmail()).getLikes();
}
```



# like-list.html

/view/store/like-list.html 추가 

```html
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<!-- TODO 4 -->
<div layout:fragment="content" class="container">
    <div class="container px-5 py-5">
    <table class="table table-bordered table-responsive-sm">
        <thead class="thead-dark">
        <tr>
            <th scope="col" style="width:8%">선택</th>
            <th scope="col">이미지</th>
            <th scope="col">이름</th>
            <th scope="col">가격</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="item:${likeList}">
            <td class="text-center align-middle">

                <input type="checkbox" id="customCheck1" name="item_id" th:value="${item.id}">

            </td>
            <td class="align-middle text-center"><img th:src="${item.imageUrl}" style="width: 50px;"></td>

            <td class="align-middle" th:text="${item.name}"></td>

            <td class="align-middle">
                <span class="lead" th:text="${#numbers.formatInteger(item.price, 3, 'COMMA')}"></span>
                <span class="lead"> 원</span>
            </td>

        </tr>
        </tbody>
        <tfoot>
            <td colspan="4" class="text-center">
                <button class="btn btn-dark">장바구니에 추가</button>
            </td>
        </tfoot>
    </table>
    </div>
</div>


</html>
```

