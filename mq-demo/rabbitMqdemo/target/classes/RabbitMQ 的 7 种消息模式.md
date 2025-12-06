1. Simple Queue (简单队列模式)
   最基础的模式:一个生产者 → 一个队列 → 一个消费者
   java// 生产者
   @Autowired
   private RabbitTemplate rabbitTemplate;

public void send() {
rabbitTemplate.convertAndSend("queue.simple", "Hello");
}

// 消费者
@RabbitListener(queues = "queue.simple")
public void receive(String message) {
System.out.println("收到: " + message);
}

2. Work Queue (工作队列模式)
   一个生产者 → 一个队列 → 多个消费者竞争消费
   java// 配置
   @Bean
   public Queue workQueue() {
   return new Queue("queue.work", true);
   }

// 消费者1
@RabbitListener(queues = "queue.work")
public void worker1(String message) {
System.out.println("Worker1 处理: " + message);
}

// 消费者2
@RabbitListener(queues = "queue.work")
public void worker2(String message) {
System.out.println("Worker2 处理: " + message);
}


3. Publish/Subscribe (发布订阅模式 - Fanout)
   使用 Fanout Exchange,广播消息到所有绑定的队列
   java// 配置
   @Bean
   public FanoutExchange fanoutExchange() {
   return new FanoutExchange("exchange.fanout");
   }

@Bean
public Queue fanoutQueue1() {
return new Queue("queue.fanout.1");
}

@Bean
public Queue fanoutQueue2() {
return new Queue("queue.fanout.2");
}

@Bean
public Binding binding1(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
}

@Bean
public Binding binding2(FanoutExchange fanoutExchange, Queue fanoutQueue2) {
return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
}

// 生产者
public void send() {
rabbitTemplate.convertAndSend("exchange.fanout", "", "广播消息");
}

// 消费者1
@RabbitListener(queues = "queue.fanout.1")
public void receive1(String message) {
System.out.println("队列1收到: " + message);
}

// 消费者2
@RabbitListener(queues = "queue.fanout.2")
public void receive2(String message) {
System.out.println("队列2收到: " + message);
}
特点: 所有绑定的队列都能收到消息



4. Routing (路由模式 - Direct)
   使用 Direct Exchange,根据 routing key 精确匹配
   java// 配置
   @Bean
   public DirectExchange directExchange() {
   return new DirectExchange("exchange.direct");
   }

@Bean
public Queue errorQueue() {
return new Queue("queue.error");
}

@Bean
public Queue infoQueue() {
return new Queue("queue.info");
}

@Bean
public Binding errorBinding(DirectExchange directExchange, Queue errorQueue) {
return BindingBuilder.bind(errorQueue).to(directExchange).with("error");
}

@Bean
public Binding infoBinding(DirectExchange directExchange, Queue infoQueue) {
return BindingBuilder.bind(infoQueue).to(directExchange).with("info");
}

// 生产者
public void sendError() {
rabbitTemplate.convertAndSend("exchange.direct", "error", "错误日志");
}

public void sendInfo() {
rabbitTemplate.convertAndSend("exchange.direct", "info", "普通日志");
}

// 消费者
@RabbitListener(queues = "queue.error")
public void receiveError(String message) {
System.out.println("错误队列: " + message);
}

@RabbitListener(queues = "queue.info")
public void receiveInfo(String message) {
System.out.println("信息队列: " + message);
}
特点: 根据 routing key 精确路由


5. Topics (主题模式 - Topic)
   使用 Topic Exchange,支持通配符匹配
   java// 配置
   @Bean
   public TopicExchange topicExchange() {
   return new TopicExchange("exchange.topic");
   }

@Bean
public Queue topicQueue1() {
return new Queue("queue.topic.1");
}

@Bean
public Queue topicQueue2() {
return new Queue("queue.topic.2");
}

@Bean
public Binding topicBinding1(TopicExchange topicExchange, Queue topicQueue1) {
return BindingBuilder.bind(topicQueue1).to(topicExchange).with("user.*.create");
}

