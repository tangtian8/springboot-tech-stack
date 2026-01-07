package top.tangtian.elacticjob.job.dataflowjob;

/**
 * @author tangtian
 * @date 2025-12-17 09:41
 */

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.stereotype.Component;
import top.tangtian.elacticjob.entity.Order;
import top.tangtian.elacticjob.entity.Payment;
import top.tangtian.elacticjob.repository.OrderRepository;
import top.tangtian.elacticjob.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 支付对账数据流任务 - 实时对账支付记录
 * 特点: 分片处理不同支付渠道，持续对账
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReconciliationDataflowJob implements DataflowJob<PaymentReconciliationDataflowJob.PaymentTask> {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;

	@Data
	public static class PaymentTask {
		private Payment payment;
		private LocalDateTime fetchTime;
		private int retryCount;
	}

	/**
	 * 获取待对账的支付记录
	 */
	@Override
	public List<PaymentTask> fetchData(ShardingContext context) {
		log.info("========== [支付对账] 获取待对账数据 ==========");
		log.info("分片: {}/{}, 支付渠道: {}",
				context.getShardingItem(),
				context.getShardingTotalCount(),
				context.getShardingParameter());

		try {
			// 根据分片参数获取不同支付渠道的数据
			Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(
					context.getShardingParameter());

			// 获取最近10分钟内的成功支付记录
			LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
			List<Payment> payments = paymentRepository
					.findByStatusAndPaidTimeRange(
							Payment.PaymentStatus.SUCCESS,
							tenMinutesAgo,
							LocalDateTime.now())
					.stream()
					.filter(p -> p.getMethod() == method)
					.limit(100)
					.toList();

			if (payments.isEmpty()) {
				log.info("分片 {} 暂无 {} 渠道的支付记录需要对账",
						context.getShardingItem(), method);
				return null;
			}

			// 包装成任务对象
			List<PaymentTask> tasks = new ArrayList<>();
			for (Payment payment : payments) {
				PaymentTask task = new PaymentTask();
				task.setPayment(payment);
				task.setFetchTime(LocalDateTime.now());
				task.setRetryCount(0);
				tasks.add(task);
			}

			log.info("分片 {} 获取到 {} 条 {} 渠道的支付记录",
					context.getShardingItem(), tasks.size(), method);

			return tasks;

		} catch (Exception e) {
			log.error("获取支付数据失败", e);
			return null;
		}
	}

	/**
	 * 处理支付对账
	 */
	@Override
	public void processData(ShardingContext context, List<PaymentTask> tasks) {
		log.info("========== [支付对账] 开始处理 {} 条支付记录 ==========", tasks.size());

		int matchedCount = 0;
		int mismatchCount = 0;
		int missingCount = 0;

		for (PaymentTask task : tasks) {
			Payment payment = task.getPayment();

			try {
				// 查找对应的订单
				Optional<Order> orderOpt = orderRepository.findByOrderNo(payment.getOrderNo());

				if (orderOpt.isEmpty()) {
					log.warn("支付记录 {} 找不到对应订单", payment.getPaymentNo());
					missingCount++;
					recordMissingOrder(payment);
					continue;
				}

				Order order = orderOpt.get();

				// 对账金额
				if (!payment.getAmount().equals(order.getAmount())) {
					log.warn("支付记录 {} 金额不匹配: 订单金额={}, 支付金额={}",
							payment.getPaymentNo(), order.getAmount(), payment.getAmount());
					mismatchCount++;
					recordAmountMismatch(payment, order);
				} else {
					matchedCount++;
					log.debug("支付记录 {} 对账成功", payment.getPaymentNo());
				}

				// 模拟处理耗时
				Thread.sleep(30);

			} catch (Exception e) {
				log.error("处理支付记录 {} 失败", payment.getPaymentNo(), e);
				task.setRetryCount(task.getRetryCount() + 1);
			}
		}

		log.info("分片 {} 对账完成: 匹配={}, 金额不符={}, 订单缺失={}",
				context.getShardingItem(), matchedCount, mismatchCount, missingCount);
	}

	private void recordMissingOrder(Payment payment) {
		log.warn("记录订单缺失: paymentNo={}", payment.getPaymentNo());
	}

	private void recordAmountMismatch(Payment payment, Order order) {
		BigDecimal diff = payment.getAmount().subtract(order.getAmount());
		log.warn("记录金额不匹配: orderNo={}, 差额={}", order.getOrderNo(), diff);
	}
}