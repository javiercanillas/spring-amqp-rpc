package test.rabbitmq.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import test.rabbitmq.model.FibonacciService;
import test.rabbitmq.model.StopitService;

@SpringBootApplication
@ComponentScan
public class AppClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppClient.class);

    public static void main(String[] args) {
        try(ConfigurableApplicationContext context = SpringApplication.run(AppClient.class, args)) {
            FibonacciService fibonacciService = context.getBean(FibonacciService.class);
            StopitService stopitService = context.getBean(StopitService.class);
            for (int i = 0; i < 100; i++) {
                LOGGER.info("Calculating first {} from Fibonacci series: {}", i, fibonacciService.calculateFirst(i));
                if (stopitService.isAskedForStop()) {
                    LOGGER.info("Being asked to stop");
                    break;
                }
            }
        }
    }
}
