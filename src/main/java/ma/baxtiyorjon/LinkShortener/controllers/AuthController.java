package ma.baxtiyorjon.LinkShortener.controllers;

import ma.baxtiyorjon.LinkShortener.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password) {
        return authService.register(username, email, password);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return authService.login(username, password);
    }

    @GetMapping("/checkEmail")
    public Boolean checkEmail(@RequestParam("email") String email) {
        return authService.checkEmail(email);
    }

    @GetMapping("/checkUsername")
    public Boolean checkUsername(@RequestParam("username") String username) {
        return authService.checkUsername(username);
    }
}
