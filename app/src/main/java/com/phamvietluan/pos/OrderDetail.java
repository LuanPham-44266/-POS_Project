package com.phamvietluan.pos;

public class OrderDetail {
    private String orderId;  // ID đơn hàng
    private int menuId;      // ID món ăn
    private int quantity;    // Số lượng món

    public OrderDetail(String orderId, int menuId, int quantity) {
        this.orderId = orderId;
        this.menuId = menuId;
        this.quantity = quantity;
    }

    // Getter
    public String getOrderId() {
        return orderId;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setter (nếu cần)
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Ghi đè phương thức toString() để debug dễ hơn
    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderId='" + orderId + '\'' +
                ", menuId=" + menuId +
                ", quantity=" + quantity +
                '}';
    }
}
