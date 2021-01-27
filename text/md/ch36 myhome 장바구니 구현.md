# 장바구니 구현

- 찜목록 체크박스, 제품 상세보기의 `장바구니에 추가` 버튼을 클릭하면 `/cart/list` 를 요청한다.  + 상품 id 파라미터
- 장바구니 (`cart`) 목록을 조회 후 파라미터로 넘어온 상품을 cart 엔티티에 추가한다.
- `/view/cart/list.html`에 뿌린다.
  - 현재 장바구니의 목록
  - 각 아이템의 수량
  - `주문` 버튼
  - `삭제` 체크박스



## like-list.html

- 체크 박스를 위한 `<form>` 추가
- `장바구니에 추가` 버튼에 submit 추가

```html
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<div layout:fragment="content" class="container">
    <div class="container px-5 py-5">
        <!-- TODO 1 --> 
        <form action="" method="post" name="cartForm">
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


                    <td class="align-middle text-center" th:onclick="itemDetail([[${item.id}]])"><img
                            th:src="${item.imageUrl}" style="width: 50px;"></td>

                    <td class="align-middle" th:text="${item.name}" th:onclick="itemDetail([[${item.id}]])"></td>

                    <td class="align-middle">
                        <span class="lead" th:text="${#numbers.formatInteger(item.price, 3, 'COMMA')}"></span>
                        <span class="lead"> 원</span>
                    </td>

                </tr>

                </tbody>
                <tfoot>
                <td colspan="4" class="text-center">
                    <!-- TODO 2 -->
                    <button class="btn btn-dark" onclick="forms['cartForm'].submit()">장바구니에 추가</button>
                </td>
                </tfoot>
            </table>
        </form>
    </div>
    <script>
        function itemDetail(id) {
            location.href = '[[@{/store/detail}]]?id=' + id;
        }
    </script>
</div>


</html>
```



## Order, OrderItem 엔티티 

- `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` 추가 

> `@Builder`, `@NoArgsConstructor` 두 개만 쓰면 에러남. `@AllArgsConstructor` 를 추가해주면 된다.




## header.html

**장바구니 아이콘**에 `th:href` 추가

```html
<li class="nav-item">
   <a class="nav-link" th:href="@{/store/like-list}" sec:authorize="isAuthenticated()"><i class="fas fa-heart"></i></a>
</li>
```





## MainController

`OrderService` 빈 추가

```java
private final OrderService orderService;
```



요청 핸들러 추가

```java
@PostMapping("/cart/list")
public String addCart(@CurrentUser Member member, @RequestParam("item_id") String[] itemIds, Model model){

    List<Long> idList = List.of(Arrays.stream(itemIds).map(Long::parseLong).toArray(Long[]::new));
    orderService.addCart(member, idList);
    itemService.deleteLikes(member, idList);

    return cartList(member, model);
}
@GetMapping("/cart/list")
public String cartList(@CurrentUser Member member, Model model){

    try {
        List<OrderItem> cartList = orderService.getCart(member);
        model.addAttribute("cartList", cartList);
        model.addAttribute("totalPrice", orderService.getTotalPrice(cartList));

    } catch (IllegalStateException e){
        model.addAttribute("error_message", e.getMessage());
    }
    return "view/cart/list";
}
```





## ItemService 수정

**deleteLikes()** 추가

```java
@Transactional
public void deleteLikes(Member member, List<Long> idList) {
    member = memberRepository.getOne(member.getId());
    member.getLikes().removeAll(itemRepository.findAllById(idList));
}
```







## OrderService 추가

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ItemService itemService;
    @PostConstruct
    @Transactional
    protected void setTestUser() {
        Member member = memberRepository.findByEmail("a@a.a");
        itemService.addLike(member, 30L);
        itemService.addLike(member, 31L);
        itemService.addLike(member, 32L);
        itemService.addLike(member, 33L);
    }

    @Transactional
    public void addCart(Member member, List<Long> itemIds) {
        member = memberRepository.findByEmail(member.getEmail());
        Optional<Order> orderOptional = orderRepository.findByStatusAndMember(Status.CART, member);
        Order order = null;
        if (orderOptional.isEmpty()) {
            order = orderRepository.save(
                    Order.builder()
                            .member(member)
                            .status(Status.CART)
                            .build());
        } else {
            order = orderOptional.get();
        }

        List<Item> itemList = itemRepository.findAllById(itemIds);

        Order finalOrder = order;
        List<OrderItem> orderItemList = itemList.stream().map(item -> OrderItem.builder()
                .item(item)
                .order(finalOrder)
                .count(1)
                .orderPrice(item.getPrice())
                .build()).collect(Collectors.toList());


        order = orderRepository.getOne(finalOrder.getId());

        if (order.getOrderItems() == null) {
            order.setOrderItems(new ArrayList<>());
        }
        order.getOrderItems().addAll(orderItemList);
    }
    public List<OrderItem> getCart(Member member) {
        Optional<Order> orderOptional = orderRepository.findByStatusAndMember(Status.CART, member);
        if (orderOptional.isEmpty()) {
            throw new IllegalStateException("empty.cart");
        }
        return orderOptional.get().getOrderItems();
    }


    public int getTotalPrice(List<OrderItem> list) {
        return list.stream().mapToInt(orderItem -> orderItem.getItem().getPrice()).sum();
    }
}
```



## OrderRepository 추가

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByStatusAndMember(Status status, Member member);
}
```



## view/cart/list.html

```html
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout">

<div layout:fragment="content" class="container">
  <div class="container px-5 py-5">
    <h2 class="text-center" th:if="${'empty.cart' == error_message}">장바구니가 비었습니다.</h2>

    <form th:if="${error_message == null}" th:action="@{/cart/list}" method="post" name="buyForm">
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
        <tr th:each="orderItem:${cartList}">
          <td class="text-center align-middle">
            <input type="checkbox" id="customCheck1" name="item_id" th:value="${orderItem.id}">
          </td>
          <td class="align-middle text-center" th:onclick="itemDetail([[${orderItem.item.id}]])"><img
              th:src="${orderItem.item.imageUrl}" style="width: 50px;"></td>
          <td class="align-middle" th:text="${orderItem.item.name}"
              th:onclick="itemDetail([[${orderItem.item.id}]])"></td>
          <td class="align-middle">
            <span class="lead" th:text="${#numbers.formatInteger(orderItem.item.price, 3, 'COMMA')}"></span>
            <span class="lead"> 원</span>
          </td>
        </tr>
        </tbody>
          
        <tfoot>
        <td colspan="2" class="text-right">
          <span class="lead" th:text="${#numbers.formatInteger(totalPrice, 3, 'COMMA')}"></span>
          <span class="lead"> 원</span>
        </td>
        <td colspan="2" class="text-right">
          <button class="btn btn-dark" onclick="forms['buyForm'].submit()">구매하기</button>
        </td>
        </tfoot>
          
      </table>
    </form>
  </div>
</div>
</html>
```



# 실습. 장바구니 삭제하기

# 실습. 장바구니 ajax로 수정하기