package com.serikscode;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.serikscode.customer.Customer;
import com.serikscode.customer.Gender;
import com.serikscode.repository.CustomerRepository;
import com.serikscode.s3.S3Buckets;
import com.serikscode.s3.S3Service;
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
            createRandomCustomer(customerRepository, passwordEncoder);
//            testS3BucketUploadAndDownload(s3Service, s3Buckets);
        };
    }

    private void testS3BucketUploadAndDownload(S3Service s3Service, S3Buckets s3Buckets) {
        s3Service.putObject(
                s3Buckets.getCustomer(),
                "foo",
                "Hello World".getBytes()
        );

        byte[] obj = s3Service.getObject(
                "fs-customer-bucket",
                "foo"
        );

        System.out.println("Hooray: " + new String(obj));
    }

    private void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
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
    }


}
