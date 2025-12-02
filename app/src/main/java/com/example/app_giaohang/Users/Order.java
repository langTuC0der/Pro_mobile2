package com.example.app_giaohang.Users;
import java.text.DecimalFormat;
public class Order {
    private String orderId;      // Mã đơn (#DH...)
    private String pickupAddress; // Điểm đón
    private String dropoffAddress; // Điểm trả
    private String price;        // Giá tiền (Tự động tính)
    private String status;       // Trạng thái (Tìm xe, Đang giao...)
    private String dateTime;     // Thời gian
    private double distance;     // Khoảng cách (km)

    // Constructor rỗng bắt buộc cho Firebase
    public Order() { }

    // Constructor đầy đủ
    public Order(String orderId, String pickupAddress, String dropoffAddress, double distance, String status, String dateTime) {
        this.orderId = orderId;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.distance = distance;
        this.price = calculatePrice(distance); // Tự động tính giá tiền
        this.status = status;
        this.dateTime = dateTime;
    }

    // --- HÀM TÍNH TIỀN THEO KHOẢNG CÁCH ---
    // Công thức: 12.000đ cho 2km đầu, mỗi km sau cộng thêm 5.000đ
    public static String calculatePrice(double km) {
        double total = 0;
        if (km <= 2) {
            total = 12000;
        } else {
            total = 12000 + (km - 2) * 5000;
        }

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(total) + "đ";
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDropoffAddress() { return dropoffAddress; }
    public String getPrice() { return price; }
    public String getStatus() { return status; }
    public String getDateTime() { return dateTime; }
    public double getDistance() { return distance; }
}
