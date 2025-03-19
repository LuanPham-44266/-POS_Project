package com.phamvietluan.pos;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private ListView lvOrderHistory;
    private List<Order> orderList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lvOrderHistory = findViewById(R.id.lvOrderHistory);
        databaseHelper = new DatabaseHelper(this);
        orderList = databaseHelper.getAllOrders(); // Lấy danh sách đơn từ database
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderList);
        lvOrderHistory.setAdapter(orderHistoryAdapter);
    }
}
