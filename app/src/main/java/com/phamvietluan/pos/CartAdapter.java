package com.phamvietluan.pos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        CartItem cartItem = cartItems.get(position);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);

        tvName.setText(cartItem.getMenuItem().getName());
        tvPrice.setText(String.format("%.0f đ", cartItem.getMenuItem().getPrice()));
        tvQuantity.setText("SL: " + cartItem.getQuantity());

        return convertView;
    }
}
