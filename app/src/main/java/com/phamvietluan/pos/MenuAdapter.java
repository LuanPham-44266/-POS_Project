package com.phamvietluan.pos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<MenuItem> menuItems;
    private CartManager cartManager;

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
        this.cartManager = CartManager.getInstance();
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Kiểm tra ID hợp lệ
        if (holder.tvItemName == null || holder.tvItemPrice == null || holder.tvQuantity == null || holder.btnIncrease == null || holder.btnDecrease == null) {
            Log.e("MenuAdapter", "ViewHolder contains null components. Check XML IDs.");
            return convertView;
        }

        // Lấy dữ liệu từ danh sách
        MenuItem item = menuItems.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemPrice.setText(String.format("%,.2f VNĐ", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Xử lý nút Tăng (+)
        holder.btnIncrease.setOnClickListener(v -> {
            item.increaseQuantity();
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        });

        // Xử lý nút Giảm (-)
        holder.btnDecrease.setOnClickListener(v -> {
            item.decreaseQuantity();
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvQuantity;
        Button btnIncrease;
        Button btnDecrease;
    }
}
