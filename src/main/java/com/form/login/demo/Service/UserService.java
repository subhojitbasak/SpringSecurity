package com.form.login.demo.Service;

import com.form.login.demo.Entiry.UserInfo;
import com.form.login.demo.Repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserInfoRepository repository;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> newUserSignUp(UserInfo userInfo) {
        Optional<UserInfo> byEmail = repository.findByEmail(userInfo.getEmail());
        Optional<UserInfo> byUsername = repository.findByUsername(userInfo.getUsername());
        String url = "http://localhost:8081/sendfromgateway";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> map = new HashMap<>();
        map.put("username", userInfo.getUsername());
        map.put("email", userInfo.getEmail());
        map.put("roles", userInfo.getRoles());
        if (byEmail.isEmpty() && byUsername.isEmpty()) {
            try {
                userInfo.setPassword(encoder.encode(userInfo.getPassword()));
                repository.save(userInfo);
                HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
                restTemplate.postForObject(url, request, Void.class);
                return ResponseEntity.ok().body("User successfully signed up!!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.\nPlease try again later");
            }
        } else {
//            System.out.println(byEmail.getUsername());
            if(byUsername.isPresent()){
                return ResponseEntity.badRequest().body("User mane is not unique. Chose another Username!!");
            }else if(byEmail.isPresent()){
                return ResponseEntity.badRequest().body("User is already registered. Chose another email address!!");

            }
            return ResponseEntity.badRequest().body("User is already registered. Chose another email address & Username!!");

        }
    }


}
