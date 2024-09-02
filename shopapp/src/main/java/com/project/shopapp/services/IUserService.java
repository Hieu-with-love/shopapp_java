package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    User createUser(UserDTO user) throws DataNotFoundException;
    User login(String phoneNumber, String password) throws Exception;
    List<User> getAllUsers();
}
