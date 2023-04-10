package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.*;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repositories.FixtureRepository;
import com.jwt.repositories.StatRepository;
import com.jwt.repositories.TeamsheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FixtureService {
    @Value("${club.name}")
    String clubName;

    private final FixtureRepository fixtureRepository;
    StatService statService;
    ClubService clubService;
    CompetitionService competitionService;
    private final TeamsheetRepository teamsheetRepository;


    @Autowired
    public FixtureService(FixtureRepository fixtureRepository, ClubService clubService, CompetitionService competitionService,
                          TeamsheetRepository teamsheetRepository) {
        this.fixtureRepository = fixtureRepository;
        this.clubService = clubService;
        this.competitionService = competitionService;

        this.teamsheetRepository = teamsheetRepository;
    }

    // setter injection used to avoid circular dependencies
    @Autowired
    public void setStatService(StatService statService)  {
        this.statService = statService;
    }

    // return all fixtures

    public List<Fixture> findAll() {  return fixtureRepository.findAll();    }

    // return all fixtures

    public List<Fixture> findUpcoming() {
        // the upcoming fixtures will pick up all fixtures after todays date
        // but they should include todays fixture also
        // to ensure we get todays fixture the date after is todays date - 1 day
        long day = 1000 * 60 * 60 * 24;
        Date date = new Date(System.currentTimeMillis() - day);
        return fixtureRepository.findByFixtureDateAfterOrderByFixtureDate(date).orElse(new ArrayList<>());
    }

    // get date of last fixture id back from todays date

    public Long findMostRecentFixtureIdByClubIdAndDate() {
        Date today = new Date(System.currentTimeMillis());
        Long clubId = clubService.getIdByName(clubName);
        return fixtureRepository.findMostRecentFixtureIdByClubIdAndDate(clubId, today);
    }

    // list fixtures by date

    public List<Fixture> findByFixtureDate(Date fixtureDate) {
        List<Fixture> fixtures = fixtureRepository.findByFixtureDate(fixtureDate).orElse(new ArrayList<>());

        if(fixtures.isEmpty()) {
            new MyMessageResponse("No Fixtures found for this date: " + fixtureDate, MessageTypes.WARN);
        }
        return fixtures;
    }

    public List<Fixture> findByHomeTeamAndAwayTeamOrHomeTeamAndAwayTeam(Club club, Club opposition) {
        return fixtureRepository.findByHomeTeamAndAwayTeamOrHomeTeamAndAwayTeam(club, opposition, opposition, club).orElse(new ArrayList<>());
    }

    // find fixtures by opposition id

    public List<Fixture> findByOppositionId(Long clubId) {
        Long id = clubService.getIdByName(clubName);
        Club club = clubService.findById(id);
        Club opposition = clubService.findById(clubId);

        List<Fixture> fixtures = findByHomeTeamAndAwayTeamOrHomeTeamAndAwayTeam(club, opposition);

        if(fixtures.isEmpty()) {
            new MyMessageResponse("No Fixtures found for this clubId: " + clubId, MessageTypes.WARN);
        }
        return fixtures;
    }

    // list fixtures by date

    public Fixture getById(Long fixtureId) {
        Fixture fixture = fixtureRepository.findById(fixtureId).orElse(new Fixture());

        if(fixture.getId()==null) {
            new MyMessageResponse("No Fixtures found for this Id: " + fixtureId, MessageTypes.WARN);
        }
        return fixture;
    }

    // Return all fixture for a given club

    public List<Fixture> getClubFixtures(ClubModel clubModel)  {
        List<Fixture> fixtures = getClubHomeFixtures(clubModel);
        fixtures.addAll(getClubAwayFixtures(clubModel.getName()));
        return fixtures;
    }

    // return all the home fixtures for a club

    public List<Fixture> getClubHomeFixtures(ClubModel clubModel)  {
        Long id = getClubId(clubModel.getName());

        if(id == null) {
            new MyMessageResponse("No Home fixtures found for Club: " + clubModel.getName(), MessageTypes.WARN);
            return new ArrayList<>();
        }
        Optional<List<Fixture>> fixtures = fixtureRepository.findByHomeTeamId(id);
        return fixtures.orElse(new ArrayList<>());
    }

    // return all the away fixtures for a club

    public List<Fixture> getClubAwayFixtures(String name)  {
        Long id = getClubId(name);

        if(id == null) {
            new MyMessageResponse("No Away fixtures found for Club: " + name, MessageTypes.WARN);
            return new ArrayList<>();
        }

        Optional<List<Fixture>> fixtures = fixtureRepository.findByAwayTeamId(id);
        return fixtures.orElse(new ArrayList<>());
    }

    // find next fixture for club

    public Fixture getNextClubFixture(ClubModel clubModel) {
        Long clubId = getClubId(clubModel.getName());
        Date today = new Date(Calendar.getInstance().getTime().getTime());

        if(clubId == null) {
            new MyMessageResponse("No Next fixture found with club id: " + clubId + " and date: " + today, MessageTypes.WARN);
            return new Fixture();
        }

        // using todays date and home and away team IDs collect all the fixtures for 'name' greater than todays date - sorted by fixture date
        // return the first fixture in this list - this will be the next fixture.

        Optional<Fixture> fixture = fixtureRepository.findFirstByAwayTeamIdOrHomeTeamIdAndFixtureDateGreaterThanOrderByFixtureDate(clubId, clubId, today);
        if(fixture.isEmpty())
            new MyMessageResponse("No Fixture Found with clubid: "+ clubId + ", and date: "+today,MessageTypes.WARN);

        return fixture.orElse(new Fixture());
    }

    // return fixture by id

    public Fixture findById(Long id) {
        Optional<Fixture> fixture = fixtureRepository.findById(id);
        if(fixture.isEmpty())
            new MyMessageResponse("Fixture Does not exist",MessageTypes.WARN);

        return fixture.orElse(new Fixture());
    }

    // Return fixture by Competition, HomeTeam, AwayTeam, FixtureDate and Season

    public Fixture findByCompetitionHomeTeamAwayTeamFixtureDateSeason(FixtureModel fixtureModel) {

        Optional<Fixture> fixture = fixtureRepository.findByCompetitionIdAndHomeTeamIdAndAwayTeamIdAndFixtureDateAndSeason(
                fixtureModel.getCompetitionId(),
                fixtureModel.getHomeTeamId(),
                fixtureModel.getAwayTeamId(),
                fixtureModel.getFixtureDate(),
                fixtureModel.getSeason()
        );
        if(fixture.isEmpty())
            new MyMessageResponse("Error: Fixture does not exist", MessageTypes.WARN);

        return fixture.orElse(new Fixture());
    }

    // Add Fixture

    public ResponseEntity<MessageResponse> add(FixtureModel fixtureModel){

        Club homeTeam = clubService.findById(fixtureModel.getHomeTeamId());
        Club awayTeam = clubService.findById(fixtureModel.getAwayTeamId());
        Competition competition = competitionService.findById(fixtureModel.getCompetitionId());

        if(!fixtureRepository.existsByHomeTeamAndAwayTeamAndCompetitionAndSeason(homeTeam, awayTeam, competition, fixtureModel.getSeason())) {
            fixtureRepository.save(fixtureModel.translateModelToFixture(competitionService, clubService));
            return ResponseEntity.ok(new MyMessageResponse("new Fixture added", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Fixture already exists", MessageTypes.WARN));
        }

    }

    // edit/update Fixture

    public ResponseEntity<MessageResponse> update(Long id, Fixture fixture){
        if(fixtureRepository.existsById(id)) {
            fixtureRepository.save(fixture);
            return ResponseEntity.ok(new MyMessageResponse("Fixture record updated", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Fixture with Id: [" + id + "] -> does not exist - cannot update record", MessageTypes.WARN));
        }

    }

    // delete fixture

    public @ResponseBody List<Fixture> delete(Fixture fixture) {
        Long id = fixture.getId();

        if(fixtureRepository.existsById(id)) {
            fixtureRepository.deleteById(id);
             ResponseEntity.ok(new MyMessageResponse("Fixture deleted with id: " + id, MessageTypes.INFO));
        } else {
             ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Cannot delete fixture with id: " + id, MessageTypes.WARN));
        }
        return findAll();
    }

    // get clubid from name

    private Long getClubId(String name) {
        return clubService.getIdByName(name);
    }

    // retrieve fixture by date where one of the clubs is St Judes

    public Fixture findByFixtureDateAndHomeTeamIdOrFixtureDateAndAwayTeamId(Date fixtureDate, Long clubId, Date fixtureDate1, Long clubId1) {
        List<Fixture> fixtures = fixtureRepository.findByFixtureDateAndHomeTeamIdOrFixtureDateAndAwayTeamId(fixtureDate, clubId, fixtureDate, clubId).orElse(new ArrayList<>());

        if(fixtures.size() > 1)
            new MyMessageResponse("Unique Fixture Does not exist",MessageTypes.WARN);
        else if(fixtures.size() == 0)
            new MyMessageResponse("No Fixture Found for this date" + fixtureDate,MessageTypes.WARN);
        return fixtures.get(0);
    }

    public List<Fixture> findLastFive() {
        return fixtureRepository.findLastFive().orElse(new ArrayList<>());
    }



    // retrieve fixtures with no teamsheets

    public List<Fixture> findWithMoTeamsheet() {
        List<Fixture> fixtures = findAll();
        List<Teamsheet> teamsheets = teamsheetRepository.findAll();

        // get set of fixtures with teamsheets
        Set<Long> fixtureIdsWithTeamsheets = teamsheets.stream().map(teamsheet -> teamsheet.getId().getFixtureId()).collect(Collectors.toSet());

        // find those without teamsheest
        List<Fixture> fixturesWithNoTeamsheets = fixtures.stream()
                .filter(fixture -> !fixtureIdsWithTeamsheets.contains(fixture.getId()))
                .collect(Collectors.toList());

        return fixturesWithNoTeamsheets;
    }
}
