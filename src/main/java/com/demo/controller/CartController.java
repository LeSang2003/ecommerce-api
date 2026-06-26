package com.demo.controller;

import com.demo.dto.CartItemRequest;
import com.demo.dto.CartItemResponse;
import com.demo.dto.CartResponse;
import com.demo.model.CartItem;
import com.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add to cart
   @PostMapping("/add")
    public CartItemResponse addToCart(@RequestBody CartItemRequest request) {
        return cartService.addToCart(request);
    }

    // View my cart
    @GetMapping
    public List<CartItem> getMyCart() {
        return cartService.getMyCart();
    }

    // Remove item
    @DeleteMapping("/{id}")
    public String removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return "Removed successfully";
    }

    //Update Quantity
    @PutMapping("/{id}")
    public CartItem updateQuantity(
        @PathVariable Long id, 
        @RequestParam Integer quantity){
        return cartService.updateQuantity(id, quantity);
    }

    //Clear Cart
    @DeleteMapping("/clear")
    public String clearCart(){
        cartService.clearCart();
        return "Cart cleared";
    }

    //Total price
    @GetMapping("/total")
    public Double getTotalPrice(){
        return cartService.getTotalPrice();
    }

    //Summary
    @GetMapping("/summary")
    public CartResponse getCartSummary(){
        return cartService.getCartSummary();
    }
}