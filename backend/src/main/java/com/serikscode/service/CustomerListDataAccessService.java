package com.serikscode.service;

import com.serikscode.customer.Customer;
import com.serikscode.customer.Gender;
import com.serikscode.repository.CustomerDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    private static List<Customer> customers;

    static {

        //db
        customers = new ArrayList<>();
        Customer alex = new Customer(1, "Alex", "alex@gmail.com", 21, Gender.MALE);
        Customer jamila = new Customer(2, "Jamila", "jamila@gmail.com", 22, Gender.FEMALE);
        customers.add(alex);
        customers.add(jamila);

    }


    @Override
    public List<Customer> selectAllCustomer() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {

        return customers
                .stream()
                .filter(customer -> customer.getId() == id)
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers
                .stream()
                .anyMatch(customer -> customer.equals(email));
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.contains(id);
    }

    @Override
    public void deleteCustomer(Integer id) {
        customers.remove(id);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        customers.add(updatedCustomer);
    }
}
