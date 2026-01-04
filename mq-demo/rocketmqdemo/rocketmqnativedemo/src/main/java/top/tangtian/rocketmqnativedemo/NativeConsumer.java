package top.tangtian.rocketmqnativedemo;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-03 10:27
 */
public class NativeConsumer {
	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];
		// ========================================
		// 第一步：创建消费者实例
		// ========================================
		/*
		 * 核心概念：消费者组（Consumer Group）
		 *
		 * 消费者组是 RocketMQ 的核心概念，作用：
		 * 1. 同一个消费者组内的消费者，共同消费一个 Topic 的消息（负载均衡）
		 * 2. 不同消费者组之间，互不影响，各自独立消费
		 *
		 * 例如：
		 * - order_consumer_group_1 的所有消费者，共同消费 order_topic
		 * - order_consumer_group_2 的所有消费者，也能完整消费 order_topic
		 *
		 * 这就像数据库的读写分离，多个应用可以独立读取同一份数据
		 */
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("order_consumer_group_1");

		// ========================================
		// 第二步：配置 NameServer 地址
		// ========================================
		consumer.setNamesrvAddr(namesrvAddr);

		// ========================================
		// 第三步：订阅 Topic（核心！建立订阅关系）
		// ========================================
		/*
		 * consumer.subscribe(topic, subExpression)
		 *
		 * 这一步是建立"消费者组"和"Topic"之间的订阅关系！
		 *
		 * 参数1: topic - 要订阅的主题
		 * 参数2: subExpression - 过滤表达式（Tag 过滤）
		 *
		 * 订阅关系：order_consumer_group_1 订阅 order_topic
		 */

		// 示例1：订阅所有消息（不过滤）
		consumer.subscribe("order_topic", "*");

		// 示例2：只订阅 VIP 标签的消息
		// consumer.subscribe("order_topic", "VIP");

		// 示例3：订阅多个标签（OR 关系）
		// consumer.subscribe("order_topic", "VIP || URGENT");

		// 示例4：订阅多个 Topic
		// consumer.subscribe("order_topic", "*");
		// consumer.subscribe("payment_topic", "*");

		System.out.println("订阅关系已建立：");
		System.out.println("  消费者组: order_consumer_group_1");
		System.out.println("  订阅Topic: order_topic");
		System.out.println("  过滤条件: *（所有消息）");

		// ========================================
		// 可选配置项
		// ========================================

		// 1. 消费模式：集群模式 vs 广播模式
		/*
		 * CLUSTERING（集群模式，默认）：
		 *   同一个消费者组内的多个消费者，负载均衡消费消息
		 *   每条消息只会被组内的一个消费者消费
		 *
		 * BROADCASTING（广播模式）：
		 *   同一个消费者组内的每个消费者，都会收到所有消息
		 *   每条消息会被组内的每个消费者都消费一次
		 */
		consumer.setMessageModel(MessageModel.CLUSTERING);  // 集群模式
		// consumer.setMessageModel(MessageModel.BROADCASTING);  // 广播模式

		// 2. 消费位置：从哪里开始消费
		/*
		 * CONSUME_FROM_LAST_OFFSET: 从最后的偏移量开始消费（默认）
		 *   新消费者启动后，只消费启动后产生的新消息
		 *
		 * CONSUME_FROM_FIRST_OFFSET: 从第一条消息开始消费
		 *   会消费 Topic 中所有的历史消息
		 *
		 * CONSUME_FROM_TIMESTAMP: 从指定时间戳开始消费
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

		// 3. 消费线程数配置
		consumer.setConsumeThreadMin(1);   // 最小消费线程数
		consumer.setConsumeThreadMax(20);  // 最大消费线程数

		// 4. 一次拉取的消息数量
		consumer.setPullBatchSize(32);     // 一次拉取32条消息

		// 5. 消费消息的批量大小
		consumer.setConsumeMessageBatchMaxSize(1);  // 每次消费1条消息

		// 6. 消费超时时间（分钟）
		consumer.setConsumeTimeout(15);

		// 7. 最大重试次数（默认16次）
		consumer.setMaxReconsumeTimes(3);

		// ========================================
		// 第四步：注册消息监听器（处理消息）
		// ========================================
		/*
		 * 这里注册的监听器，会处理从订阅的 Topic 中拉取到的消息
		 *
		 * MessageListenerConcurrently: 并发消费（多线程）
		 * MessageListenerOrderly: 顺序消费（单线程，保证顺序）
		 */
		consumer.registerMessageListener(new MessageListenerConcurrently() {

			/**
			 * 消费消息的回调方法
			 *
			 * @param msgs 消息列表（批量消费）
			 * @param context 消费上下文
			 * @return 消费状态
			 */
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(
					List<MessageExt> msgs,
					ConsumeConcurrentlyContext context) {

				// 遍历消息列表
				for (MessageExt msg : msgs) {
					System.out.println("========================================");
					System.out.println("收到消息:");
					System.out.println("  消息ID: " + msg.getMsgId());
					System.out.println("  Topic: " + msg.getTopic());
					System.out.println("  Tag: " + msg.getTags());
					System.out.println("  Key: " + msg.getKeys());
					System.out.println("  消费者组: order_consumer_group_1");
					System.out.println("  队列ID: " + msg.getQueueId());
					System.out.println("  重试次数: " + msg.getReconsumeTimes());

					// 获取消息体
					String body = new String(msg.getBody());
					System.out.println("  消息内容: " + body);

					// 处理业务逻辑
					try {
						processMessage(msg);
						System.out.println("  处理结果: 成功");
					} catch (Exception e) {
						System.err.println("  处理结果: 失败 - " + e.getMessage());
						// 返回失败，消息会重新投递
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
				}

				// 返回成功，消息消费完成
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});

		// ========================================
		// 第五步：启动消费者
		// ========================================
		System.out.println("开始启动消费者...");
		consumer.start();
		System.out.println("消费者启动成功！等待消息...");
		System.out.println("========================================");

		// 保持运行，持续消费消息
		// 实际应用中，消费者会一直运行，这里用 Thread.sleep 模拟
		// 在 Spring Boot 中，容器会保持运行，不需要这行代码
		Thread.sleep(Long.MAX_VALUE);

		// ========================================
		// 第六步：关闭消费者（正常情况下不会执行到这里）
		// ========================================
		// consumer.shutdown();
	}

	/**
	 * 处理消息的业务方法
	 */
	private static void processMessage(MessageExt msg) throws Exception {
		// 模拟业务处理
		String body = new String(msg.getBody());

		// 根据 Tag 执行不同的业务逻辑
		if ("VIP".equals(msg.getTags())) {
			System.out.println("    -> 处理VIP订单逻辑");
		} else if ("URGENT".equals(msg.getTags())) {
			System.out.println("    -> 处理紧急订单逻辑");
		} else {
			System.out.println("    -> 处理普通订单逻辑");
		}

		// 模拟处理耗时
		Thread.sleep(100);
	}
}
