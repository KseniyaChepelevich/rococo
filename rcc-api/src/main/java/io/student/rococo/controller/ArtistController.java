package io.student.rococo.controller;

import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.api.ArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
@RequestMapping("/api/artist")
public class ArtistController {
    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public Page<ArtistJson> getAllArtist(@RequestParam(required = false) String title,
                                         @PageableDefault Pageable pageable) {
        return artistService.getAll(title, pageable);
    }

    @GetMapping("/{id}")
    public ArtistJson getArtistById(@PathVariable String id) {
        return artistService.findArtistById(id);
    }

    @GetMapping(params = "name")
    public Page<ArtistJson> searchArtistByName(@RequestParam String name, Pageable pageable) {
        return artistService.findArtistByName(name, pageable);
    }

    @PostMapping
    public ArtistJson createArtist(@RequestBody ArtistJson artist, @AuthenticationPrincipal Jwt principal) {
        return artistService.add(artist);
    }

    @PatchMapping
    public ArtistJson updateArtist(@RequestBody ArtistJson artist, @AuthenticationPrincipal Jwt principal) {
        return artistService.update(artist);
    }


}
