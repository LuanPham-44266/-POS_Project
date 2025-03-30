package com.phamvietluan.pos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DeleteItemActivity extends AppCompatActivity {

    private ListView listView;
    private MenuAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<MenuItem> menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);
        Button btnExit = findViewById(R.id.btnExit);
        databaseHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);

        loadMenuItems();
        // Nút Thoát
        btnExit.setOnClickListener(v -> finish());
    }

    private void loadMenuItems() {

        menuList = databaseHelper.getAllMenuItems();
        adapter = new MenuAdapter(this, menuList, true, this::confirmDeleteItem); // Bật chế độ xóa
        listView.setAdapter(adapter);
    }

    private void confirmDeleteItem(int itemId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa món này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    databaseHelper.deleteMenuItem(itemId);
                    loadMenuItems();
                    Toast.makeText(this, "Đã xóa món thành công!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
