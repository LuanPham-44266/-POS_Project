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
    private List<MenuItem> cartItems;
    private CartManager cartManager;

    public CartAdapter(Context context, List<MenuItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartManager = CartManager.getInstance();
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

        MenuItem menuItem = cartItems.get(position);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);

        tvName.setText(menuItem.getName());
        tvPrice.setText(String.format("%.0f đ", menuItem.getPrice()));
        tvQuantity.setText("SL: " + cartManager.getQuantity(menuItem));

        return convertView;
    }
}
