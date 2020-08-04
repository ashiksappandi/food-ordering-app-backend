package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.common.AppConstants;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.*;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity newCustomer) throws SignUpRestrictedException{
        try {
            System.out.println(newCustomer.toString());
            if(!validateMandatoryFields(newCustomer)){
                throw new SignUpRestrictedException(SGR_005.getCode(), SGR_005.getDefaultMessage());
            }
            if(!isValidEmail(newCustomer.getEmail())){
                throw new SignUpRestrictedException(SGR_002.getCode(), SGR_002.getDefaultMessage());
            }
            if(!isValidContactNumber(newCustomer.getContactNumber())){
                throw new SignUpRestrictedException(SGR_003.getCode(), SGR_003.getDefaultMessage());
            }
            if(!isStrongPassword(newCustomer.getPassword())){
                throw new SignUpRestrictedException(SGR_004.getCode(), SGR_004.getDefaultMessage());
            }
            final String[] encryptedText = passwordCryptographyProvider.encrypt(newCustomer.getPassword());
            newCustomer.setSalt(encryptedText[0]);
            newCustomer.setPassword(encryptedText[1]);
            return customerDao.saveCustomer(newCustomer);
        }catch (DataIntegrityViolationException dataIntegrityViolationException) {
            if (dataIntegrityViolationException.getCause() instanceof ConstraintViolationException) {
                String constraintName = ((ConstraintViolationException) dataIntegrityViolationException.getCause()).getConstraintName();
                if (StringUtils.containsIgnoreCase(constraintName, "customer_contact_number_key")) {
                    throw new SignUpRestrictedException(SGR_001.getCode(), SGR_001.getDefaultMessage());
                } else {
                    throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
                }
            } else {
                throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
            }
        }
        catch (Exception exception){
            throw new UnexpectedException(GEN_001, exception);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNumber, final String password) throws AuthenticationFailedException {
        final CustomerEntity customerEntity = getCustomerByContact(contactNumber);
        if(customerEntity == null){
            throw new AuthenticationFailedException(ATH_001.getCode(),ATH_001.getDefaultMessage());
        }
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if(encryptedPassword!=null && encryptedPassword.equals(customerEntity.getPassword())){
            final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            final CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);
            customerAuthEntity.setUuid(customerEntity.getUuid());
            final ZonedDateTime loginAt = ZonedDateTime.now();
            final ZonedDateTime expiresAt = loginAt.plusHours(AppConstants.EIGHT_8);
            customerAuthEntity.setLoginAt(loginAt.toLocalDateTime());
            customerAuthEntity.setExpiresAt(expiresAt.toLocalDateTime());
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), loginAt, expiresAt));
            return customerDao.saveAuthentication(customerAuthEntity);

        }
        else{
            throw new AuthenticationFailedException(ATH_002.getCode(),ATH_002.getDefaultMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = getCustomerByAccessToken(accessToken);
        if(customerAuthEntity != null){
            if(customerAuthEntity.getExpiresAt().isBefore(LocalDateTime.now())){
                throw new AuthorizationFailedException(ATHR_003.getCode(),ATHR_003.getDefaultMessage());
            }
            else{
                if(customerAuthEntity.getLogoutAt() != null){
                    System.out.println(customerAuthEntity.getLogoutAt());
                    throw new AuthorizationFailedException(ATHR_002.getCode(),ATHR_002.getDefaultMessage());
                }
                else{
                    customerAuthEntity.setLogoutAt(LocalDateTime.now());
                    return customerDao.saveAuthentication(customerAuthEntity);
                }
            }
        }
        else{
            throw new AuthorizationFailedException(ATHR_001.getCode(),ATHR_001.getDefaultMessage());
        }
    }

    private CustomerEntity getCustomerByContact(final String contactNumber){
        return customerDao.getCustomerByContact(contactNumber);
    }

    private CustomerAuthEntity getCustomerByAccessToken(final String accessToken){
        return customerDao.getCustomerByAccessToken(accessToken);
    }

    private boolean validateMandatoryFields(final CustomerEntity customer){
        return (customer.getContactNumber() != null) &&
                (customer.getFirstName() != null) &&
                        (customer.getEmail() != null) &&
                                (customer.getPassword() != null);
    }

    private boolean validatePassword(final CustomerEntity customer){
        return true;
    }

    // This method users regular expressions to guage the strength of a user's
    // password returns password score
    private boolean isStrongPassword(final String password) {
        return password.matches(AppConstants.REG_EXP_PASSWD_UPPER_CASE_CHAR) && password.matches(AppConstants.REG_EXP_PASSWD_SPECIAL_CHAR) && password.matches(AppConstants.REG_EXP_PASSWD_DIGIT) && (password.length() > AppConstants.NUMBER_7);
    }

    private boolean isValidContactNumber(final String contactNumber){
        return StringUtils.isNumeric(contactNumber) && (contactNumber.length() == AppConstants.NUMBER_10);
    }

    private boolean isValidEmail(final String email){
        return email.matches(AppConstants.REG_EXP_VALID_EMAIL);
    }
}
