package com.jwt.repositories;

import com.jwt.models.Club;
import com.jwt.models.Competition;
import com.jwt.models.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixtureRepository extends JpaRepository<Fixture, Long> {
    List<Fixture> findAll();
    Optional<List<Fixture>> findByFixtureDateAfterOrderByFixtureDate(Date date);

    Optional<Fixture> findById(Long id);
    Optional<Fixture> findByOrderByFixtureDate();
    Optional<List<Fixture>> findByAwayTeamId(Long id);
    @Query(value = "SELECT * " +
            "FROM teamstats.fixtures " +
            "WHERE fixture_date < CURRENT_DATE() " +
            "ORDER BY fixture_date DESC ",
            nativeQuery = true)
    Optional<List<Fixture>> findByHomeTeamIdOrderByFixtureDateDesc(Long id);
    Optional<Fixture> findFirstByAwayTeamIdOrHomeTeamIdAndFixtureDateGreaterThanOrderByFixtureDate(Long id1, Long id2, Date today);
    Optional<Fixture> findByCompetitionIdAndHomeTeamIdAndAwayTeamIdAndFixtureDateAndSeason(Long compId, Long homeId, Long awayId, Date fixtureDate, int season);
    boolean existsByHomeTeamAndAwayTeamAndCompetitionAndSeason(Club home, Club away, Competition comp, int season);
    Optional<List<Fixture>> findByFixtureDate(Date fixtureDate);
    Optional<List<Fixture>> findByFixtureDateAndHomeTeamIdOrFixtureDateAndAwayTeamId(Date fixtureDate, Long clubId, Date fixtureDate1, Long clubId1);
    Optional<List<Fixture>> findByHomeTeamAndAwayTeamOrHomeTeamAndAwayTeam(Club club1, Club opposition1, Club opposition2, Club club2);

    @Query(value = "SELECT * " +
            "FROM teamstats.fixtures " +
            "WHERE fixture_date < CURRENT_DATE() " +
            "ORDER BY fixture_date DESC " +
            "LIMIT 5 ",
            nativeQuery = true)
    Optional<List<Fixture>> findLastFive();

    @Query("SELECT f.id FROM Fixture f WHERE (f.homeTeam.id = :clubId OR f.awayTeam.id = :clubId) AND f.fixtureDate = (SELECT MAX(f2.fixtureDate) FROM Fixture f2 WHERE f2.fixtureDate < :today)")
    Long findMostRecentFixtureIdByClubIdAndDate(Long clubId, Date today);

}

