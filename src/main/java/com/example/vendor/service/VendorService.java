package com.example.vendor.service;

import com.example.vendor.model.*;
import org.springframework.web.multipart.MultipartFile;

public interface VendorService {

    ResponseModel login(LoginModel loginModel);

    ResponseModel registration(VendorRegisterModel model);

    ResponseModel uploadProfile(MultipartFile file);

    ResponseModel viewProfile();

    ResponseModel updateVendor(VendorUpdateModel model);

    ResponseModel addProduct(NewProductModel model);

    ResponseModel verifyVendor(VerificationModel model);

    PageResponseModel myProdustList (PageDetailModel model);
}
