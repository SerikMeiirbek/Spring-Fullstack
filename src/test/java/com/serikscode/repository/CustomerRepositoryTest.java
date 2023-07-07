package com.serikscode.repository;

import com.serikcode.AbstractTestContainerUnitTest;
import com.serikscode.customer.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;


import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainerUnitTest {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {

        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().firstName(),
                email,
                20
        );

        //When
        underTest.save(customer);

        //Then
        Assertions.assertThat(underTest.existsCustomerByEmail(email)).isTrue();

    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {

        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();

        //When
        var actual = underTest.existsCustomerByEmail(email);

        //Then
        Assertions.assertThat(actual).isFalse();

    }

    @Test
    void existsCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress() +  "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().firstName(),
                email,
                20
        );

        //When
        underTest.save(customer1);

        int id = underTest.findAll()
                .stream()
                .filter(c->c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //Then
        Assertions.assertThat(underTest.existsCustomerById(id)).isTrue();

    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        //Given
        int id = -1;

        //When
        var actual = underTest.existsCustomerById(id);

        //Then
        Assertions.assertThat(actual).isFalse();

    }



}