package com.gridu.store.service.implementation;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductInformationForCart;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.ShopItemEntity;
import com.gridu.store.service.Cart;
import com.gridu.store.service.CartService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductServiceImpl productService;
    private final Cart cart;

    @Transactional
    @Override
    public void addItemToCart(UserCartRequestDto requestDto) {
        ShopItemEntity shopItem = productService.getShopItem(requestDto.getId());
        HashMap<Long, Long> itemsList = getItemsList();
        if (itemsList.get(requestDto.getId()) == null) {
            checkQuantity(shopItem.getAvailable(), requestDto.getQuantity());
            itemsList.put(shopItem.getId(), requestDto.getQuantity());
        } else {
            Long quantity = itemsList.get(shopItem.getId()) + requestDto.getQuantity();
            checkQuantity(shopItem.getAvailable(), quantity);
            itemsList.put(shopItem.getId(), quantity);
        }
    }

    @Override
    public CartResponseDto getCart() {
        List<ProductInformationForCart> products = new ArrayList<>();
        Long productsNumber = 0L;
        double totalPrice = 0;
        HashMap<Long, Long> itemsList = getItemsList();
        if (itemsList.isEmpty()) {
            return new CartResponseDto(products, totalPrice);
        }
        for (Map.Entry<Long, Long> entry : itemsList.entrySet()) {
            productsNumber++;
            Long quantity = entry.getValue();
            ProductEntity product = productService.getProduct(entry.getKey());
            double subtotalPrice = product.getPrice() * quantity;
            products.add(getProductForCartResponse(productsNumber, quantity, product, subtotalPrice));
            totalPrice += subtotalPrice;
        }
        return CartResponseDto.builder()
                .products(products)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void deleteProductFromCart(Long id) {
        if (!cart.getItemsList().containsKey(id)) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Product not found");
        }
        cart.getItemsList().remove(id);
    }

    @Transactional
    @Override
    public void modifyNumberOfItem(UserCartModifyDto requestDto) {
        Long productId = requestDto.getProductId();
        ShopItemEntity shopItem = productService.getShopItem(productId);
        checkQuantity(shopItem.getAvailable(), requestDto.getQuantity());
        cart.getItemsList().put(productId, requestDto.getQuantity());
    }

    private static ProductInformationForCart getProductForCartResponse(
            Long productsNumber, Long quantity, ProductEntity product, double subtotalPrice) {
        return ProductInformationForCart.builder()
                .numberOfProduct(productsNumber)
                .title(product.getTitle())
                .price(product.getPrice())
                .quantities(quantity)
                .subtotalPrice(subtotalPrice)
                .build();
    }

    public HashMap<Long, Long> getItemsList() {
        if (cart.getItemsList() == null) {
            cart.setItemsList(new HashMap<>());
        }
        return cart.getItemsList();
    }

    void checkQuantity(Long available, Long needQuantity) {
        if (available < needQuantity) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(202), "Amount of products not enough");
        }
    }
}
