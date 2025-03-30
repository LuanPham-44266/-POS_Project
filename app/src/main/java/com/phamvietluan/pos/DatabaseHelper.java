package com.phamvietluan.pos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pos_database";
    private static final int DATABASE_VERSION = 2;

    // Bảng Menu
    private static final String TABLE_MENU = "menu";
    private static final String COLUMN_MENU_ID = "id";
    private static final String COLUMN_MENU_NAME = "name";
    private static final String COLUMN_MENU_PRICE = "price";
    private static final String COLUMN_MENU_IMAGE = "image";

    // Bảng Đơn Hàng
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_DATE_TIME = "date_time";

    // Bảng Chi tiết Đơn hàng
    private static final String TABLE_ORDER_DETAILS = "order_details";
    private static final String COLUMN_DETAIL_ID = "detail_id";
    private static final String COLUMN_DETAIL_ORDER_ID = "order_id";
    private static final String COLUMN_DETAIL_MENU_ID = "menu_id";
    private static final String COLUMN_DETAIL_QUANTITY = "quantity";

    // **📌 Bảng Người dùng (Users)**
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role"; // 'admin' hoặc 'staff'


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Menu
        String createMenuTable = "CREATE TABLE " + TABLE_MENU + " ("
                + COLUMN_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MENU_NAME + " TEXT, "
                + COLUMN_MENU_PRICE + " REAL, "
                + COLUMN_MENU_IMAGE + " TEXT)"; // ✅ Thêm dấu `,` và khoảng trắng
        ;

        db.execSQL(createMenuTable);

        // Tạo bảng Đơn hàng
        String createOrderTable = "CREATE TABLE " + TABLE_ORDERS + " ("
                + COLUMN_ORDER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_TOTAL_PRICE + " REAL, "
                + COLUMN_DATE_TIME + " TEXT)";
        db.execSQL(createOrderTable);

        // Tạo bảng Chi tiết Đơn hàng
        String createOrderDetailsTable = "CREATE TABLE " + TABLE_ORDER_DETAILS + " ("
                + COLUMN_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DETAIL_ORDER_ID + " TEXT, "
                + COLUMN_DETAIL_MENU_ID + " INTEGER, "
                + COLUMN_DETAIL_QUANTITY + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_DETAIL_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_DETAIL_MENU_ID + ") REFERENCES " + TABLE_MENU + "(" + COLUMN_MENU_ID + "))";
        db.execSQL(createOrderDetailsTable);
        // **📌 Tạo bảng Người dùng**
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_ROLE + " TEXT)";
        db.execSQL(createUserTable);

        // **📌 Thêm tài khoản Admin mặc định**
//        addAdminAccount(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // **📌 1. Thêm món vào menu**
    public boolean addMenuItem(String name, double price, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MENU_NAME, name);
        values.put(COLUMN_MENU_PRICE, price);
        values.put(COLUMN_MENU_IMAGE, imagePath);

        long result = db.insert(TABLE_MENU, null, values);
        db.close();

        return result != -1; // ✅ Nếu `insert()` thành công, trả về `true`, ngược lại `false`
    }


    // **📌 2. Cập nhật món trong menu**
    public void updateMenuItem(int id, String newName, double newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MENU_NAME, newName);
        values.put(COLUMN_MENU_PRICE, newPrice);
        db.update(TABLE_MENU, values, COLUMN_MENU_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // **📌 3. Xóa món trong menu**
    public void deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU, COLUMN_MENU_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // **📌 4. Lấy danh sách món từ Menu**
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                menuList.add(new MenuItem(id, name, price)); // ✅ Đã thêm ID vào MenuItem
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return menuList;
    }
//    // **📌 Thêm tài khoản Admin mặc định**
//    private void addAdminAccount(SQLiteDatabase db) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_USERNAME, "admin");
//        values.put(COLUMN_PASSWORD, "123456"); // ⚠️ Mật khẩu này nên được mã hóa
//        values.put(COLUMN_ROLE, "admin");
//        db.insert(TABLE_USERS, null, values);
//    }
//
//    // **📌 Kiểm tra đăng nhập**
//    public boolean checkLogin(String username, String password) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?", new String[]{username, password});
//
//        boolean isLoggedIn = cursor.moveToFirst();
//        cursor.close();
//        db.close();
//        return isLoggedIn;
//    }
//
//    // **📌 Kiểm tra người dùng có phải Admin không**
//    public boolean isAdmin(String username) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND role = 'admin'", new String[]{username});
//
//        boolean isAdmin = cursor.moveToFirst();
//        cursor.close();
//        db.close();
//        return isAdmin;
//    }

    // **📌 5. Lưu đơn hàng vào lịch sử (có chi tiết món)**
//    public void saveOrder(String orderId, double totalPrice, String dateTime) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        try {
//            db.beginTransaction();
//
//            // Lưu thông tin đơn hàng
//            ContentValues orderValues = new ContentValues();
//            orderValues.put(COLUMN_ORDER_ID, orderId);
//            orderValues.put(COLUMN_TOTAL_PRICE, totalPrice);
//            orderValues.put(COLUMN_DATE_TIME, dateTime);
//            db.insert(TABLE_ORDERS, null, orderValues);
//
//            // Lưu từng món vào order_details
//            for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
//                ContentValues detailValues = new ContentValues();
//                detailValues.put(COLUMN_DETAIL_ORDER_ID, orderId);
//                detailValues.put(COLUMN_DETAIL_QUANTITY, entry.getValue());
//                db.insert(TABLE_ORDER_DETAILS, null, detailValues);
//            }
//
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();
//            db.close();
//        }
//    }

    public void saveOrder(String orderId, double totalPrice, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // ✅ Lưu thông tin đơn hàng
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_ID, orderId);
            orderValues.put(COLUMN_TOTAL_PRICE, totalPrice);
            orderValues.put(COLUMN_DATE_TIME, dateTime);
            db.insert(TABLE_ORDERS, null, orderValues);

            // ✅ Lấy danh sách món từ giỏ hàng (CartManager)
            List<CartItem> cartItems = CartManager.getInstance().getCartItems();

            // ✅ Lưu từng món vào order_details
            for (CartItem cartItem : cartItems) {
                ContentValues detailValues = new ContentValues();
                detailValues.put(COLUMN_DETAIL_ORDER_ID, orderId);
                detailValues.put(COLUMN_DETAIL_MENU_ID, cartItem.getMenuItem().getId()); // Lấy ID của món
                detailValues.put(COLUMN_DETAIL_QUANTITY, cartItem.getQuantity()); // Lấy số lượng
                db.insert(TABLE_ORDER_DETAILS, null, detailValues);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }



    // **📌 6. Lấy danh sách đơn hàng từ lịch sử**
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

    // **📌 7. Lấy chi tiết món từ đơn hàng**
    public List<OrderDetail> getOrderDetails(String orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT menu_id, quantity FROM " + TABLE_ORDER_DETAILS + " WHERE order_id=?", new String[]{orderId});

        if (cursor.moveToFirst()) {
            do {
                int menuId = cursor.getInt(0);
                int quantity = cursor.getInt(1);
                orderDetails.add(new OrderDetail(orderId, menuId, quantity));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orderDetails;
    }

}
