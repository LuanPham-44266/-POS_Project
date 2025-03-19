package com.phamvietluan.pos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pos_database";
    private static final int DATABASE_VERSION = 1;

    // Bảng Menu
    private static final String TABLE_MENU = "menu";
    private static final String COLUMN_MENU_ID = "id";
    private static final String COLUMN_MENU_NAME = "name";
    private static final String COLUMN_MENU_PRICE = "price";

    // Bảng Đơn Hàng
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_DATE_TIME = "date_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Menu
        String createMenuTable = "CREATE TABLE " + TABLE_MENU + " ("
                + COLUMN_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MENU_NAME + " TEXT, "
                + COLUMN_MENU_PRICE + " REAL)";
        db.execSQL(createMenuTable);

        // Tạo bảng Lịch sử đơn hàng
        String createOrderTable = "CREATE TABLE " + TABLE_ORDERS + " ("
                + COLUMN_ORDER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_TOTAL_PRICE + " REAL, "
                + COLUMN_DATE_TIME + " TEXT)";
        db.execSQL(createOrderTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // **📌 2. Chức năng Thêm món vào menu**
    public void addMenuItem(String name, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MENU_NAME, name);
        values.put(COLUMN_MENU_PRICE, price);
        db.insert(TABLE_MENU, null, values);
        db.close();
    }

    // **📌 3. Lấy danh sách món từ Menu**
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                menuList.add(new MenuItem(name, price)); // 🔥 Chỉ truyền 2 tham số

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return menuList;
    }

    // **📌 4. Lưu đơn hàng vào lịch sử**
    public void saveOrder(String orderId, double totalPrice, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderId);
        values.put(COLUMN_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_DATE_TIME, dateTime);
        db.insert(TABLE_ORDERS, null, values);
        db.close();
    }

    // **📌 5. Lấy danh sách đơn hàng từ lịch sử**
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_DATE_TIME + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String orderId = cursor.getString(0);
                double totalPrice = cursor.getDouble(1);
                String dateTime = cursor.getString(2);
                orders.add(new Order(orderId, totalPrice, dateTime));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orders;
    }
}
