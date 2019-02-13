package test.rabbitmq.server;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import test.rabbitmq.client.StopitServiceImpl;
import test.rabbitmq.model.FibonacciService;
import test.rabbitmq.model.StopitService;

@ImportResource(locations = {"classpath:test/rabbitmq/common-context.xml", "classpath:test/rabbitmq/server/application-context.xml"})
@PropertySource({ "classpath:test/rabbitmq/server/application.properties" })
@Configuration
public class ServerConfiguration {

    @Bean("fibonacci.service.exporter")
    public AmqpInvokerServiceExporter exporter(RabbitTemplate template, FibonacciService service) {
        AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
        exporter.setAmqpTemplate(template);
        exporter.setMessageConverter(template.getMessageConverter());
        exporter.setService(service);
        exporter.setServiceInterface(FibonacciService.class);

        return exporter;
    }

    @Bean("fibonacci.queue.listener")
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    Queue queue,
                                                    AmqpInvokerServiceExporter exporter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue);
        container.setMessageListener(exporter);

        return container;
    }

    @Bean
    public AmqpProxyFactoryBean stopitProxy(AmqpTemplate template) {
        AmqpProxyFactoryBean proxy = new AmqpProxyFactoryBean();
        proxy.setAmqpTemplate(template);
        proxy.setServiceInterface(StopitService.class);
        proxy.setRoutingKey(StopitService.class.getSimpleName());

        return proxy;
    }
}
