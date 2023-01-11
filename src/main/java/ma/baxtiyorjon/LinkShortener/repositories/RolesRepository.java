package ma.baxtiyorjon.LinkShortener.repositories;

import ma.baxtiyorjon.LinkShortener.models.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Long> {

    Optional<RolesEntity> findByName(String name);
}