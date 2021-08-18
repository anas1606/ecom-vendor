package com.example.vendor.controller;


import com.example.vendor.model.LoginModel;
import com.example.vendor.model.ResponseModel;
import com.example.vendor.model.VendorRegisterModel;
import com.example.vendor.service.VendorAuthService;
import com.example.vendor.util.CommanUtil;
import com.example.vendor.util.Message;
import com.example.vendor.util.ObjectMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/vendor")
public class VendorAuthController {

    private static final Logger logger = LoggerFactory.getLogger(VendorAuthController.class);

    @Autowired
    private VendorAuthService vendorAuthService;

    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("login")
    public ResponseModel customerLogin(@RequestBody LoginModel loginModel) {
        return vendorAuthService.login(loginModel);
    }

    @PostMapping("register")
    public ResponseModel register(HttpServletRequest req, @RequestParam("data") String data, MultipartHttpServletRequest multipartRequest) {
        try {
            VendorRegisterModel model;
            model = ObjectMapperUtil.getObjectMapper().readValue(data, VendorRegisterModel.class);

            final MultipartFile[] image = {null};
            multipartRequest.getFileMap().entrySet().stream().forEach(e -> {
                switch (e.getKey()) {
                    case "profile":
                        image[0] = e.getValue();
                        break;
                    default:
                        logger.warn("Some thing Wrong Add new product image fetch");
                        break;
                }
            });
            model.setProfileURL(image[0]);
            return vendorAuthService.registration(model);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception {}", e.getMessage());
            return new CommanUtil().create(Message.SOMTHING_WRONG, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("mail")
    public ResponseModel mail() {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("7043027540a@gmail.com");
            mail.setSubject("test");
            mail.setText("test");
            javaMailSender.send(mail);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

