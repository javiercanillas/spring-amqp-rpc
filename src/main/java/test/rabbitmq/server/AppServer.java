package test.rabbitmq.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import test.rabbitmq.model.FibonacciService;

import java.io.IOException;

@SpringBootApplication
@ComponentScan
public class AppServer {

    public static void main(String[] args) throws IOException  {
        ConfigurableApplicationContext context = SpringApplication.run(AppServer.class, args);
        FibonacciService directService = (FibonacciService) context.getBean(FibonacciService.class);
        System.in.read();
    }
}
