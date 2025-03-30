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

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(MenuItem item, int quantity) {
        if (item == null || quantity <= 0) return;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId() == item.getId()) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                Log.d("CartManager", "Cập nhật SL món: " + item.getName() + " -> " + cartItem.getQuantity());
                return;
            }
        }

        cartItems.add(new CartItem(item, quantity));
        Log.d("CartManager", "Thêm món: " + item.getName() + " SL: " + quantity);
    }



    public void removeItem(MenuItem item) {
        if (item == null) return;

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItem = cartItems.get(i);
            if (cartItem.getMenuItem().getId() == item.getId()) {
                cartItem.decreaseQuantity();
                Log.d("CartManager", "Giảm món: " + item.getName() + " -> SL: " + cartItem.getQuantity());

                if (cartItem.getQuantity() <= 0) {
                    cartItems.remove(i);
                    Log.d("CartManager", "Xóa món khỏi giỏ: " + item.getName());
                }
                return;
            }
        }
    }

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

    public void clearCart() {
        cartItems.clear();
        Log.d("CartManager", "Giỏ hàng đã được xóa.");
    }

    // ** Kiểm tra giỏ hàng có trống không**
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public int getItemQuantity(MenuItem item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId() == item.getId()) {
                return cartItem.getQuantity();
            }
        }
        return 0;
    }
    
    public boolean hasItem(MenuItem item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }
}
