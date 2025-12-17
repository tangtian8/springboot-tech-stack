package top.tangtian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.tangtian.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author tangtian
 * @date 2025-12-16 18:13
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNo(String orderNo);

	List<Order> findByStatus(Order.OrderStatus status);

	@Query("SELECT o FROM Order o WHERE o.createdTime BETWEEN :startTime AND :endTime")
	List<Order> findByTimeRange(@Param("startTime") LocalDateTime startTime,
								@Param("endTime") LocalDateTime endTime);

	@Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdTime BETWEEN :startTime AND :endTime")
	List<Order> findByStatusAndTimeRange(@Param("status") Order.OrderStatus status,
										 @Param("startTime") LocalDateTime startTime,
										 @Param("endTime") LocalDateTime endTime);
}