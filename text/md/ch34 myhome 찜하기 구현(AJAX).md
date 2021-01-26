# detail.html

하단에 ajax 추가 

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

            <!-- TODO 1 -->
            <button class="btn btn-dark col-12 mx-1 my-21" id="like">찜하기</button>
        </div>
    </div>
    <!-- TODO 2 -->
    <script th:inline="javascript">
        $(document).ready(function (){
            $("#like").click(function () {
                $.ajax({
                    type: 'GET',       // post 방식으로 전송
                    url: '/store/like', // 요청보낼 주소
                    data:'id=[[${item.id}]]', // 요청으로 보낼 파라미터
                    dataType: 'json',    // 응답의 content-type (json 타입으로 받겠다!)
                    success: function (result){ // 요청을 성공적으로 했다면
                        alert(result.message);
                    }
                });
            });
        });
    </script>
</div>
</html>
```



# MainController

**addLike()** 메서드 추가.

```java
// TODO 3
@RequestMapping("/store/like")
@ResponseBody
@Transactional
public String addLike(@CurrentUser Member member, Long id){
    JsonObject object = new JsonObject();
    if(member == null){
        object.addProperty("result", false);
        object.addProperty("message", "로그인이 필요한 기능입니다.");
        return object.toString();
    }

    // id 에 해당하는 Item 을 조회한다.

    // 현재 유저(Member)의 likes에 조회한 Item을 추가한다.
    // Item의 liked 에 1 증가
    // => H2 에서 liked 증가된 것 확인.
    // => ItemService로 위 내용을 옮기기 (addLike??)
    
    logger.info("id : " + id);
    logger.info("name : " + member.getEmail());
    object.addProperty("result", true);
    object.addProperty("message", "찜 목록에 등록되었습니다.");
    return object.toString();
}
```



통신이 잘 되는 지 확인.

****

# build.gradle

```groovy
// https://mvnrepository.com/artifact/com.google.code.gson/gson
implementation group: 'com.google.code.gson', name: 'gson', version: '2.7'
```





# MainController

**MainController**에 url 핸들링 메서드 추가

```java
// TODO 3
@RequestMapping("/store/like")
@ResponseBody
public String addLike(@CurrentUser Member member, Long id){
    JsonObject object = new JsonObject();
    if(member == null){
        object.addProperty("result", false);
        object.addProperty("message", "로그인이 필요한 기능입니다.");
        return object.toString();
    }


    try {
        itemService.addLike(member, id);
        logger.info("id : " + id);
        logger.info("name : " + member.getEmail());
        object.addProperty("result", true);
        object.addProperty("message", "찜 목록에 등록되었습니다.");

    } catch (IllegalStateException e){
        object.addProperty("result", false);
        object.addProperty("message", e.getMessage());
    }


    return object.toString();
}
```



# ItemService

메서드 추가

```java
@Transactional
public void addLike(Member member, Long id) {
    member = memberRepository.findByEmail(member.getEmail());
    List<Item> list = member.getLikes();


    Optional<Item> optionalItem = itemRepository.findById(id);
    if(optionalItem.isPresent()) {
        Item item = optionalItem.get();
        if (list.contains(item)){
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }
        list.add(item);
        item.setLiked(item.getLiked() + 1);
    }
}
```



# 참고) Modal 띄우기

**detail.html**

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
    <hr class="col-12 my-4"/>
    <div class="row justify-content-center">
        <div class="pb-5 row">
            <button class="btn btn-dark col-12 mx-1 my-2">장바구니 담기</button>

            <button class="btn btn-dark col-12 mx-1 my-21" id="like">찜하기</button>
        </div>
    </div>


    <!-- Modal -->
    <div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog"
         aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalCenterTitle">My Book Store</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    ...
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-dark" data-dismiss="modal">닫기</button>
                </div>
            </div>
        </div>
    </div>


    <script th:inline="javascript">
        $(document).ready(function () {
            $("#like").click(function () {
                $.ajax({
                    type: 'GET',       // post 방식으로 전송
                    url: '/store/like', // 요청보낼 주소
                    data: 'id=[[${item.id}]]', // 요청으로 보낼 파라미터
                    dataType: 'json',    // 응답의 content-type (json 타입으로 받겠다!)
                    success: function (result) { // 요청을 성공적으로 했다면
                        // alert(result.message);
                        $('.modal-body').html(result.message);
                        $('#exampleModalCenter').modal('show');

                    }
                });
            });
        });
    </script>


</div>

</html>
```

