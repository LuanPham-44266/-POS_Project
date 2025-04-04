package com.phamvietluan.pos;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout btnMenu, btnCart, btnOrderHistory, btnAddItem, btnDeleteItem, btnUpdateItem, btnChangePassword;
    private static final String ADMIN_PASSWORD = "1234"; // Mật khẩu mặc định
    private static final String PREFS_NAME = "POSPrefs";
    private static final String PASSWORD_KEY = "admin_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMenu = findViewById(R.id.btnMenu);
        btnCart = findViewById(R.id.btnCart);
        btnOrderHistory = findViewById(R.id.btnOrderHistory);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnMenu.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MenuActivity.class)));
        btnCart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        btnOrderHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class)));

        // Xác thực Admin trước khi mở AddItemActivity
        btnAddItem.setOnClickListener(v -> showAdminPasswordDialog(AddItemActivity.class));

        // Xác thực Admin trước khi mở UpdateItemActivity
        btnUpdateItem.setOnClickListener(v -> showAdminPasswordDialog(UpdateItemActivity.class));

        // Xác thực Admin trước khi mở DeleteItemActivity
        btnDeleteItem.setOnClickListener(v -> showAdminPasswordDialog(DeleteItemActivity.class));
        
        // Xử lý nút đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    // Hiển thị hộp thoại nhập mật khẩu Admin
    private void showAdminPasswordDialog(Class<?> activityToOpen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mật khẩu Admin");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String password = input.getText().toString();
            if (password.equals(getAdminPassword())) {
                startActivity(new Intent(MainActivity.this, activityToOpen));
            } else {
                Toast.makeText(MainActivity.this, "Không có quyền truy cập vào chức năng này", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    // Hiển thị hộp thoại đổi mật khẩu
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu Admin");
        
        // Tạo layout cho dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);
        
        // Ánh xạ các view
        final EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        final EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);
        
        builder.setPositiveButton("Xác nhận", null); // Sẽ gán lại sự kiện click dưới đây
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        
        // Tạo dialog
        final AlertDialog dialog = builder.create();
        
        // Hiển thị dialog
        dialog.show();
        
        // Gán lại sự kiện cho nút Xác nhận để không tự động đóng khi có lỗi
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            
            // Kiểm tra mật khẩu cũ
            if (!oldPassword.equals(getAdminPassword())) {
                etOldPassword.setError("Mật khẩu cũ không đúng");
                return;
            }
            
            // Kiểm tra mật khẩu mới
            if (newPassword.isEmpty()) {
                etNewPassword.setError("Vui lòng nhập mật khẩu mới");
                return;
            }
            
            // Kiểm tra xác nhận mật khẩu
            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Xác nhận mật khẩu không khớp");
                return;
            }
            
            // Lưu mật khẩu mới
            saveAdminPassword(newPassword);
            
            Toast.makeText(MainActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
    
    // Lấy mật khẩu admin từ SharedPreferences (hoặc mật khẩu mặc định nếu chưa lưu)
    private String getAdminPassword() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(PASSWORD_KEY, ADMIN_PASSWORD);
    }
    
    // Lưu mật khẩu admin mới vào SharedPreferences
    private void saveAdminPassword(String newPassword) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PASSWORD_KEY, newPassword);
        editor.apply();
    }
}
