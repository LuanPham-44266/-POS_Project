package com.phamvietluan.pos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        // Đảm bảo cơ sở dữ liệu hỗ trợ UTF-8
        db.execSQL("PRAGMA encoding = 'UTF-8'");
        
        // Tạo bảng Menu
        String createMenuTable = "CREATE TABLE " + TABLE_MENU + " ("
                + COLUMN_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MENU_NAME + " TEXT COLLATE NOCASE, "
                + COLUMN_MENU_PRICE + " REAL, "
                + COLUMN_MENU_IMAGE + " TEXT)";
        
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
        try {
            ContentValues values = new ContentValues();
            // Đảm bảo lưu trữ tiếng Việt đúng cách (UTF-8)
            values.put(COLUMN_MENU_NAME, name);
            values.put(COLUMN_MENU_PRICE, price);
            values.put(COLUMN_MENU_IMAGE, imagePath);

            long result = db.insert(TABLE_MENU, null, values);
            Log.d("DatabaseHelper", "Thêm món: " + name + ", Kết quả: " + result);
            return result != -1; // ✅ Nếu `insert()` thành công, trả về `true`, ngược lại `false`
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi khi thêm món: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
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

    // Cập nhật món trong menu (bao gồm cả ảnh)
    public void updateMenuItem(int id, String newName, double newPrice, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MENU_NAME, newName);
        values.put(COLUMN_MENU_PRICE, newPrice);
        
        // Cập nhật đường dẫn ảnh nếu có
        if (imagePath != null && !imagePath.isEmpty()) {
            values.put(COLUMN_MENU_IMAGE, imagePath);
        }
        
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
                String imagePath = cursor.getString(3); // Lấy đường dẫn ảnh
                
                // Tạo MenuItem với đường dẫn ảnh
                MenuItem item = new MenuItem(id, name, price, imagePath);
                menuList.add(item);
                
                // Log để debug
                Log.d("DatabaseHelper", "Đọc món: " + name + ", ID: " + id + ", Ảnh: " + imagePath);
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
    public void saveOrder(String orderId, double totalPrice, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            Log.d("DatabaseHelper", "Bắt đầu lưu đơn hàng: ID=" + orderId + ", Giá=" + totalPrice);

            // ✅ Lưu thông tin đơn hàng
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_ID, orderId);
            orderValues.put(COLUMN_TOTAL_PRICE, totalPrice);
            orderValues.put(COLUMN_DATE_TIME, dateTime);
            long orderRowId = db.insert(TABLE_ORDERS, null, orderValues);
            
            if (orderRowId == -1) {
                Log.e("DatabaseHelper", "Lỗi: Không thể lưu thông tin đơn hàng");
                return;
            }
            
            Log.d("DatabaseHelper", "Đã lưu thông tin đơn hàng. Kết quả insert: " + orderRowId);

            // ✅ Lấy danh sách món từ giỏ hàng (CartManager)
            List<CartItem> cartItems = CartManager.getInstance().getCartItems();
            
            if (cartItems == null || cartItems.isEmpty()) {
                Log.e("DatabaseHelper", "Lỗi: Giỏ hàng trống hoặc null");
                return;
            }
            
            Log.d("DatabaseHelper", "Số món trong đơn hàng: " + cartItems.size());

            // ✅ Lưu từng món vào order_details
            for (CartItem cartItem : cartItems) {
                if (cartItem == null || cartItem.getMenuItem() == null) {
                    Log.e("DatabaseHelper", "Lỗi: CartItem hoặc MenuItem null");
                    continue; // Bỏ qua item này, tiếp tục với item khác
                }
                
                ContentValues detailValues = new ContentValues();
                detailValues.put(COLUMN_DETAIL_ORDER_ID, orderId);
                detailValues.put(COLUMN_DETAIL_MENU_ID, cartItem.getMenuItem().getId()); // Lấy ID của món
                detailValues.put(COLUMN_DETAIL_QUANTITY, cartItem.getQuantity()); // Lấy số lượng
                long detailRowId = db.insert(TABLE_ORDER_DETAILS, null, detailValues);
                
                if (detailRowId == -1) {
                    Log.e("DatabaseHelper", "Lỗi: Không thể lưu chi tiết món: " + 
                        (cartItem.getMenuItem() != null ? cartItem.getMenuItem().getName() : "null"));
                } else {
                    Log.d("DatabaseHelper", "Đã lưu chi tiết món: " + cartItem.getMenuItem().getName() 
                        + ", SL: " + cartItem.getQuantity() + ", Kết quả insert: " + detailRowId);
                }
            }

            db.setTransactionSuccessful();
            Log.d("DatabaseHelper", "Lưu đơn hàng thành công!");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi khi lưu đơn hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // **📌 6. Lấy danh sách đơn hàng từ lịch sử**
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            
            if (db == null) {
                Log.e("DatabaseHelper", "Không thể mở cơ sở dữ liệu để đọc");
                return orders;
            }
            
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_DATE_TIME + " DESC", null);
            
            Log.d("DatabaseHelper", "Đang đọc lịch sử đơn hàng...");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String orderId = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_ID));
                        double totalPrice = cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_PRICE));
                        String dateTime = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME));
                        
                        orders.add(new Order(orderId, totalPrice, dateTime));
                        Log.d("DatabaseHelper", "Đọc đơn hàng: ID=" + orderId + ", Giá=" + totalPrice + ", Ngày=" + dateTime);
                    } catch (Exception e) {
                        Log.e("DatabaseHelper", "Lỗi khi đọc bản ghi đơn hàng: " + e.getMessage());
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "Không có đơn hàng nào trong lịch sử!");
            }
            
            Log.d("DatabaseHelper", "Tổng số đơn hàng đọc được: " + orders.size());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi khi truy vấn lịch sử đơn hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        
        return orders;
    }

    // **📌 7. Lấy chi tiết món từ đơn hàng**
    public List<OrderDetail> getOrderDetails(String orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        if (orderId == null || orderId.isEmpty()) {
            Log.e("DatabaseHelper", "Lỗi: orderId null hoặc rỗng");
            return orderDetails;
        }
        
        try {
            db = this.getReadableDatabase();
            
            if (db == null) {
                Log.e("DatabaseHelper", "Không thể mở cơ sở dữ liệu để đọc");
                return orderDetails;
            }
            
            Log.d("DatabaseHelper", "Đang lấy chi tiết đơn hàng: " + orderId);
            
            cursor = db.rawQuery("SELECT " + COLUMN_DETAIL_MENU_ID + ", " + COLUMN_DETAIL_QUANTITY 
                + " FROM " + TABLE_ORDER_DETAILS 
                + " WHERE " + COLUMN_DETAIL_ORDER_ID + "=?", new String[]{orderId});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        int menuId = cursor.getInt(0);
                        int quantity = cursor.getInt(1);
                        OrderDetail detail = new OrderDetail(orderId, menuId, quantity);
                        orderDetails.add(detail);
                        Log.d("DatabaseHelper", "Đọc chi tiết: menuID=" + menuId + ", SL=" + quantity);
                    } catch (Exception e) {
                        Log.e("DatabaseHelper", "Lỗi khi đọc chi tiết đơn hàng: " + e.getMessage());
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "Không có chi tiết nào cho đơn hàng: " + orderId);
            }
            
            Log.d("DatabaseHelper", "Tổng số chi tiết đơn hàng: " + orderDetails.size());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi khi truy vấn chi tiết đơn hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        
        return orderDetails;
    }

    /**
     * Lấy thông tin món ăn theo ID
     */
    public MenuItem getMenuItemById(int menuId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        MenuItem menuItem = null;
        
        try {
            db = this.getReadableDatabase();
            
            if (db == null) {
                Log.e("DatabaseHelper", "Không thể mở cơ sở dữ liệu để đọc");
                return null;
            }
            
            cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU + " WHERE " + COLUMN_MENU_ID + "=?", 
                    new String[]{String.valueOf(menuId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_MENU_NAME));
                double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_MENU_PRICE));
                String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_MENU_IMAGE));
                
                menuItem = new MenuItem(menuId, name, price, imagePath);
                Log.d("DatabaseHelper", "Đã tìm thấy món: " + name + " (ID: " + menuId + ")");
            } else {
                Log.d("DatabaseHelper", "Không tìm thấy món với ID: " + menuId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi khi tìm món theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        
        return menuItem;
    }

}
