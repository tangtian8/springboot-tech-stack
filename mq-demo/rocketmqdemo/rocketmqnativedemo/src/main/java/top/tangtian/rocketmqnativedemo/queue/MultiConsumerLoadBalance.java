package top.tangtian.rocketmqnativedemo.queue;

/**
 * @author tangtian
 * @date 2026-01-03 10:51
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.Set;

/**
 * 演示多个消费者如何分配队列
 */
// ============================================
// 队列分配算法详解
// ============================================

/*

RocketMQ 默认使用 "平均分配算法"（AllocateMessageQueueAveragely）

示例场景：

情况1：Topic有4个队列，1个消费者
├─ Consumer1: Queue 0, 1, 2, 3  （全部队列）

情况2：Topic有4个队列，2个消费者
├─ Consumer1: Queue 0, 1  （前2个队列）
└─ Consumer2: Queue 2, 3  （后2个队列）

情况3：Topic有4个队列，3个消费者
├─ Consumer1: Queue 0, 1  （2个队列）
├─ Consumer2: Queue 2     （1个队列）
└─ Consumer3: Queue 3     （1个队列）

情况4：Topic有4个队列，5个消费者
├─ Consumer1: Queue 0     （1个队列）
├─ Consumer2: Queue 1     （1个队列）
├─ Consumer3: Queue 2     （1个队列）
├─ Consumer4: Queue 3     （1个队列）
└─ Consumer5: 无队列      （空闲，浪费资源）

关键结论：
1. 消费者数量 <= 队列数量：可以充分利用
2. 消费者数量 > 队列数量：部分消费者空闲，浪费资源
3. 最佳配置：消费者数量 = 队列数量

*/

// ============================================
// 完整的消息流转流程（带队列）
// ============================================

