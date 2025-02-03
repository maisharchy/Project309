package backend.demo2.repository;

import backend.demo2.model.SignUpUser;

import org.springframework.data.jpa.repository.JpaRepository;



public interface SignUpUserRepository extends JpaRepository<SignUpUser, Long> {
     SignUpUser findByUsername(String username);
   
}
