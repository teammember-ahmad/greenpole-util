package org.greenpole.util.udividend;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.greenpole.util.properties.QueueConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndPoint {
    private final Logger logger = LoggerFactory.getLogger(EndPoint.class);
    private final QueueConfigProperties queueProp = QueueConfigProperties.getInstance();
    
    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public EndPoint(String endpointName) throws IOException {
        this.endPointName = endpointName;

        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        //getting a connection
        try {

            //hostname of your rabbitmq server
            //factory.setHost("192.168.180.18");
            factory.setHost(queueProp.get);
            factory.setUsername("guest1");
            factory.setPassword("password");
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(490000); // In case of broken connection, try again every 30 seconds
            factory.setRequestedHeartbeat(45); //Keep sending the heartbeat every 45 seconds to prevent any routers from considering the connection stale

            connection = factory.newConnection();
            channel = connection.createChannel();

            //declaring a queue for this channel. If queue does not exist,
            //it will be created on the server.
            channel.exchangeDeclare(endpointName, "direct", true);
            channel.queueDeclare(endpointName, true, false, false, null);
            channel.queueBind(endpointName, endpointName, endPointName);

        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            //connection.close();
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
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.connection.close();
    }
}
