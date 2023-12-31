package com.serikscode.unitTest.repository;

import com.serikscode.AbstractTestContainerUnitTest;
import com.serikscode.customer.Customer;
import com.serikscode.customer.Gender;
import com.serikscode.service.CustomerJDBCDataAccessService;
import com.serikscode.utills.CustomerRowMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractTestContainerUnitTest {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomer() {
        //Given
        Customer customer = new Customer(
                FAKER.name().firstName(),
                FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID(),
                "password", 20,
                Gender.MALE
        );

        //When
        underTest.insertCustomer(customer);

        List<Customer> customers = underTest.selectAllCustomer();

        //Then
        Assertions.assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().firstName(),
                email,
                "password", 20,
                Gender.MALE);

        //When
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        Assertions.assertThat(actual).isPresent().hasValueSatisfying(c -> {
            Assertions.assertThat(c.getId()).isEqualTo(id);
            Assertions.assertThat(c.getName()).isEqualTo(customer.getName());
            Assertions.assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            Assertions.assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById(){
        //Given
        int id = -1;
        //When
        var actual = underTest.selectCustomerById(id);
        //Then
        Assertions.assertThat(actual).isEmpty();


    }

    @Test
    void insertCustomer() {

        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().firstName(),
                email,
                "password", 20,
                Gender.MALE);

        //When
        underTest.insertCustomer(customer);

        //Then
        var actual = underTest.selectAllCustomer()
                .stream()
                .filter(c->c.getEmail().equals(email))
                .findFirst()
                .orElseThrow();


        Assertions.assertThat(actual.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(actual.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(actual.getAge()).isEqualTo(customer.getAge());


    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().firstName(),
                email,
                "password", 20,
                Gender.MALE);

        //When
        underTest.insertCustomer(customer1);

        //Then
        Assertions.assertThat(underTest.existsPersonWithEmail(email)).isTrue();

    }

    @Test
    void existsCustomerWithId() {
        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().firstName(),
                email,
                "password", 20,
                Gender.MALE);

        //When
        underTest.insertCustomer(customer1);

        int id = underTest.selectAllCustomer()
                .stream()
                .filter(c->c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //Then
        Assertions.assertThat(underTest.existsPersonWithId(id)).isTrue();


    }

    @Test
    void deleteCustomer() {
        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().firstName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer1);
        int id = underTest.selectAllCustomer()
                .stream()
                .filter(c->c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        underTest.deleteCustomer(id);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        Assertions.assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password",
                20,
                Gender.MALE,
                null
        );
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName = "foo";

        // When age is name
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        Assertions.assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            Assertions.assertThat(updated.getId()).isEqualTo(id);
            Assertions.assertThat(updated.getGender()).isEqualTo(Gender.MALE);
            Assertions.assertThat(updated.getName()).isEqualTo(newName);
        });


    }

    @Test
    void canUpdateProfileImageId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.updateCustomerProfileImageId("2222", id);

        // Then
        Optional<Customer> customerOptional = underTest.selectCustomerById(id);
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(
                        c -> assertThat(c.getProfileImageId()).isEqualTo("2222")
                );
    }
}