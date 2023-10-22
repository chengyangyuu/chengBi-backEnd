# SpringBoot AIGC 橙智能BI 项目的后端

## 项目介绍

1.基于程序员鱼皮的 Java SpringBoot 的项目初始模板开发，该模版整合了常用框架和主流业务的示例代码。可通过yupi.icu进行了解

2.该项目目前只有一个核心业务  就是传入一个excel文件 并输入分析诉求 既可得到AIGC返回的分析结果 以及 一个根据Echatsd生成的图表

3.需要注意的是 目前业务层面只支持 传入单行表头 下面是内容的Excel文件 ，还不支持多行表头或者列表头的Excel文件，如需要可自行扩展

4.该项目chartController的Async接口引入了并发线程池 由于AIGC的请求需要时间 为了避免用户等待用于异步输出请求结果 

5.为使消息持久化 使用了消息队列来保存生成请求 但该项目并没有添加死信队列来捞失败的请求 这也可以作为一个可扩展点


## 快速上手

> 本项目存在一些该初始模版留下的 类似帖子表这种的db 但并没有实际用处  请悉知

### MySQL 数据库

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

[^关于无用的表]: 本项目存在一些该初始模版留下的 类似帖子表这种的db 但并没有实际用处  请悉知

3）配置你的 redis,MQ 在yml 打上xx的地方 配置上你的信息

[^关于redisClint]: 在redisClint的配置中 如果你的redis 没有密码 一定要全局搜索 setpassword 找到redis客户端配置那里并注释 ,否则会报错

3）启动项目，该项目集成了Knife4j 访问 `http://localhost:8107(或者是你的端口号)/api/doc.html`  即可打开接口文档，不需要写前端就能在线调试接口了~

![](doc/swagger.png)


