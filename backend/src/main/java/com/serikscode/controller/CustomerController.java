package com.serikscode.controller;

import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.dto.CustomerDTO;
import com.serikscode.jwt.JWTUtil;
import com.serikscode.service.CustomerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public List<CustomerDTO> getCustomers(){
        return customerService.getAllCustomer();
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getCustomer(@PathVariable("customerId") Integer customerId){
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

    @PostMapping(
            value = "/{customerId}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadCustomerProfilePicture(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("file") MultipartFile file){
        customerService.uploadCustomerProfileImage(customerId, file);
    }

    @GetMapping(
            value = "{customerId}/profile-image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getCustomerProfileImage(
            @PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerProfileImage(customerId);
    }
}
