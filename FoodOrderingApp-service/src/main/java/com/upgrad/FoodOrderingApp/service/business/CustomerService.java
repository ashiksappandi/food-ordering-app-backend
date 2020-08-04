package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.common.AppConstants;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import javax.validation.*;

import java.util.Set;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.*;

@Service
public class CustomerService {

    @Autowired
    CustomerDao customerDao;

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
            String[] encryptedText = passwordCryptographyProvider.encrypt(newCustomer.getPassword());
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
