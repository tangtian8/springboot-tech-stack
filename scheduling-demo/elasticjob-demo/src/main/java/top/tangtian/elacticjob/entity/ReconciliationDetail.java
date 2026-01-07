package top.tangtian.elacticjob.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2025-12-16 18:12
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reconciliation_details", indexes = {
		@Index(name = "idx_record_id", columnList = "recordId"),
		@Index(name = "idx_order_no", columnList = "orderNo")
})
public class ReconciliationDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long recordId;

	@Column(nullable = false, length = 64)
	private String orderNo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private DifferenceType differenceType;

	@Column(precision = 12, scale = 2)
	private BigDecimal systemAmount;

	@Column(precision = 12, scale = 2)
	private BigDecimal thirdPartyAmount;

	@Column(precision = 12, scale = 2)
	private BigDecimal differenceAmount;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false)
	private LocalDateTime createdTime;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ProcessStatus processStatus;

	public enum DifferenceType {
		AMOUNT_MISMATCH, ORDER_MISSING, PAYMENT_MISSING, STATUS_MISMATCH
	}

	public enum ProcessStatus {
		PENDING, PROCESSING, RESOLVED, IGNORED
	}
}