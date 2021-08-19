package com.example.vendor.util;


import com.example.vendor.model.PageDetailModel;
import com.example.vendor.model.PageResponseModel;
import com.example.vendor.model.PageResultModel;
import com.example.vendor.model.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CommanUtil {

    @Autowired
    private JavaMailSender javaMailSender;


    public ResponseModel create(String message, Object data, HttpStatus status) {
        ResponseModel rs = new ResponseModel();
        rs.setData(data);
        rs.setMessage(message);
        rs.setStatus(status);
        rs.setStatusCode(status.value());
        return rs;
    }

    public PageResponseModel create(String message, Object data, Object page, HttpStatus status) {
        PageResponseModel rs = new PageResponseModel();
        rs.setData(data);
        rs.setMessage(message);
        rs.setStatus(status);
        rs.setResult(page);
        rs.setStatusCode(status.value());
        return rs;
    }

    public String genrateRandomOTP() {
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public void sendVerificationEmail(String email, String otp) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject("Email Verification");
            mail.setText("Your Verification OTP is : " + otp);
            javaMailSender.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Pageable getPageDetail(PageDetailModel pageDetailModel) {
        /*creating page detail
                 page : PageNo: 0 , Limit: 20,
                                             Sort : Sort.Direction.ASC ,
                                              property :"CreatedAt" {sort on BaseOf})*/
        return PageRequest.of(pageDetailModel.getPage(),
                pageDetailModel.getLimit(),
                (pageDetailModel.getSortorder().equalsIgnoreCase("ASC")) ? Sort.Direction.ASC : Sort.Direction.DESC,
                "price"
        );
    }

    public PageDetailModel fillValueToPageModel(PageDetailModel pageDetailModel) {
        if (checkNull(pageDetailModel.getPage())) {
            pageDetailModel.setPage(0);
        }
        if (checkNull(pageDetailModel.getLimit())) {
            pageDetailModel.setLimit(20);
        }
        if (checkNull(pageDetailModel.getSortorder())) {
            pageDetailModel.setSortorder("ASC");
        }

        return pageDetailModel;
    }

    public PageResultModel pagersultModel(Page page) {
        PageResultModel model = new PageResultModel();
        model.setTotalCount(page.getTotalElements());
        model.setPageno(page.getNumber());
        model.setPagecount(page.getTotalPages());

        return model;
    }

    public boolean checkNull(String str) {
        return str == null || str.equals("");
    }

    private boolean checkNull(Integer no) {
        return no == null;
    }
}

