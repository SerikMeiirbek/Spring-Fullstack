package com.serikscode;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.serikscode.customer.Customer;
import com.serikscode.customer.Gender;
import com.serikscode.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(Main.class, args);

//        printBeans(applicationContext);

    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder){
        return args-> {

            int age = new Random().nextInt(16,99);
            Faker faker = new Faker();
            Name name = faker.name();

            String firstName = name.firstName();
            String lastName = name.lastName();
            String email = firstName.toLowerCase()+ "." + lastName.toLowerCase() + "@gmail.com";

            Random random = new Random();
            Gender gender = (random.nextInt(0,2) == 0) ? (gender = Gender.MALE) : (gender = Gender.FEMALE);

            Customer customer = new Customer(firstName + " " + lastName, email, passwordEncoder.encode("password"), age, gender);
            customerRepository.save(customer);
            System.out.println("Email: " + email);
        };
    }


}
