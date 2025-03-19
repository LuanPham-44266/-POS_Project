package com.phamvietluan.pos;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems; // Danh sách món trong giỏ hàng

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // **📌 Thêm món vào giỏ hàng với số lượng**
    public void addItem(MenuItem item, int quantity) {  // Truyền quantity từ bên ngoài
        if (item == null || quantity <= 0) return;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId() == item.getId()) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity); // ✅ Cập nhật số lượng
                return;
            }
        }
        cartItems.add(new CartItem(item, quantity)); // ✅ Thêm mới nếu chưa có
    }



    // **📌 Giảm số lượng hoặc xóa món nếu số lượng = 1**
    public void removeItem(MenuItem item) {
        if (item == null) return; // Tránh lỗi null

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItem = cartItems.get(i);
            if (cartItem.getMenuItem().getId() == item.getId()) {
                cartItem.decreaseQuantity();
                Log.d("CartManager", "Giảm số lượng món: " + item.getName() + " -> " + cartItem.getQuantity());
                if (cartItem.getQuantity() <= 0) {
                    cartItems.remove(i);
                    Log.d("CartManager", "Xóa món khỏi giỏ: " + item.getName());
                }
                return;
            }
        }
    }

    // **📌 Lấy tổng tiền của giỏ hàng**
    public double getTotalPrice() {
        double total = 0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getMenuItem().getPrice() * cartItem.getQuantity();
        }
        Log.d("CartManager", "Tổng tiền giỏ hàng: " + total);
        return total;
    }

    // **📌 Lấy danh sách món trong giỏ hàng**
    public List<CartItem> getCartItems() {
        Log.d("CartManager", "Số món trong giỏ: " + cartItems.size());
        for (CartItem item : cartItems) {
            Log.d("CartManager", "Món: " + item.getMenuItem().getName() + ", SL: " + item.getQuantity());
        }
        return new ArrayList<>(cartItems);
    }

    // **📌 Xóa toàn bộ giỏ hàng sau khi thanh toán**
    public void clearCart() {
        cartItems.clear();
        Log.d("CartManager", "Giỏ hàng đã được xóa.");
    }

    // **📌 Kiểm tra giỏ hàng có trống không**
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
}
