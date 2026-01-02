package top.tangtian.rocketmqdemo.rocketmqconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2026-01-02 17:23
 */
@Slf4j
@Component
@RocketMQTransactionListener
public class OrderTransactionListener implements RocketMQLocalTransactionListener {

	/**
	 * 执行本地事务
	 */
	@Override
	public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		String orderId = (String) arg;
		log.info("执行本地事务: orderId={}", orderId);

		try {
			// 执行本地数据库操作
			boolean success = performLocalTransaction(orderId);

			if (success) {
				log.info("本地事务执行成功: orderId={}", orderId);
				return RocketMQLocalTransactionState.COMMIT;
			} else {
				log.warn("本地事务执行失败: orderId={}", orderId);
				return RocketMQLocalTransactionState.ROLLBACK;
			}
		} catch (Exception e) {
			log.error("本地事务异常: orderId={}", orderId, e);
			return RocketMQLocalTransactionState.UNKNOWN;
		}
	}

	/**
	 * 检查本地事务状态（回查）
	 */
	@Override
	public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
		String orderId = (String) msg.getHeaders().get("orderId");
		log.info("回查本地事务状态: orderId={}", orderId);

		// 查询本地数据库，检查事务状态
		boolean isSuccess = checkTransactionStatus(orderId);

		if (isSuccess) {
			return RocketMQLocalTransactionState.COMMIT;
		} else {
			return RocketMQLocalTransactionState.ROLLBACK;
		}
	}

	private boolean performLocalTransaction(String orderId) {
		// 模拟本地事务操作
		// 实际场景中这里会执行数据库操作
		return true;
	}

	private boolean checkTransactionStatus(String orderId) {
		// 模拟查询事务状态
		// 实际场景中查询数据库
		return true;
	}
}