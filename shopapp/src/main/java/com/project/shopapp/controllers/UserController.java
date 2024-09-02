package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try{
            if (result.hasErrors()){
                List<String> lstError = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(lstError);
            }

            userService.createUser(userDTO);

            return ResponseEntity.status(HttpStatus.OK).body("Register a account successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Fail");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO userDTO, BindingResult result) throws Exception {
        // Kiểm tra thông tin đăng nhập và generate token
        // Thực hiện trả về token trong response
        User token = userService.login(userDTO.getPhoneNumber() ,userDTO.getPassword());
        return ResponseEntity.ok(token);
    }
}
