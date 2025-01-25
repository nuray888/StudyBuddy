package com.example.studybuddy.utility;

import com.example.studybuddy.model.Authority;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class JwtUtil {
    private final UserRepository clientRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secret_key;
    @Value("${application.security.jwt.expiration}")
    private long accessTokenValidity;
    private static Key key;
    public JwtUtil(UserRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public Key initializeKey(){
        byte[] keyBytes;
        keyBytes = Decoders.BASE64.decode(secret_key);
        key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
    public String createToken(User client) {
        key = initializeKey();
        client = clientRepository.findByUserName(client.getUserName())
                .orElseThrow(() -> new EntityNotFoundException("CLIENT_NOT_FOUND"));
        Claims claims = Jwts.claims().setSubject(client.getUserName());
        Set<Authority> authorities = client.getAuthorities();
        List<String> roles = new ArrayList<>();
        for (Authority authority : authorities) {
            roles.add(authority.getName());
        }
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("authorities",roles);
        claimsMap.put("username", client.getUserName());
        claimsMap.put("user_id", client.getId());

        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        final JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(client.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(tokenValidity)
                .addClaims(claimsMap)
                .signWith(key, SignatureAlgorithm.HS512);
        log.info("Jwt token created for user: {}", client.getUserName());
        return jwtBuilder.compact();
    }

    private Claims parseJwtClaims(String token) {
        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            log.error("Error due to: {}", ex.getMessage());
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error due to: {}", ex.getMessage());
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String TOKEN_HEADER = "Authorization";
        String bearerToken = request.getHeader(TOKEN_HEADER);
        String TOKEN_PREFIX = "Bearer ";
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public Integer getUserId(Claims claims){
        return (Integer) claims.get("user_id");
    }

    public Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (claims.containsKey("authorities")) {
            List<String> roles = (List<String>) claims.get("authorities");
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        return authorities;
    }

}


//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import javax.naming.AuthenticationException;
//import java.security.Key;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtUtil {
//
//    private final UserRepository clientRepository;
//
//    @Value("${application.security.jwt.secret-key}")
//    private String secret_key;
//
//    @Value("${application.security.jwt.expiration}")
//    private long accessTokenValidity;
//
//    private Key key;
//
//    @PostConstruct
//    private void initializeKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret_key);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public String createToken(User client) {
//        client = clientRepository.findByUserName(client.getUserName())
//                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND"));
//        Set<Authority> authorities = client.getAuthorities();
//        List<String> roles = new ArrayList<>();
//        for (Authority authority : authorities) {
//            roles.add(authority.getName());
//        }
//        Map<String, Object> claimsMap = new HashMap<>();
//        claimsMap.put("authorities", roles);
//        claimsMap.put("username", client.getUserName());
//        claimsMap.put("user_id", client.getId());
//
//        Date tokenCreateTime = new Date();
//        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
//        final JwtBuilder jwtBuilder = Jwts.builder()
//                .setSubject(client.getUserName())
//                .setIssuedAt(new Date())
//                .setExpiration(tokenValidity)
//                .addClaims(claimsMap)
//                .signWith(key, SignatureAlgorithm.HS512);
//        log.info("Jwt token created for user: {}", client.getUserName());
//        return jwtBuilder.compact();
//    }
//
//    private Claims parseJwtClaims(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
//    }
//
//    public Claims resolveClaims(HttpServletRequest req) {
//        try {
//            String token = resolveToken(req);
//            if (token != null) {
//                return parseJwtClaims(token);
//            }
//            return null;
//        } catch (ExpiredJwtException ex) {
//            log.error("Error due to: {}", ex.getMessage());
//            req.setAttribute("expired", ex.getMessage());
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Error due to: {}", ex.getMessage());
//            req.setAttribute("invalid", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//        String TOKEN_HEADER = "Authorization";
//        String bearerToken = request.getHeader(TOKEN_HEADER);
//        String TOKEN_PREFIX = "Bearer ";
//        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
//            return bearerToken.substring(TOKEN_PREFIX.length());
//        }
//        return null;
//    }
//
//    public boolean validateClaims(Claims claims) throws AuthenticationException {
//        try {
//            return claims.getExpiration().after(new Date());
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//
//    public Integer getUserId(Claims claims) {
//        return (Integer) claims.get("user_id");
//    }
//
//    public Collection<GrantedAuthority> extractAuthorities(Claims claims) {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        if (claims.containsKey("authorities")) {
//            List<String> roles = (List<String>) claims.get("authorities");
//            for (String role : roles) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//            }
//        }
//        return authorities;
//    }
//}
//


//package com.example.studybuddy.utility;
//
//import com.example.studybuddy.model.Authority;
//import com.example.studybuddy.model.User;
//import com.example.studybuddy.repository.UserRepository;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import javax.naming.AuthenticationException;
//import java.security.Key;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//
//@Slf4j
//@Configuration
//@PropertySource("classpath:application.properties")
//@RequiredArgsConstructor
//public class JwtUtil {
//    private final UserRepository clientRepository;
//
//    @Value("${application.security.jwt.secret-key}")
//    private static String secret_key;
//    @Value("${application.security.jwt.expiration}")
//    private long accessTokenValidity;
//    private static Key key = initializeKey();
//
////    public JwtUtil(UserRepository clientRepository){
////        this.clientRepository = clientRepository;
////    }
//
//    public static Key initializeKey(){
//        byte[] keyBytes;
//        keyBytes = Decoders.BASE64.decode(secret_key);
//        key = Keys.hmacShaKeyFor(keyBytes);
//        return key;
//    }
//    public String createToken(User client) {
//        client = clientRepository.findByUserName(client.getUserName())
//                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND"));
//        Set<Authority> authorities = client.getAuthorities();
//        List<String> roles = new ArrayList<>();
//        for (Authority authority : authorities) {
//            roles.add(authority.getName());
//        }
//        Map<String, Object> claimsMap = new HashMap<>();
//        claimsMap.put("authorities",roles);
//        claimsMap.put("username", client.getUserName());
//        claimsMap.put("user_id", client.getId());
//
//        Date tokenCreateTime = new Date();
//        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
//        final JwtBuilder jwtBuilder = Jwts.builder()
//                .setSubject(client.getUserName())
//                .setIssuedAt(new Date())
//                .setExpiration(tokenValidity)
//                .addClaims(claimsMap)
//                .signWith(key, SignatureAlgorithm.HS512);
//        log.info("Jwt token created for user: {}", client.getUserName());
//        return jwtBuilder.compact();
//    }
//
//    private Claims parseJwtClaims(String token) {
//        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
//    }
//
//    public Claims resolveClaims(HttpServletRequest req) {
//        try {
//            String token = resolveToken(req);
//            if (token != null) {
//                return parseJwtClaims(token);
//            }
//            return null;
//        } catch (ExpiredJwtException ex) {
//            log.error("Error due to: {}", ex.getMessage());
//            req.setAttribute("expired", ex.getMessage());
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Error due to: {}", ex.getMessage());
//            req.setAttribute("invalid", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//
//        String TOKEN_HEADER = "Authorization";
//        String bearerToken = request.getHeader(TOKEN_HEADER);
//        String TOKEN_PREFIX = "Bearer ";
//        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
//            return bearerToken.substring(TOKEN_PREFIX.length());
//        }
//        return null;
//    }
//
//    public boolean validateClaims(Claims claims) throws AuthenticationException {
//        try {
//            return claims.getExpiration().after(new Date());
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//
//    public Integer getUserId(Claims claims){
//        return (Integer) claims.get("user_id");
//    }
//
//    public Collection<GrantedAuthority> extractAuthorities(Claims claims) {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        if (claims.containsKey("authorities")) {
//            List<String> roles = (List<String>) claims.get("authorities");
//            for (String role : roles) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//            }
//        }
//        return authorities;
//    }
//
//}
