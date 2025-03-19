package com.phamvietluan.pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<MenuItem, Integer> cartItems = new HashMap<>();


    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(MenuItem item) {
        // Thêm logic để kiểm tra và cập nhật số lượng sản phẩm
    }

    public void removeItem(MenuItem item) {
        // Xóa sản phẩm khỏi giỏ hàng
    }

    public double getTotalPrice() {
        // Tính tổng tiền
        double total = 0;
        for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }
    public Map<MenuItem, Integer> getCartItems() {
        return cartItems;
    }
    public void clearCart() {
        cartItems.clear(); // Xóa toàn bộ món trong giỏ hàng
    }
    public int getQuantity(MenuItem item) {
        return cartItems.containsKey(item) ? cartItems.get(item) : 0;
    }

}
