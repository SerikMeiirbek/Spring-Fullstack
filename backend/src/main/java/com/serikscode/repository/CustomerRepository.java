package com.serikscode.repository;

import com.serikscode.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Integer id);
}
