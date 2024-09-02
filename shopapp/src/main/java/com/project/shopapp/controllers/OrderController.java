package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("")
    public ResponseEntity<String> getAllOrders(){
        return ResponseEntity.ok("This is get all orders method. Called by GET HTTP without parameters");
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ){
        try{
            if (result.hasErrors()){
                List<String> lstError = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.ok(lstError);
            }
            Order order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Create failed\n" + e);
        }
    }

    // Lấy chi tiết đơn hàng với id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @Valid @PathVariable("id") Long orderId
    ){
        try{
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Get failed  " + ex);
        }
    }

    // Lấy các đơn hàng người dùng (theo user_id)
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrderByUserId(
            @Valid @PathVariable("user_id") Long userId
    ){
        try{
            List<Order> orders = orderService.getOrderByUserId(userId);
            return ResponseEntity.ok(orders);
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Get failed " + ex);
        }
    }

    // Cập nhật order
    // Cong viec cua nguoi quan tri
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable Long id,
            @RequestBody OrderDTO orderDTO,
            BindingResult result
    ){
        try{
            if (result.hasErrors()){
                List<String> lstError = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.ok(lstError);
            }

            Order order = orderService.updateOrder(id, orderDTO);

            return ResponseEntity.ok("Updated order successfully ! " + order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Update failed + " + e);
        }
    }

    // Xóa order. Không xóa trực tiếp mà ta thực hiện xóa mềm -> active = false
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @Valid @PathVariable Long id,
            BindingResult result
    ){
        // Thực hien xóa mềm -> active = false
        try{
            if (result.hasErrors()){
                List<String> lstError = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.ok(lstError);
            }
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Delete successfully");
        }
        catch (Exception ex){
            return ResponseEntity.badRequest().body("Delete failed " + ex);
        }
    }
}
