package com.example.vendor.service;


import com.example.commanentity.Customer;
import com.example.commanentity.Vendor;
import com.example.vendor.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtUserDetailService implements UserDetailsService {


    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Vendor user = vendorRepository.findByEmailid(username);
        if (user == null) {
            throw new UsernameNotFoundException("Customer not found with username: " + username);
        }
        return new User(user.getEmailid(), user.getPassword(),
                getAuthority());
    }


    private Set<SimpleGrantedAuthority> getAuthority() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_VENDOR"));
        return authorities;
    }

}

