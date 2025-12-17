package top.tangtian.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.entity.Order;
import top.tangtian.entity.Payment;
import top.tangtian.entity.ReconciliationDetail;
import top.tangtian.entity.ReconciliationRecord;
import top.tangtian.repository.OrderRepository;
import top.tangtian.repository.PaymentRepository;
import top.tangtian.repository.ReconciliationDetailRepository;
import top.tangtian.repository.ReconciliationRecordRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author tangtian
 * @date 2025-12-16 18:15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final ReconciliationRecordRepository recordRepository;
	private final ReconciliationDetailRepository detailRepository;

	// 原有的对账方法...
	@Transactional
	public void performOrderReconciliation(LocalDate date) {
		log.info("执行订单对账: {}", date);
		// ... 原有实现 ...
	}

	// 按金额范围对账
	@Transactional
	public void performPaymentReconciliationByAmount(LocalDate date,
													 BigDecimal minAmount,
													 BigDecimal maxAmount,
													 List<Payment> payments) {
		log.info("执行支付对账 - 日期: {}, 金额范围: {} - {}", date, minAmount, maxAmount);

		ReconciliationRecord record = ReconciliationRecord.builder()
				.recordDate(date)
				.type(ReconciliationRecord.ReconciliationType.PAYMENT)
				.status(ReconciliationRecord.ReconciliationStatus.RUNNING)
				.startTime(LocalDateTime.now())
				.totalCount(0)
				.matchedCount(0)
				.unmatchedCount(0)
				.totalAmount(BigDecimal.ZERO)
				.matchedAmount(BigDecimal.ZERO)
				.unmatchedAmount(BigDecimal.ZERO)
				.build();

		try {
			int matchedCount = 0;
			BigDecimal totalAmount = BigDecimal.ZERO;
			BigDecimal matchedAmount = BigDecimal.ZERO;
			List<ReconciliationDetail> details = new ArrayList<>();

			for (Payment payment : payments) {
				totalAmount = totalAmount.add(payment.getAmount());

				Optional<Order> orderOpt = orderRepository.findByOrderNo(payment.getOrderNo());
				if (orderOpt.isPresent() && orderOpt.get().getAmount().equals(payment.getAmount())) {
					matchedCount++;
					matchedAmount = matchedAmount.add(payment.getAmount());
				} else {
					details.add(createDetail(record.getId(), payment.getOrderNo(),
							ReconciliationDetail.DifferenceType.AMOUNT_MISMATCH,
							orderOpt.map(Order::getAmount).orElse(null),
							payment.getAmount(),
							payment.getAmount(),
							"金额不匹配"));
				}
			}

			record.setTotalCount(payments.size());
			record.setMatchedCount(matchedCount);
			record.setUnmatchedCount(payments.size() - matchedCount);
			record.setTotalAmount(totalAmount);
			record.setMatchedAmount(matchedAmount);
			record.setUnmatchedAmount(totalAmount.subtract(matchedAmount));
			record.setStatus(ReconciliationRecord.ReconciliationStatus.SUCCESS);
			record.setEndTime(LocalDateTime.now());

			recordRepository.save(record);
			detailRepository.saveAll(details);

		} catch (Exception e) {
			record.setStatus(ReconciliationRecord.ReconciliationStatus.FAILED);
			record.setErrorMessage(e.getMessage());
			record.setEndTime(LocalDateTime.now());
			recordRepository.save(record);
			throw e;
		}
	}

	// 按用户ID范围对账
	@Transactional
	public void performUserOrderReconciliation(LocalDate date,
											   long minUserId,
											   long maxUserId,
											   List<Order> orders) {
		log.info("执行用户订单对账 - 日期: {}, 用户ID: {} - {}", date, minUserId, maxUserId);

		Map<Long, List<Order>> userOrdersMap = new HashMap<>();
		for (Order order : orders) {
			userOrdersMap.computeIfAbsent(order.getUserId(), k -> new ArrayList<>()).add(order);
		}

		log.info("共 {} 个用户, {} 条订单", userOrdersMap.size(), orders.size());

		for (Map.Entry<Long, List<Order>> entry : userOrdersMap.entrySet()) {
			Long userId = entry.getKey();
			List<Order> userOrders = entry.getValue();

			log.info("处理用户 {} 的 {} 条订单", userId, userOrders.size());

			// 对每个用户的订单进行对账
			for (Order order : userOrders) {
				Optional<Payment> paymentOpt = paymentRepository.findByOrderNo(order.getOrderNo());
				if (paymentOpt.isEmpty() || !paymentOpt.get().getAmount().equals(order.getAmount())) {
					log.warn("用户 {} 的订单 {} 存在差异", userId, order.getOrderNo());
				}
			}
		}
	}

	// 处理对账差异
	@Transactional
	public void processReconciliationDetail(ReconciliationDetail detail) {
		log.info("处理对账差异 - ID: {}, 订单号: {}", detail.getId(), detail.getOrderNo());

		try {
			// 更新状态为处理中
			detail.setProcessStatus(ReconciliationDetail.ProcessStatus.PROCESSING);
			detailRepository.save(detail);

			// 执行差异处理逻辑
			switch (detail.getDifferenceType()) {
				case AMOUNT_MISMATCH:
					handleAmountMismatch(detail);
					break;
				case ORDER_MISSING:
					handleOrderMissing(detail);
					break;
				case PAYMENT_MISSING:
					handlePaymentMissing(detail);
					break;
				default:
					break;
			}

			// 更新状态为已解决
			detail.setProcessStatus(ReconciliationDetail.ProcessStatus.RESOLVED);
			detailRepository.save(detail);

			log.info("差异处理完成 - ID: {}", detail.getId());

		} catch (Exception e) {
			log.error("差异处理失败 - ID: {}", detail.getId(), e);
			detail.setProcessStatus(ReconciliationDetail.ProcessStatus.PENDING);
			detailRepository.save(detail);
			throw e;
		}
	}

	private void handleAmountMismatch(ReconciliationDetail detail) {
		log.info("处理金额不匹配: {}", detail.getOrderNo());
		// 实现金额差异处理逻辑
	}

	private void handleOrderMissing(ReconciliationDetail detail) {
		log.info("处理订单缺失: {}", detail.getOrderNo());
		// 实现订单缺失处理逻辑
	}

	private void handlePaymentMissing(ReconciliationDetail detail) {
		log.info("处理支付缺失: {}", detail.getOrderNo());
		// 实现支付缺失处理逻辑
	}

	private ReconciliationDetail createDetail(Long recordId, String orderNo,
											  ReconciliationDetail.DifferenceType type,
											  BigDecimal systemAmount,
											  BigDecimal thirdPartyAmount,
											  BigDecimal differenceAmount,
											  String description) {
		return ReconciliationDetail.builder()
				.recordId(recordId)
				.orderNo(orderNo)
				.differenceType(type)
				.systemAmount(systemAmount)
				.thirdPartyAmount(thirdPartyAmount)
				.differenceAmount(differenceAmount)
				.description(description)
				.createdTime(LocalDateTime.now())
				.processStatus(ReconciliationDetail.ProcessStatus.PENDING)
				.build();
	}
}