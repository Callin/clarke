package com.two.clarke;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsConnectionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.connection.JmsResourceHolder;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
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