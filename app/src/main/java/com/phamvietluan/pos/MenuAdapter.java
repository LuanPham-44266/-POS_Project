package com.phamvietluan.pos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
        
        // Hiển thị số lượng hiện tại (có thể là từ CartManager hoặc từ MenuItem)
        CartManager cartManager = CartManager.getInstance();
        int quantity = 0;
        
        if (cartManager.hasItem(item)) {
            quantity = cartManager.getItemQuantity(item);
        }
        
        // Hiển thị số lượng từ CartManager thay vì từ MenuItem
        holder.tvQuantity.setText(String.valueOf(quantity));

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
            // Thêm trực tiếp vào CartManager
            cartManager.addItem(item, 1);
            
            // Cập nhật hiển thị
            int newQuantity = cartManager.getItemQuantity(item);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            
            // Thông báo cho người dùng
            Toast.makeText(context, "Đã thêm " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút Giảm (-)
        holder.btnDecrease.setOnClickListener(v -> {
            if (cartManager.hasItem(item)) {
                // Giảm số lượng trong CartManager
                cartManager.removeItem(item);
                
                // Cập nhật hiển thị
                int newQuantity = cartManager.getItemQuantity(item);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                
                // Thông báo cho người dùng nếu đã xóa hết
                if (newQuantity == 0) {
                    Toast.makeText(context, "Đã xóa " + item.getName() + " khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvQuantity;
        Button btnIncrease;
        Button btnDecrease;
        Button btnDelete; // Thêm nút xóa
    }
}
