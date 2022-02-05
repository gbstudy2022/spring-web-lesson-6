package com.geekbrains.spring.web.core.services;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.api.exceptions.ResourceNotFoundException;
import com.geekbrains.spring.web.core.dto.OrderDetailsDto;
import com.geekbrains.spring.web.core.entities.Order;
import com.geekbrains.spring.web.core.entities.OrderItem;
import com.geekbrains.spring.web.core.repositories.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository ordersRepository;
    @Autowired
    private RestTemplate restTemplate;
    private final ProductsService productsService;

    @Transactional
    public void createOrder(String username, OrderDetailsDto orderDetailsDto) {

        String cartKey = restTemplate.getForObject("http://localhost:8191/web-market-cart/api/v1/cart/uuid?username={username}", String.class, username);
        Cart currentCart = restTemplate.getForObject("http://localhost:8191/web-market-cart/api/v1/cart/{uuid}", Cart.class, cartKey);
        Order order = new Order();
        order.setAddress(orderDetailsDto.getAddress());
        order.setPhone(orderDetailsDto.getPhone());
        order.setUsername(username);
//        order.setTotalPrice(currentCart.getTotalPrice());
//        List<OrderItem> items = currentCart.getItems().stream()
//                .map(o -> {
//                    OrderItem item = new OrderItem();
//                    item.setOrder(order);
//                    item.setQuantity(o.getQuantity());
//                    item.setPricePerProduct(o.getPricePerProduct());
//                    item.setPrice(o.getPrice());
//                    item.setProduct(productsService.findById(o.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
//                    return item;
//                }).collect(Collectors.toList());
//        order.setItems(items);
//        ordersRepository.save(order);
//        cartService.clearCart(cartKey);
    }

    public List<Order> findOrdersByUsername(String username) {
        return ordersRepository.findAllByUsername(username);
    }
}
