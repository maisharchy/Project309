package backend.demo2.ProfileSettings;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    // Custom method to find a Profile by userId
    Optional<Profile> findByUserId(int userId);
}