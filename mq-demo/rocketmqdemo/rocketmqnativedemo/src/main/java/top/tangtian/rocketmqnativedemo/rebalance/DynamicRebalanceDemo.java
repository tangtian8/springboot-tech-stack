package top.tangtian.rocketmqnativedemo.rebalance;

/**
 * @author tangtian
 * @date 2026-01-03 11:11
 * // ============================================
 * // Rebalance 触发时机
 * // ============================================
 *
 * /*
 * Rebalance（重平衡）会在以下情况触发：
 *
 * 1. 消费者启动时
 *    └─ 新消费者加入消费者组，触发重新分配
 *
 * 2. 消费者停止时
 *    └─ 消费者下线，其队列需要分配给其他消费者
 *
 * 3. Topic 队列数量变化
 *    └─ 管理员增加或减少队列，触发重新分配
 *
 * 4. 定期检查（默认20秒）
 *    └─ 消费者定期检查是否需要重平衡
 *
 * 触发流程：
 *
 * 消费者1启动
 *     ↓
 * 向Broker注册
 *     ↓
 * Broker通知所有消费者
 *     ↓
 * 所有消费者触发Rebalance
 *     ↓
 * 重新分配队列
 *     ↓
 * 从新队列拉取消息
 *
 * 		 * // ============================================
 * 		 * // 实际运行中的队列分配演示
 * 		 * // ============================================
 * */


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 演示动态队列分配过程
 */
public class DynamicRebalanceDemo {

	private static final String TOPIC = "test-topic";
	private static final String GROUP = "test-group";

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];

		System.out.println("╔════════════════════════════════════════╗");
		System.out.println("║    动态队列分配演示                     ║");
		System.out.println("╚════════════════════════════════════════╝\n");

		// ========================================
		// 第一步：启动消费者1
		// ========================================
		System.out.println("【第一步】启动消费者1");
		System.out.println("════════════════════════════════════════");
		DefaultMQPushConsumer consumer1 = startConsumer("consumer-1",namesrvAddr);

		Thread.sleep(3000);
		System.out.println("\n消费者1分配的队列：");
		printAllocatedQueues(consumer1);

		/*
		 * 预期结果：
		 * Topic 有4个队列，只有1个消费者
		 * consumer-1: Queue 0, 1, 2, 3 (全部队列)
		 */

		Thread.sleep(5000);

		// ========================================
		// 第二步：启动消费者2（触发Rebalance）
		// ========================================
		System.out.println("\n【第二步】启动消费者2（触发Rebalance）");
		System.out.println("════════════════════════════════════════");
		DefaultMQPushConsumer consumer2 = startConsumer("consumer-2",namesrvAddr);

		Thread.sleep(3000);
		System.out.println("\nRebalance后，消费者1分配的队列：");
		printAllocatedQueues(consumer1);

		System.out.println("\nRebalance后，消费者2分配的队列：");
		printAllocatedQueues(consumer2);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1
		 * consumer-2: Queue 2, 3
		 */

		Thread.sleep(5000);

		// ========================================
		// 第三步：启动消费者3（再次触发Rebalance）
		// ========================================
		System.out.println("\n【第三步】启动消费者3（再次触发Rebalance）");
		System.out.println("════════════════════════════════════════");
		DefaultMQPushConsumer consumer3 = startConsumer("consumer-3",namesrvAddr);

		Thread.sleep(3000);
		System.out.println("\nRebalance后，消费者1分配的队列：");
		printAllocatedQueues(consumer1);

		System.out.println("\nRebalance后，消费者2分配的队列：");
		printAllocatedQueues(consumer2);

		System.out.println("\nRebalance后，消费者3分配的队列：");
		printAllocatedQueues(consumer3);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1 (2个)
		 * consumer-2: Queue 2    (1个)
		 * consumer-3: Queue 3    (1个)
		 */

		Thread.sleep(5000);

		// ========================================
		// 第四步：关闭消费者2（触发Rebalance）
		// ========================================
		System.out.println("\n【第四步】关闭消费者2（触发Rebalance）");
		System.out.println("════════════════════════════════════════");
		consumer2.shutdown();
		System.out.println("消费者2已关闭");

		Thread.sleep(3000);
		System.out.println("\nRebalance后，消费者1分配的队列：");
		printAllocatedQueues(consumer1);

		System.out.println("\nRebalance后，消费者3分配的队列：");
		printAllocatedQueues(consumer3);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1
		 * consumer-3: Queue 2, 3
		 *
		 * consumer-2原来负责的Queue 2被分配给consumer-3
		 */

		System.out.println("\n════════════════════════════════════════");
		System.out.println("演示完成，保持运行...");
		System.out.println("════════════════════════════════════════\n");

		Thread.sleep(Long.MAX_VALUE);
	}

	/**
	 * 启动消费者
	 */
	private static DefaultMQPushConsumer startConsumer(String instanceName,String namesrvAddr)
			throws Exception {

		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
		consumer.setNamesrvAddr(namesrvAddr);
		consumer.setInstanceName(instanceName);
		consumer.subscribe(TOPIC, "*");

		consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[" + instanceName + "] 消费: " +
						new String(msg.getBody()) +
						" (Queue " + msg.getQueueId() + ")");
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer.start();
		System.out.println(instanceName + " 已启动");
		return consumer;
	}

	/**
	 * 打印消费者分配的队列
	 */
	private static void printAllocatedQueues(DefaultMQPushConsumer consumer) {
		consumer.getDefaultMQPushConsumerImpl()
				.getRebalanceImpl()
				.getProcessQueueTable()
				.keySet()
				.forEach(queue ->
						System.out.println("  ├─ " + queue.getBrokerName() +
								" - Queue " + queue.getQueueId())
				);
	}
}


