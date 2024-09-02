package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 0, message = "Price order must be >= 0")
    private double price;

    @Min(value = 0, message = "Quantity must be >= 0")
    private Long quantity;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private double totalMoney;

    private String color;
}
