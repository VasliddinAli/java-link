package ma.baxtiyorjon.LinkShortener.repositories;

import ma.baxtiyorjon.LinkShortener.models.LinksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinksRepository extends JpaRepository<LinksEntity, Long> {

    Optional<LinksEntity> findByFullLink(String fullLink);

    Optional<LinksEntity> findByShortLink(String shortLink);

    Optional<LinksEntity> findByLinkKey(String linkKey);

}