package com.two.clarke;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsConnectionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.jms.support.converter.oracle.MappingAdtMessageConverter;
import org.springframework.data.jdbc.jms.support.oracle.DatumMapper;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.sql.DataSource;
import java.sql.SQLException;

import static com.two.clarke.OracleAQConstants.DB_PASSWORD;
import static com.two.clarke.OracleAQConstants.DB_URL;
import static com.two.clarke.OracleAQConstants.DB_USERNAME;
import static com.two.clarke.OracleAQConstants.QUEUE_NAME;


@Configuration
@EnableJms
@Slf4j
public class OracleAQConfiguration {

    private final DataSource dataSource;

    private final BeanFactory springContextBeanFactory;

    @Autowired
    public OracleAQConfiguration(DataSource dataSource, BeanFactory springContextBeanFactory) {
        this.dataSource = dataSource;
        this.springContextBeanFactory = springContextBeanFactory;
    }

    @Bean
    public AQjmsConnectionFactory aQjmsConnectionFactory() throws JMSException {
        AQjmsConnectionFactory aQjmsConnectionFactory = new AQjmsConnectionFactory();
        aQjmsConnectionFactory.setDatasource(dataSource);

        return aQjmsConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws JMSException {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(aQjmsConnectionFactory());
        factory.setDestinationResolver(new BeanFactoryDestinationResolver(springContextBeanFactory));
        factory.setConcurrency("5");
        return factory;
    }

    @Bean
    public Queue queue() throws JMSException {
        log.info("Creating a queue with the name: " + QUEUE_NAME);
        Connection connection = aQjmsConnectionFactory().createConnection();
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(QUEUE_NAME);

        log.info("Queue " + queue.getQueueName() + " created");

        return queue;
    }

    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate() throws JMSException {
        JmsMessagingTemplate jmsMessagingTemplate = new JmsMessagingTemplate();
        jmsMessagingTemplate.setConnectionFactory(aQjmsConnectionFactory());

        DatumMapper datumMapper = new UserDatumMapper();
        MessageConverter messageConverter = new MappingAdtMessageConverter(datumMapper);
//        StructDatumMapper
        jmsMessagingTemplate.setJmsMessageConverter(messageConverter);

        jmsMessagingTemplate.setDefaultDestinationName(QUEUE_NAME);
        return jmsMessagingTemplate;
    }

    @Bean
    public static DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setURL(DB_URL);
        dataSource.setImplicitCachingEnabled(true);
        dataSource.setFastConnectionFailoverEnabled(true);

        return dataSource;
    }
}
