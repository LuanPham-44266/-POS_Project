package com.phamvietluan.pos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.view.View;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgItem;
    private EditText etItemName, etItemPrice;
    private Uri imageUri; // Để lưu đường dẫn ảnh
    private String savedImagePath; // Đường dẫn ảnh đã lưu
    private int selectedDrawableId = -1; // ID của drawable đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Ánh xạ view
        Button btnExit = findViewById(R.id.btnExit);
        imgItem = findViewById(R.id.imgItem);
        Button btnChooseImage = findViewById(R.id.btnChooseImage);
        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        Button btnSaveItem = findViewById(R.id.btnSaveItem);
        
        // Nút Thoát
        btnExit.setOnClickListener(v -> finish());

        // Nút chọn ảnh - hiển thị danh sách ảnh drawable
        btnChooseImage.setOnClickListener(v -> showDrawableImagePicker());

        // Nút Lưu món
        btnSaveItem.setOnClickListener(v -> {
            // Lấy nội dung từ EditText và xử lý đúng cách với Unicode
            String name = etItemName.getText().toString().trim();
            String priceStr = etItemPrice.getText().toString().trim();

            // Kiểm tra đầu vào
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên và giá món!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double price = Double.parseDouble(priceStr);
                
                // Nếu chưa chọn ảnh, yêu cầu chọn
                if (selectedDrawableId == -1 && imageUri == null && savedImagePath == null) {
                    Toast.makeText(this, "Vui lòng chọn ảnh cho món ăn!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Nếu đã chọn drawable nhưng chưa lưu, lưu ảnh drawable
                if (selectedDrawableId != -1 && savedImagePath == null) {
                    savedImagePath = saveDrawableToInternalStorage(selectedDrawableId);
                }
                
                // Nếu có URI ảnh nhưng chưa lưu, tiến hành lưu ảnh
                if (imageUri != null && savedImagePath == null) {
                    // Sao chép ảnh từ URI vào thư mục ứng dụng
                    savedImagePath = saveImageToInternalStorage(imageUri);
                    if (savedImagePath == null) {
                        Toast.makeText(this, "Không thể lưu ảnh đã chọn! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                
                // Lưu món vào Database với đường dẫn ảnh đã lưu nội bộ
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                boolean success = databaseHelper.addMenuItem(name, price, savedImagePath);
                
                if (success) {
                    Toast.makeText(this, "Thêm món \"" + name + "\" thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa dữ liệu đã nhập để chuẩn bị thêm món mới
                    etItemName.setText("");
                    etItemPrice.setText("");
                    imgItem.setImageResource(R.drawable.logo_7tea);
                    imageUri = null;
                    savedImagePath = null;
                    selectedDrawableId = -1;
                } else {
                    Toast.makeText(this, "Lỗi khi thêm món!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá món không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // Hiển thị hộp thoại chọn ảnh từ drawable
    private void showDrawableImagePicker() {
        // Lấy danh sách tất cả các drawable trong thư mục drawable
        List<Integer> drawableList = getAllDrawableIds();
        
        // Tạo layout để hiển thị danh sách ảnh
        LinearLayout imageLayout = new LinearLayout(this);
        imageLayout.setOrientation(LinearLayout.VERTICAL);
        
        // LinearLayout cho mỗi hàng ảnh
        LinearLayout currentRow = null;
        
        // Hiển thị 3 ảnh trên mỗi hàng
        int imagesPerRow = 3;
        int imgSize = getResources().getDisplayMetrics().widthPixels / 4;
        int padding = 10;
        
        for (int i = 0; i < drawableList.size(); i++) {
            // Tạo hàng mới sau mỗi 3 ảnh
            if (i % imagesPerRow == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setPadding(padding, padding, padding, padding);
                imageLayout.addView(currentRow);
            }
            
            int drawableId = drawableList.get(i);
            
            // Tạo ImageView để hiển thị ảnh
            ImageView imageView = new ImageView(this);
            imageView.setId(View.generateViewId());
            imageView.setImageResource(drawableId);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            
            // Đặt kích thước và padding
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize, imgSize);
            params.setMargins(padding, padding, padding, padding);
            imageView.setLayoutParams(params);
            
            // Khi click vào ảnh
            final int finalDrawableId = drawableId;
            imageView.setOnClickListener(v -> {
                // Đặt ảnh đã chọn vào imgItem
                imgItem.setImageResource(finalDrawableId);
                selectedDrawableId = finalDrawableId;
                imageUri = null;
                savedImagePath = null;
                Toast.makeText(AddItemActivity.this, "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
                
                // Đóng dialog khi đã chọn ảnh
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            });
            
            // Thêm vào hàng hiện tại
            if (currentRow != null) {
                currentRow.addView(imageView);
            }
        }
        
        // Tạo scrollview để có thể cuộn nếu có nhiều ảnh
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh từ thư viện ứng dụng");
        builder.setView(imageLayout);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        
        alertDialog = builder.create();
        alertDialog.show();
    }
    
    // Biến toàn cục để lưu trạng thái dialog
    private AlertDialog alertDialog;
    
    // Lấy danh sách tất cả ID drawable
    private List<Integer> getAllDrawableIds() {
        List<Integer> drawables = new ArrayList<>();
        
        // Lấy tất cả các trường drawable
        Field[] fields = R.drawable.class.getFields();
        
        for (Field field : fields) {
            try {
                drawables.add(field.getInt(null));
            } catch (Exception e) {
                // Bỏ qua lỗi
            }
        }
        
        return drawables;
    }
    
    // Lưu ảnh drawable vào bộ nhớ nội bộ
    private String saveDrawableToInternalStorage(int drawableId) {
        try {
            // Tạo bitmap từ drawable
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
            
            // Tối ưu kích thước hình ảnh
            bitmap = resizeImageIfNeeded(bitmap, 800, 800);
            
            // Tạo tên file duy nhất dựa trên thời gian hiện tại
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            
            // Tạo file trong thư mục files của ứng dụng
            File destinationFile = new File(getFilesDir(), fileName);
            
            // Lưu bitmap vào file với nén 80%
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            
            // Trả về đường dẫn file đã lưu
            return destinationFile.getAbsolutePath();
            
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Lưu ảnh vào bộ nhớ nội bộ của ứng dụng
    private String saveImageToInternalStorage(Uri sourceUri) {
        try {
            // Tạo tên file duy nhất dựa trên thời gian hiện tại
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            
            // Tạo file trong thư mục files của ứng dụng
            File destinationFile = new File(getFilesDir(), fileName);
            
            // Đọc ảnh từ URI và tối ưu hóa
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            
            if (bitmap == null) {
                Toast.makeText(this, "Không thể đọc hình ảnh!", Toast.LENGTH_SHORT).show();
                return null;
            }
            
            // Tối ưu kích thước hình ảnh
            bitmap = resizeImageIfNeeded(bitmap, 800, 800);
            
            // Lưu bitmap vào file với nén 80%
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            
            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        } catch (OutOfMemoryError e) {
            Toast.makeText(this, "Hình ảnh quá lớn, không thể xử lý!", Toast.LENGTH_SHORT).show();
            return null;
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
