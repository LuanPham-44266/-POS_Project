package com.phamvietluan.pos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    private ListView lvCart;
    private TextView tvTotal;
    private Button btnCheckout,btnExit;
    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private List<MenuItem> cartItems;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        lvCart = findViewById(R.id.lvCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        cartManager = CartManager.getInstance();
        btnExit = findViewById(R.id.btnExit);
        cartItems = new ArrayList<>(cartManager.getCartItems().keySet());
        cartAdapter = new CartAdapter(this, cartItems);
        lvCart.setAdapter(cartAdapter);

        updateTotal();

        btnCheckout.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                cartManager.clearCart();
                cartAdapter.notifyDataSetChanged();
                updateTotal();
            } else {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            }
        });
        btnCheckout.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                String orderId = UUID.randomUUID().toString().substring(0, 8); // Tạo mã đơn ngẫu nhiên
                double totalPrice = cartManager.getTotalPrice();
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Lưu vào database
                databaseHelper.saveOrder(orderId, totalPrice, dateTime);

                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                cartManager.clearCart(); // Xóa giỏ hàng
                cartAdapter.notifyDataSetChanged();
                updateTotal();
            } else {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            }
        });
        // Xử lý nút Thoát
        btnExit.setOnClickListener(v -> finish());


    }

    private void updateTotal() {
        tvTotal.setText(String.format("Tổng: %.0f đ", cartManager.getTotalPrice()));
    }
}
