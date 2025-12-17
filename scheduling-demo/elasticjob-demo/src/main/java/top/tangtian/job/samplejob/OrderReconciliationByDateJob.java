package top.tangtian.job.samplejob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;
import top.tangtian.service.ReconciliationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author tangtian
 * @date 2025-12-17 09:38
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReconciliationByDateJob implements SimpleJob {

	private final ReconciliationService reconciliationService;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void execute(ShardingContext context) {
		log.info("========== 开始执行订单对账任务 ==========");
		log.info("任务名称: {}", context.getJobName());
		log.info("分片总数: {}", context.getShardingTotalCount());
		log.info("当前分片项: {}", context.getShardingItem());
		log.info("分片参数: {}", context.getShardingParameter());
		log.info("任务参数: {}", context.getJobParameter());

		try {
			// 获取分片参数(日期)
			String dateStr = context.getShardingParameter();
			LocalDate targetDate;

			if (dateStr != null && !dateStr.isEmpty()) {
				targetDate = LocalDate.parse(dateStr, FORMATTER);
			} else {
				// 如果没有指定日期,默认处理昨天的数据
				targetDate = LocalDate.now().minusDays(1);
			}

			log.info("分片 {} 开始处理日期: {}", context.getShardingItem(), targetDate);

			// 执行对账逻辑
			reconciliationService.performOrderReconciliation(targetDate);

			log.info("分片 {} 完成处理日期: {}", context.getShardingItem(), targetDate);

		} catch (Exception e) {
			log.error("分片 {} 执行失败", context.getShardingItem(), e);
			throw new RuntimeException("订单对账任务执行失败", e);
		}

		log.info("========== 订单对账任务执行完成 ==========");
	}
}
