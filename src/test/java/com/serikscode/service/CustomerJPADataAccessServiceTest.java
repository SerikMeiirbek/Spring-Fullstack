package com.serikscode.service;

import com.serikscode.customer.Customer;
import com.serikscode.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomer() {

        //When
        underTest.selectAllCustomer();

        //Then
        verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {

        //Given
        int id = 1;

        //When
        underTest.selectCustomerById(id);

        //Then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer(
                "Sam",
                "same@gmail.com",
                23
        );

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);

    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = "abc@gmail.com";

        //When
        underTest.existsPersonWithEmail(email);

        //Then
        verify(customerRepository).existsCustomerByEmail(email);

    }

    @Test
    void existsPersonWithId() {
        //Given
        int id = 1;

        //When
        underTest.existsPersonWithId(id);

        //Then
        verify(customerRepository).existsCustomerById(id);

    }

    @Test
    void deleteCustomer() {

        //Given
        int id = 1;

        //When
        underTest.deleteCustomer(id);

        //Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {

        //Given
        Customer customer = new Customer(
                "Sam",
                "same@gmail.com",
                23
        );

        //When
        underTest.updateCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }
}