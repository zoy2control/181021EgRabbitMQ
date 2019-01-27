package CH4com.rabbitmq.spring.CH4S2P1RabbitAdmin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * Created by zoypong on 2019/1/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitAdminTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testRabbitAdmin() throws Exception {

        /**
         * ·通过 RabbitAdmin进行 交换机、队列声明和 绑定等操作
         */

        // ·交换机声明：direct/topic/fanout等类型
        rabbitAdmin.declareExchange(new DirectExchange("testRabbitAdmin.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("testRabbitAdmin.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("testRabbitAdmin.fanout", false, false));

        // ·队列声明
        rabbitAdmin.declareQueue(new Queue("testRabbitAdmin.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("testRabbitAdmin.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("testRabbitAdmin.fanout.queue", false));

        // ·交换机和 队列绑定
        // ·方式一：
        rabbitAdmin.declareBinding(
                new Binding(// ·绑定 exchange/queue
                        "testRabbitAdmin.direct.queue",// ·queue
                        Binding.DestinationType.QUEUE,
                "testRabbitAdmin.direct", // ·exchange
                        "testRabbitAdmin.direct.routingKey",// ·routingKey
                        new HashMap<>()) // ·arguments
                );

        // ·方式二：
        rabbitAdmin.declareBinding(
                BindingBuilder // ·bindBuilder的 链式调用 .bind().to().with() 把 xx队列用 xx路由键绑定到 xx交换机上
                        .bind(new Queue("testRabbitAdmin.topic.queue", false))
                        .to(new TopicExchange("testRabbitAdmin.topic", false, false))
                        .with("testRabbitAdmin.topic.routingKey")
        );

        rabbitAdmin.declareBinding(
                BindingBuilder // ·bindBuilder的 链式调用 .bind().to().with() 把 xx队列用 xx路由键绑定到 xx交换机上
                        .bind(new Queue("testRabbitAdmin.topic.queue", false))
                        .to(new FanoutExchange("testRabbitAdmin.topic", false, false))
        );// ·注意，此处 fanoutExchange不用 路由键



        // ·清空队列数据
        rabbitAdmin.purgeQueue("testRabbitAdmin.topic.queue", false);
    }






    // ·@Bean已注入，此处直接使用 RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessageV01() throws Exception {
        // ·创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述..");// ·自定义消息属性
        messageProperties.getHeaders().put("type", "自定义消息类型..");// ·自定义消息属性
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);

        // ·发送消息。注意，这里 MessagePostProcessor()可以对 原来的消息进行再一次加工
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.err.println("------添加额外的设置---------");
                message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");// ·
                message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
                return message;
            }
        });
    }



    @Test
    public void testSendMessageV02() throws Exception {
        // ·发送消息第一种方式：
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");// ·设置消息属性
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.abc", message);

        // ·发送消息第二种方式：直接发送。这种方式比较便捷
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send! topic001--Exchange");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send! topic002--Exchange");
    }
}
