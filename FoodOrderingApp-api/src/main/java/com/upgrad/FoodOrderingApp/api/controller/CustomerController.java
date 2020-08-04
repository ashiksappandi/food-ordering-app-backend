package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

//@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {
    // TODO :
    //  - login - "/customer/login"
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
}
