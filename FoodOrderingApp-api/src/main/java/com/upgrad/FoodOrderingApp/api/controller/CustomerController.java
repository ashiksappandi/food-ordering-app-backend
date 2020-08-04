package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.AppConstants;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.ATH_003;
import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.GEN_001;

//@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {
    // TODO :
    //  - logout - "/customer/logout"
    //  - Update - “/customer”
    //  - Change Password - “/customer/password”
    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignupCustomerResponse> registerCustomer(@RequestBody(required = false) final SignupCustomerRequest request) throws SignUpRestrictedException, UnexpectedException {
        final CustomerEntity newCustomer = new CustomerEntity();
        newCustomer.setUuid(UUID.randomUUID().toString());
        newCustomer.setFirstName(request.getFirstName());
        newCustomer.setLastName(request.getLastName());
        newCustomer.setEmail(request.getEmailAddress());
        newCustomer.setPassword(request.getPassword());
        newCustomer.setContactNumber(request.getContactNumber());
        newCustomer.setSalt(UUID.randomUUID().toString());
        final CustomerEntity customer = customerService.saveCustomer(newCustomer);
        final SignupCustomerResponse response = new SignupCustomerResponse();
        response.id(customer.getUuid()).status("CUSTOMER CREATED SUCCESSFULLY");
        return new ResponseEntity<SignupCustomerResponse>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginCustomer(@RequestHeader("authorization") final String headerParam) throws UnexpectedException, AuthenticationFailedException {
        String authToken = StringUtils.substringAfter(headerParam,"Basic ");
        if(authToken == null || authToken.isEmpty()){
            throw new AuthenticationFailedException(ATH_003.getCode(),ATH_003.getDefaultMessage());
        }
        StringTokenizer tokens =  new StringTokenizer(new String (Base64.getDecoder().decode(authToken)), AppConstants.COLON);
        final CustomerAuthEntity customerAuth = customerService.authenticate(tokens.nextToken(),tokens.nextToken());
        final LoginResponse response = new LoginResponse();
        response.id(customerAuth.getCustomer().getUuid()).firstName(customerAuth.getCustomer().getFirstName()).lastName(customerAuth.getCustomer().getLastName()).contactNumber(customerAuth.getCustomer().getContactNumber()).emailAddress(customerAuth.getCustomer().getEmail()).message("LOGGED IN SUCCESSFULLY");
        return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
    }
}
