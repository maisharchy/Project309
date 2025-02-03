package backend.demo2.User;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    // Method to delete a user by their ID
    public void deleteUser(int userId) {
        userRepository.deleteById(Long.valueOf(userId));
    }

}
