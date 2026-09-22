package io.student.rococo.data.repository;

import io.student.rococo.data.entity.ArtistEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    @NonNull
    Page<ArtistEntity> findAllByNameContainsIgnoreCase(@NonNull String name, @NonNull Pageable pageable);
}
