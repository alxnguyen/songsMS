package de.htw.songservice.repository;

import de.htw.songservice.model.Songlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SonglistRepository extends JpaRepository<Songlist, Integer> {
    Iterable<Songlist> findAllByUserIdAndIsPrivate(String userId, boolean isPrivate);
    Iterable<Songlist> findAllByUserId(String userId);
}
