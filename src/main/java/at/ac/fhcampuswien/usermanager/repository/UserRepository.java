package at.ac.fhcampuswien.usermanager.repository;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    UserEntity findUsersByUsername(String username);
}
