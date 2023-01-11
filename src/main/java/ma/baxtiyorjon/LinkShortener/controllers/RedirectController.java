package ma.baxtiyorjon.LinkShortener.controllers;

import ma.baxtiyorjon.LinkShortener.services.LinksService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class RedirectController {
    private final LinksService linksService;

    public RedirectController(LinksService linksService) {
        this.linksService = linksService;
    }

    @GetMapping("/{link}")
    public ResponseEntity<Void> redirect(@PathVariable String link) {
        return linksService.redirect(link);
    }
}
