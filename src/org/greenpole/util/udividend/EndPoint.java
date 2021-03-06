package org.greenpole.util.udividend;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndPoint {
    private final Logger logger = LoggerFactory.getLogger(EndPoint.class);
    
    protected Channel channel;
    protected Connection connection;

    public EndPoint(String endpointName, String host, String username, String password) throws IOException {
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        //getting a connection
        try {
            factory.setHost(host);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(490000); //In case of broken connection, try again every 30 seconds
            factory.setRequestedHeartbeat(45);//Keep sending the heartbeat every 45 seconds to prevent any routers from considering the connection stale

            connection = factory.newConnection();
            channel = connection.createChannel();

            //declaring a queue for this channel. If queue does not exist,
            //it will be created on the server.
            channel.exchangeDeclare(endpointName, "direct", true);
            channel.queueDeclare(endpointName, true, false, false, null);
            channel.queueBind(endpointName, endpointName, endpointName);

        } catch (TimeoutException ex) {
            logger.info("An error occured in the Udividend Rabbit-MQ endpoint bridge. See Error log");
            logger.error("An error occured in the Udividend Rabbit-MQ endpoint bridge. See Error log", ex);
        } finally {//connection.close();
            
        }
        //creating a channel
    }

    /**
     * Close channel and connection. Not necessary as it happens implicitly any
     * way.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        try {
            this.channel.close();
        } catch (TimeoutException ex) {
            logger.info("An error occured while trying to close channel connection of the Udividend Rabbit-MQ endpoint bridge. See Error log");
            logger.error("An error occured while trying to close channel connection of the Udividend Rabbit-MQ endpoint bridge. See Error log", ex);
        }
        this.connection.close();
    }
}
