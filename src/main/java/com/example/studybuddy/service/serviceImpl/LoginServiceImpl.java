package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.ExceptionDTO;
import com.example.studybuddy.dto.request.LoginRequestDto;
import com.example.studybuddy.dto.response.LoginResponseDto;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.LoginService;
import com.example.studybuddy.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public ResponseEntity<?> login(LoginRequestDto loginReq){
        log.info("authenticate method started by: {}", loginReq.getUserName());
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getUserName(),
                            loginReq.getPassword()));
            log.info("authentication details: {}", authentication);
            String userName = authentication.getName();
            User client = new User(userName,"");
            String token = jwtUtil.createToken(client);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            LoginResponseDto loginRes = new LoginResponseDto(userName,token);
            log.info("user: {} logged in",  client.getUserName());
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(loginRes);

        }catch (BadCredentialsException e){
            ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(),"Invalid username or password");
            log.error("Error due to {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
        }catch (Exception e){
            ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            log.error("Error due to {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
        }
    }
}
