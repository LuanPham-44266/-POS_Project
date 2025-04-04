package com.phamvietluan.pos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {
    private ListView lvCart;
    private TextView tvTotalText;
    private LinearLayout btnCheckout, btnExit, btnCart, btnTotal, tvTotal;
    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ các thành phần từ layout
        lvCart = findViewById(R.id.lvCart);
        tvTotal = findViewById(R.id.tvTotal);
        tvTotalText = findViewById(R.id.tvTotalText);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnExit = findViewById(R.id.btnExit);
        btnCart = findViewById(R.id.btnCart);
        btnTotal = findViewById(R.id.btnTotal);
        
        cartManager = CartManager.getInstance();
        databaseHelper = new DatabaseHelper(this);

        cartItems = cartManager.getCartItems();
        Log.d("CartActivity", "Số món trong giỏ: " + cartItems.size());
        for (CartItem item : cartItems) {
            Log.d("CartActivity", "Món: " + item.getMenuItem().getName() + ", SL: " + item.getQuantity());
        }

        // Khởi tạo adapter với CartItemListener
        cartAdapter = new CartAdapter(this, cartItems, this);
        lvCart.setAdapter(cartAdapter);
        updateTotal();

        // Thiết lập các sự kiện onClick
        btnCheckout.setOnClickListener(v -> confirmCheckout());
        btnExit.setOnClickListener(v -> finish());
        btnCart.setOnClickListener(v -> refreshCart());
    }

    private void refreshCart() {
        // Làm mới giỏ hàng nếu cần
        cartAdapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        // Cập nhật hiển thị tổng tiền
        tvTotalText.setText(String.format("%.0f VNĐ", cartManager.getTotalPrice()));
    }

    @Override
    public void onRemoveItem(int position) {
        // Xử lý khi người dùng nhấn nút xóa item
        if (position >= 0 && position < cartItems.size()) {
            CartItem item = cartItems.get(position);
            
            // Hiển thị dialog xác nhận xóa
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa " + item.getMenuItem().getName() + " khỏi giỏ hàng?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        cartManager.removeItem(position);
                        cartItems.remove(position);
                        cartAdapter.notifyDataSetChanged();
                        updateTotal();
                        Toast.makeText(CartActivity.this, "Đã xóa món khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        }
    }

    private void confirmCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có chắc muốn thanh toán không?")
                .setPositiveButton("Có", (dialog, which) -> processCheckout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void processCheckout() {
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        double totalPrice = cartManager.getTotalPrice();
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        databaseHelper.saveOrder(orderId, totalPrice, dateTime);
        
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
        
        BillPrintHelper.printBill(this, orderId, totalPrice, dateTime, cartItems);

        cartManager.clearCart();
        cartItems.clear();
        cartItems.addAll(cartManager.getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotal();
    }
}
