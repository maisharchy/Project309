package backend.demo2.ProblemReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import backend.demo2.ProblemReport.ProblemReport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProblemReportRepository extends JpaRepository<ProblemReport, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ProblemReport p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") int userId);
    List<ProblemReport> findByUserId(Long userId);
    @Query("SELECT pr FROM ProblemReport pr WHERE pr.user.id IN :userIds")
    List<ProblemReport> findByUserIdIn(@Param("userIds") List<Long> userIds);


}
