package com.example.vendor.util;


import com.example.vendor.model.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CommanUtil {
    public ResponseModel create(String message, Object data, HttpStatus status) {
        ResponseModel rs = new ResponseModel();
        rs.setData(data);
        rs.setMessage(message);
        rs.setStatus(status);
        rs.setStatusCode(status.value());
        return rs;
    }

    public String genrateRandomOTP() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public void sendVerificationEmail(String otp) {

    }
}

