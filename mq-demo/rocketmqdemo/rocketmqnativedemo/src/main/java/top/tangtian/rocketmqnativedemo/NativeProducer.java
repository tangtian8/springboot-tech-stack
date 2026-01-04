package top.tangtian.rocketmqnativedemo;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author tangtian
 * @date 2026-01-03 10:24
 */
public class NativeProducer {
	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];
		// ========================================
		// 第一步：创建生产者实例（类比创建数据库连接）
		// ========================================
		// 参数是生产者组名，用于标识这个生产者属于哪个组
		DefaultMQProducer producer = new DefaultMQProducer("producer_group_1");

		// ========================================
		// 第二步：配置 NameServer 地址（类比数据库连接URL）
		// ========================================
		// NameServer 是 RocketMQ 的注册中心，类似于数据库的地址
		producer.setNamesrvAddr(namesrvAddr);

		// ========================================
		// 可选配置项（类比数据库连接池参数）
		// ========================================
		// 发送消息超时时间（毫秒）
		producer.setSendMsgTimeout(3000);

		// 消息最大大小（字节）默认 4MB
		producer.setMaxMessageSize(1024 * 1024 * 4);

		// 失败重试次数
		producer.setRetryTimesWhenSendFailed(2);
		producer.setRetryTimesWhenSendAsyncFailed(2);

		// 实例名称（可选，用于区分同一个JVM中的多个Producer实例）
		producer.setInstanceName("producer_instance_1");

		// ========================================
		// 第三步：启动生产者（类比建立数据库连接）
		// ========================================
		System.out.println("开始启动生产者...");
		producer.start();
		System.out.println("生产者启动成功！");

		// ========================================
		// 第四步：创建消息（类比创建 SQL 语句）
		// ========================================
		/*
		 * Message 构造参数说明：
		 * 参数1: topic - 主题名称（消息发送的目的地）
		 * 参数2: tags - 标签（用于消息过滤，可以为空）
		 * 参数3: keys - 消息键（用于索引查询，可以为空）
		 * 参数4: body - 消息体（字节数组）
		 */

		// 示例1：最简单的消息（只有 topic 和消息体）
		Message message1 = new Message(
				"order_topic",                          // topic
				"这是一条订单消息".getBytes(StandardCharsets.UTF_8)  // body
		);

		// 示例2：带 Tag 的消息（用于消费端过滤）
		Message message2 = new Message(
				"order_topic",                          // topic
				"VIP",                                  // tag - 标记为VIP订单
				"这是一条VIP订单消息".getBytes(StandardCharsets.UTF_8)
		);

		// 示例3：完整的消息（带 Tag 和 Key）
		Message message3 = new Message(
				"order_topic",                          // topic
				"URGENT",                               // tag - 标记为紧急订单
				"ORDER_20260103_001",                   // key - 订单号（用于查询）
				"这是一条紧急订单消息".getBytes(StandardCharsets.UTF_8)
		);

		// ========================================
		// 第五步：发送消息（类比执行 SQL）
		// ========================================

		// 方式1：同步发送（等待 Broker 响应）
		SendResult result1 = producer.send(message1);
		System.out.println("同步发送结果: " + result1.getSendStatus());
		System.out.println("消息ID: " + result1.getMsgId());
		System.out.println("消息队列ID: " + result1.getMessageQueue().getQueueId());

		// 方式2：同步发送，指定超时时间
		SendResult result2 = producer.send(message2, 3000);
		System.out.println("带Tag的消息发送成功: " + result2.getSendStatus());

		// 方式3：异步发送（不等待响应，通过回调处理结果）
		producer.send(message3, new org.apache.rocketmq.client.producer.SendCallback() {
			@Override
			public void onSuccess(SendResult sendResult) {
				System.out.println("异步发送成功: " + sendResult.getMsgId());
			}

			@Override
			public void onException(Throwable e) {
				System.err.println("异步发送失败: " + e.getMessage());
			}
		});

		// 方式4：单向发送（不关心结果，最快）
		Message message4 = new Message("order_topic", "单向发送的消息".getBytes());
		producer.sendOneway(message4);
		System.out.println("单向发送完成（不等待结果）");

		// 等待异步发送完成
		Thread.sleep(1000);

		// ========================================
		// 第六步：关闭生产者（类比关闭数据库连接）
		// ========================================
		producer.shutdown();
		System.out.println("生产者已关闭");
	}
}
