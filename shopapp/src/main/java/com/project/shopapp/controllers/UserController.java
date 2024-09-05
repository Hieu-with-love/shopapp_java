package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.request.AuthenticationRequest;
import com.project.shopapp.dtos.request.IntrospectRequest;
import com.project.shopapp.models.User;
import com.project.shopapp.services.AuthenticationService;
import com.project.shopapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @GetMapping("")
    public ResponseEntity<?> allUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok("Lay thanh cong " + users);
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

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest request, BindingResult result) throws Exception {
        // Kiểm tra thông tin đăng nhập và generate token
        // Thực hiện trả về token trong response
        try {
            var token = authenticationService.authenticate(request);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@Valid @RequestBody IntrospectRequest request, BindingResult bindingResult) throws Exception {
        // Kiểm tra thông tin đăng nhập và generate token
        // Thực hiện trả về token trong response
        try {
            var result = authenticationService.introspectVerify(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
