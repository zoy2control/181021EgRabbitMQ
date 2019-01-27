package CH1A3com.rabbitmq.CH3S5userDef;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by zoypong on 2018/11/17.
 */
public class MyConsumer extends DefaultConsumer {

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public MyConsumer(Channel channel) {
        super(channel);
    }

    // ·实现 .handleDelivery()，消费者处理函数
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("===================自定义消费者：user def==================");
        System.out.println("consumerTag: " + consumerTag);
        System.out.println("envelop: " + envelope);
        System.out.println("properties: " + properties);
        System.out.println("body: " + new String(body));
    }
}
