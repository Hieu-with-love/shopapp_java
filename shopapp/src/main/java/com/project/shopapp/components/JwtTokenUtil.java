package com.project.shopapp.components;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.expiration}")
    private int expireation; // luu vao bien moi truong

    public String generateToken(com.project.shopapp.models.User user){
        // properties => claims
        Map<String, Object> claims = new HashMap<>();
        try{
            String token = Jwts.builder()

                    .compact();
        }catch (Exception e){

        }
        return null;
    }

}
