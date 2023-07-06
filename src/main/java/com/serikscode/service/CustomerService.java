package com.serikscode.service;

import com.serikscode.customer.Customer;
import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.exception.DuplicateResourseException;
import com.serikscode.exception.RequestValidationException;
import com.serikscode.exception.ResourceNotFoundException;
import com.serikscode.repository.CustomerDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomer(){
        return  customerDao.selectAllCustomer();
    }

    public Customer getCustomerById(Integer id){
        return customerDao.selectCustomerById(id).orElseThrow(()-> new ResourceNotFoundException("customer id %s not found".formatted(id)));
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
                        customerRegistrationRequest.age()
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
        Customer customer = getCustomerById(id);
        boolean changes = false;

        if(customerRegistrationRequest.name() != null && customerRegistrationRequest.email() != null && customerRegistrationRequest.age() != null){
            customer.setName(customerRegistrationRequest.name());
            customer.setEmail(customerRegistrationRequest.email());
            customer.setAge(customerRegistrationRequest.age());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}
