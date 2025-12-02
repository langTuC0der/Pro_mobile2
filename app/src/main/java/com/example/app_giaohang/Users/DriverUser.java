package com.example.app_giaohang.Users;

public class DriverUser {
    private String fullName;
    private String dob;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String password;
    private VehicleInfo vehicleInfo; // Lồng class xe vào đây

    // 1. Constructor rỗng (BẮT BUỘC)
    public DriverUser() { }

    // 2. Constructor đầy đủ
    public DriverUser(String fullName, String dob, String gender, String address, String email, String phone, String password, VehicleInfo vehicleInfo) {
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.vehicleInfo = vehicleInfo;
    }

    // 3. Getter & Setter (Tạo tự động bằng Alt+Insert)
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public VehicleInfo getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(VehicleInfo vehicleInfo) { this.vehicleInfo = vehicleInfo; }
}
