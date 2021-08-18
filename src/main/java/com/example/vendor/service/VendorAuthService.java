package com.example.vendor.service;

import com.example.vendor.model.LoginModel;
import com.example.vendor.model.ResponseModel;
import com.example.vendor.model.VendorRegisterModel;

public interface VendorAuthService {

    ResponseModel login(LoginModel loginModel);

    ResponseModel registration(VendorRegisterModel model);
}
