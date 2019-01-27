package CH4com.rabbitmq.spring;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ·RabbitMQ配置类
 * Created by zoypong on 2019/1/26.
 */
@Configuration // ·配置Bean
@ComponentScan("CH4com.rabbitmq.spring.*") // ·要扫描的包路径
public class RabbitMqConfig {

    /**
     * ·将 ConnectFactory注入到 Bean容器中
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        // ·这里设置一些 连接参数
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("127.0.0.1:5672");
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;// ·注入到Bean容器中
    }

    /**
     * ·将 RabbitAdmin注入到 Bean容器中。
     * ·注意，@Bean(name="")可以自定义名称，没写name默认以方法名为 beanName
     * ·所以这里 rabbitAdmin需要的入参 ConnectionFactory从容器中取，就需要这个 入参名称与上面的 Bean名称一样
     * ·否则就找不到这个 Bean
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);// ·Spring容器加载的时候，一定要把这个 Bean加载上
        return rabbitAdmin;// ·注入到 Bean容器中
    }
}