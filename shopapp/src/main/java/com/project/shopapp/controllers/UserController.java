package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.AuthenticationResponse;
import com.project.shopapp.services.AuthenticationService;
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
    private final AuthenticationService authenticationService;

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
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body("Passwords do not match");
            }

            User user = userService.createUser(userDTO);

            return ResponseEntity.status(HttpStatus.OK).body(user);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Fail " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        // Kiểm tra thông tin đăng nhập và generate token
        // Thực hiện trả về token trong response
        try{
            String token = userService.login(userDTO.getPhoneNumber() ,userDTO.getPassword());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/authenticate/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try{
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(userDTO);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
