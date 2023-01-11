package ma.baxtiyorjon.LinkShortener.controllers;

import ma.baxtiyorjon.LinkShortener.models.LinkResultModel;
import ma.baxtiyorjon.LinkShortener.models.LinksEntity;
import ma.baxtiyorjon.LinkShortener.services.LinksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/links")
public class LinksController {
    private final LinksService linksService;

    public LinksController(LinksService linksService) {
        this.linksService = linksService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<LinksEntity>> getAllLinks() {
        return linksService.allLinks();
    }

    @PostMapping("/create")
    public ResponseEntity<LinkResultModel> create(@RequestParam("link") String fullLink, HttpServletRequest request) {
        return linksService.createShortLink(fullLink, request);
    }
}
