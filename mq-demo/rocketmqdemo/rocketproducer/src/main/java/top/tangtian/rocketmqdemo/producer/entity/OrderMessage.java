package top.tangtian.rocketmqdemo.producer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tangtian
 * @date 2026-01-02 17:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {
	private String orderId;
	private String userId;
	private Double amount;
	private Long timestamp;
}