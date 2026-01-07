package top.tangtian.elacticjob.job.dataflowjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.stereotype.Component;
import top.tangtian.elacticjob.entity.ReconciliationDetail;
import top.tangtian.elacticjob.repository.ReconciliationDetailRepository;
import top.tangtian.elacticjob.service.ReconciliationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2025-12-17 09:39
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataflowReconciliationJob implements DataflowJob<ReconciliationDetail> {

	private final ReconciliationDetailRepository detailRepository;
	private final ReconciliationService reconciliationService;

	// 获取待处理数据
	@Override
	public List<ReconciliationDetail> fetchData(ShardingContext context) {
		log.info("========== 数据流任务 - 获取数据 ==========");
		log.info("分片: {}/{}, 状态: {}",
				context.getShardingItem() + 1,
				context.getShardingTotalCount(),
				context.getShardingParameter());

		try {
			// 根据分片参数获取不同状态的数据
			String statusStr = context.getShardingParameter();
			ReconciliationDetail.ProcessStatus status =
					ReconciliationDetail.ProcessStatus.valueOf(statusStr);

			// 每次获取100条待处理数据
			List<ReconciliationDetail> details = detailRepository.findByProcessStatus(status)
					.stream()
					.limit(100)
					.collect(Collectors.toList());

			log.info("分片 {} 获取到 {} 条 {} 状态的数据",
					context.getShardingItem(), details.size(), status);

			return details;

		} catch (Exception e) {
			log.error("获取数据失败 - 分片: {}", context.getShardingItem(), e);
			return List.of();
		}
	}
	// 处理数据
	@Override
	public void processData(ShardingContext context, List<ReconciliationDetail> data) {
		log.info("========== 数据流任务 - 处理数据 ==========");
		log.info("分片 {} 开始处理 {} 条数据", context.getShardingItem(), data.size());

		int successCount = 0;
		int failCount = 0;

		for (ReconciliationDetail detail : data) {
			try {
				// 处理每条对账差异
				reconciliationService.processReconciliationDetail(detail);
				successCount++;

				// 模拟处理耗时
				Thread.sleep(100);

			} catch (Exception e) {
				log.error("处理数据失败 - ID: {}", detail.getId(), e);
				failCount++;
			}
		}

		log.info("分片 {} 处理完成: 成功 {}, 失败 {}",
				context.getShardingItem(), successCount, failCount);
	}
}