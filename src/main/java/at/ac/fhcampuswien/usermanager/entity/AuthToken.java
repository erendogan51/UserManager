package at.ac.fhcampuswien.usermanager.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant createdDateTime;

    @Column(nullable = false)
    private Instant expiresAtDateTime;

    private Instant confirmedDateTime;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;
}
