package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDTO user) throws DataNotFoundException {
        // Register user
        String phoneNumber = user.getPhoneNumber();
        // Kiem tra sdt da ton tai chua
        if (userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        // convert from UserDTO -> User

        User newUser = User.builder()
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .password(user.getPassword())
                .dayOfBirth(user.getDayOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .build();

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        newUser.setRole(role);
        // Neu dang co accountId thi khong can password
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0){
            String password = user.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }

        return userRepository.save(newUser);
    }

    @Override
    public User login(String phoneNumber, String password) throws Exception {
        // doan nay lien quan nhieu den Spring Security
//        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
//        if (optionalUser.isEmpty()){
//            throw new DataNotFoundException("User not found");
//        }
//        return optionalUser.get(); // muốn trả ve JWT Token?
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
