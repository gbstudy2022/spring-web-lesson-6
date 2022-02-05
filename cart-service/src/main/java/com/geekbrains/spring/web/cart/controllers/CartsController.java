package com.geekbrains.spring.web.cart.controllers;

import com.geekbrains.spring.web.api.dto.StringResponse;
import com.geekbrains.spring.web.cart.dto.Cart;
import com.geekbrains.spring.web.cart.services.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartsController {
    private final CartService cartService;

    //получение айди корзины по юзернейму
    @GetMapping("/uuid")
    public String getCartByUsername(@RequestHeader(required = true) String username) {
        return getCurrentCartUuid(username, null);
    }

    //получение корзины
    @GetMapping("/{uuid}")
    public Cart getCart(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        return cartService.getCurrentCart(getCurrentCartUuid(username, uuid));
    }

    //генерим айди корзины первым делом при посещении приложения
    @GetMapping("/generate")
    public StringResponse getCart() {
        log.info("generate reached");
        return new StringResponse(cartService.generateCartUuid());
    }

    //добавить из каталога
    @GetMapping("/{uuid}/add/{productId}")
    public void add(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
        cartService.addToCart(getCurrentCartUuid(username, uuid), productId);
    }

//    @GetMapping("/{uuid}/decrement/{productId}")
//    public void decrement(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
//        cartService.decrementItem(getCurrentCartUuid(username, uuid), productId);
//    }
//
//    @GetMapping("/{uuid}/remove/{productId}")
//    public void remove(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
//        cartService.removeItemFromCart(getCurrentCartUuid(username, uuid), productId);
//    }

    //очистка корзины
    @GetMapping("/{uuid}/clear")
    public void clear(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        cartService.clearCart(getCurrentCartUuid(username, uuid));
    }

    //мерджим временную корзину и постоянную, вызывается на стартовой странице
    @GetMapping("/{uuid}/merge")
    public void merge(@RequestHeader String username, @PathVariable String uuid) {
        log.info("merge reached");
        cartService.merge(
                getCurrentCartUuid(username, null),
                getCurrentCartUuid(null, uuid)
        );
    }

    private String getCurrentCartUuid(String username, String uuid) {
        if (username != null) {
            return cartService.getCartUuidFromSuffix(username);
        }
        return cartService.getCartUuidFromSuffix(uuid);
    }
}
