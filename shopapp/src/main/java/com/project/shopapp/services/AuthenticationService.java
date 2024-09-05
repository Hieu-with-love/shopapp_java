package com.project.shopapp.services;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.request.AuthenticationRequest;
import com.project.shopapp.dtos.request.IntrospectRequest;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.AuthenticationResponse;
import com.project.shopapp.responses.IntrospectResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.expiration}")
    private int expiration;

    private final UserRepository userRepository;

    public IntrospectResponse introspectVerify(IntrospectRequest request) throws JsonEOFException, ParseException {
        var token = request.getToken();

        String secretKey = secretKey();
        try {
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            var verified = signedJWT.verify(verifier);

            return IntrospectResponse.builder()
                    .valid(verified && expiryTime.after(new Date()))
                    .build();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = userRepository.findByPhoneNumber(request.getPhone_number())
                .orElseThrow(() -> new UsernameNotFoundException("Cannot found user with sdt " + request.getPhone_number()));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated){
            throw new AuthenticationException("Wrong phone/password");
        }

        String token = generateToken(request.getPhone_number());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }

    private String generateToken(String phoneNumber){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(phoneNumber)
                .issuer("devzeus.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .claim("phoneNumber", phoneNumber)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        String secretKey = secretKey();

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error while signing JWT object", e);
            throw new RuntimeException(e);
        }
    }

    private String secretKey(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
