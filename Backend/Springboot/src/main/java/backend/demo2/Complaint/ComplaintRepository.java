package backend.demo2.Complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;
import backend.demo2.User.User;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByComplainantUserId(int complainantUserId);
    List<Complaint> findByComplainedUserOrComplainantUser(User complainedUser, User complainantUser);
    @Modifying
    @Transactional
    @Query("DELETE FROM Complaint c WHERE c.complainedUser.id = :userId")
    void deleteByComplainedUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Complaint c WHERE c.complainantUser.id = :userId")
    void deleteByComplainantUserId(@Param("userId") int userId);
}
