package top.tangtian.rocketmqnativedemo.rebalance;

import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-03 11:10
 * *
 * 核心概念：Rebalance（重平衡）
 *
 * Rebalance 是 RocketMQ 自动调整队列分配的机制：
 * 1. 当消费者数量变化时触发
 * 2. 根据分配算法重新分配队列
 * 3. 确保队列在消费者之间均匀分布
 *
 * 队列分配的决定因素：
 * ├─ 1. Topic 的队列数量（总队列数）
 * ├─ 2. 消费者组内的消费者数量（总消费者数）
 * ├─ 3. 队列分配算法（默认：平均分配算法）
 * └─ 4. 消费者的排序（按 ClientID 排序）
 *
 */

public class QueueAllocationAlgorithm {

	/**
	 * 核心分配算法
	 *
	 * @param consumerGroup 消费者组名
	 * @param currentCID 当前消费者ID
	 * @param mqAll 所有可用队列列表
	 * @param cidAll 所有消费者ID列表（已排序）
	 * @return 分配给当前消费者的队列列表
	 */
	public List<MessageQueue> allocate(
			String consumerGroup,
			String currentCID,
			List<MessageQueue> mqAll,
			List<String> cidAll) {

		List<MessageQueue> result = new ArrayList<>();

		// ========================================
		// 第一步：验证参数
		// ========================================
		if (currentCID == null || currentCID.isEmpty()) {
			System.out.println("错误：消费者ID不能为空");
			return result;
		}

		if (mqAll == null || mqAll.isEmpty()) {
			System.out.println("错误：队列列表为空");
			return result;
		}

		if (cidAll == null || cidAll.isEmpty()) {
			System.out.println("错误：消费者列表为空");
			return result;
		}

		// ========================================
		// 第二步：找到当前消费者在列表中的位置
		// ========================================
		int index = cidAll.indexOf(currentCID);
		if (index < 0) {
			System.out.println("错误：当前消费者不在消费者列表中");
			return result;
		}

		System.out.println("\n┌─────────────────────────────────────┐");
		System.out.println("│      队列分配算法详细过程            │");
		System.out.println("└─────────────────────────────────────┘");
		System.out.println("消费者组: " + consumerGroup);
		System.out.println("当前消费者: " + currentCID);
		System.out.println("当前消费者索引: " + index);
		System.out.println("总队列数: " + mqAll.size());
		System.out.println("总消费者数: " + cidAll.size());
		System.out.println("────────────────────────────────────");

		// ========================================
		// 第三步：计算分配参数
		// ========================================
		int mqSize = mqAll.size();      // 总队列数
		int cidSize = cidAll.size();    // 总消费者数

		// mod: 余数（多出来的队列数）
		int mod = mqSize % cidSize;

		// averageSize: 商（平均每个消费者分配的队列数）
		int averageSize = mqSize / cidSize;

		System.out.println("计算参数:");
		System.out.println("  商 (averageSize) = " + mqSize + " ÷ " + cidSize + " = " + averageSize);
		System.out.println("  余数 (mod) = " + mqSize + " % " + cidSize + " = " + mod);

		// ========================================
		// 第四步：计算当前消费者应该分配多少个队列
		// ========================================
		/*
		 * 分配规则：
		 *
		 * 如果有余数（mod > 0）：
		 *   - 前 mod 个消费者：每人分配 (averageSize + 1) 个队列
		 *   - 后面的消费者：每人分配 averageSize 个队列
		 *
		 * 举例：4个队列，3个消费者
		 *   - averageSize = 4 ÷ 3 = 1
		 *   - mod = 4 % 3 = 1
		 *   - 消费者0: 分配 1 + 1 = 2 个队列（index=0 < mod=1）
		 *   - 消费者1: 分配 1 个队列（index=1 >= mod=1）
		 *   - 消费者2: 分配 1 个队列（index=2 >= mod=1）
		 */
		int mySize;  // 当前消费者应该分配的队列数

		if (mod > 0 && index < mod) {
			// 前 mod 个消费者多分配1个队列
			mySize = averageSize + 1;
			System.out.println("  当前消费者索引 " + index + " < 余数 " + mod);
			System.out.println("  → 分配队列数 = " + averageSize + " + 1 = " + mySize);
		} else {
			// 后面的消费者按平均值分配
			mySize = averageSize;
			System.out.println("  当前消费者索引 " + index + " >= 余数 " + mod);
			System.out.println("  → 分配队列数 = " + mySize);
		}

		// ========================================
		// 第五步：计算起始队列索引
		// ========================================
		/*
		 * 起始索引计算：
		 *
		 * 前 mod 个消费者：
		 *   startIndex = index × (averageSize + 1)
		 *
		 * 后面的消费者：
		 *   startIndex = index × averageSize + mod
		 *
		 * 举例：4个队列，3个消费者
		 *   - 消费者0: startIndex = 0 × 2 = 0
		 *   - 消费者1: startIndex = 1 × 1 + 1 = 2
		 *   - 消费者2: startIndex = 2 × 1 + 1 = 3
		 */
		int startIndex;

		if (mod > 0 && index < mod) {
			// 前 mod 个消费者
			startIndex = index * (averageSize + 1);
			System.out.println("  起始索引 = " + index + " × " + (averageSize + 1) + " = " + startIndex);
		} else {
			// 后面的消费者
			startIndex = index * averageSize + mod;
			System.out.println("  起始索引 = " + index + " × " + averageSize + " + " + mod + " = " + startIndex);
		}

		// ========================================
		// 第六步：分配队列
		// ========================================
		System.out.println("\n分配结果:");
		for (int i = 0; i < mySize; i++) {
			int queueIndex = (startIndex + i) % mqSize;
			MessageQueue mq = mqAll.get(queueIndex);
			result.add(mq);
			System.out.println("  ✓ Queue " + mq.getQueueId() +
					" (" + mq.getBrokerName() + ")");
		}
		System.out.println("────────────────────────────────────\n");

		return result;
	}

