package com.two.clarke.lowlevel;

import com.two.clarke.OracleAQConstants;
import oracle.AQ.AQQueueTable;
import oracle.AQ.AQQueueTableProperty;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsDestination;
import oracle.jms.AQjmsDestinationProperty;
import oracle.jms.AQjmsFactory;
import oracle.jms.AQjmsSession;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.sql.DataSource;
import java.sql.SQLException;

import static com.two.clarke.OracleAQConstants.DB_PASSWORD;
import static com.two.clarke.OracleAQConstants.DB_USERNAME;
import static com.two.clarke.OracleAQConstants.QUEUE_TABLE_NAME;


public class OracleAQ {

    public static void main(String[] args) throws JMSException {
        QueueConnectionFactory qc_fact = null;
        createQueueTableAndQueue();
    }

    public static void createQueueTableAndQueue() {

        QueueConnectionFactory queueConnectionFactory = null;
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;
        AQQueueTableProperty aqQueueTableProperty = null;
        AQQueueTable aqQueueTable = null;
        AQjmsDestinationProperty aQjmsDestinationProperty = null;
        Queue queue = null;
        BytesMessage bytes_msg = null;

        try {
   /* get queue connection factory */
            queueConnectionFactory = AQjmsFactory.getQueueConnectionFactory (getDataSource());

   /* create queue connection */
            queueConnection = queueConnectionFactory.createQueueConnection(DB_USERNAME, DB_PASSWORD);

   /* create QueueSession */
            queueSession = queueConnection.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);

   /* start the queue connection */
            queueConnection.start();

            aqQueueTableProperty = new AQQueueTableProperty("SYS.AQ$_JMS_TEXT_MESSAGE");

   /* create a queue table */
            aqQueueTable = ((AQjmsSession) queueSession).createQueueTable(DB_USERNAME,
                    QUEUE_TABLE_NAME,
                    aqQueueTableProperty);

            aQjmsDestinationProperty = new AQjmsDestinationProperty();

   /* create a queue */
            queue = ((AQjmsSession) queueSession).createQueue(aqQueueTable, OracleAQConstants.QUEUE_NAME,
                    aQjmsDestinationProperty);

   /* start the queue */
            ((AQjmsDestination) queue).start(queueSession, true, true);

   /* create a bytes message */
            bytes_msg = queueSession.createBytesMessage();

   /* close session */
            queueSession.close();

   /* close connection */
            queueConnection.close();
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }


    public static DataSource getDataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setURL("jdbc:oracle:thin:@localhost:1521:XE");
        dataSource.setImplicitCachingEnabled(true);
        dataSource.setFastConnectionFailoverEnabled(true);

        return dataSource;
    }

}
