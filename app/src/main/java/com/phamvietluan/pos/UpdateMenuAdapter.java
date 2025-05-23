package com.phamvietluan.pos;

import android.content.Context;
import android.graphics.Bitmap;
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

public class UpdateMenuAdapter extends BaseAdapter {
    private final Context context;
    private final List<MenuItem> menuItems;
    private final OnItemUpdateListener updateListener;

    public interface OnItemUpdateListener {
        void onUpdate(MenuItem item);
    }

    public UpdateMenuAdapter(Context context, List<MenuItem> menuItems, OnItemUpdateListener updateListener) {
        this.context = context;
        this.menuItems = menuItems;
        this.updateListener = updateListener;
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
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem item = menuItems.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemPrice.setText(String.format("%,.0f VNĐ", item.getPrice()));
        holder.tvQuantity.setText("0");
        
        // Hiển thị ảnh từ đường dẫn với tối ưu hóa bộ nhớ
        try {
            String imagePath = item.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    // Tải ảnh với kích thước phù hợp để tránh lỗi OutOfMemoryError
                    int targetWidth = 300; // Giới hạn kích thước ảnh
                    int targetHeight = 300;
                    Bitmap bitmap = MenuAdapter.decodeSampledBitmapFromFile(imagePath, targetWidth, targetHeight);
                    if (bitmap != null) {
                        holder.imgItem.setImageBitmap(bitmap);
                        Log.d("UpdateMenuAdapter", "Hiển thị ảnh đã tối ưu từ: " + imagePath);
                    } else {
                        holder.imgItem.setImageResource(R.drawable.background_7tea);
                        Log.d("UpdateMenuAdapter", "Không thể tải ảnh tối ưu từ: " + imagePath);
                    }
                } else {
                    // Nếu file không tồn tại, hiển thị ảnh mặc định
                    holder.imgItem.setImageResource(R.drawable.background_7tea);
                    Log.d("UpdateMenuAdapter", "File ảnh không tồn tại: " + imagePath);
                }
            } else {
                // Nếu không có đường dẫn ảnh, hiển thị ảnh mặc định
                holder.imgItem.setImageResource(R.drawable.background_7tea);
                Log.d("UpdateMenuAdapter", "Không có đường dẫn ảnh cho " + item.getName());
            }
        } catch (Exception e) {
            // Nếu có lỗi, hiển thị ảnh mặc định
            holder.imgItem.setImageResource(R.drawable.background_7tea);
            Log.e("UpdateMenuAdapter", "Lỗi khi hiển thị ảnh: " + e.getMessage());
        }

        // Đổi nút (-) thành nút cập nhật
        holder.btnDecrease.setVisibility(View.GONE);
        holder.btnIncrease.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.VISIBLE);
        holder.btnDelete.setImageResource(R.drawable.ic_update_btn);
        holder.btnDelete.setContentDescription("Cập nhật món");
        
        // Đặt sự kiện click cho nút cập nhật
        holder.btnDelete.setOnClickListener(v -> updateListener.onUpdate(item));

        return convertView;
    }

    static class ViewHolder {
        ImageView imgItem;
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvQuantity;
        ImageButton btnIncrease;
        ImageButton btnDecrease;
        ImageButton btnDelete;
    }
}