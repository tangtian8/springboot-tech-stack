package top.tangtian.rocketmqnativedemo;

/**
 * @author tangtian
 * @date 2026-01-03 10:31
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 完整测试案例：演示订阅关系的建立和消息收发
 */
public class RocketMQCompleteTest {

	private static final String TOPIC = "test_topic";

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];
		// 第一步：启动消费者组1（先启动消费者，建立订阅关系）
		System.out.println("========================================");
		System.out.println("启动消费者组1: consumer_group_1");
		System.out.println("========================================");
		startConsumerGroup1(namesrvAddr);

		Thread.sleep(2000);  // 等待消费者启动完成

		// 第二步：启动消费者组2
		System.out.println("========================================");
		System.out.println("启动消费者组2: consumer_group_2");
		System.out.println("========================================");
		startConsumerGroup2(namesrvAddr);

		Thread.sleep(2000);  // 等待消费者启动完成

		// 第三步：发送测试消息
		System.out.println("========================================");
		System.out.println("开始发送测试消息");
		System.out.println("========================================");
		sendTestMessages(namesrvAddr);

		// 保持运行，观察消费情况
		System.out.println("========================================");
		System.out.println("观察消费情况（按 Ctrl+C 退出）");
		System.out.println("========================================");
		Thread.sleep(Long.MAX_VALUE);
	}

	/**
	 * 启动消费者组1：订阅所有消息
	 */
	private static void startConsumerGroup1(String namesrvAddr) throws Exception {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group_1");
		consumer.setNamesrvAddr(namesrvAddr);

		// 订阅关系1：订阅所有消息
		consumer.subscribe(TOPIC, "*");
		System.out.println("订阅关系已建立：");
		System.out.println("  消费者组: consumer_group_1");
		System.out.println("  Topic: " + TOPIC);
		System.out.println("  过滤条件: * (所有消息)");

		consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("\n[消费者组1] 收到消息:");
				System.out.println("  消息ID: " + msg.getMsgId());
				System.out.println("  Topic: " + msg.getTopic());
				System.out.println("  Tag: " + msg.getTags());
				System.out.println("  内容: " + new String(msg.getBody()));
				System.out.println("  消费者组: consumer_group_1");
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer.start();
		System.out.println("消费者组1启动成功\n");
	}

	/**
	 * 启动消费者组2：只订阅 VIP 标签的消息
	 */
	private static void startConsumerGroup2(String namesrvAddr) throws Exception {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group_2");
		consumer.setNamesrvAddr(namesrvAddr);

		// 订阅关系2：只订阅 VIP 标签
		consumer.subscribe(TOPIC, "VIP");
		System.out.println("订阅关系已建立：");
		System.out.println("  消费者组: consumer_group_2");
		System.out.println("  Topic: " + TOPIC);
		System.out.println("  过滤条件: VIP (只订阅VIP标签)");

		consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("\n[消费者组2] 收到消息:");
				System.out.println("  消息ID: " + msg.getMsgId());
				System.out.println("  Topic: " + msg.getTopic());
				System.out.println("  Tag: " + msg.getTags());
				System.out.println("  内容: " + new String(msg.getBody()));
				System.out.println("  消费者组: consumer_group_2");
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer.start();
		System.out.println("消费者组2启动成功\n");
	}

	/**
	 * 发送测试消息
	 */
	private static void sendTestMessages(String namesrvAddr) throws Exception {
		DefaultMQProducer producer = new DefaultMQProducer("test_producer_group");
		producer.setNamesrvAddr(namesrvAddr);
		producer.start();
		System.out.println("生产者启动成功\n");

		// 消息1：普通消息（无Tag）
		Message msg1 = new Message(TOPIC, "普通消息：订单001".getBytes());
		SendResult result1 = producer.send(msg1);
		System.out.println("发送消息1: " + result1.getSendStatus());
		System.out.println("  内容: 普通消息：订单001");
		System.out.println("  Tag: 无");
		System.out.println("  预期: 只有消费者组1会收到\n");

		Thread.sleep(1000);

		// 消息2：VIP 标签消息
		Message msg2 = new Message(TOPIC, "VIP", "VIP消息：VIP订单002".getBytes());
		SendResult result2 = producer.send(msg2);
		System.out.println("发送消息2: " + result2.getSendStatus());
		System.out.println("  内容: VIP消息：VIP订单002");
		System.out.println("  Tag: VIP");
		System.out.println("  预期: 消费者组1和消费者组2都会收到\n");

		Thread.sleep(1000);

		// 消息3：URGENT 标签消息
		Message msg3 = new Message(TOPIC, "URGENT", "紧急消息：紧急订单003".getBytes());
		SendResult result3 = producer.send(msg3);
		System.out.println("发送消息3: " + result3.getSendStatus());
		System.out.println("  内容: 紧急消息：紧急订单003");
		System.out.println("  Tag: URGENT");
		System.out.println("  预期: 只有消费者组1会收到\n");

		Thread.sleep(1000);

		// 消息4：再发一条 VIP 消息
		Message msg4 = new Message(TOPIC, "VIP", "VIP消息：VIP订单004".getBytes());
		SendResult result4 = producer.send(msg4);
		System.out.println("发送消息4: " + result4.getSendStatus());
		System.out.println("  内容: VIP消息：VIP订单004");
		System.out.println("  Tag: VIP");
		System.out.println("  预期: 消费者组1和消费者组2都会收到\n");

		producer.shutdown();
		System.out.println("生产者已关闭\n");
	}
}
