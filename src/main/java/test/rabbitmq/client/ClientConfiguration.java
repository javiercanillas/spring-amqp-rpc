package test.rabbitmq.client;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import test.rabbitmq.model.FibonacciService;
import test.rabbitmq.model.StopitService;

@ImportResource(locations = {"classpath:test/rabbitmq/common-context.xml", "classpath:test/rabbitmq/client/application-context.xml"})
@PropertySource({ "classpath:test/rabbitmq/client/application.properties" })
@Configuration
public class ClientConfiguration {

    @Bean
    public AmqpProxyFactoryBean proxy(@Qualifier("clientTemplate") AmqpTemplate template) {
        AmqpProxyFactoryBean proxy = new AmqpProxyFactoryBean();
        proxy.setAmqpTemplate(template);
        proxy.setServiceInterface(FibonacciService.class);
        proxy.setRoutingKey(FibonacciService.class.getSimpleName());

        return proxy;
    }

    @Bean("stopit.service.exporter")
    public AmqpInvokerServiceExporter exporter(RabbitTemplate template, StopitService service) {
        AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
        exporter.setAmqpTemplate(template);
        exporter.setMessageConverter(template.getMessageConverter());
        exporter.setService(service);
        exporter.setServiceInterface(FibonacciService.class);

        return exporter;
    }

    @Bean("stopit.queue.listener")
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    Queue queue,
                                                    AmqpInvokerServiceExporter exporter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue);
        container.setMessageListener(exporter);

        return container;
    }

}
