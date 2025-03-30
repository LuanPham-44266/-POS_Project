package com.phamvietluan.pos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgItem;
    private EditText etItemName, etItemPrice;
    private Uri imageUri; // Để lưu đường dẫn ảnh

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

        // Nút chọn ảnh
        btnChooseImage.setOnClickListener(v -> openImageChooser());

        // Nút Lưu món
        btnSaveItem.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String priceStr = etItemPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            // Lưu món vào Database (cần thêm databaseHelper)
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            boolean success = databaseHelper.addMenuItem(name, price, imageUri.toString());
            if (success) {
                Toast.makeText(this, "Thêm món thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm món!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Mở thư viện chọn ảnh
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgItem.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
