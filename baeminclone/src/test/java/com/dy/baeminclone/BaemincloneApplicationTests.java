package com.dy.baeminclone;

import com.dy.baeminclone.domain.Cart;
import com.dy.baeminclone.domain.CartMenu;
import com.dy.baeminclone.domain.Menu;
import com.dy.baeminclone.domain.Review;
import com.dy.baeminclone.domain.RoleType;
import com.dy.baeminclone.domain.Store;
import com.dy.baeminclone.domain.StoreDetail;
import com.dy.baeminclone.domain.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootTest
class BaemincloneApplicationTests {

	@PersistenceContext
	private EntityManager em;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	void initDB(){
		String birth = "1997.02.23";
		User kim = new User("kim","pw", RoleType.USER, birth);
		Cart cart =Cart.builder()
				.user(kim)
				.build();

		StoreDetail storeDetail = StoreDetail.builder()
				.openTime("3")
				.liked(214L)
				.recentOrderCount(231L)
				.closedDayInfo("no holiday")
				.closeTime("23")
				.deliveryRange("in Seoul")
				.build();

		Store eggStore = Store.builder()
				.name("egg farm")
				.storeDetail(storeDetail)
				.menuList(new ArrayList<>())
				.build();

		Menu menu1 = new Menu(eggStore, "egg", 3000);
		Menu menu2 = new Menu(eggStore, "rice", 1000);
		CartMenu cartMenu1 = new CartMenu(cart, menu1, 3);
		CartMenu cartMenu2 = new CartMenu(cart, menu2, 4);

//		em.persist(storeDetail);
		em.persist(eggStore);
		em.persist(menu1);
		em.persist(menu2);

		em.persist(cart);
		em.persist(kim);

		em.persist(cartMenu1);
		em.persist(cartMenu2);
	}

	private User makeUSer(String email, String password){
		return User.builder()
				.email(email)
				.password(password)
				.regdate(LocalDateTime.now())
				.build();
	}

	private Cart makeCart(User user){
		return Cart.builder()
				.user(user)
				.build();
	}

	private Store makeStore(String name){
		return Store.builder()
				.name(name)
				.build();
	}

	@Test
	@Rollback(value = false)
	@Transactional
	void contextLoads() {
		initDB();
	}

	@Test
	@Rollback(value = false)
	@Transactional
	void store_review_persist_test(){

		Store store = Store.builder()
				.name("happy store")
				.build();

		Review review1 = Review.builder()
				.content("content1")
				.star(4)
				.regdate(LocalDateTime.now())
				.build();

		Review review2 = Review.builder()
				.content("content2")
				.star(5)
				.regdate(LocalDateTime.now())
				.build();

		review1.setStore(store);
		review2.setStore(store);
		review1.setStore(store);

		em.persist(store);

		for(Review review : store.getReviewList()){
			logger.info("================================");
			logger.info(review.getContent());
		}

		System.out.println(store);
	}

	@Test
	@Rollback(value = false)
	@Transactional
	void test3(){
		User kim = makeUSer("kim@", "pas");
		User lee = makeUSer("lee@", "pas");

		Cart cart = makeCart(kim);
		Cart cart1 = makeCart(lee);

		cart.setUser(kim);
		cart1.setUser(lee);

		em.persist(kim);
		em.persist(lee);

		Store store = Store.builder()
				.name("store1")
				.build();

		Store store2 = Store.builder()
				.name("store2")
				.build();

		Menu menu = Menu.builder()
				.name("스토어1 메뉴1")
				.build();

		Menu menu2 = Menu.builder()
				.name("스토어 2 메뉴")
				.build();

		menu.setStore(store);
		menu2.setStore(store2);

		CartMenu cartMenu1 = CartMenu.builder()
				.count(5)
				.build();

		cartMenu1.setCart(cart);
		cartMenu1.setMenu(menu);

		CartMenu cartMenu2= CartMenu.builder()
				.count(2)
				.build();

		cartMenu2.setCart(cart1);
		cartMenu2.setMenu(menu2);

		CartMenu cartMenu3= CartMenu.builder()
				.count(5)
				.build();

		cartMenu3.setCart(cart);
		cartMenu3.setMenu(menu2);

		em.persist(store);
		em.persist(store2);
		em.persist(cartMenu1);
		em.persist(cartMenu2);
		em.persist(cartMenu3);

		em.remove(em.find(Store.class, 1L));

		System.out.println(kim.getCart().getCartMenuList());
		User user = em.find(User.class, 1L);
		Cart cart2 = user.getCart();

	}

	@Test
	@Rollback(value = false)
	@Transactional
	void test4(){
		Menu menu1 = Menu.builder()
				.name("사과")
				.build();

		Menu menu2 = Menu.builder()
				.name("귤")
				.build();

		Menu menu3 = Menu.builder()
				.name("배")
				.build();

		Store store = Store.builder()
				.name("과일 농장")
				.build();

		menu1.setStore(store);
		menu2.setStore(store);
		menu3.setStore(store);

		em.persist(store);

		Menu menu = em.find(Menu.class, 1L);

		em.remove(menu);

		logger.info(store.getMenuList().toString());

	}

}