package com.phamvietluan.pos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    private ListView lvCart;
    private TextView tvTotal;
    private Button btnCheckout, btnExit;
    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        lvCart = findViewById(R.id.lvCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnExit = findViewById(R.id.btnExit);
        cartManager = CartManager.getInstance();
        databaseHelper = new DatabaseHelper(this);

        cartItems = cartManager.getCartItems();
        Log.d("CartActivity", "Số món trong giỏ: " + cartItems.size());
        for (CartItem item : cartItems) {
            Log.d("CartActivity", "Món: " + item.getMenuItem().getName() + ", SL: " + item.getQuantity());
        }

        cartAdapter = new CartAdapter(this, cartItems);
        lvCart.setAdapter(cartAdapter);
        updateTotal();

        btnCheckout.setOnClickListener(v -> confirmCheckout());
        btnExit.setOnClickListener(v -> finish());
    }

    private void updateTotal() {
        tvTotal.setText(String.format("Tổng: %.0f VNĐ", cartManager.getTotalPrice()));
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

        cartManager.clearCart();
        cartItems.clear();
        cartItems.addAll(cartManager.getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotal();
    }
}
