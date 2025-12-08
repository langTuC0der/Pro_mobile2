package com.example.app_giaohang;

public class Order {
    private String orderId;      // Mã đơn (Firebase tự tạo sẽ gán vào đây)
    private String name;         // Tên hàng hóa
    private String addressFrom;  // Nơi gửi
    private String addressTo;    // Nơi nhận
    private double price;        // Số tiền
    private String userId;       // ID của người tạo đơn (User)
    private String status;       // Trạng thái: "Chờ xác nhận", "Đang giao"...
    private long timestamp;      // Thời gian tạo

    // Constructor rỗng (Bắt buộc để Firebase đọc dữ liệu)
    public Order() { }

    public Order(String name, String addressFrom, String addressTo, double price, String userId, String status, long timestamp) {
        this.name = name;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.price = price;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getter & Setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddressFrom() { return addressFrom; }
    public void setAddressFrom(String addressFrom) { this.addressFrom = addressFrom; }

    public String getAddressTo() { return addressTo; }
    public void setAddressTo(String addressTo) { this.addressTo = addressTo; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
