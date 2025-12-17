package top.tangtian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.tangtian.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author tangtian
 * @date 2025-12-16 18:13
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByPaymentNo(String paymentNo);

	Optional<Payment> findByOrderNo(String orderNo);

	Optional<Payment> findByThirdPartyNo(String thirdPartyNo);

	@Query("SELECT p FROM Payment p WHERE p.createdTime BETWEEN :startTime AND :endTime")
	List<Payment> findByTimeRange(@Param("startTime") LocalDateTime startTime,
								  @Param("endTime") LocalDateTime endTime);

	@Query("SELECT p FROM Payment p WHERE p.status = :status AND p.paidTime BETWEEN :startTime AND :endTime")
	List<Payment> findByStatusAndPaidTimeRange(@Param("status") Payment.PaymentStatus status,
											   @Param("startTime") LocalDateTime startTime,
											   @Param("endTime") LocalDateTime endTime);
}
