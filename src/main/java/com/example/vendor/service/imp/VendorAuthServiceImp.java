package com.example.vendor.service.imp;


import com.example.commanentity.Company_Address;
import com.example.commanentity.Vendor;
import com.example.vendor.auth.JwtTokenUtil;
import com.example.vendor.model.LoginModel;
import com.example.vendor.model.ResponseModel;
import com.example.vendor.model.VendorRegisterModel;
import com.example.vendor.repository.ComapanyAddressRepository;
import com.example.vendor.repository.CountryRepository;
import com.example.vendor.repository.StateRepository;
import com.example.vendor.repository.VendorRepository;
import com.example.vendor.service.VendorAuthService;
import com.example.vendor.util.CommanUtil;
import com.example.vendor.util.FileUpload;
import com.example.vendor.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class VendorAuthServiceImp implements VendorAuthService {

    private static final Logger logger = LoggerFactory.getLogger(VendorAuthServiceImp.class);

    @Value("${const.path}")
    private String folder;
    @Autowired
    private CommanUtil commanUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private ComapanyAddressRepository comapanyAddressRepository;

    @Override
    public ResponseModel login(LoginModel loginModel) {
        ResponseModel responseModel;

        try {
            //Load the userdetail of the user where emailID = "Example@gmail.com"
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginModel.getEmailId(),
                            loginModel.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Vendor vendor = vendorRepository.findByEmailid(loginModel.getEmailId());
            final String token = jwtTokenUtil.generateToken(authentication);

            //set the Bearer token to AdminUser data
//            vendor.setSession_token(token);
            //update the adminuser to database with token And ExpirationDate
            vendorRepository.save(vendor);

            responseModel = commanUtil.create(Message.LOGIN_SUCCESS,
                    vendor,
                    HttpStatus.OK);

        } catch (BadCredentialsException ex) {
            System.out.println(loginModel);
            logger.info("vendor login BadCredentialsException ================== {}", ex.getMessage());
            responseModel = commanUtil.create(Message.LOGIN_ERROR,
                    null,
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.info("Customer Login Exception ================== {}", e.getMessage());
            responseModel = commanUtil.create(Message.DELETE_STATUS_ERROR,
                    null,
                    HttpStatus.BAD_REQUEST);
        }
        return responseModel;
    }


    @Override
    public ResponseModel registration(VendorRegisterModel model) {
        int exist = vendorRepository.countByEmailid(model.getEmailId());
        if (exist == 0) {
            try {
//            get the Customer Object From DTO
                Vendor vendor = model.getVendorFromModel();
                vendor.setPassword(passwordEncoder.encode(model.getPassword()));
                String str = null;

//            Upload the image
                if (model.getProfileURL() != null)
                    str = new FileUpload().saveFile(folder, model.getProfileURL(), vendor.getId());

                vendor.setProfile_url(str);


                vendorRepository.save(vendor);
                insertAddress(vendor, model);

                return commanUtil.create(Message.CUSTOMER_REGISTER, null, HttpStatus.OK);

            } catch (Exception e) {
                logger.error("Error Will Registration");
                e.printStackTrace();
                return commanUtil.create(Message.SOMTHING_WRONG, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return commanUtil.create(Message.EMAIL_EXIST, null, HttpStatus.BAD_REQUEST);
        }
    }

    private void insertAddress(Vendor vendor, VendorRegisterModel model) {

//            Get CustomerAddress object From DTO
        Company_Address companyAddress = model.getCompanyAddressFromModel();
        companyAddress.setVendor(vendor);
//            Fill the Country and State
//                Country country = countryRepository.findByName(model.getCountry());
//                if (country != null)
//                    companyAddress.setCountry(country);
//                State state = stateRepository.findByName(model.getState());
//                if (state != null)
//                    companyAddress.setState(state);

        comapanyAddressRepository.save(companyAddress);
    }
}
