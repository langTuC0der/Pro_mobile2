package com.example.app_giaohang;

public class User {
    private String id; // <-- THÊM DÒNG NÀY ĐỂ LƯU ID
    private String fullName;
    private String phone;
    private String role;
    private String email;

    public User() { }

    // --- THÊM 2 HÀM GET VÀ SET CHO ID ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    // ------------------------------------

    // Các hàm getter và setter khác giữ nguyên
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
