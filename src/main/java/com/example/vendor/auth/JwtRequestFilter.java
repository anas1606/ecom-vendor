package com.example.vendor.auth;


import com.example.vendor.repository.VendorRepository;
import com.example.vendor.service.JwtUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    private static final Logger log = Logger.getLogger(JwtRequestFilter.class.getName());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private JwtUserDetailService jwtUserDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.info("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.info("JWT Token has expired");
            }
        } else {
            log.warning("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            userDetails = this.jwtUserDetailService.loadUserByUsername(username);
            //Firstly check If user Loging at another system then this token is Expired
            if (vendorRepository.countBySessionTokenAndStatus(jwtToken) == 0)
                log.info("JWT Token has expired Bcoz Login At Another System Or you are Deactivated");
            else {
                // if token is valid configure Spring Security to manually set
                // authentication
                boolean isValidToken = jwtTokenUtil.validateToken(jwtToken, userDetails);
                if (isValidToken) {

                    UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(jwtToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
