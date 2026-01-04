package top.tangtian.rocketmqnativedemo;

/**
 * @author tangtian
 * @date 2026-01-03 10:29
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 演示：同一个消费者组内的多个消费者实例，负载均衡消费
 */
public class LoadBalanceDemo {

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];

		// ========================================
		// 消费者实例1（属于同一个组）
		// ========================================
		DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer("order_process_group");
		consumer1.setNamesrvAddr(namesrvAddr);
		consumer1.setInstanceName("consumer_instance_1");  // 实例名称（区分）

		consumer1.subscribe("order_topic", "*");

		consumer1.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[实例1] 消费消息: " + new String(msg.getBody()));
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer1.start();
		System.out.println("消费者实例1启动");

		// ========================================
		// 消费者实例2（属于同一个组）
		// ========================================
		DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer("order_process_group");
		consumer2.setNamesrvAddr(namesrvAddr);
		consumer2.setInstanceName("consumer_instance_2");  // 实例名称（区分）

		consumer2.subscribe("order_topic", "*");

		consumer2.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[实例2] 消费消息: " + new String(msg.getBody()));
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer2.start();
		System.out.println("消费者实例2启动");

		/*
		 * 关键理解：
		 *
		 * consumer1 和 consumer2 属于同一个消费者组（order_process_group）
		 *
		 * 当生产者发送消息到 order_topic 时：
		 * - 消息会被分配到 consumer1 或 consumer2（二选一）
		 * - 不会同时被两个消费者消费
		 * - 实现了负载均衡，提高消费能力
		 *
		 * 这就像多台服务器处理同一个任务队列：
		 * - 每个任务只会被一台服务器处理
		 * - 多台服务器共同分担负载
		 */

		Thread.sleep(Long.MAX_VALUE);
	}
}
