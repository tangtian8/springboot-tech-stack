package top.tangtian.rocketmqnativedemo.queue;

/**
 * @author tangtian
 * @date 2026-01-03 10:48
 */

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * 演示生产者如何选择队列发送消息
 */
public class ProducerQueueSelection {

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];

		DefaultMQProducer producer = new DefaultMQProducer("test_producer");
		producer.setNamesrvAddr(namesrvAddr);
		producer.start();

		String topic = "order_topic";

		System.out.println("========================================");
		System.out.println("演示生产者队列选择机制");
		System.out.println("========================================\n");

		// ========================================
		// 方式1：默认发送（轮询选择队列）
		// ========================================
		System.out.println("方式1：默认发送（RocketMQ自动轮询选择队列）");
		System.out.println("----------------------------------------");

		for (int i = 0; i < 5; i++) {
			Message msg = new Message(topic, ("消息-" + i).getBytes());
			SendResult result = producer.send(msg);

			// 查看消息发送到哪个队列
			MessageQueue mq = result.getMessageQueue();
			System.out.println("消息-" + i + " 发送到：");
			System.out.println("  Broker: " + mq.getBrokerName());
			System.out.println("  队列ID: " + mq.getQueueId());
			System.out.println("  消息ID: " + result.getMsgId());
		}

		/*
		 * 输出示例（轮询机制）：
		 * 消息-0 发送到：Broker: broker-a，队列ID: 0
		 * 消息-1 发送到：Broker: broker-a，队列ID: 1
		 * 消息-2 发送到：Broker: broker-a，队列ID: 2
		 * 消息-3 发送到：Broker: broker-a，队列ID: 3
		 * 消息-4 发送到：Broker: broker-a，队列ID: 0  ← 重新从0开始
		 *
		 * 这就是默认的"轮询策略"！
		 */

		System.out.println("\n========================================\n");

		// ========================================
		// 方式2：手动指定队列（顺序消息）
		// ========================================
		System.out.println("方式2：手动指定队列（用于顺序消息）");
		System.out.println("----------------------------------------");

		// 场景：同一个订单的消息要发送到同一个队列，保证顺序
		String orderId = "ORDER_12345";

		for (int i = 0; i < 3; i++) {
			String status = "";
			if (i == 0) status = "已创建";
			if (i == 1) status = "已支付";
			if (i == 2) status = "已发货";

			Message msg = new Message(topic, ("订单" + orderId + "-" + status).getBytes());

			// ========================================
			// 核心：使用 MessageQueueSelector 选择队列
			// ========================================
			SendResult result = producer.send(
					msg,                          // 消息
					new MessageQueueSelector() {  // 队列选择器
						@Override
						public MessageQueue select(
								List<MessageQueue> mqs,  // 所有可用队列
								Message msg,             // 当前消息
								Object arg) {            // 自定义参数（orderId）

							// 根据 orderId 的 hash 值选择队列
							String orderId = (String) arg;
							int hash = Math.abs(orderId.hashCode());
							int index = hash % mqs.size();  // 取模，确保同一订单在同一队列

							MessageQueue selectedQueue = mqs.get(index);
							System.out.println("  选择队列: " + selectedQueue.getQueueId() +
									" (orderId=" + orderId + ", hash=" + hash + ")");

							return selectedQueue;
						}
					},
					orderId  // 传递给选择器的参数
			);

			System.out.println("发送: " + orderId + "-" + status);
			System.out.println("  目标队列: " + result.getMessageQueue().getQueueId());
		}

		/*
		 * 输出示例（hash选择）：
		 *   选择队列: 2 (orderId=ORDER_12345, hash=1234567890)
		 * 发送: ORDER_12345-已创建
		 *   目标队列: 2
		 *   选择队列: 2 (orderId=ORDER_12345, hash=1234567890)
		 * 发送: ORDER_12345-已支付
		 *   目标队列: 2
		 *   选择队列: 2 (orderId=ORDER_12345, hash=1234567890)
		 * 发送: ORDER_12345-已发货
		 *   目标队列: 2
		 *
		 * 关键：同一个订单的所有消息都发送到队列2！
		 * 这样消费者从队列2顺序消费，就能保证订单状态的顺序性
		 */

		System.out.println("\n========================================\n");

		// ========================================
		// 方式3：直接指定队列ID
		// ========================================
		System.out.println("方式3：直接指定队列ID");
		System.out.println("----------------------------------------");

		// 获取所有队列
		List<MessageQueue> queues = producer.fetchPublishMessageQueues(topic);

		// 直接发送到队列2
		MessageQueue targetQueue = queues.get(2);
		Message msg = new Message(topic, "指定发送到队列2".getBytes());
		SendResult result = producer.send(msg, targetQueue);

		System.out.println("消息发送到指定队列：");
		System.out.println("  Broker: " + result.getMessageQueue().getBrokerName());
		System.out.println("  队列ID: " + result.getMessageQueue().getQueueId());

		producer.shutdown();
	}
}