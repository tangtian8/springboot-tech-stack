package top.tangtian.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.tangtian.entity.ReconciliationDetail;
import top.tangtian.entity.ReconciliationRecord;
import top.tangtian.repository.ReconciliationDetailRepository;
import top.tangtian.repository.ReconciliationRecordRepository;
import top.tangtian.service.ReconciliationService;

import java.time.LocalDate;
import java.util.List;

/**
 * @author tangtian
 * @date 2025-12-16 18:17
 */
@RestController
@RequestMapping("/api/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {
	private final ReconciliationService reconciliationService;
	private final ReconciliationRecordRepository recordRepository;
	private final ReconciliationDetailRepository detailRepository;

	@PostMapping("/order/manual")
	public ResponseEntity<String> manualOrderReconciliation(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		reconciliationService.performOrderReconciliation(date);
		return ResponseEntity.ok("订单对账任务已触发");
	}

	@PostMapping("/payment/manual")
	public ResponseEntity<String> manualPaymentReconciliation(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		reconciliationService.performOrderReconciliation(date);
		return ResponseEntity.ok("支付对账任务已触发");
	}

	@GetMapping("/records")
	public ResponseEntity<List<ReconciliationRecord>> getRecords(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(recordRepository.findByRecordDate(date));
	}

	@GetMapping("/records/{id}/details")
	public ResponseEntity<List<ReconciliationDetail>> getDetails(@PathVariable Long id) {
		return ResponseEntity.ok(detailRepository.findByRecordId(id));
	}

	@GetMapping("/records/{id}")
	public ResponseEntity<ReconciliationRecord> getRecord(@PathVariable Long id) {
		return recordRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
