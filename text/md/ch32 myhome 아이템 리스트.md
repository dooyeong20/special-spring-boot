# Repository 추가

**AlbumRespository** 추가

```java
public interface AlbumRepository extends JpaRepository<Album, Long> {
}
```



**BookRepository** 추가

```java
public interface BookRepository extends JpaRepository<Book, Long> {
}
```



# MainController 수정

```java
package com.megait.myhome.controller;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Member;
import com.megait.myhome.repository.AlbumRepository;
import com.megait.myhome.repository.BookRepository;
import com.megait.myhome.repository.ItemRepository;
import com.megait.myhome.service.CurrentUser;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {

    private final ItemRepository itemRepository;
    private final BookRepository bookRepository; // 이 부분 추가
    private final AlbumRepository albumRepository; // 이 부분 추가


    private final Logger logger = LoggerFactory.getLogger(getClass());


   
    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model){
 
        List<Album> albumList = albumRepository.findAll();  // 이 부분 추가
        List<Book> bookList = bookRepository.findAll();  // 이 부분 추가
 
        model.addAttribute("albumList", albumList); // 이 부분 추가
        model.addAttribute("bookList", bookList); // 이 부분 추가

        logger.info(albumList.toString());
        logger.info(bookList.toString());

        if(member != null){
            model.addAttribute(member);
        }

        return "view/index";
    }

    @GetMapping("/login")
    public String login(){
        return "/view/user/login";
    }
}

```





# index.html 수정

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<!--&lt;!&ndash; index.html 고유 CSS 추가 &ndash;&gt;-->
<!--<th:block layout:fragment="css">-->
<!--    <link rel="stylesheet" th:href="@{/css/page/index.css}" >-->
<!--</th:block>-->

<!--&lt;!&ndash; index.html 고유 스크립트 추가 &ndash;&gt;-->
<!--<th:block layout:fragment="script">-->
<!--    <script th:src="@{/js/page/index.js}"></script>-->
<!--</th:block>-->

<div layout:fragment="content">
    <div class="py-5 ml-2">
        <h2>이달의 추천 도서</h2>
        <p class="lead">현재 핫한 도서 리스트!</p>
    </div>
    <th:block th:insert="fragment/item_list :: ItemListFragment(${bookList})"/>

    <div class="py-5 ml-2">
        <h2>이달의 추천 앨범</h2>
        <p class="lead">현재 핫한 음반 리스트!</p>
    </div>
    <th:block th:insert="fragment/item_list :: ItemListFragment(${albumList})"/>
    <hr class="col-12 my-4">

</div>

</html>
```







# fragment/item_list.html 수정

```html
<html lang="ko"
      xmlns:th="http://www.thymeleaf.org">

<!--footerFragment 선언-->
<div th:fragment="ItemListFragment(itemList)">
    <div class="row" >
        <div th:each="item:${itemList}" class="col-sm-2 justify-content-center">
            <div class="card" style="width:200px">
                <img class="card-img-top" th:src="${item.imageUrl}" alt="Card image">
                <div class="card-body">
                    <h4 class="card-title" th:text="${item.name}"></h4>
                    <p class="card-text">
                        <span th:text="${#numbers.formatInteger(item.price, 3,'COMMA')}"></span>
                        원
                    </p>
                    <a th:href="@{/store/detail/{id}(id=${item.id})}" class="btn btn-primary stretched-link">상세 보기</a>
                </div>
            </div>
        </div>
    </div>
</div>

</html>
```



# 리팩토링 - ItemService

**ItemService** 에 메서드 추가

```java
public List<Album> getAlbumList() {
    return albumRepository.findAll();
}

public List<Book> getBookList() {
    return bookRepository.findAll();
}
```



**MainController** 수정

```java
package com.megait.myhome.controller;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Member;
import com.megait.myhome.repository.AlbumRepository;
import com.megait.myhome.repository.BookRepository;
import com.megait.myhome.repository.ItemRepository;
import com.megait.myhome.service.CurrentUser;
import com.megait.myhome.service.ItemService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {


    private final ItemService itemService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model){

        List<Album> albumList = itemService.getAlbumList();
        List<Book> bookList = itemService.getBookList();

        model.addAttribute("albumList", albumList);
        model.addAttribute("bookList", bookList);

        logger.info(albumList.toString());
        logger.info(bookList.toString());

        if(member != null){
            model.addAttribute(member);
        }

        return "view/index";
    }

    @GetMapping("/login")
    public String login(){
        return "/view/user/login";
    }
}
```





