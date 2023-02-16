package org.rajman.authentication.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by behzad on 2/20/18.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @SequenceGenerator(name = "userIdSeq", schema = "auth", sequenceName = "users_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIdSeq")
    Long id;
    @Column(name = "username", nullable = false, unique = true)
    String username;
    @Column(name = "password", nullable = false)
    String password;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}

