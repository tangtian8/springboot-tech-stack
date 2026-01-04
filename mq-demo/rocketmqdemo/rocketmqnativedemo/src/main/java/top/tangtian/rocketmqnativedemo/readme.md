// ============================================
// 订阅关系总结图（重点理解）
// ============================================

/*

生产者                Topic              消费者组1             消费者组2
(order_process)      (data_statistics)

Producer  ────────>  order_topic  ────>  Consumer1-1  ───>  处理订单
│                Consumer1-2  ───>  处理订单
│                (负载均衡)
│
└────>  Consumer2-1  ───>  数据统计
Consumer2-2  ───>  数据统计
(负载均衡)

核心理解：

1. 订阅关系 = 消费者组 + Topic
    - order_process 组订阅 order_topic
    - data_statistics 组也订阅 order_topic

2. 消费者组内负载均衡
    - Consumer1-1 和 Consumer1-2 共同消费，每条消息只被其中一个消费

3. 消费者组间独立消费
    - 同一条消息，order_process 组会消费一次
    - 同一条消息，data_statistics 组也会消费一次

4. 类比：
    - Topic 就像一个消息发布渠道（电视台）
    - 消费者组就像不同的订阅用户（观众）
    - 每个用户都能看到完整的节目内容
    - 用户家里的多台电视（消费者实例）只需要看一次

*/

// ============================================
// RocketMQ vs JDBC 深度对比
// ============================================

/*

┌─────────────────────────────────────────────────────────────────┐
│                    JDBC 操作数据库                                │
└─────────────────────────────────────────────────────────────────┘

// 1. 加载驱动
Class.forName("com.mysql.jdbc.Driver");

// 2. 创建连接
Connection conn = DriverManager.getConnection(
"jdbc:mysql://localhost:3306/test",  // 数据库地址
"root",                               // 用户名
"password"                            // 密码
);

// 3. 创建语句
PreparedStatement stmt = conn.prepareStatement(
"INSERT INTO orders (id, user_id, amount) VALUES (?, ?, ?)"
);

// 4. 设置参数
stmt.setString(1, "ORDER001");
stmt.setString(2, "USER001");
stmt.setDouble(3, 199.99);

// 5. 执行
int result = stmt.executeUpdate();

// 6. 关闭连接
stmt.close();
conn.close();


┌─────────────────────────────────────────────────────────────────┐
│                RocketMQ 生产者（发送消息）                        │
└─────────────────────────────────────────────────────────────────┘

// 1. 创建生产者（类似加载驱动）
DefaultMQProducer producer = new DefaultMQProducer("producer_group");

// 2. 设置 NameServer 地址（类似数据库地址）
producer.setNamesrvAddr(namesrvAddr);

// 3. 启动生产者（类似建立连接）
producer.start();

// 4. 创建消息（类似创建 SQL 语句）
Message message = new Message(
"order_topic",                // Topic（类似表名）
"VIP",                        // Tag（类似WHERE条件）
"ORDER001",                   // Key（类似主键）
"消息内容".getBytes()          // Body（类似记录数据）
);

// 5. 发送消息（类似执行 SQL）
SendResult result = producer.send(message);

// 6. 关闭生产者（类似关闭连接）
producer.shutdown();


┌─────────────────────────────────────────────────────────────────┐
│                RocketMQ 消费者（接收消息）                        │
└─────────────────────────────────────────────────────────────────┘

// 1. 创建消费者（指定消费者组，类似用户名）
DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("order_consumer_group");

// 2. 设置 NameServer 地址
consumer.setNamesrvAddr(namesrvAddr);

// 3. 订阅 Topic（类似 SELECT 语句）
consumer.subscribe("order_topic", "*");  // 订阅所有消息
// 相当于 SQL: SELECT * FROM order_topic

consumer.subscribe("order_topic", "VIP");  // 只订阅 VIP 标签
// 相当于 SQL: SELECT * FROM order_topic WHERE tag = 'VIP'

consumer.subscribe("order_topic", "VIP || URGENT");  // 订阅多个标签
// 相当于 SQL: SELECT * FROM order_topic WHERE tag IN ('VIP', 'URGENT')

// 4. 注册监听器（类似 ResultSet 处理）
consumer.registerMessageListener(new MessageListenerConcurrently() {
@Override
public ConsumeConcurrentlyStatus consumeMessage(
List<MessageExt> msgs,
ConsumeConcurrentlyContext context) {

        for (MessageExt msg : msgs) {
            // 类似遍历 ResultSet
            String topic = msg.getTopic();       // 表名
            String tag = msg.getTags();          // 过滤条件
            String key = msg.getKeys();          // 主键
            String body = new String(msg.getBody());  // 数据
            
            System.out.println("处理消息: " + body);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
});

// 5. 启动消费者（开始接收消息，类似执行查询）
consumer.start();

// 消费者会持续运行，不断接收新消息
// JDBC 是一次性查询，RocketMQ 是持续监听


┌─────────────────────────────────────────────────────────────────┐
│                        核心概念对比                               │
└─────────────────────────────────────────────────────────────────┘

JDBC                          RocketMQ
────────────────────────────────────────────────────────────────
数据库地址                      NameServer 地址
表名 (Table)                   主题 (Topic)
记录 (Record)                  消息 (Message)
主键 (Primary Key)             消息Key
WHERE 条件                     Tag 过滤
INSERT                        Producer.send()
SELECT                        Consumer.subscribe()
事务                           事务消息
索引                           消息Key索引


┌─────────────────────────────────────────────────────────────────┐
│              订阅关系存储位置（重点理解）                          │
└─────────────────────────────────────────────────────────────────┘

当执行 consumer.subscribe("order_topic", "*") 时：

1. 消费者向 Broker 发送订阅请求
2. Broker 记录订阅关系：
   {
   "consumerGroup": "order_consumer_group",
   "topic": "order_topic",
   "subExpression": "*",
   "consumers": [
   "consumer_instance_1@192.168.1.100",
   "consumer_instance_2@192.168.1.101"
   ]
   }

3. 这个订阅关系保存在 Broker 的内存和磁盘中
4. 即使消费者重启，订阅关系仍然存在
5. 通过命令可以查看：
   sh mqadmin consumerProgress -n 127.108.167.224:9876 -g order_consumer_group


┌─────────────────────────────────────────────────────────────────┐
│                    完整的消息流转过程                             │
└─────────────────────────────────────────────────────────────────┘

Step 1: 生产者发送消息
├─ Producer 连接 NameServer
├─ 查询 order_topic 在哪个 Broker
├─ 连接目标 Broker
└─ 发送消息到 Broker 的某个队列（Queue）

Step 2: Broker 存储消息
├─ 消息写入 CommitLog（所有消息的日志文件）
├─ 构建 ConsumeQueue（消费队列索引）
└─ 等待消费者拉取

Step 3: 消费者订阅和拉取
├─ Consumer 启动，执行 subscribe("order_topic", "*")
├─ 连接 NameServer，查询 order_topic 的队列信息
├─ 连接 Broker，注册订阅关系
├─ 定期从 Broker 拉取消息（Pull 模式，虽然叫 Push Consumer）
└─ 消息拉取后，调用 MessageListener 处理

Step 4: 消息确认和重试
├─ 如果返回 CONSUME_SUCCESS，消息标记为已消费
├─ 如果返回 RECONSUME_LATER，消息进入重试队列
└─ 重试次数达到上限后，进入死信队列（DLQ）

*/