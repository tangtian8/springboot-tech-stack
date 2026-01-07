package top.tangtian.elacticjob.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts;
import org.apache.shardingsphere.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener;

/**
 * @author tangtian
 * @date 2025-12-17 09:50
 */
@Slf4j
public class ReconciliationJobListener extends AbstractDistributeOnceElasticJobListener {

	public ReconciliationJobListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
		super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
		log.info("==================== 对账任务开始 ====================");
		log.info("任务名称: {}", shardingContexts.getJobName());
		log.info("任务参数: {}", shardingContexts.getJobParameter());
		log.info("分片总数: {}", shardingContexts.getShardingTotalCount());
		log.info("所有分片项: {}", shardingContexts.getShardingItemParameters());
	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
		log.info("==================== 对账任务完成 ====================");
		log.info("任务名称: {}", shardingContexts.getJobName());
		log.info("执行完成时间: {}", System.currentTimeMillis());
	}

	@Override
	public String getType() {
		return "";
	}
}
