package top.tangtian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2025-12-16 18:11
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reconciliation_records", indexes = {
		@Index(name = "idx_record_date", columnList = "recordDate"),
		@Index(name = "idx_status", columnList = "status")
})
public class ReconciliationRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate recordDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReconciliationType type;

	@Column(nullable = false)
	private Integer totalCount;

	@Column(nullable = false)
	private Integer matchedCount;

	@Column(nullable = false)
	private Integer unmatchedCount;

	@Column(nullable = false, precision = 16, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, precision = 16, scale = 2)
	private BigDecimal matchedAmount;

	@Column(nullable = false, precision = 16, scale = 2)
	private BigDecimal unmatchedAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReconciliationStatus status;

	@Column(nullable = false)
	private LocalDateTime startTime;

	private LocalDateTime endTime;

	@Column(length = 1000)
	private String errorMessage;

	public enum ReconciliationType {
		ORDER, PAYMENT, LOGISTICS
	}

	public enum ReconciliationStatus {
		RUNNING, SUCCESS, FAILED, PARTIAL_SUCCESS
	}
}