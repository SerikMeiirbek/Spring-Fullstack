package com.serikscode.unitTest.service;

import com.serikscode.customer.Customer;
import com.serikscode.customer.CustomerRegistrationRequest;
import com.serikscode.customer.Gender;
import com.serikscode.dto.CustomerDTO;
import com.serikscode.exception.DuplicateResourseException;
import com.serikscode.exception.RequestValidationException;
import com.serikscode.exception.ResourceNotFoundException;
import com.serikscode.repository.CustomerDao;
import com.serikscode.s3.S3Buckets;
import com.serikscode.s3.S3Service;
import com.serikscode.service.CustomerService;
import com.serikscode.utills.CustomerDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
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
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;

    private CustomerService underTest;

    private CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao,  customerDTOMapper, passwordEncoder, s3Service, s3Buckets);
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

    @Test
    void canUploadProfileImage(){
        //Given
        int customerId = 10;
        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", bytes);
        String bucket = "customer-bucket";

        when(s3Buckets.getCustomer()).thenReturn(bucket);
        //When
        underTest.uploadCustomerProfileImage(
                customerId,
                multipartFile
        );

        //Then
        ArgumentCaptor<String> profileImageIdArgumentCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(customerDao).updateCustomerProfileImageId(
                profileImageIdArgumentCaptor.capture(),
                eq(customerId)
        );

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageIdArgumentCaptor.getValue()),
                bytes
        );
    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExists(){
        //Given
        int customerId = 10;
        when(customerDao.existsPersonWithId(customerId)).thenReturn(false);

        //When
        assertThatThrownBy(() -> {
            underTest.uploadCustomerProfileImage(customerId, mock(MultipartFile.class));
        }).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id "+customerId+" not found");

        //Then
        verify(customerDao).existsPersonWithId(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);


    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        //Given
        int customerId = 10;
        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        //When
        assertThatThrownBy(() -> {
            underTest.uploadCustomerProfileImage(customerId, multipartFile);
        }).isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("failed to upload profile image");

        //Then
        verify(customerDao,never()).updateCustomerProfileImageId(any(), any());
    }


    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 10;
        String profileImageId = "2222";
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password",
                19,
                Gender.MALE,
                profileImageId
        );
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void cannotDownloadWhenNoProfileImageId() {
        // Given
        int customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password",
                19,
                Gender.MALE
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile image not found".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }
}