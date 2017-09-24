package com.two.clarke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Queue;

@Service
public class OracleAQService {
    private static final Logger log = LoggerFactory.getLogger(OracleAQService.class);

    private JmsTemplate jmsTemplate;
    private JmsMessagingTemplate jmsMessagingTemplate;
    private Queue queue;

    @Autowired
    public OracleAQService(JmsTemplate jmsTemplate, JmsMessagingTemplate jmsMessagingTemplate, Queue queue) {
        this.jmsTemplate = jmsTemplate;
        this.jmsMessagingTemplate = jmsMessagingTemplate;
        this.queue = queue;
    }

    @JmsListener(containerFactory = "jmsListenerContainerFactory", destination = "queue")
    public void processMessage(String data) {
        log.info("Messaje: " + data);
    }

    public void sendMessage() {
        this.jmsTemplate.send(this.queue, session -> {
            log.info("Create message ...");
            return session.createTextMessage("hello queue world");
        });
        log.info("Message created");
    }

    public void sendMessageJMT() {
        jmsMessagingTemplate.convertAndSend("lalalala");
        log.info("Message created");
    }
}