	/**
	 * 演示不同场景下的队列分配
	 */
	public static void main(String[] args) {
		QueueAllocationAlgorithm algorithm = new QueueAllocationAlgorithm();

		// ========================================
		// 场景1：4个队列，1个消费者
		// ========================================
		System.out.println("╔═══════════════════════════════════════╗");
		System.out.println("║  场景1：4个队列，1个消费者             ║");
		System.out.println("╚═══════════════════════════════════════╝");

		List<MessageQueue> queues1 = createQueues(4);
		List<String> consumers1 = List.of("consumer-1");

		algorithm.allocate("test-group", "consumer-1", queues1, consumers1);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1, 2, 3 (全部队列)
		 */

		// ========================================
		// 场景2：4个队列，2个消费者
		// ========================================
		System.out.println("╔═══════════════════════════════════════╗");
		System.out.println("║  场景2：4个队列，2个消费者             ║");
		System.out.println("╚═══════════════════════════════════════╝");

		List<MessageQueue> queues2 = createQueues(4);
		List<String> consumers2 = List.of("consumer-1", "consumer-2");

		algorithm.allocate("test-group", "consumer-1", queues2, consumers2);
		algorithm.allocate("test-group", "consumer-2", queues2, consumers2);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1
		 * consumer-2: Queue 2, 3
		 */

		// ========================================
		// 场景3：4个队列，3个消费者
		// ========================================
		System.out.println("╔═══════════════════════════════════════╗");
		System.out.println("║  场景3：4个队列，3个消费者             ║");
		System.out.println("╚═══════════════════════════════════════╝");

		List<MessageQueue> queues3 = createQueues(4);
		List<String> consumers3 = List.of("consumer-1", "consumer-2", "consumer-3");

		algorithm.allocate("test-group", "consumer-1", queues3, consumers3);
		algorithm.allocate("test-group", "consumer-2", queues3, consumers3);
		algorithm.allocate("test-group", "consumer-3", queues3, consumers3);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1 (2个)
		 * consumer-2: Queue 2    (1个)
		 * consumer-3: Queue 3    (1个)
		 */

		// ========================================
		// 场景4：4个队列，5个消费者
		// ========================================
		System.out.println("╔═══════════════════════════════════════╗");
		System.out.println("║  场景4：4个队列，5个消费者（浪费）     ║");
		System.out.println("╚═══════════════════════════════════════╝");

		List<MessageQueue> queues4 = createQueues(4);
		List<String> consumers4 = List.of("consumer-1", "consumer-2",
				"consumer-3", "consumer-4", "consumer-5");

		for (String consumer : consumers4) {
			algorithm.allocate("test-group", consumer, queues4, consumers4);
		}

		/*
		 * 预期结果：
		 * consumer-1: Queue 0
		 * consumer-2: Queue 1
		 * consumer-3: Queue 2
		 * consumer-4: Queue 3
		 * consumer-5: 无队列（空闲，浪费资源）
		 */

		// ========================================
		// 场景5：8个队列，3个消费者
		// ========================================
		System.out.println("╔═══════════════════════════════════════╗");
		System.out.println("║  场景5：8个队列，3个消费者             ║");
		System.out.println("╚═══════════════════════════════════════╝");

		List<MessageQueue> queues5 = createQueues(8);
		List<String> consumers5 = List.of("consumer-1", "consumer-2", "consumer-3");

		algorithm.allocate("test-group", "consumer-1", queues5, consumers5);
		algorithm.allocate("test-group", "consumer-2", queues5, consumers5);
		algorithm.allocate("test-group", "consumer-3", queues5, consumers5);

		/*
		 * 预期结果：
		 * consumer-1: Queue 0, 1, 2 (3个)
		 * consumer-2: Queue 3, 4, 5 (3个)
		 * consumer-3: Queue 6, 7    (2个)
		 *
		 * 计算过程：
		 * averageSize = 8 ÷ 3 = 2
		 * mod = 8 % 3 = 2
		 *
		 * consumer-1 (index=0 < mod=2): 2 + 1 = 3个队列
		 * consumer-2 (index=1 < mod=2): 2 + 1 = 3个队列
		 * consumer-3 (index=2 >= mod=2): 2个队列
		 */
	}

	/**
	 * 创建指定数量的队列
	 */
	private static List<MessageQueue> createQueues(int count) {
		List<MessageQueue> queues = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			queues.add(new MessageQueue("test-topic", "broker-a", i));
		}
		return queues;
	}
}

