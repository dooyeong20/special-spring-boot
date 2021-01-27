package com.megait.myhome.service;

import com.megait.myhome.domain.*;
import com.megait.myhome.repository.ItemRepository;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ItemService itemService;

//    @PostConstruct
//    public void initLikeUser() throws IOException {
//        String email = "test@test.com";
//
//        Member member = memberRepository.findByEmail(email);
//        itemService.processLike(member, itemService.getItem(1L));
//        itemService.processLike(member, itemService.getItem(3L));
//        itemService.processLike(member, itemService.getItem(5L));
//        itemService.processLike(member, itemService.getItem(6L));
//    }

    public void addCart(Member member, List<Long> idList) {
        Order order = null;
        List<Item> itemList = null;
        List<OrderItem> orderItemList = null;

        member = memberRepository.getOne(member.getId());

        Optional<Order> orderOptional = orderRepository.findByStatusAndMember(Status.CART, member);
        if(orderOptional.isEmpty()){
            order = orderRepository.save(Order.builder()
                    .member(member)
                    .status(Status.CART)
                    .build());
        } else {
            order = orderOptional.get();
        }

        itemList = itemRepository.findAllById(idList);

        Order finalOrder = order;
        orderItemList = itemList.stream().map(
                item -> OrderItem.builder()
                            .item(item)
                            .order(finalOrder)
                            .count(1)
                            .orderPrice(item.getPrice())
                            .build()
        ).collect(Collectors.toList());

        order = orderRepository.getOne(finalOrder.getId());

        if(order.getOrderItems() == null){
            order.setOrderItems(new ArrayList<>());
        }

        order.getOrderItems().addAll(orderItemList);
    }

    public List<OrderItem> getCart(Member member){
        Optional<Order> orderOptional = orderRepository.findByStatusAndMember(Status.CART, member);
        if(orderOptional.isEmpty()){
            throw new IllegalStateException("empty.cart");
        }

        return orderOptional.get().getOrderItems();
    }

    public int getTotal(List<OrderItem> list){
        return list.stream().mapToInt(
                orderItem -> orderItem.getItem().getPrice()
        ).sum();
    }
}

