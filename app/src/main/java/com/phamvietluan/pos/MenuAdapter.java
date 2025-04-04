package com.phamvietluan.pos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private final Context context;
    private final List<MenuItem> menuItems;
    private final boolean isDeleteMode; // Biến kiểm tra có đang ở DeleteActivity không
    private final OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDelete(int itemId);
    }

    public MenuAdapter(Context context, List<MenuItem> menuItems, boolean isDeleteMode, OnItemDeleteListener deleteListener) {
        this.context = context;
        this.menuItems = menuItems;
        this.isDeleteMode = isDeleteMode;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_menu, parent, false);

            holder = new ViewHolder();
            holder.imgItem = convertView.findViewById(R.id.imgItem);
            holder.tvItemName = convertView.findViewById(R.id.tvItemName);
            holder.tvItemPrice = convertView.findViewById(R.id.tvItemPrice);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.btnIncrease = convertView.findViewById(R.id.btnIncrease);
            holder.btnDecrease = convertView.findViewById(R.id.btnDecrease);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete); // Nút xóa
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem item = menuItems.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemPrice.setText(String.format("%,.0f VNĐ", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        // Hiển thị ảnh từ đường dẫn với tối ưu hóa bộ nhớ
        try {
            String imagePath = item.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    // Tải ảnh với kích thước phù hợp để tránh lỗi OutOfMemoryError
                    int targetWidth = 300; // Giới hạn kích thước ảnh
                    int targetHeight = 300;
                    Bitmap bitmap = decodeSampledBitmapFromFile(imagePath, targetWidth, targetHeight);
                    holder.imgItem.setImageBitmap(bitmap);
                    Log.d("MenuAdapter", "Hiển thị ảnh đã tối ưu từ: " + imagePath);
                } else {
                    // Nếu file không tồn tại, hiển thị ảnh mặc định
                    holder.imgItem.setImageResource(R.drawable.ic_tea);
                    Log.d("MenuAdapter", "File ảnh không tồn tại: " + imagePath);
                }
            } else {
                // Nếu không có đường dẫn ảnh, hiển thị ảnh mặc định
                holder.imgItem.setImageResource(R.drawable.ic_tea);
                Log.d("MenuAdapter", "Không có đường dẫn ảnh cho " + item.getName());
            }
        } catch (Exception e) {
            // Nếu có lỗi, hiển thị ảnh mặc định
            holder.imgItem.setImageResource(R.drawable.ic_tea);
            Log.e("MenuAdapter", "Lỗi khi hiển thị ảnh: " + e.getMessage());
        }

        // Nếu ở chế độ xóa, hiển thị nút "Xóa", ẩn nút "+" và "-"
        if (isDeleteMode) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnIncrease.setVisibility(View.GONE);
            holder.btnDecrease.setVisibility(View.GONE);
            holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(item.getId()));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnIncrease.setVisibility(View.VISIBLE);
            holder.btnDecrease.setVisibility(View.VISIBLE);
        }

        // Xử lý nút Tăng (+)
        holder.btnIncrease.setOnClickListener(v -> {
            item.increaseQuantity();
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            CartManager.getInstance().addItem(item, 1);
            notifyDataSetChanged();
        });

        // Xử lý nút Giảm (-)
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.decreaseQuantity();
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                CartManager.getInstance().removeItem(item);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    // Phương thức tối ưu để tải hình ảnh với kích thước phù hợp
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        try {
            // Đọc kích thước ảnh mà không tải toàn bộ vào bộ nhớ
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Tính toán tỷ lệ thu nhỏ
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Tải ảnh với tỷ lệ thu nhỏ
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Sử dụng ít bộ nhớ hơn
            return BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            Log.e("MenuAdapter", "Lỗi hết bộ nhớ khi tải ảnh: " + e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e("MenuAdapter", "Lỗi khi tải ảnh: " + e.getMessage());
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Chiều cao và rộng thực của ảnh
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Tính toán giá trị inSampleSize lớn nhất là lũy thừa của 2
            // mà vẫn cho kích thước lớn hơn hoặc bằng kích thước yêu cầu
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    static class ViewHolder {
        ImageView imgItem;
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvQuantity;
        ImageButton btnIncrease;
        ImageButton btnDecrease;
        ImageButton btnDelete; // Nút xóa
    }
}
