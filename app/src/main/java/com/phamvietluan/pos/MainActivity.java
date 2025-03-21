package com.phamvietluan.pos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnMenu, btnCart, btnOrderHistory, btnAddItem;
    private static final String ADMIN_PASSWORD = "1234"; // Đổi mật khẩu Admin tại đây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMenu = findViewById(R.id.btnMenu);
        btnCart = findViewById(R.id.btnCart);
        btnOrderHistory = findViewById(R.id.btnOrderHistory);
        btnAddItem = findViewById(R.id.btnAddItem); // Nút thêm món

        btnMenu.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MenuActivity.class)));
        btnCart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        btnOrderHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class)));

        // Xác thực Admin trước khi mở AddItemActivity
        btnAddItem.setOnClickListener(v -> showAdminPasswordDialog());
    }

    // Hiển thị hộp thoại nhập mật khẩu Admin
    private void showAdminPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mật khẩu Admin");

        // Tạo EditText để nhập mật khẩu
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Xử lý nút "Xác nhận"
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String password = input.getText().toString();
            if (password.equals(ADMIN_PASSWORD)) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Không có quyền truy cập vào chức năng này", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
