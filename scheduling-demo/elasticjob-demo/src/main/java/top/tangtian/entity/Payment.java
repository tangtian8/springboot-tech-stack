package top.tangtian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
@Table(name = "payments", indexes = {
		@Index(name = "idx_payment_no", columnList = "paymentNo"),
		@Index(name = "idx_order_no", columnList = "orderNo"),
		@Index(name = "idx_third_party_no", columnList = "thirdPartyNo")
})
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String paymentNo;

	@Column(nullable = false, length = 64)
	private String orderNo;

	@Column(length = 64)
	private String thirdPartyNo;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentMethod method;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentStatus status;

	@Column(nullable = false)
	private LocalDateTime createdTime;

	private LocalDateTime paidTime;

	public enum PaymentMethod {
		ALIPAY, WECHAT, BANK_CARD, CREDIT_CARD
	}

	public enum PaymentStatus {
		PENDING, SUCCESS, FAILED, REFUNDED
	}
}

