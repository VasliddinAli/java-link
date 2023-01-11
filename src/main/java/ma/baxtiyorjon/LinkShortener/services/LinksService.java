package ma.baxtiyorjon.LinkShortener.services;

import ma.baxtiyorjon.LinkShortener.enumerations.Plan;
import ma.baxtiyorjon.LinkShortener.models.LinkResultModel;
import ma.baxtiyorjon.LinkShortener.models.LinksEntity;
import ma.baxtiyorjon.LinkShortener.models.UsersEntity;
import ma.baxtiyorjon.LinkShortener.repositories.LinksRepository;
import ma.baxtiyorjon.LinkShortener.repositories.UsersRepository;
import ma.baxtiyorjon.LinkShortener.security.TokenGenerator;
import org.hashids.Hashids;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class LinksService {

    private final Long LINK_VALIDITY_PRO = 10368000000L;
    private final Long LINK_VALIDITY_FREE = 2592000000L;

    private final Hashids hashids;
    private final LinksRepository linksRepository;
    private final AuthService authService;
    private final TokenGenerator tokenGenerator;
    private final UsersRepository usersRepository;

    public LinksService(LinksRepository linksRepository, AuthService authService, TokenGenerator tokenGenerator, UsersRepository usersRepository) {
        this.linksRepository = linksRepository;
        this.authService = authService;
        this.tokenGenerator = tokenGenerator;
        this.usersRepository = usersRepository;
        this.hashids = new Hashids(getClass().getName(), 6);
    }

    public ResponseEntity<LinkResultModel> createShortLink(String fullLink, HttpServletRequest request) {
        String token = authService.getTokenFromRequest(request);
        String username = tokenGenerator.getUsernameFromToken(token);
        UsersEntity usersEntity = usersRepository.findByUsername(username).orElse(null);
        if (usersEntity == null) {
            return ResponseEntity.notFound().build();
        }
        final LinksEntity linksEntity = new LinksEntity();
        String genKey = hashids.encode(new Date().getTime());
        String shortLink = "http://localhost:8080/" + genKey;
        linksEntity.setFullLink(fullLink);
        linksEntity.setShortLink(shortLink);
        linksEntity.setLinkKey(genKey);
        linksEntity.setClicked(0);
        boolean isPro = usersEntity.getPlan() == Plan.PRO;
        linksEntity.setCreatedAt(new Timestamp(new Date().getTime() + (isPro ? LINK_VALIDITY_PRO : LINK_VALIDITY_FREE)));
        final LinksEntity savedEntity = linksRepository.save(linksEntity);
        usersEntity.setId(usersEntity.getId());
        usersEntity.setUsername(usersEntity.getUsername());
        usersEntity.setPassword(usersEntity.getPassword());
        usersEntity.setEmail(usersEntity.getEmail());
        usersEntity.setPlan(usersEntity.getPlan());
        usersEntity.setRoles(usersEntity.getRoles());
        if (usersEntity.getPlan() == Plan.FREE)
            usersEntity.setTotalAmount(150);
        else
            usersEntity.setTotalAmount(500);
        Integer linkLeft = usersEntity.getLinksLeft() - 1;
        usersEntity.setLinksLeft(linkLeft);
        if (usersEntity.getLinks() == null) {
            usersEntity.setLinks(Collections.singletonList(savedEntity));
        } else {
            usersEntity.getLinks().add(savedEntity);
            usersEntity.setLinks(usersEntity.getLinks());
        }
        usersRepository.save(usersEntity);

        LinkResultModel linkResultModel = new LinkResultModel(true, savedEntity.getFullLink(), savedEntity.getShortLink(), savedEntity.getLinkKey());
        return ResponseEntity.ok(linkResultModel);
    }

    public ResponseEntity<Void> redirect(String linkKey) {
        final LinksEntity linksEntity = linksRepository.findByLinkKey(linkKey).orElse(null);
        if (linksEntity != null) {
            final String url;
            if (linksEntity.getFullLink().startsWith("https://") || linksEntity.getFullLink().startsWith("http://")) {
                url = linksEntity.getFullLink();
            } else {
                url = "https://" + linksEntity.getFullLink();
            }
            linksEntity.setId(linksEntity.getId());
            linksEntity.setLinkKey(linksEntity.getLinkKey());
            linksEntity.setFullLink(linksEntity.getFullLink());
            linksEntity.setShortLink(linksEntity.getShortLink());
            linksEntity.setCreatedAt(linksEntity.getCreatedAt());
            linksEntity.setClicked(linksEntity.getClicked() + 1);
            linksRepository.save(linksEntity);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<LinksEntity>> allLinks() {
        return ResponseEntity.ok(linksRepository.findAll());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredLinks() {
        List<UsersEntity> allLinks = usersRepository.findAll();
        for (UsersEntity entity : allLinks) {
            entity.setId(entity.getId());
            entity.setLinksLeft(entity.getLinksLeft());
            entity.setUsername(entity.getUsername());
            entity.setEmail(entity.getEmail());
            entity.setRoles(entity.getRoles());
            entity.setPlan(entity.getPlan());
            entity.setTotalAmount(entity.getTotalAmount());
            entity.getLinks().forEach(e -> {
                if (e.getCreatedAt().getTime() > new Date().getTime()) {
                    linksRepository.deleteById(e.getId());
                    entity.getLinks().remove(e);
                    entity.setLinks(entity.getLinks());
                }
            });
            usersRepository.save(entity);
        }
    }
}
