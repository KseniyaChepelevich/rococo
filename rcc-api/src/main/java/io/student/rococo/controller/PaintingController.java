package io.student.rococo.controller;

import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.api.PaintingService;
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
@RequestMapping("/api/painting")
public class PaintingController {

    private final PaintingService paintingService;

    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public Page<PaintingJson> getAllPaintings(@RequestParam(required = false) String title,
                                              @PageableDefault Pageable pageable) {
        return paintingService.getAll(title, pageable);
    }

    @GetMapping("/{id}")
    public PaintingJson getPaintingById(@PathVariable String id) {
        return paintingService.findPaintingById(id);
    }

    @GetMapping("/author/{artistId}")
    public Page<PaintingJson> getPaintingByArtist(@PathVariable String artistId, Pageable pageable) {
        return paintingService.findPaintingByArtistId(artistId, pageable);
    }

    @GetMapping(params = "title")
    public Page<PaintingJson> getPaintingByTitle(@RequestParam String title, Pageable pageable) {
        return paintingService.findPaintingByTitle(title, pageable);
    }

    @PostMapping
    public PaintingJson createPainting(@AuthenticationPrincipal Jwt principal, @RequestBody PaintingJson painting) {
        return paintingService.add(painting);
    }

    @PatchMapping
    public PaintingJson updatePainting(@RequestBody PaintingJson painting, @AuthenticationPrincipal Jwt principal) {
        return paintingService.update(painting);
    }
}
