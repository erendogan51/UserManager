package at.ac.fhcampuswien.usermanager.repository;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    UserEntity findUsersByUsername(String username);

    @Modifying
    @Query("update UserEntity u set u.loggedIn = :logged_in where u.username = :username")
    void updateLogin(@Param("username") String username, @Param("logged_in") boolean loggedIn);

    @Modifying
    @Transactional
    void deleteByUsername(String username);
}
