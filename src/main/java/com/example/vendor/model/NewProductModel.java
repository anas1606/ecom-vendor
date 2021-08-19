package com.example.vendor.model;

import com.example.commanentity.Product;
import lombok.Data;

@Data
public class NewProductModel {
    private String productName;
    private String desc;
    private String price;
    private String category;

    public Product getProductFromModel() {
        Product p = new Product();
        p.setName(productName);
        p.setDescription(desc);
        p.setPrice(Double.valueOf(price));
        return p;
    }
}
