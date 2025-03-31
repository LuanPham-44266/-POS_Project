package com.phamvietluan.pos;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateItemActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnExit;
    private DatabaseHelper databaseHelper;
    private List<MenuItem> menuList;
    private UpdateMenuAdapter adapter;
    
    private static final int PICK_IMAGE_REQUEST = 1;
    private String tempImagePath;
    private MenuItem currentMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        
        databaseHelper = new DatabaseHelper(this);
        
        listView = findViewById(R.id.listView);
        btnExit = findViewById(R.id.btnExit);
        
        loadMenuItems();
        
        btnExit.setOnClickListener(v -> finish());
    }
    
    private void loadMenuItems() {
        menuList = databaseHelper.getAllMenuItems();
        adapter = new UpdateMenuAdapter(this, menuList, this::showUpdateDialog);
        listView.setAdapter(adapter);
    }
    
    private void showUpdateDialog(MenuItem item) {
        currentMenuItem = item;
        
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật món");
        
        // Inflate layout cho dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_item, null);
        builder.setView(view);
        
        // Ánh xạ các view trong dialog
        EditText etName = view.findViewById(R.id.etItemName);
        EditText etPrice = view.findViewById(R.id.etItemPrice);
        ImageView imgItem = view.findViewById(R.id.imgItem);
        Button btnChooseImage = view.findViewById(R.id.btnChooseImage);
        
        // Điền thông tin hiện tại của món
        etName.setText(item.getName());
        etPrice.setText(String.valueOf(item.getPrice()));
        
        // Hiển thị ảnh hiện tại nếu có
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            File imageFile = new File(item.getImagePath());
            if (imageFile.exists()) {
                // Sử dụng phương thức tối ưu hóa để hiển thị ảnh
                try {
                    Bitmap bitmap = MenuAdapter.decodeSampledBitmapFromFile(item.getImagePath(), 300, 300);
                    if (bitmap != null) {
                        imgItem.setImageBitmap(bitmap);
                    } else {
                        imgItem.setImageResource(R.drawable.ic_tea);
                    }
                } catch (Exception e) {
                    imgItem.setImageResource(R.drawable.ic_tea);
                }
            } else {
                imgItem.setImageResource(R.drawable.ic_tea);
            }
        } else {
            imgItem.setImageResource(R.drawable.ic_tea);
        }
        
        // Lưu đường dẫn ảnh hiện tại
        tempImagePath = item.getImagePath();
        
        // Sự kiện chọn ảnh
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        
        // Nút cập nhật
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            try {
                String name = etName.getText().toString().trim();
                String priceText = etPrice.getText().toString().trim();
                
                if (name.isEmpty() || priceText.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                double price = Double.parseDouble(priceText);
                
                // Cập nhật thông tin món ăn trong database
                databaseHelper.updateMenuItem(item.getId(), name, price, tempImagePath);
                
                // Cập nhật lại danh sách
                loadMenuItems();
                
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Nút hủy
        builder.setNegativeButton("Hủy", null);
        
        builder.show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            
            try {
                // Lưu ảnh vào bộ nhớ trong
                tempImagePath = saveImageToInternalStorage(imageUri);
                
                // Hiển thị dialog lại với ảnh mới (nếu cần)
                AlertDialog currentDialog = (AlertDialog) findViewById(android.R.id.content).getRootView().getTag();
                if (currentDialog != null && currentDialog.isShowing()) {
                    ImageView imgItem = currentDialog.findViewById(R.id.imgItem);
                    if (imgItem != null && tempImagePath != null && !tempImagePath.isEmpty()) {
                        // Sử dụng phương thức tối ưu hóa để hiển thị ảnh
                        Bitmap bitmap = MenuAdapter.decodeSampledBitmapFromFile(tempImagePath, 300, 300);
                        if (bitmap != null) {
                            imgItem.setImageBitmap(bitmap);
                        } else {
                            imgItem.setImageResource(R.drawable.ic_tea);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private String saveImageToInternalStorage(Uri sourceUri) {
        try {
            // Đọc ảnh từ URI và tối ưu hóa
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) {
                return "";
            }
            
            // Đọc bitmap từ inputStream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            
            if (bitmap == null) {
                Toast.makeText(this, "Không thể đọc hình ảnh!", Toast.LENGTH_SHORT).show();
                return "";
            }
            
            // Tối ưu kích thước hình ảnh
            bitmap = resizeImageIfNeeded(bitmap, 800, 800);
            
            // Tạo tên file duy nhất với timestamp
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "ITEM_" + timeStamp + ".jpg";
            
            // Đường dẫn thư mục lưu trữ trong bộ nhớ trong của ứng dụng
            File imageDir = new File(getFilesDir(), "item_images");
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            
            // Tạo file ảnh mới
            File imageFile = new File(imageDir, imageFileName);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            
            // Lưu bitmap vào file với nén 80%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } catch (OutOfMemoryError e) {
            Toast.makeText(this, "Hình ảnh quá lớn, không thể xử lý!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
    
    // Phương thức tối ưu kích thước hình ảnh
    private Bitmap resizeImageIfNeeded(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) return null;
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Nếu hình ảnh đã nhỏ hơn kích thước tối đa, không cần resize
        if (width <= maxWidth && height <= maxHeight) {
            return image;
        }
        
        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;
        
        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }
        
        return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
    }
}