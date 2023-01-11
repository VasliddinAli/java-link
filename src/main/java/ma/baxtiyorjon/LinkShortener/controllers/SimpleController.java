package ma.baxtiyorjon.LinkShortener.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SimpleController {
    @GetMapping("/simple")
    public ResponseEntity<String> simple(){
        return ResponseEntity.ok("Welcome");
    }
}
