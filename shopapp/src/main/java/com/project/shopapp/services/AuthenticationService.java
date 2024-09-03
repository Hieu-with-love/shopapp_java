package com.project.shopapp.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.AuthenticationResponse;
import io.jsonwebtoken.io.Encoders;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Data
@RequiredArgsConstructor
@Builder
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    public AuthenticationResponse authenticate(UserDTO userDTO) throws Exception{
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User existingUser = userRepository.findByPhoneNumber(userDTO.getPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user)"));

        boolean authenticated = passwordEncoder.matches(userDTO.getPassword(), existingUser.getPassword());

        if (!authenticated) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        String token = generateToken(userDTO.getPhoneNumber());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    private String generateToken(String phoneNumber) {
        // Define header, where contains token type and algorithm for token
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(phoneNumber)
                .issuer("devzeus.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600 * 1000)) // expire after 1 hour
                .claim("phoneNumber", phoneNumber)
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        // secret-salt, use for signature
        String secretKey = generateSecretKey();
        try {
            JWSSigner signer = new MACSigner(secretKey);
            jwsObject.sign(signer);
            return jwsObject.serialize(); // token decoded
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
}
