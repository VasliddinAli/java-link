
package ma.baxtiyorjon.LinkShortener.repositories;

import ma.baxtiyorjon.LinkShortener.models.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    Optional<UsersEntity> findByUsername(String username);

    Optional<UsersEntity> findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}