package com.phamvietluan.pos;

public class MenuItem {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String imagePath; // Đường dẫn đến ảnh món
    
    // Constructor cũ - để đảm bảo tính tương thích
    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0;
        this.imagePath = null; // Ảnh mặc định
    }
    
    // Constructor mới có thêm tham số imagePath
    public MenuItem(int id, String name, double price, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0;
        this.imagePath = imagePath;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void increaseQuantity() {
        this.quantity++;
    }

    // Giảm số lượng món (không cho phép nhỏ hơn 1)
    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}