package ma.baxtiyorjon.LinkShortener.services;

import ma.baxtiyorjon.LinkShortener.enumerations.Plan;
import ma.baxtiyorjon.LinkShortener.models.LinksEntity;
import ma.baxtiyorjon.LinkShortener.models.RolesEntity;
import ma.baxtiyorjon.LinkShortener.models.UsersEntity;
import ma.baxtiyorjon.LinkShortener.repositories.RolesRepository;
import ma.baxtiyorjon.LinkShortener.repositories.UsersRepository;
import ma.baxtiyorjon.LinkShortener.security.TokenGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final AuthService authService;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, RolesRepository rolesRepository, AuthService authService, TokenGenerator tokenGenerator, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.authService = authService;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UsersEntity> getUser(HttpServletRequest request) {
        String token = authService.getTokenFromRequest(request);
        String username = tokenGenerator.getUsernameFromToken(token);
        UsersEntity usersEntity = usersRepository.findByUsername(username).orElse(null);
        if (usersEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usersEntity);
    }

    public ResponseEntity<Map<String, Object>> changePlan(Boolean toPro, HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String token = authService.getTokenFromRequest(request);
        String username = tokenGenerator.getUsernameFromToken(token);
        UsersEntity usersEntity = usersRepository.findByUsername(username).orElse(null);
        if (usersEntity == null) {
            return ResponseEntity.notFound().build();
        }
        usersEntity.setId(usersEntity.getId());
        usersEntity.setLinks(usersEntity.getLinks());
        usersEntity.setLinksLeft(usersEntity.getLinksLeft());
        usersEntity.setEmail(usersEntity.getEmail());
        usersEntity.setRoles(usersEntity.getRoles());
        usersEntity.setPassword(usersEntity.getPassword());
        usersEntity.setUsername(usersEntity.getUsername());
        if (usersEntity.getPlan() == Plan.FREE){
            if (toPro){
                result.put("status", true);
                result.put("message", "User plan upgraded to PRO");
                usersEntity.setPlan(Plan.PRO);
                usersEntity.setTotalAmount(500);
            } else {
                result.put("status", false);
                result.put("message", "Already FREE");
                usersEntity.setTotalAmount(usersEntity.getTotalAmount());
            }
        } else if (usersEntity.getPlan() == Plan.PRO) {
            if (toPro){
                result.put("status", false);
                result.put("message", "Already PRO");
                usersEntity.setTotalAmount(usersEntity.getTotalAmount());
            } else {
                result.put("status", true);
                result.put("message", "User plan downgraded to FREE");
                usersEntity.setPlan(Plan.FREE);
                usersEntity.setTotalAmount(150);
            }
        }
        usersRepository.save(usersEntity);
        return ResponseEntity.ok(result);
    }

    public Boolean changePassword(String password, HttpServletRequest request) {
        String token = authService.getTokenFromRequest(request);
        String username = tokenGenerator.getUsernameFromToken(token);
        UsersEntity usersEntity = usersRepository.findByUsername(username).orElse(null);
        if (usersEntity == null) {
            return false;
        }
        usersEntity.setId(usersEntity.getId());
        usersEntity.setUsername(usersEntity.getUsername());
        usersEntity.setPassword(passwordEncoder.encode(password));
        usersEntity.setEmail(usersEntity.getEmail());
        RolesEntity role = rolesRepository.findByName("USER").orElse(null);
        usersEntity.setRoles(Collections.singleton(role));
        usersEntity.setPlan(Plan.FREE);
        usersEntity.setTotalAmount(150);
        usersEntity.setLinksLeft(150);
        usersEntity.setLinks(null);
        usersRepository.save(usersEntity);
        return true;
    }

    public ResponseEntity<List<UsersEntity>> getAllUsers(){
        return ResponseEntity.ok(usersRepository.findAll());
    }
}
