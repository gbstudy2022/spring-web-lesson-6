package com.geekbrains.spring.web.cart;

import com.geekbrains.spring.web.api.dto.OrderItemDto;
import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.cart.dto.Cart;
import com.geekbrains.spring.web.cart.services.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

public class CartServiceTest extends AbstractSpringBootTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private RedisTemplate redisTemplate;
    @MockBean
    private RestTemplate restTemplate;

    //с префиксами
    private String tempCartKey;
    private String cartKey;

    @BeforeEach
    public void initCarts() {
        //постоянная корзина
        Cart cart = new Cart();
        cart.getItems().add(new OrderItemDto(1L,"TITLE1",1,100,100));
        cart.getItems().add(new OrderItemDto(2L,"TITLE2",1,50,50));
        cart.setTotalPrice(150);
        cartKey = cartService.getCartUuidFromSuffix("testuser");
        redisTemplate.opsForValue().set(cartKey, cart);

        //временная корзина
        Cart tempCart = new Cart();
        tempCart.getItems().add(new OrderItemDto(3L,"TITLE3",2,100,200));
        tempCart.setTotalPrice(200);
        tempCartKey = cartService.getCartUuidFromSuffix(cartService.generateCartUuid());
        redisTemplate.opsForValue().set(tempCartKey, tempCart);
    }

    @Test
    public void mergeWithDifferentProductsCheck() {
        cartService.merge(cartKey, tempCartKey);

        Assertions.assertEquals(3, cartService.getCurrentCart(cartKey).getItems().size());
        Assertions.assertEquals(0, cartService.getCurrentCart(tempCartKey).getItems().size());
    }

    @Test
    public void mergeWithSameProductsCheck() {
        Cart newTempCart = new Cart();
        newTempCart.getItems().add(new OrderItemDto(1L,"TITLE1",4,100,400));
        newTempCart.setTotalPrice(200);
        redisTemplate.opsForValue().set(tempCartKey, newTempCart); //перезатерли временную из BeforeEach
        cartService.merge(cartKey, tempCartKey);

        Assertions.assertEquals(2, cartService.getCurrentCart(cartKey).getItems().size());
        Assertions.assertEquals(5, cartService.getCurrentCart(cartKey).getItems().get(0).getQuantity());
        Assertions.assertEquals(550, cartService.getCurrentCart(cartKey).getTotalPrice());

        Assertions.assertEquals(0, cartService.getCurrentCart(tempCartKey).getItems().size());
    }

    @Test
    public void addNewProductToCartCheck() {
        ProductDto productDto = new ProductDto();
        productDto.setId(5L);
        productDto.setTitle("title5");
        productDto.setPrice(100);

        Mockito.doReturn(productDto).when(restTemplate).getForObject("http://localhost:8189/web-market-core/api/v1/products/{id}",ProductDto.class,5L);
        cartService.addToCart(cartKey, 5L);

        Assertions.assertEquals(3, cartService.getCurrentCart(cartKey).getItems().size());
        Assertions.assertEquals(250, cartService.getCurrentCart(cartKey).getTotalPrice());
    }

    @Test
    public void addExistingProductToCartCheck() {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setTitle("TITLE1");
        productDto.setPrice(100);

        Mockito.doReturn(productDto).when(restTemplate).getForObject("http://localhost:8189/web-market-core/api/v1/products/{id}",ProductDto.class,1L);
        cartService.addToCart(cartKey, 1L);
        cartService.addToCart(cartKey, 1L);

        Assertions.assertEquals(2, cartService.getCurrentCart(cartKey).getItems().size());
        Assertions.assertEquals(350, cartService.getCurrentCart(cartKey).getTotalPrice());
        Assertions.assertEquals(3, cartService.getCurrentCart(cartKey).getItems().get(0).getQuantity());
    }

    @Test
    public void clearCartCheck() {
        cartService.clearCart(cartKey);
        Assertions.assertEquals(0, cartService.getCurrentCart(cartKey).getItems().size());
    }
}
