package top.tangtian.rocketmqnativedemo.queue;

/**
 * @author tangtian
 * @date 2026-01-03 10:50
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;
import java.util.Set;

/**
 * 演示消费者如何从队列消费消息
 */
public class ConsumerQueueAllocation {

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];

		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer_group");
		consumer.setNamesrvAddr(namesrvAddr);

		String topic = "order_topic";
		consumer.subscribe(topic, "*");

		// ========================================
		// 注册消息监听器
		// ========================================
		consumer.registerMessageListener(new MessageListenerConcurrently() {
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(
					List<MessageExt> msgs,
					ConsumeConcurrentlyContext context) {

				for (MessageExt msg : msgs) {
					// ========================================
					// 核心：可以看到消息来自哪个队列
					// ========================================
					System.out.println("========================================");
					System.out.println("消费消息：");
					System.out.println("  消息内容: " + new String(msg.getBody()));
					System.out.println("  来自Broker: " + msg.getBrokerName());
					System.out.println("  来自队列ID: " + msg.getQueueId());  // ← 关键！
					System.out.println("  队列偏移量: " + msg.getQueueOffset());
					System.out.println("  消息ID: " + msg.getMsgId());
					System.out.println("  消费者组: test_consumer_group");
				}

				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});

		consumer.start();
		System.out.println("消费者启动成功");
		System.out.println("等待消息...\n");

		// 每隔10秒查看一次队列分配情况
		while (true) {
			Thread.sleep(10000);

			// ========================================
			// 核心：查看当前消费者分配到哪些队列
			// ========================================
			Set<MessageQueue> queues =
					consumer.getDefaultMQPushConsumerImpl()
							.getRebalanceImpl()
							.getProcessQueueTable()
							.keySet();

			System.out.println("\n========================================");
			System.out.println("当前消费者分配的队列：");
			for (org.apache.rocketmq.common.message.MessageQueue queue : queues) {
				System.out.println("  Topic: " + queue.getTopic());
				System.out.println("  Broker: " + queue.getBrokerName());
				System.out.println("  队列ID: " + queue.getQueueId());
				System.out.println("  --------");
			}
			System.out.println("========================================\n");
		}
	}
}

