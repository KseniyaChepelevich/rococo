package io.student.rococo.data.repository;

import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByUsername(String username);
}