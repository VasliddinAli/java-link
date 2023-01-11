package ma.baxtiyorjon.LinkShortener.services;

import ma.baxtiyorjon.LinkShortener.enumerations.Plan;
import ma.baxtiyorjon.LinkShortener.models.RolesEntity;
import ma.baxtiyorjon.LinkShortener.models.UsersEntity;
import ma.baxtiyorjon.LinkShortener.repositories.RolesRepository;
import ma.baxtiyorjon.LinkShortener.repositories.UsersRepository;
import ma.baxtiyorjon.LinkShortener.security.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository userRepository;
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public AuthService(AuthenticationManager authenticationManager, UsersRepository userRepository, RolesRepository roleRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    public ResponseEntity<Map<String, Object>> register(String username, String email, String password) {
        final Map<String, Object> result = new HashMap<>();
        if (userRepository.existsByEmail(email)) {
            result.put("success", false);
            result.put("message", "Email taken");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByUsername(username)) {
            result.put("success", false);
            result.put("message", "Username taken");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(email) && userRepository.existsByUsername(username)){
            result.put("success", false);
            result.put("message", "Username and email taken");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        if (password.length() < 6) {
            result.put("success", false);
            result.put("message", "Minimum password length 6 symbols");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        if (username.length() < 4) {
            result.put("success", false);
            result.put("message", "Minimum username length 4 symbols");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        if (email.length() < 4) {
            result.put("success", false);
            result.put("message", "Minimum email length 4 symbols");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        final UsersEntity usersEntity = new UsersEntity();
        usersEntity.setUsername(username);
        usersEntity.setPassword(passwordEncoder.encode(password));
        usersEntity.setEmail(email);
        RolesEntity role = roleRepository.findByName("ROLE_USER").orElse(null);
        usersEntity.setRoles(Collections.singleton(role));
        usersEntity.setPlan(Plan.FREE);
        usersEntity.setTotalAmount(150);
        usersEntity.setLinksLeft(150);
        usersEntity.setLinks(null);
        userRepository.save(usersEntity);
        result.put("success", true);
        result.put("message", "Successfully registered!");
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Map<String, Object>> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        Authentication authentication = null;
        if (username != null) {
            UsersEntity usersEntity = userRepository.findByUsername(username).orElse(null);
            if (usersEntity == null) {
                usersEntity = userRepository.findByEmail(username).orElse(null);
                if (usersEntity == null) {
                    return ResponseEntity.notFound().build();
                }
            }
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usersEntity.getUsername(), password));
        }
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenGenerator.createToken(authentication);
            result.put("success", true);
            result.put("token", token);
            result.put("type", "Bearer");
        } else {
            result.put("success", false);
            result.put("message", "Username and email empty");
        }
        return ResponseEntity.ok(result);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        } else {
            return null;
        }
    }

    public Boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
