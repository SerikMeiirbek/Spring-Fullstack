package com.serikscode.service;

import com.serikscode.customer.Customer;
import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.dto.CustomerDTO;
import com.serikscode.exception.DuplicateResourseException;
import com.serikscode.exception.RequestValidationException;
import com.serikscode.exception.ResourceNotFoundException;
import com.serikscode.repository.CustomerDao;
import com.serikscode.utills.CustomerDTOMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;


    public CustomerService(@Qualifier("jpa") CustomerDao customerDao, CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomer(){
        return  customerDao.selectAllCustomer()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer id){
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(()->
                new ResourceNotFoundException("customer id %s not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){

        // check if email exists
        if(customerDao.existsPersonWithEmail(customerRegistrationRequest.email())){
            throw new DuplicateResourseException("customer with  %s email is already registered".formatted(customerRegistrationRequest.email()));
        }

        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        passwordEncoder.encode(customerRegistrationRequest.password()),
                        customerRegistrationRequest.age(),
                        customerRegistrationRequest.gender()
                )
        );


    }

    public void deleteCustomerById(Integer id){

        if(!customerDao.existsPersonWithId(id)){
            throw new ResourceNotFoundException(
                    "customer with id %s not found".formatted(id)
            );
        }
        customerDao.deleteCustomer(id);
    }

    public void updateCustomer(Integer id, CustomerRegistrationRequest customerRegistrationRequest) {
        // TODO: for JPA use .getReferenceById(customerId) as it does does not bring object into memory and instead a reference
        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));

        boolean changes = false;

        if (customerRegistrationRequest.name() != null && !customerRegistrationRequest.name().equals(customer.getName())) {
            customer.setName(customerRegistrationRequest.name());
            changes = true;
        }

        if (customerRegistrationRequest.age() != null && !customerRegistrationRequest.age().equals(customer.getAge())) {
            customer.setAge(customerRegistrationRequest.age());
            changes = true;
        }

        if (customerRegistrationRequest.email() != null && !customerRegistrationRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
                throw new DuplicateResourseException(
                        "email already taken"
                );
            }
            customer.setEmail(customerRegistrationRequest.email());
            changes = true;
        }
        if (customerRegistrationRequest.gender() != null && !customerRegistrationRequest.gender().equals(customer.getGender())) {
            customer.setGender(customerRegistrationRequest.gender());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}
