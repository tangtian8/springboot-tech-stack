package top.tangtian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.tangtian.entity.ReconciliationDetail;

import java.util.List;

/**
 * @author tangtian
 * @date 2025-12-16 18:14
 */
@Repository
public interface ReconciliationDetailRepository extends JpaRepository<ReconciliationDetail, Long> {
	List<ReconciliationDetail> findByRecordId(Long recordId);

	List<ReconciliationDetail> findByProcessStatus(ReconciliationDetail.ProcessStatus status);

	List<ReconciliationDetail> findByRecordIdAndDifferenceType(Long recordId,
															   ReconciliationDetail.DifferenceType type);
}
