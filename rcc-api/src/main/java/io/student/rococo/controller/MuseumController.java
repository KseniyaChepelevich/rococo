package io.student.rococo.controller;

import io.student.rococo.model.MuseumJson;
import io.student.rococo.service.api.MuseumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController()
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumService museumService;

    @Autowired
    public MuseumController(MuseumService museumService) {
        this.museumService = museumService;
    }

    @GetMapping()
    public Page<MuseumJson> getAllMuseums(@RequestParam(required = false) String title,
                                          @PageableDefault Pageable pageable) {
        return museumService.getAll(title, pageable);
    }

    @GetMapping(params = "title")
    public Page<MuseumJson> searchMuseumsByTitle(@RequestParam String title, @PageableDefault Pageable pageable) {
        return museumService.findMuseumByTitle(title, pageable);
    }


    @GetMapping("/{id}")
    public MuseumJson getMuseumById(@PathVariable("id") String id) {
        return museumService.findMuseumById(id);
    }

    @PatchMapping()
    public MuseumJson updateMuseum(@AuthenticationPrincipal Jwt principal, @RequestBody MuseumJson museum) {
        return museumService.update(museum);
    }

    @PostMapping()
    public MuseumJson createMuseum(@AuthenticationPrincipal Jwt principal, @RequestBody MuseumJson museum) {
        return museumService.add(museum);
    }
}
