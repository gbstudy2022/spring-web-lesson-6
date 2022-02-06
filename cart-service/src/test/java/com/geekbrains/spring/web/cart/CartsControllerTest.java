package com.geekbrains.spring.web.cart;

import com.geekbrains.spring.web.api.dto.OrderItemDto;
import com.geekbrains.spring.web.cart.dto.Cart;
import com.geekbrains.spring.web.cart.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


public class CartsControllerTest extends AbstractSpringBootTest {

    private static final String REST_URL = "/api/v1/cart";
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartService cartService;

    private String cartKey;

    @BeforeEach
    public void initCarts() {
        //постоянная корзина
        Cart cart = new Cart();
        cart.getItems().add(new OrderItemDto(1L,"TITLE1",1,100,100));
        cart.getItems().add(new OrderItemDto(2L,"TITLE2",1,50,50));
        cart.setTotalPrice(150);

        redisTemplate.opsForValue().set(cartService.getCartUuidFromSuffix("testuser"), cart);

        //временная корзина
        Cart tempCart = new Cart();
        tempCart.getItems().add(new OrderItemDto(3L,"TITLE3",2,100,200));
        tempCart.setTotalPrice(200);
        cartKey = cartService.generateCartUuid();
        redisTemplate.opsForValue().set(cartService.getCartUuidFromSuffix(cartKey), tempCart);
    }

    @Test
    public void generateCartUuidCheck() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/generate"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").isNotEmpty());
    }

    @Test
    //как сделать Transactional для редиса?
    public void getCartForUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/testuser")
                    .header("username", "testuser"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.size()").value(2));
    }

    @Test
    public void getCartForNewGuest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/" + cartKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.size()").value(1));
    }

}