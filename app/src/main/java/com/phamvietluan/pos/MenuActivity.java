package com.phamvietluan.pos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private ListView lvMenu;
    private List<MenuItem> menuItems;
    private MenuAdapter menuAdapter;
    private DatabaseHelper databaseHelper;

    Button btnCheckout, btnExit, btnAddItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Ánh xạ View
        lvMenu = findViewById(R.id.lvMenu);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnExit = findViewById(R.id.btnExit);
        btnAddItem = findViewById(R.id.btnAddItem); // Nút thêm món

        // Khởi tạo databaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Khởi tạo danh sách món & Adapter
        menuItems = new ArrayList<>();
        menuAdapter = new MenuAdapter(this, menuItems);
        lvMenu.setAdapter(menuAdapter);

        // 🔥 Tải menu từ database
        loadMenuItems();

        // Sự kiện khi chọn món ăn từ danh sách
        lvMenu.setOnItemClickListener((parent, view, position, id) -> {
            MenuItem selectedItem = menuItems.get(position);
            CartManager.getInstance().addItem(selectedItem, 1); // Mặc định thêm 1 món

            Toast.makeText(MenuActivity.this, selectedItem.getName() + " đã thêm vào giỏ!", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút Thoát
        btnExit.setOnClickListener(v -> finish());

        // Xử lý nút Thanh Toán
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // 🔥 Thêm món mới (Chỉ hiển thị nếu là Admin)
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    // ✅ Tải danh sách món từ database và cập nhật ListView
    private void loadMenuItems() {
        menuItems.clear(); // Xóa danh sách cũ
        menuItems.addAll(databaseHelper.getAllMenuItems()); // Lấy danh sách từ database
        menuAdapter.notifyDataSetChanged(); // Cập nhật giao diện
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems(); // Cập nhật danh sách món khi quay lại màn hình
    }
}
