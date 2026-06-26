package com.demo.service;

import com.demo.dto.CartItemDTO;
import com.demo.dto.CartItemRequest;
import com.demo.dto.CartItemResponse;
import com.demo.dto.CartResponse;
import com.demo.exception.NotFoundException;
import com.demo.model.CartItem;
import com.demo.model.Product;
import com.demo.model.User;
import com.demo.repository.CartRepository;
import com.demo.repository.ProductRepository;
import com.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    // Add product to cart
    public CartItemResponse addToCart(CartItemRequest request) {

    User user = getCurrentUser();

    Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new NotFoundException("Product not found"));

    CartItem cartItem = new CartItem();
    cartItem.setUser(user);
    cartItem.setProduct(product);
    cartItem.setQuantity(request.getQuantity());

    cartRepository.save(cartItem);

    CartItemResponse response = new CartItemResponse();
    response.setId(cartItem.getId());
    response.setProductId(product.getId());
    response.setProductName(product.getName());
    response.setPrice(product.getPrice());
    response.setQuantity(cartItem.getQuantity());

    return response;
    }

    // Get my cart
    public List<CartItem> getMyCart() {

        User user = getCurrentUser();

        return cartRepository.findByUser(user);
    }

    // Remove item
    public void removeItem(Long id) {

        User user = getCurrentUser();

        CartItem item = cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete this item");
        }

        cartRepository.delete(item);
    }

    //Update Quantity
    public CartItem updateQuantity(Long id, Integer quantity) {

        User user = getCurrentUser();

        CartItem item = cartRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("You cannot update this item");
        }
        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    //Clear Cart
    public void clearCart(){
        User user = getCurrentUser();
        List<CartItem> items = cartRepository.findByUser(user);
        cartRepository.deleteAll(items);
    }

    //Total price
    public Double getTotalPrice() {

        User user = getCurrentUser();
        List<CartItem> items = cartRepository.findByUser(user);

        if (items == null || items.isEmpty()) {
        return 0.0;
        }
        double total = 0;
        for (CartItem item : items) {
        if (item.getProduct() != null) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }}
        return total;
    }

    //Summary Cart
    public CartResponse getCartSummary() {
    User user = getCurrentUser();
    List<CartItem> items = cartRepository.findByUser(user);
    List<CartItemDTO> itemDTOList = items.stream().map(item -> {

        Double subtotal = item.getProduct().getPrice() * item.getQuantity();

        return new CartItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal
        );
    }).toList();
    Double totalPrice = itemDTOList.stream()
            .mapToDouble(CartItemDTO::getSubtotal)
            .sum();
    return new CartResponse(itemDTOList, totalPrice);
    }
}