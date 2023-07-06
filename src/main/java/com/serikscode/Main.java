package com.serikscode;


import com.serikscode.customer.Customer;
import com.serikscode.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(Main.class, args);

//        printBeans(applicationContext);

    }

//    @Bean
//    CommandLineRunner runner(CustomerRepository customerRepository){
//        return args-> {
//            Customer alex = new Customer("Alex123", "alex@gmail.com", 21);
//            Customer jamila = new Customer("Jamila", "jamila@gmail.com", 22);
//
//            List<Customer> customers = List.of(alex,jamila);
//            customerRepository.saveAll(customers);
//
//        };
//    }









    @Bean
    public Foo getFoo(){
        return new Foo("bar");
    }

    record Foo(String name){}

    public static void printBeans(ConfigurableApplicationContext ctx){
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

}
