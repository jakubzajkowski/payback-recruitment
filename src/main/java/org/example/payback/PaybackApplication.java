package org.example.payback;

import org.example.payback.dto.Request;
import org.example.payback.dto.RequestType;
import org.example.payback.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Scanner;

@SpringBootApplication
@EnableAsync
public class PaybackApplication implements CommandLineRunner {

    @Autowired
    RequestService requestService;

    private volatile boolean running = true;

    public static void main(String[] args) {
        SpringApplication.run(PaybackApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        requestService.startRequestProcessor();

        Thread producer = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                System.out.println("Add type of request (TYPE1, TYPE2, TYPE3, TYPE4) or 'exit' to quit:");
                String type = scanner.nextLine();
                if ("exit".equalsIgnoreCase(type)){
                    running = false;
                    break;
                }
                System.out.println("Add content of request:");
                String content = scanner.nextLine();
                try {
                    requestService.addRequest(new Request(RequestType.valueOf(type), content));
                } catch (IllegalArgumentException e) {
                    System.out.println("Bad type! Please enter a valid request type.");
                }
            }
            scanner.close();
        });


        producer.start();
        producer.join();

        requestService.stopRequestProcessor();
        System.out.println("Application stopped.");
    }
}
