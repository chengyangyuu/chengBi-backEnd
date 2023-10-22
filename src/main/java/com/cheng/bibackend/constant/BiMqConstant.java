package com.cheng.bibackend.constant;

/**
 * BI配置通用常量
 */
public interface BiMqConstant {

    /**
     * HOST
     */
    String BI_HOST = "localhost";

    /**
     * USERNAME
     */
    String BI_USERNAME = "guest";

    /**
     * PASSWORD
     */
    String BI_PASSWORD = "guest";

    /**
     * HOST
     */
    String BI_HOST_PROD = "43.140.246.183";

    /**
     * USERNAME
     */
    String BI_USERNAME_PROD = "admin";

    /**
     * PASSWORD
     */
    String BI_PASSWORD_PROD = "chengxiang";

    /**
     * 交换机名字
     */
    String BI_EXCHANGE_NAME = "bi_exchange";

    /**
     * BI队列名称
     */
    String BI_QUEUE="bi_queue";

    /**
     * BI路由键
     */
    String BI_ROUTING_KEY="bi_routingKey";
}
