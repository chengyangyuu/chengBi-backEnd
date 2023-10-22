package com.cheng.bibackend.bizmq;
import com.cheng.bibackend.constant.BiMqConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class BiInitMain {
    public static void main(String[] args) {
        try {
            //创建一个工厂
            ConnectionFactory factory = new ConnectionFactory();
            // 设置 rabbitmq 对应的信息
            factory.setHost(BiMqConstant.BI_HOST_PROD);
            factory.setUsername(BiMqConstant.BI_USERNAME_PROD);
            factory.setPassword(BiMqConstant.BI_PASSWORD_PROD);
            //新建连接
            Connection connection = factory.newConnection();
            //创建交换机
            Channel channel = connection.createChannel();
            //交换机名字
            String biExchange = BiMqConstant.BI_EXCHANGE_NAME;
            //对应路由
            channel.exchangeDeclare(biExchange,"direct");

            //创建队列 随机分配一个队列名称
            String queueName = BiMqConstant.BI_QUEUE;
            channel.queueDeclare(queueName, true, false, false, null);
            //队列绑定交换机
            channel.queueBind(queueName, biExchange, BiMqConstant.BI_ROUTING_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