/*

                        ┌─────────────────────────────────┐
                        │         NameServer             │
                        │  (存储Topic和Queue的路由信息)    │
                        └─────────────────────────────────┘
                                      ▲
                                      │ ① 查询路由
            ┌─────────────────────────┼─────────────────────────┐
            │                         │                         │
            ▼                         │                         ▼
    ┌───────────────┐                 │                 ┌───────────────┐
    │   Producer    │                 │                 │   Consumer    │
    │               │                 │                 │               │
    │ ② 选择Queue   │                 │                 │ ⑤ 分配Queue   │
    └───────┬───────┘                 │                 └───────┬───────┘
            │                         │                         │
            │ ③ 发送消息                │                         │ ⑥ 拉取消息
            ▼                         ▼                         ▼
    ┌─────────────────────────────────────────────────────────────┐
    │                         Broker                              │
    │                                                             │
    │  Topic: order_topic                                         │
    │  ├─ Queue 0  [msg1, msg4, msg7, ...]  ◄─────────┐          │
    │  ├─ Queue 1  [msg2, msg5, msg8, ...]            │          │
    │  ├─ Queue 2  [msg3, msg6, msg9, ...]      ④ 存储消息       │
    │  └─ Queue 3  [msg10, msg11, ...]                │          │
    │                                                  │          │
    │  CommitLog (实际存储) ──────────────────────────┘          │
    └─────────────────────────────────────────────────────────────┘

流程说明：

① Producer 从 NameServer 查询 Topic 的路由信息
   - 获取 Topic 有哪些 Broker
   - 获取每个 Broker 有哪些 Queue

② Producer 选择一个 Queue 发送消息
   - 默认：轮询选择
   - 顺序消息：hash 选择
   - 自定义：实现 MessageQueueSelector

③ 消息发送到 Broker 的指定 Queue

④ Broker 将消息存储到 CommitLog
   - CommitLog 是所有消息的物理存储
   - ConsumeQueue 是逻辑队列，存储消息在 CommitLog 的索引

⑤ Consumer 从 Broker 获取分配的 Queue
   - 同一消费者组内的多个 Consumer
   - 会平均分配 Queue（负载均衡）

⑥ Consumer 从分配的 Queue 拉取消息并消费

*/
public class MultiConsumerLoadBalance {

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];

		String consumerGroup = "test_consumer_group";
		String topic = "order_topic";

		/*
		 * 场景：
		 * - Topic 有 4 个队列：Queue 0, 1, 2, 3
		 * - 启动 2 个消费者实例
		 *
		 * 预期分配：
		 * - 消费者1: Queue 0, 1
		 * - 消费者2: Queue 2, 3
		 *
		 * 每个消费者负责 2 个队列，实现负载均衡！
		 */

		// ========================================
		// 消费者实例1
		// ========================================
		DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(consumerGroup);
		consumer1.setNamesrvAddr(namesrvAddr);
		consumer1.setInstanceName("consumer_1");  // 实例名称
		consumer1.subscribe(topic, "*");

		consumer1.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[消费者1] 消费消息:");
				System.out.println("  内容: " + new String(msg.getBody()));
				System.out.println("  队列ID: " + msg.getQueueId());
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer1.start();
		System.out.println("消费者1启动");

		Thread.sleep(3000);  // 等待队列分配完成

		// 查看消费者1分配的队列
		System.out.println("\n消费者1分配的队列：");
		printAllocatedQueues(consumer1);

		// ========================================
		// 消费者实例2
		// ========================================
		DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer(consumerGroup);
		consumer2.setNamesrvAddr(namesrvAddr);
		consumer2.setInstanceName("consumer_2");  // 实例名称
		consumer2.subscribe(topic, "*");

		consumer2.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[消费者2] 消费消息:");
				System.out.println("  内容: " + new String(msg.getBody()));
				System.out.println("  队列ID: " + msg.getQueueId());
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer2.start();
		System.out.println("消费者2启动");

		Thread.sleep(3000);  // 等待队列重新分配

		// ========================================
		// 核心：队列重新分配（Rebalance）
		// ========================================
		System.out.println("\n队列重新分配后：");
		System.out.println("消费者1分配的队列：");
		printAllocatedQueues(consumer1);

		System.out.println("\n消费者2分配的队列：");
		printAllocatedQueues(consumer2);

		/*
		 * 输出示例：
		 *
		 * 消费者1分配的队列：
		 *   order_topic@broker-a, queueId=0
		 *   order_topic@broker-a, queueId=1
		 *
		 * 消费者2分配的队列：
		 *   order_topic@broker-a, queueId=2
		 *   order_topic@broker-a, queueId=3
		 *
		 * 关键理解：
		 * 1. 同一个消费者组内，队列会平均分配给各个消费者
		 * 2. 每个队列只会被一个消费者消费（避免重复消费）
		 * 3. 当消费者数量变化时，会触发 Rebalance（重新分配）
		 */

		// ========================================
		// 模拟消费者1下线
		// ========================================
		Thread.sleep(10000);
		System.out.println("\n模拟消费者1下线...");
		consumer1.shutdown();

		Thread.sleep(3000);  // 等待队列重新分配

		System.out.println("\n消费者1下线后，消费者2的队列分配：");
		printAllocatedQueues(consumer2);

		/*
		 * 输出示例：
		 *
		 * 消费者2分配的队列：
		 *   order_topic@broker-a, queueId=0
		 *   order_topic@broker-a, queueId=1
		 *   order_topic@broker-a, queueId=2
		 *   order_topic@broker-a, queueId=3
		 *
		 * 关键：消费者1下线后，它负责的队列0和队列1被分配给消费者2
		 * 这就是 RocketMQ 的高可用机制！
		 */

		Thread.sleep(Long.MAX_VALUE);
	}

	/**
	 * 打印消费者分配的队列
	 */
	private static void printAllocatedQueues(DefaultMQPushConsumer consumer) {
		Set<MessageQueue> queues =
				consumer.getDefaultMQPushConsumerImpl()
						.getRebalanceImpl()
						.getProcessQueueTable()
						.keySet();

		for (org.apache.rocketmq.common.message.MessageQueue queue : queues) {
			System.out.println("  " + queue.getTopic() +
					"@" + queue.getBrokerName() +
					", queueId=" + queue.getQueueId());
		}
	}
}
