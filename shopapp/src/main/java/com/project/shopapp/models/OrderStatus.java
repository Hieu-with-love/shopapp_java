package com.project.shopapp.models;

public enum OrderStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");

    private final String value;
    OrderStatus(String value) {
        this.value = value;
    }

    // Chuyển giá trị sang thường -> phù hợp vói CSDL
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    // Lấy ngược lại Enum từ CSDL
    public static OrderStatus getOrderStatus(String status) {
        return OrderStatus.valueOf(status.toUpperCase());
    }
}
