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
 * @date 2025-12-16 18:06
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders", indexes = {
		@Index(name = "idx_order_no", columnList = "orderNo"),
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_created_time", columnList = "createdTime")
})
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String orderNo;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Column(nullable = false)
	private LocalDateTime createdTime;

	private LocalDateTime paidTime;

	private LocalDateTime deliveredTime;

	@Column(length = 500)
	private String remark;

	public enum OrderStatus {
		PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
	}
}