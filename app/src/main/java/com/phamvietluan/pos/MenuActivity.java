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
    Button btnIncrease, btnDecrease, btnCheckout, btnExit;
    TextView tvQuantity;
    int quantity = 1; // Giá trị mặc định

    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        lvMenu = findViewById(R.id.lvMenu);
        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(1,"Trà sữa truyền thống", 30000));
        menuItems.add(new MenuItem(2,"Trà sữa matcha", 35000));
        menuItems.add(new MenuItem(3,"Trà đào cam sả", 40000));
       // btnIncrease = findViewById(R.id.btnIncrease);
        //btnDecrease = findViewById(R.id.btnDecrease);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnExit = findViewById(R.id.btnExit);
        //tvQuantity = findViewById(R.id.tvQuantity);


        menuAdapter = new MenuAdapter(this, menuItems);
        lvMenu.setAdapter(menuAdapter);

        lvMenu.setOnItemClickListener((parent, view, position, id) -> {
            MenuItem selectedItem = menuItems.get(position);
            CartManager.getInstance().addItem(selectedItem, 2); // Ví dụ: thêm 2 món

            Toast.makeText(MenuActivity.this, selectedItem.getName() + " đã thêm vào giỏ!", Toast.LENGTH_SHORT).show();
        });

       /* // Xử lý tăng số lượng
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        // Xử lý giảm số lượng
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
*/
        // Xử lý nút Thoát
        btnExit.setOnClickListener(v -> finish());

        // Xử lý nút Thanh Toán
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CartActivity.class);
            startActivity(intent);
        });


    }

}