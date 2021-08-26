package com.example.vendor.model;

import com.example.commanentity.CompanyAddress;
import com.example.commanentity.Vendor;
import lombok.Data;

@Data
public class VendorUpdateModel {
    private String firstName;
    private String lastName;
    private String companyName;
    private String phoneno;
    private String address1;
    private String address2;
    private String country;
    private String state;
    private String pincode;

    public Vendor getUpdatedvendorFromModel(Vendor v) {
        v.setFirst_name(firstName);
        v.setLast_name(lastName);
        v.setPhoneno(phoneno);
        v.setCompany_name(companyName);
        return v;
    }

    public CompanyAddress getUpdatedCompanyAddressFromModel(CompanyAddress c) {
        c.setAddress1(address1);
        c.setAddress2(address2);
        c.setPincode(pincode);
        return c;
    }
}
