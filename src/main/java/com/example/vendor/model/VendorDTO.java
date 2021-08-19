package com.example.vendor.model;

import com.example.commanentity.Company_Address;
import lombok.Data;

@Data
public class VendorDTO {
    private String firstName;
    private String lastName;
    private String emailid;
    private String phoneno;
    private String compnayName;
    private String profile_url;
    private String address1;
    private String address2;
    private String country;
    private String state;
    private int pincode;

    public VendorDTO(Company_Address ca) {
        this.firstName = ca.getVendor().getFirst_name();
        this.lastName = ca.getVendor().getLast_name();
        this.emailid = ca.getVendor().getEmailid();
        this.phoneno = ca.getVendor().getPhoneno();
        this.compnayName = ca.getVendor().getCompany_name();
        this.profile_url = ca.getVendor().getProfile_url();
        this.address1 = ca.getAddress1();
        this.address2 = ca.getAddress2();
        this.country = ca.getCountry().getName();
        this.state = ca.getState().getName();
        this.pincode = ca.getPincode();
    }
}
