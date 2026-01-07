package top.tangtian.elacticjob.job.dataflowjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.stereotype.Component;
import top.tangtian.elacticjob.entity.Order;
import top.tangtian.elacticjob.repository.OrderRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tangtian
 * @date 2025-12-17 09:41
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderImportDataflowJob implements DataflowJob<List<Order>> {

	private final OrderRepository orderRepository;
	private static final int BATCH_SIZE = 1000;
	private static final AtomicInteger lineCounter = new AtomicInteger(0);

	/**
	 * 从CSV文件读取订单数据
	 */
	@Override
	public List<List<Order>> fetchData(ShardingContext context) {
		log.info("========== [订单导入] 读取CSV文件 ==========");
		log.info("分片: {}/{}", context.getShardingItem(), context.getShardingTotalCount());

		try {
			String filePath = "/data/orders_import.csv";
			List<Order> orderBatch = new ArrayList<>();

			try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
				String line;
				int currentLine = 0;

				// 跳过已处理的行
				int startLine = context.getShardingItem() * 10000;
				int endLine = startLine + 10000;

				while ((line = reader.readLine()) != null) {
					currentLine++;

					// 跳过头部和不属于当前分片的行
					if (currentLine == 1 || currentLine < startLine || currentLine >= endLine) {
						continue;
					}

					Order order = parseCsvLine(line);
					if (order != null) {
						orderBatch.add(order);
					}

					// 达到批次大小时返回
					if (orderBatch.size() >= BATCH_SIZE) {
						break;
					}
				}
			}

			if (orderBatch.isEmpty()) {
				log.info("分片 {} 没有更多数据需要导入", context.getShardingItem());
				return null;
			}

			log.info("分片 {} 读取到 {} 条订单数据",
					context.getShardingItem(), orderBatch.size());

			// 返回批次列表
			List<List<Order>> batches = new ArrayList<>();
			batches.add(orderBatch);
			return batches;

		} catch (Exception e) {
			log.error("读取CSV文件失败", e);
			return null;
		}
	}

	/**
	 * 批量导入订单数据
	 */
	@Override
	public void processData(ShardingContext context, List<List<Order>> batches) {
		log.info("========== [订单导入] 开始导入数据 ==========");

		int totalImported = 0;
		int totalFailed = 0;

		for (List<Order> batch : batches) {
			try {
				// 批量保存
				List<Order> savedOrders = orderRepository.saveAll(batch);
				totalImported += savedOrders.size();

				log.info("分片 {} 成功导入 {} 条订单",
						context.getShardingItem(), savedOrders.size());

				// 记录进度
				int currentCount = lineCounter.addAndGet(batch.size());
				log.info("总导入进度: {} 条", currentCount);

			} catch (Exception e) {
				log.error("批量导入失败", e);
				totalFailed += batch.size();

				// 失败时尝试逐条导入
				for (Order order : batch) {
					try {
						orderRepository.save(order);
						totalImported++;
					} catch (Exception ex) {
						log.error("订单 {} 导入失败", order.getOrderNo(), ex);
					}
				}
			}
		}

		log.info("分片 {} 导入完成: 成功={}, 失败={}",
				context.getShardingItem(), totalImported, totalFailed);
	}

	/**
	 * 解析CSV行数据
	 */
	private Order parseCsvLine(String line) {
		try {
			String[] fields = line.split(",");

			if (fields.length < 5) {
				log.warn("CSV行数据格式错误: {}", line);
				return null;
			}

			return Order.builder()
					.orderNo(fields[0].trim())
					.userId(Long.parseLong(fields[1].trim()))
					.amount(new BigDecimal(fields[2].trim()))
					.status(Order.OrderStatus.valueOf(fields[3].trim()))
					.createdTime(LocalDateTime.parse(fields[4].trim()))
					.build();

		} catch (Exception e) {
			log.error("解析CSV行失败: {}", line, e);
			return null;
		}
	}
}
