package backend.demo2.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    SecurityQuestion findByUser(User user); // Find security question for a user
}
