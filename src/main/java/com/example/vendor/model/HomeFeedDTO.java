package com.example.vendor.model;

import com.example.commanentity.Product;
import lombok.Data;

@Data
public class HomeFeedDTO {
    private String productid;
    private String productName;
    private String desc;
    private String price;
    private String category;

    public HomeFeedDTO(Product product) {
        this.productid = product.getId();
        this.productName = product.getName();
        this.desc = product.getDescription();
        this.price = product.getPrice().toString();
        this.category = product.getCategory().getName();
    }
}
