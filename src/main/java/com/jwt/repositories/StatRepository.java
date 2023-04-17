package com.jwt.repositories;

// identify the repo entries for basic crud to begin with.

import com.jwt.models.Stat;
import com.jwt.models.StatId;
import com.jwt.models.StatName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatRepository extends JpaRepository<Stat, StatId> {
    boolean existsById(StatId id);
    void deleteById(StatId id);
    Optional<Stat> findByStatName(StatName statName);
    boolean existsByStatName(StatName statName);
    Optional<Stat> findById(StatId id);
    Optional<List<Stat>> findByFixtureIdAndSuccessAndStatNameOrStatName(Long fixtureId, boolean success, String free, String score);
    Optional<List<Stat>> findByFixtureId(Long fixtureId);

    @Query("SELECT s FROM Stat s WHERE s.fixture.id = :fixtureId AND s.player.id <> 80")
    Optional<List<Stat>> findByFixtureIdAndLastNameNotOpposition(Long fixtureId);
    Optional<List<Stat>> findByFixtureIdAndSuccessAndStatNameOrFixtureIdAndSuccessAndStatName(Long id, boolean success, StatName freescore, Long id1, boolean success1, StatName goal);
}
