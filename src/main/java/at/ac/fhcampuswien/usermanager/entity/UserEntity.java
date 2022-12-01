package at.ac.fhcampuswien.usermanager.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "userDb") // avoid using SQL Reserved Words and Keywords
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String username;
    private String password;

    private Long loginCounter = 3L;
    public UserEntity() {}
}
