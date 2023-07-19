package com.serikscode.controller;

import com.serikscode.customer.Customer;
import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.jwt.JWTUtil;
import com.serikscode.service.CustomerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {


    private final CustomerService customerService;
    private final JWTUtil jwtUtil;


    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    //    @RequestMapping(path = "api/v1/customer", method = RequestMethod.GET)
    @GetMapping
    public List<Customer> getCustomers(){
        return customerService.getAllCustomer();
    }

    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable("customerId") Integer customerId){
        return customerService.getCustomerById(customerId);
    }


    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        customerService.addCustomer(customerRegistrationRequest);
        String jwtToken = jwtUtil.issueToken(customerRegistrationRequest.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id){
        customerService.deleteCustomerById(id);
    }

    @PutMapping("/{customerId}")
    public void updateCustomer(
                               @PathVariable("customerId") Integer id,
                               @RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        customerService.updateCustomer(id, customerRegistrationRequest);
    }
}
