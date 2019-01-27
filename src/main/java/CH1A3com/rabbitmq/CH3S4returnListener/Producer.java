package CH1A3com.rabbitmq.CH3S4returnListener;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/11/14.
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // ·1、创建 ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // ·2、获取 Connection
        Connection connection = connectionFactory.newConnection();

        // ·3、通过 Connection创建一个新的 Channel
        Channel channel = connection.createChannel();

        // ·4、重点来啦：指定我们的消息 投递模式，消息的确认模式
        channel.confirmSelect();

        String exchangeName = "test_return_exchange";
        String routingKey = "test_return_routingKey.save";
        String routingErrorKey = "test_return_routingErrorKey.save";

        // ·5、发送消息
        String msg = "hello mq send return message";

        // ·6、重点来了：设置 CH3S4returnListener
        // ·如果从 生产端发送到 MQ Broker的消息不能路由，那么 该消息将被 生产端“回收”，即 CH3S4returnListener()
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange,
                                     String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("============handle return============");
                System.out.println("replyCode: " + replyCode);
                System.out.println("replyText: " + replyText);
                System.out.println("exchange: " + exchange);
                System.out.println("routingKey: " + routingKey);
                System.out.println("properties: " + properties);
                System.out.println("body: " + new String(body));
            }
        });

        // ·发送消息
        // ·对于 第三个参数 mandatory，设置为 true，如果 发送的该消息不能路由到指定队列，那么 该消息也不会被~
        // ·删除，如果 生产端设置了 CH3S4returnListener，那么 消息将会被 生产端的 returnListener监听到

        // ·这是一个正常的可路由的消息
//        channel.basicPublish(exchangeName, routingKey, true, null, msg.getBytes());// ·eq1
        // ·这是一个 不可路由的消息（routingKey和 消费端routingKey不一样）
        channel.basicPublish(exchangeName, routingErrorKey, true, null, msg.getBytes());// ·eq2
    }
}
