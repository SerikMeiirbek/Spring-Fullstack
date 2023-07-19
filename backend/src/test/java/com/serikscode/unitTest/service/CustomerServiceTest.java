package com.serikscode.unitTest.service;

import com.serikscode.customer.Customer;
import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.customer.Gender;
import com.serikscode.dto.CustomerDTO;
import com.serikscode.exception.DuplicateResourseException;
import com.serikscode.exception.RequestValidationException;
import com.serikscode.exception.ResourceNotFoundException;
import com.serikscode.repository.CustomerDao;
import com.serikscode.service.CustomerService;
import com.serikscode.utills.CustomerDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    private CustomerService underTest;

    private CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao,  customerDTOMapper, passwordEncoder);
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
                "password", 10,
                Gender.MALE);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        //When
        CustomerDTO actual = underTest.getCustomerById(id);

        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        //Given
        int id = 10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());


        //Then
        assertThatThrownBy(()->underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id %s not found".formatted(id));
    }

    @Test
    void addCustomer() {

        //Given
        String email = "alex123@gmail.com";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                "password",
                19,
                Gender.MALE);

        String passwordHash = "24234234";
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

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
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {

        //Given
        String email = "alex123@gmail.com";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                "password", 19,
                Gender.MALE);

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
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                "ALexsandro",
                newEmail,
                "password", 23,
                Gender.MALE);
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
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                "ALexandro",
                null,
                "password", null,
                Gender.MALE);


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
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                null,
                newEmail,
                "password", null,
                Gender.MALE);
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
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                null,
                null,
                "password", 23,
                Gender.MALE);

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
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                null,
                newEmail,
                "password", null,
                Gender.MALE);
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
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "aLexsandro@gmail.com";
        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                customer.getName(),
                customer.getEmail(),
                "password", customer.getAge(),
                Gender.MALE);


        //When
        assertThatThrownBy(()->underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("no data changes found");

        //Then
        verify(customerDao,never()).updateCustomer(any());
    }
}