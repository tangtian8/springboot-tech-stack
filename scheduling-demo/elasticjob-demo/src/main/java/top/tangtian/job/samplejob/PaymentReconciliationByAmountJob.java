package top.tangtian.job.samplejob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;
import top.tangtian.entity.Payment;
import top.tangtian.repository.PaymentRepository;
import top.tangtian.service.ReconciliationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author tangtian
 * @date 2025-12-17 09:38
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReconciliationByAmountJob implements SimpleJob {

	private final ReconciliationService reconciliationService;
	private final PaymentRepository paymentRepository;

	@Override
	public void execute(ShardingContext context) {
		log.info("========== 支付对账任务开始 ==========");
		log.info("分片项: {}/{}", context.getShardingItem() + 1, context.getShardingTotalCount());
		log.info("金额范围: {}", context.getShardingParameter());

		try {
			// 解析金额范围 "0-100" -> [0, 100]
			String[] range = context.getShardingParameter().split("-");
			BigDecimal minAmount = new BigDecimal(range[0]);
			BigDecimal maxAmount = new BigDecimal(range[1]);

			LocalDate yesterday = LocalDate.now().minusDays(1);
			LocalDateTime startTime = yesterday.atStartOfDay();
			LocalDateTime endTime = yesterday.atTime(LocalTime.MAX);

			log.info("处理金额范围: {} - {}, 日期: {}", minAmount, maxAmount, yesterday);

			// 查询该金额范围的支付记录
			List<Payment> payments = paymentRepository.findByTimeRange(startTime, endTime)
					.stream()
					.filter(p -> p.getAmount().compareTo(minAmount) >= 0
							&& p.getAmount().compareTo(maxAmount) < 0)
					.toList();

			log.info("分片 {} 查询到 {} 条支付记录", context.getShardingItem(), payments.size());

			// 执行对账
			reconciliationService.performPaymentReconciliationByAmount(
					yesterday, minAmount, maxAmount, payments);

			log.info("分片 {} 处理完成", context.getShardingItem());

		} catch (Exception e) {
			log.error("支付对账任务失败 - 分片: {}", context.getShardingItem(), e);
			throw new RuntimeException("支付对账任务执行失败", e);
		}

		log.info("========== 支付对账任务完成 ==========");
	}
}
