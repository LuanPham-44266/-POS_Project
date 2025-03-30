package com.phamvietluan.pos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private ListView lvOrderHistory;
    private List<Order> orderList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private DatabaseHelper databaseHelper;
    private Button btnRefresh, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lvOrderHistory = findViewById(R.id.lvOrderHistory);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBack = findViewById(R.id.btnBack);
        
        databaseHelper = new DatabaseHelper(this);
        
        // Tải dữ liệu lịch sử đơn hàng
        loadOrderHistory();
        
        // Thiết lập sự kiện cho nút làm mới
        btnRefresh.setOnClickListener(v -> {
            loadOrderHistory();
            Toast.makeText(this, "Đã làm mới dữ liệu", Toast.LENGTH_SHORT).show();
        });
        
        // Thiết lập sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> finish());
        
        // Thiết lập sự kiện khi nhấp vào một đơn hàng
        lvOrderHistory.setOnItemClickListener((parent, view, position, id) -> {
            Order selectedOrder = orderList.get(position);
            showOrderOptionsDialog(selectedOrder);
        });
    }
    
    /**
     * Hiển thị dialog tùy chọn cho đơn hàng
     */
    private void showOrderOptionsDialog(Order order) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Tùy chọn đơn hàng")
                .setMessage("Mã đơn: " + order.getOrderId())
                .setPositiveButton("In hóa đơn", (dialog, which) -> 
                    printOrderBill(order.getOrderId(), order.getTotalPrice(), order.getDateTime()))
                .setNegativeButton("Đóng", null)
                .show();
    }
    
    /**
     * In hóa đơn của một đơn hàng từ lịch sử
     */
    private void printOrderBill(String orderId, double totalPrice, String dateTime) {
        // Lấy chi tiết đơn hàng từ database
        List<OrderDetail> orderDetails = databaseHelper.getOrderDetails(orderId);
        
        if (orderDetails.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Chuyển đổi từ OrderDetail sang CartItem để in hóa đơn
        List<CartItem> billItems = new ArrayList<>();
        for (OrderDetail detail : orderDetails) {
            // Lấy thông tin món từ menu_id
            MenuItem menuItem = databaseHelper.getMenuItemById(detail.getMenuId());
            if (menuItem != null) {
                billItems.add(new CartItem(menuItem, detail.getQuantity()));
            }
        }
        
        // In hóa đơn
        BillPrintHelper.printBill(this, orderId, totalPrice, dateTime, billItems);
    }
    
    private void loadOrderHistory() {
        Log.d("OrderHistoryActivity", "Bắt đầu tải dữ liệu lịch sử đơn hàng");
        orderList = databaseHelper.getAllOrders(); // Lấy danh sách đơn từ database
        
        Log.d("OrderHistoryActivity", "Số đơn hàng đã nhận: " + orderList.size());
        
        if (orderList.isEmpty()) {
            Log.d("OrderHistoryActivity", "Danh sách đơn hàng trống");
            Toast.makeText(this, "Chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
        } else {
            // Hiển thị thông tin của đơn hàng đầu tiên để kiểm tra
            if (!orderList.isEmpty()) {
                Order firstOrder = orderList.get(0);
                Log.d("OrderHistoryActivity", "Đơn hàng đầu tiên: ID=" + firstOrder.getOrderId() 
                    + ", Giá=" + firstOrder.getTotalPrice() + ", Ngày=" + firstOrder.getDateTime());
            }
        }
        
        // Thiết lập adapter và gắn vào ListView
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderList);
        lvOrderHistory.setAdapter(orderHistoryAdapter);
    }
}
