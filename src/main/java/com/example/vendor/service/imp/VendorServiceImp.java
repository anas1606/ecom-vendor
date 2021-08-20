package com.example.vendor.service.imp;


import com.example.commanentity.*;
import com.example.commanentity.enums.Status;
import com.example.vendor.auth.JwtTokenUtil;
import com.example.vendor.model.*;
import com.example.vendor.repository.*;
import com.example.vendor.service.VendorService;
import com.example.vendor.util.CommanUtil;
import com.example.vendor.util.FileUpload;
import com.example.vendor.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VendorServiceImp implements VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorServiceImp.class);

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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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
            vendor.setSession_token(token);
            //update the adminuser to database with token And ExpirationDate
            vendorRepository.save(vendor);

            responseModel = commanUtil.create(Message.LOGIN_SUCCESS,
                    vendor.getSession_token(),
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
                vendor.setEmail_verification_otp(commanUtil.genrateRandomOTP());

                vendorRepository.save(vendor);
                insertAddress(vendor, model);
                commanUtil.sendVerificationEmail(vendor.getEmailid(), vendor.getEmail_verification_otp());

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

    @Override
    public ResponseModel verifyVendor(VerificationModel model) {
        Vendor vendor = vendorRepository.findById(model.getCustomerid()).orElse(null);
        if (vendor != null) {
//            Check For OTP MATCH
            if (vendor.getEmail_verification_otp().equals(model.getOtp())) {
                vendor.setEmailverified(true);
                vendor.setStatus(Status.ACTIVE.getStatus());
                vendorRepository.save(vendor);
                logger.info("Vendor Verified");
                return commanUtil.create(Message.VERIFYED, null, HttpStatus.OK);
            } else {
                return commanUtil.create(Message.OTP_MISSMATCH, null, HttpStatus.BAD_REQUEST);
            }
        } else {
            return commanUtil.create(Message.VENDOR_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseModel uploadProfile(MultipartFile file) {
        //             check if user exist with email or username
        Vendor vendor = vendorRepository.findByEmailid(commanUtil.getCurrentUserEmail());
        if (vendor != null) {
            try {
                String str = null;

//            Upload the image
                if (file != null)
                    str = new FileUpload().saveFile(folder, file, vendor.getId());
                vendor.setProfile_url(str);

                return commanUtil.create(Message.PROFILE_UPLODED, null, HttpStatus.OK);

            } catch (Exception e) {
                logger.error("Error Will upload Profile");
                e.printStackTrace();
                return commanUtil.create(Message.SOMTHING_WRONG, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            return commanUtil.create(Message.EMAIL_EXIST, null, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseModel viewProfile() {
        Vendor vendor = vendorRepository.findByEmailid(commanUtil.getCurrentUserEmail());
        if (vendor != null) {
            Company_Address companyAddress = comapanyAddressRepository.findByVendor_Id(vendor.getId());
            VendorDTO dto = new VendorDTO(companyAddress);

            return commanUtil.create(Message.SUCCESS, dto, HttpStatus.OK);
        } else {
            return commanUtil.create(Message.VENDOR_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseModel updateVendor(VendorUpdateModel model) {
        Vendor vendor = vendorRepository.findByEmailid(commanUtil.getCurrentUserEmail());
        if (vendor != null) {

//            Update the vendor info And Save
            vendor = model.getUpdatedvendorFromModel(vendor);
            vendorRepository.save(vendor);

//            Update the Customer Address
            UpdateAddress(vendor, model);

            return commanUtil.create(Message.UPDATED, null, HttpStatus.OK);

        } else {
            return commanUtil.create(Message.VENDOR_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseModel addProduct(NewProductModel model) {
        Vendor vendor = vendorRepository.findByEmailid(commanUtil.getCurrentUserEmail());
        if (vendor != null) {
            if (productRepository.countByNameAndCategory_NameAndVendor_Id(model.getProductName(), model.getCategory(), vendor.getId()) == 0) {
                Product product = model.getProductFromModel();
                product.setVendor(vendor);
                Category category = categoryRepository.findByName(model.getCategory());
                product.setCategory(category);

                productRepository.save(product);
                logger.info("New Product Added");

                return commanUtil.create(Message.NEW_PRODUCT_ADDED, null, HttpStatus.OK);
            } else {
                return commanUtil.create(Message.PRODUCT_EXIST, null, HttpStatus.BAD_REQUEST);
            }
        } else {
            return commanUtil.create(Message.VENDOR_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public PageResponseModel myProdustList(PageDetailModel model) {
        try {
            Vendor vendor = vendorRepository.findByEmailid(commanUtil.getCurrentUserEmail());
            if (vendor != null) {
                model = commanUtil.fillValueToPageModel(model);
                Pageable page = commanUtil.getPageDetail(model);
                Page<HomeFeedDTO> dto;
                if (commanUtil.checkNull(model.getCategory()))
                    dto = productRepository.findAllPagable(vendor.getId(), page);
                else
                    dto = productRepository.findAllByCategoryPagable(vendor.getId(), model.getCategory(), page);

                return commanUtil.create(Message.SUCCESS, dto.getContent(), commanUtil.pagersultModel(dto), HttpStatus.OK);
            } else {
                return commanUtil.create(Message.VENDOR_NOT_FOUND, null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Exception Will getting HomeFeed");
            return commanUtil.create(Message.SOMTHING_WRONG, null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //    ********************************* Private Function ***************************
    private void insertAddress(Vendor vendor, VendorRegisterModel model) {

//            Get CustomerAddress object From DTO
        Company_Address companyAddress = model.getCompanyAddressFromModel();
        companyAddress.setVendor(vendor);
//        insert Country And State
        addCountryAndState(companyAddress, model.getCountry(), model.getState());
    }

    private void UpdateAddress(Vendor vendor, VendorUpdateModel model) {
        logger.info("Updating Address");
//        Get CustomerAddress object From DTO
        Company_Address companyAddress = comapanyAddressRepository.findByVendor_Id(vendor.getId());
        companyAddress = model.getUpdatedCompanyAddressFromModel(companyAddress);
        companyAddress.setVendor(vendor);
//        insert Country And State
        addCountryAndState(companyAddress, model.getCountry(), model.getState());
    }

    private void addCountryAndState(Company_Address companyAddress, String countryName, String stateName) {
//        Fill the Country and State
        Country country = countryRepository.findByName(countryName);
        if (country != null)
            companyAddress.setCountry(country);
        State state = stateRepository.findByName(stateName);
        if (state != null)
            companyAddress.setState(state);

        comapanyAddressRepository.save(companyAddress);
    }
}
