package com.phamvietluan.pos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OrderHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        }

        Order order = orderList.get(position);
        TextView tvOrderId = convertView.findViewById(R.id.tvOrderId);
        TextView tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);
        TextView tvDateTime = convertView.findViewById(R.id.tvDateTime);

        tvOrderId.setText("Mã đơn: " + order.getOrderId());
        tvTotalPrice.setText(String.format("Tổng tiền: %.0f đ", order.getTotalPrice()));
        tvDateTime.setText("Ngày: " + order.getDateTime());

        return convertView;
    }
}
