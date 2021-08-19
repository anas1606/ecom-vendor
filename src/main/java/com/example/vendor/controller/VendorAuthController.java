package com.example.vendor.controller;


import com.example.vendor.model.*;
import com.example.vendor.service.VendorService;
import com.example.vendor.util.CommanUtil;
import com.example.vendor.util.Message;
import com.example.vendor.util.ObjectMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/vendor")
public class VendorAuthController {

    private static final Logger logger = LoggerFactory.getLogger(VendorAuthController.class);

    @Autowired
    private VendorService vendorService;

    @PostMapping("login")
    public ResponseModel customerLogin(@RequestBody LoginModel loginModel) {
        return vendorService.login(loginModel);
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
            return vendorService.registration(model);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception {}", e.getMessage());
            return new CommanUtil().create(Message.SOMTHING_WRONG, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("upload/profile")
    public ResponseModel uploadProfile(HttpServletRequest req, MultipartHttpServletRequest multipartRequest) {
        try {
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
            return vendorService.uploadProfile(image[0]);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception {}", e.getMessage());
            return new CommanUtil().create(Message.SOMTHING_WRONG, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("myprofile")
    public ResponseModel viewProfile() {
        return vendorService.viewProfile();
    }

    @PostMapping("update")
    public ResponseModel updateVendor(@RequestBody VendorUpdateModel model) {
        return vendorService.updateVendor(model);
    }

    @PostMapping("add/product")
    public ResponseModel addProduct(@RequestBody NewProductModel model) {
        return vendorService.addProduct(model);
    }

    @PostMapping("verify")
    public ResponseModel verify(@RequestBody VerificationModel model) {
        return vendorService.verifyVendor(model);
    }

//    On Feed Display Own Product List
    @PostMapping("feed")
    public PageResponseModel feed(@RequestBody PageDetailModel model) {
        return vendorService.myProdustList(model);
    }

}

