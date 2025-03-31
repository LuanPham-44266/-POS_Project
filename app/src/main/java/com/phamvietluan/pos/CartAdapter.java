package com.phamvietluan.pos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;

    // Interface để xử lý sự kiện xóa item
    public interface CartItemListener {
        void onRemoveItem(int position);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        }

        CartItem cartItem = cartItems.get(position);
        TextView tvItemName = convertView.findViewById(R.id.tvItemName);
        Button btnRemoveItem = convertView.findViewById(R.id.btnRemoveItem);

        // Hiển thị tên món và giá
        String displayText = cartItem.getMenuItem().getName();
        if (cartItem.getQuantity() > 1) {
            displayText += " (SL: " + cartItem.getQuantity() + ")";
        }
        tvItemName.setText(displayText);

        // Xử lý sự kiện khi nhấn nút xóa
        btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRemoveItem(position);
                }
            }
        });

        return convertView;
    }
}