// ============================================
// 其他队列分配算法
// ============================================

/*
RocketMQ 支持多种队列分配算法：

1. AllocateMessageQueueAveragely（默认）
   平均分配算法，上面详细解析的就是这个

2. AllocateMessageQueueAveragelyByCircle
   循环平均分配算法

   示例：4个队列，3个消费者
   consumer-1: Queue 0, 3
   consumer-2: Queue 1
   consumer-3: Queue 2

3. AllocateMessageQueueConsistentHash
   一致性哈希分配算法
   适合消费者频繁变化的场景

4. AllocateMessageQueueByConfig
   手动配置分配算法
   可以精确指定每个消费者负责哪些队列

5. AllocateMessageQueueByMachineRoom
   机房分配算法
   按机房就近分配

6. AllocateMachineRoomNearby
   就近分配算法
   优先分配同机房的队列

如何指定分配算法：

// 原生API方式
consumer.setAllocateMessageQueueStrategy(
    new AllocateMessageQueueAveragelyByCircle()
);

// Spring Boot方式（暂不支持自定义，只能用默认的平均分配）
*/

// ============================================
// 队列分配的最佳实践
// ============================================

/*
最佳配置建议：

1. 消费者数量 = 队列数量
   ├─ 充分利用资源
   ├─ 负载最均衡
   └─ 避免空闲消费者

2. 消费者数量 < 队列数量
   ├─ 每个消费者负责多个队列
   ├─ 可以接受（正常情况）
   └─ 确保消费能力足够

3. 消费者数量 > 队列数量（不推荐）
   ├─ 部分消费者空闲
   ├─ 浪费资源
   └─ 建议减少消费者或增加队列

4. 队列数量建议
   ├─ 一般设置：4-8个队列
   ├─ 高并发：8-16个队列
   └─ 极高并发：16-32个队列

关键公式：

队列数量 ≥ 消费者数量 × 每个消费者的消费能力因子

示例：
- 单条消息处理耗时：100ms
- 期望TPS：1000条/秒
- 每个消费者TPS：10条/秒（1000ms / 100ms）
- 需要消费者数：100个
- 建议队列数：100-128个
*/