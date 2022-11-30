package at.ac.fhcampuswien.usermanager.repository;

import at.ac.fhcampuswien.usermanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    User findUsersBy(String username);
}
