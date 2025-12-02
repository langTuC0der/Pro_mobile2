package com.example.app_giaohang.Users;
public class VehicleInfo {
    private String type;   // Loại xe (Xe ga/Xe số)
    private String year;   // Năm SX
    private String plate;  // Biển số
    private String name;   // Tên xe (Honda Vision...)

    // 1. Constructor rỗng (BẮT BUỘC để Firebase hoạt động)
    public VehicleInfo() { }

    // 2. Constructor đầy đủ
    public VehicleInfo(String type, String year, String plate, String name) {
        this.type = type;
        this.year = year;
        this.plate = plate;
        this.name = name;
    }

    // 3. Getter & Setter (Bôi đen biến -> Alt+Insert -> Getter and Setter -> Chọn hết -> OK)
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}