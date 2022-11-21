package at.ac.fhcampuswien.usermanager.repository;

import at.ac.fhcampuswien.usermanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserName(String userName);
}
