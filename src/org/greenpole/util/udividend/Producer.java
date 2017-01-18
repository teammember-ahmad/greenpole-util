package org.greenpole.util.udividend;

import java.io.IOException;

import com.rabbitmq.client.MessageProperties;

public class Producer extends EndPoint {

    public Producer(String endPointName) throws IOException {
        super(endPointName);
    }

    public void sendMessage(String object) throws IOException {
        channel.basicPublish("", endPointName, MessageProperties.PERSISTENT_TEXT_PLAIN, object.getBytes());
    }

}
