package backend.demo2.repository;

import backend.demo2.model.Game;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

}