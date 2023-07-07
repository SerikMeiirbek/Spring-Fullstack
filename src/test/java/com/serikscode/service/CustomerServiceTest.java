package com.serikscode.service;

import com.serikscode.customer.Customer;
import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.exception.DuplicateResourseException;
import com.serikscode.exception.RequestValidationException;
import com.serikscode.exception.ResourceNotFoundException;
import com.serikscode.repository.CustomerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomer() {

        //When
        underTest.getAllCustomer();

        //Then
        verify(customerDao).selectAllCustomer();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;

        Customer customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                10
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getCustomerById(id);

        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        //Given
        int id = 10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());


        //Then
        assertThatThrownBy(()->underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer id %s not found".formatted(id));
    }

    @Test
    void addCustomer() {

        //Given
        String email = "alex123@gmail.com";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                19
        );

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {

        //Given
        String email = "alex123@gmail.com";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                19
        );

        //When
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        //Then
        assertThatThrownBy(
                () -> underTest.addCustomer(request)
        ).isInstanceOf(DuplicateResourseException.class)
                .hasMessageContaining("customer with  %s email is already registered".formatted(request.email()));

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {

        //Given
        int id = 12;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomer(id);

    }

    @Test
    void willThrowDeleteCustomerByIdResourceNotFoundException() {

        //Given
        int id = 12;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(
                () -> underTest.deleteCustomerById(id)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id %s not found".formatted(id));

        //Then
        verify(customerDao,never()).deleteCustomer(any());

    }

    @Test
    void updateCustomer() {
        //Given
        Integer id = 1;
        String email = "alex123@gmail.com";

        Customer customer = new Customer(
                id,
                "Alex",
                email,
                19
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                "ALexsandro",
                newEmail,
                23
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
    }

    @Test
    void canUpdateOnlyName() {
        //Given
        Integer id = 1;
        String email = "alex123@gmail.com";

        Customer customer = new Customer(
                id,
                "Alex",
                email,
                19
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                "ALexandro",
                null,
                null
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateOnlyEmail() {
        //Given
        Integer id = 1;
        String email = "alex123@gmail.com";

        Customer customer = new Customer(
                id,
                "Alex",
                email,
                19
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                null,
                newEmail,
                null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
    }

    @Test
    void canUpdateOnlyAge() {
        //Given
        Integer id = 1;
        String email = "alex123@gmail.com";

        Customer customer = new Customer(
                id,
                "Alex",
                email,
                19
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                null,
                null,
                23
        );

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        //Given
        Integer id = 1;
        String email = "alex123@gmail.com";

        Customer customer = new Customer(
                id,
                "Alex",
                email,
                19
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                null,
                newEmail,
                null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        //When
        assertThatThrownBy(()->underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourseException.class)
                .hasMessageContaining("email already taken");

        //Then
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        //Given
        Integer id = 1;
        String email = "alex123@gmail.com";

        Customer customer = new Customer(
                id,
                "Alex",
                email,
                19
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );


        //When
        assertThatThrownBy(()->underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("no data changes found");

        //Then
        verify(customerDao,never()).updateCustomer(any());
    }
}