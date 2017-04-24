package org.greenpole.util.udividend;

import java.io.IOException;

import com.rabbitmq.client.MessageProperties;

public class Producer extends EndPoint {
    protected String endpointName;

    public Producer(String endpointName, String host, String username, String password) throws IOException {
        super(endpointName, host, username, password);
        this.endpointName = endpointName;
    }

    public void sendMessage(String object) throws IOException {
        channel.basicPublish("", endpointName, MessageProperties.PERSISTENT_TEXT_PLAIN, object.getBytes());
    }

}
