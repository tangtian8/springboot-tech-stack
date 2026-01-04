package top.tangtian.rocketmqnativedemo.queue;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-03 10:46
 */
/*
核心概念：

Topic ─┬─ Queue 0  ←── 消息存储和负载均衡的基本单位
       ├─ Queue 1
       ├─ Queue 2
       └─ Queue 3  (默认4个队列)

1. 一个 Topic 包含多个 Queue（消息队列）
2. 生产者发送消息时，会选择一个 Queue
3. 消费者消费消息时，会被分配若干个 Queue
4. Queue 是实现负载均衡的关键
*/

// ============================================
// 案例一：查看 Topic 的队列信息
//
public class QueryTopicQueues {
	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];

		DefaultMQProducer producer = new DefaultMQProducer("test_producer");
		producer.setNamesrvAddr(namesrvAddr);
		producer.start();

		String topic = "order_topic";

		// ========================================
		// 核心：查询 Topic 的所有队列
		// ========================================
		/*
		 * MessageQueue 包含三个关键信息：
		 * 1. Topic 名称
		 * 2. Broker 名称
		 * 3. Queue ID（队列编号）
		 */
		List<MessageQueue> queues = producer.fetchPublishMessageQueues(topic);

		System.out.println("Topic: " + topic + " 的队列信息：");
		System.out.println("总队列数: " + queues.size());
		System.out.println("========================================");

		for (MessageQueue queue : queues) {
			System.out.println("队列详情：");
			System.out.println("  Topic: " + queue.getTopic());
			System.out.println("  Broker名称: " + queue.getBrokerName());
			System.out.println("  队列ID: " + queue.getQueueId());
			System.out.println("  完整标识: " + queue);  // broker-a:0
			System.out.println("----------------------------------------");
		}

		/*
		 * 输出示例：
		 *
		 * Topic: order_topic 的队列信息：
		 * 总队列数: 4
		 * ========================================
		 * 队列详情：
		 *   Topic: order_topic
		 *   Broker名称: broker-a
		 *   队列ID: 0
		 *   完整标识: MessageQueue [topic=order_topic, brokerName=broker-a, queueId=0]
		 * ----------------------------------------
		 * 队列详情：
		 *   Topic: order_topic
		 *   Broker名称: broker-a
		 *   队列ID: 1
		 *   完整标识: MessageQueue [topic=order_topic, brokerName=broker-a, queueId=1]
		 * ----------------------------------------
		 * 队列详情：
		 *   Topic: order_topic
		 *   Broker名称: broker-a
		 *   队列ID: 2
		 *   完整标识: MessageQueue [topic=order_topic, brokerName=broker-a, queueId=2]
		 * ----------------------------------------
		 * 队列详情：
		 *   Topic: order_topic
		 *   Broker名称: broker-a
		 *   队列ID: 3
		 *   完整标识: MessageQueue [topic=order_topic, brokerName=broker-a, queueId=3]
		 * ----------------------------------------
		 */

		producer.shutdown();
	}
}
