package com.phamvietluan.pos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.BaseAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {
    private static final String TAG = "OrderHistoryActivity";
    private static final int MAX_DAYS_BACK = 7;
    
    private ListView lvOrderHistory;
    private List<Order> orderList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private DatabaseHelper databaseHelper;
    private Button btnRefresh, btnBack, btnPrevDay, btnNextDay;
    private TextView tvCurrentDate, tvDailyRevenue, tvOrderCount;
    
    private Calendar currentDate = Calendar.getInstance();
    private int currentDayOffset = 0;
    
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Ánh xạ views
        lvOrderHistory = findViewById(R.id.lvOrderHistory);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvDailyRevenue = findViewById(R.id.tvDailyRevenue);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBack = findViewById(R.id.btnBack);
        btnPrevDay = findViewById(R.id.btnPrevDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        
        databaseHelper = new DatabaseHelper(this);
        
        // Tải dữ liệu lịch sử đơn hàng và doanh thu
        loadOrderHistory();
        updateDailyRevenueView();
        
        // Thiết lập sự kiện cho nút làm mới
        btnRefresh.setOnClickListener(v -> {
            resetToCurrentDate();
            loadOrderHistory();
            updateDailyRevenueView();
            Toast.makeText(this, "Đã làm mới dữ liệu", Toast.LENGTH_SHORT).show();
        });
        
        // Thiết lập sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> finish());
        
        // Thiết lập sự kiện cho nút chuyển ngày
        btnPrevDay.setOnClickListener(v -> {
            if (currentDayOffset < MAX_DAYS_BACK - 1) {
                currentDayOffset++;
                updateDailyRevenueView();
            } else {
                Toast.makeText(this, "Đã đạt giới hạn 7 ngày", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnNextDay.setOnClickListener(v -> {
            if (currentDayOffset > 0) {
                currentDayOffset--;
                updateDailyRevenueView();
            } else {
                Toast.makeText(this, "Đã đến ngày hiện tại", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Thiết lập sự kiện khi nhấp vào một đơn hàng
        lvOrderHistory.setOnItemClickListener((parent, view, position, id) -> {
            Order selectedOrder = orderList.get(position);
            showOrderOptionsDialog(selectedOrder);
        });
    }
    
    /**
     * Đặt lại về ngày hiện tại
     */
    private void resetToCurrentDate() {
        currentDayOffset = 0;
        currentDate = Calendar.getInstance();
    }
    
    /**
     * Cập nhật hiển thị doanh thu theo ngày
     */
    private void updateDailyRevenueView() {
        // Tính toán ngày dựa trên offset
        Calendar targetDate = (Calendar) currentDate.clone();
        targetDate.add(Calendar.DAY_OF_MONTH, -currentDayOffset);
        
        // Lấy chuỗi ngày theo định dạng API (YYYY-MM-DD)
        String apiDateString = apiDateFormat.format(targetDate.getTime());
        
        // Lấy chuỗi ngày theo định dạng hiển thị (DD/MM/YYYY)
        String displayDateString = displayDateFormat.format(targetDate.getTime());
        
        // Hiển thị ngày
        tvCurrentDate.setText("Ngày: " + displayDateString);
        
        // Lấy và hiển thị doanh thu
        double revenue = databaseHelper.getDailyRevenue(apiDateString);
        tvDailyRevenue.setText(String.format("Doanh thu: %,.0f VNĐ", revenue));
        
        // Lấy và hiển thị số đơn hàng
        int orderCount = databaseHelper.getOrderCountByDate(apiDateString);
        tvOrderCount.setText("Số đơn hàng: " + orderCount);
        
        // Lọc danh sách đơn hàng theo ngày đã chọn
        filterOrdersByDate(apiDateString);
    }
    
    /**
     * Lọc danh sách đơn hàng theo ngày
     */
    private void filterOrdersByDate(String dateFilter) {
        if (orderList == null || orderList.isEmpty()) {
            return;
        }
        
        List<Order> filteredOrders = new ArrayList<>();
        
        for (Order order : orderList) {
            if (order.getDateTime().startsWith(dateFilter)) {
                filteredOrders.add(order);
            }
        }
        
        orderHistoryAdapter = new OrderHistoryAdapter(this, filteredOrders);
        lvOrderHistory.setAdapter(orderHistoryAdapter);
        
        if (filteredOrders.isEmpty()) {
            Toast.makeText(this, "Không có đơn hàng nào trong ngày " + dateFilter, Toast.LENGTH_SHORT).show();
        }
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
        Log.d(TAG, "Bắt đầu tải dữ liệu lịch sử đơn hàng");
        orderList = databaseHelper.getAllOrders(); // Lấy danh sách đơn từ database
        
        Log.d(TAG, "Số đơn hàng đã nhận: " + orderList.size());
        
        if (orderList.isEmpty()) {
            Log.d(TAG, "Danh sách đơn hàng trống");
            Toast.makeText(this, "Chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
        } else {
            // Hiển thị thông tin của đơn hàng đầu tiên để kiểm tra
            if (!orderList.isEmpty()) {
                Order firstOrder = orderList.get(0);
                Log.d(TAG, "Đơn hàng đầu tiên: ID=" + firstOrder.getOrderId() 
                    + ", Giá=" + firstOrder.getTotalPrice() + ", Ngày=" + firstOrder.getDateTime());
            }
        }
    }
}
