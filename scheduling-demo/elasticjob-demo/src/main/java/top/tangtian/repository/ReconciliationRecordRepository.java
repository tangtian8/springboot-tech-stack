package top.tangtian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.tangtian.entity.ReconciliationRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author tangtian
 * @date 2025-12-16 18:14
 */
@Repository
public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, Long> {
	Optional<ReconciliationRecord> findByRecordDateAndType(LocalDate recordDate,
														   ReconciliationRecord.ReconciliationType type);

	List<ReconciliationRecord> findByRecordDate(LocalDate recordDate);

	List<ReconciliationRecord> findByStatus(ReconciliationRecord.ReconciliationStatus status);
}