@Bean
public Binding topicBinding2(TopicExchange topicExchange, Queue topicQueue2) {
return BindingBuilder.bind(topicQueue2).to(topicExchange).with("user.#");
}

// 生产者
public void send() {
// 匹配 user.*.create 和 user.#
rabbitTemplate.convertAndSend("exchange.topic", "user.admin.create", "创建管理员");

    // 只匹配 user.#
    rabbitTemplate.convertAndSend("exchange.topic", "user.admin.update.info", "更新管理员");
}
通配符规则:

* : 匹配一个单词
# : 匹配零个或多个单词

特点: 灵活的模糊匹配


6. RPC (远程过程调用模式)
   请求-响应模式,同步调用
   java// 服务端
   @RabbitListener(queues = "queue.rpc")
   public int compute(int n) {
   System.out.println("计算 fibonacci(" + n + ")");
   return fibonacci(n);
   }

private int fibonacci(int n) {
if (n <= 1) return n;
return fibonacci(n - 1) + fibonacci(n - 2);
}

// 客户端
@Autowired
private RabbitTemplate rabbitTemplate;

public void call() {
Integer result = (Integer) rabbitTemplate.convertSendAndReceive(
"queue.rpc", 10
);
System.out.println("结果: " + result);
}
特点: 同步调用,有返回值



7. Publisher Confirms (发布确认模式)
   确保消息可靠投递
   java// 配置
   spring:
   rabbitmq:
   publisher-confirm-type: correlated
   publisher-returns: true

// 使用
@Autowired
private RabbitTemplate rabbitTemplate;

public void sendWithConfirm() {
rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
if (ack) {
System.out.println("消息发送成功");
} else {
System.out.println("消息发送失败: " + cause);
}
});

    rabbitTemplate.setReturnsCallback(returned -> {
        System.out.println("消息被退回: " + returned.getMessage());
    });
    
    rabbitTemplate.convertAndSend("exchange.test", "key", "消息内容");
}
特点: 保证消息可靠性

模式对比表
模式Exchange 类型Routing Key使用场景Simple Queue(默认)不需要简单任务队列Work Queue(默认)不需要任务分发、负载均衡Publish/SubscribeFanout忽略广播通知、系统消息RoutingDirect精确匹配日志分级处理TopicsTopic模糊匹配复杂路由规则RPCDirect指定远程调用Publisher Confirms任意任意可靠消息投递
实际应用场景

简单队列: 发送邮件、短信
工作队列: 图片处理、视频转码
发布订阅: 系统通知、缓存更新
路由模式: 日志收集(error/info/debug)
主题模式: 订单系统(order.create.*, order.#)
RPC: 微服务间同步调用
发布确认: 支付通知、重要业务消息



Topics 模式 和 Publish/Subscribe (发布订阅模式 - Fanout) 的区别
Topics 模式 vs Fanout 模式的核心区别
核心区别总结
对比项	Fanout (发布订阅)	Topic (主题模式)
Exchange 类型	FanoutExchange	TopicExchange
Routing Key	完全忽略	支持通配符匹配
消息分发	广播到所有绑定的队列	根据规则选择性分发
灵活性	低 - 无法过滤	高 - 可以精确控制
使用场景	全员通知、系统广播	分类订阅、条件过滤



Topic (主题模式) 与 Routing (路由模式 - Direct) 是不是很像

是的!非常像! 其实 Topic 模式就是 Direct 模式的增强版。

核心区别:只有一个!
对比项	Direct (路由模式)	Topic (主题模式)
Exchange 类型	DirectExchange	TopicExchange
匹配方式	精确匹配	模糊匹配(通配符)
Routing Key	完全相同才匹配	支持 * 和 #
性能	⭐⭐⭐⭐⭐ 最快	⭐⭐⭐⭐ 稍慢
灵活性	低	高
唯一区别就是:精确匹配 vs 模糊匹配