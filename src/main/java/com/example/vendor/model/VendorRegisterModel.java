package com.example.vendor.model;


import com.example.commanentity.CompanyAddress;
import com.example.commanentity.Vendor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VendorRegisterModel {
    private String firstName;
    private String lastName;
    private String companyName;
    private String emailId;
    private String password;
    private String phoneno;
    private String address1;
    private String address2;
    private String country;
    private String state;
    private String pincode;
    private MultipartFile profileURL;

    public Vendor getVendorFromModel() {
        Vendor v = new Vendor();
        v.setFirst_name(firstName);
        v.setLast_name(lastName);
        v.setCompany_name(companyName);
        v.setEmailid(emailId);
        v.setPhoneno(phoneno);
        v.setPassword(password);
        return v;
    }

    public CompanyAddress getCompanyAddressFromModel() {
        CompanyAddress ca = new CompanyAddress();
        ca.setAddress1(address1);
        ca.setAddress2(address2);
        ca.setPincode(pincode);
        return ca;
    }
}

