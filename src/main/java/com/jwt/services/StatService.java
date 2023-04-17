package com.jwt.services;

// identify the basic crud services which should be included here

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.*;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatService {
    @Value("${club.name}")
    private String clubName;

    StatRepository statRepository;
    PitchGridService pitchGridService;
    FixtureService fixtureService;
    FellowshipService fellowshipService;
    StatNameService statNameService;
    PlayerService playerService;
    ManagerService managerService;
    ClubService clubService;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public StatService(StatRepository statRepository, PitchGridService pitchGridService,
                       StatNameService statNameService, PlayerService playerService, FellowshipService fellowshipService,
                       ClubService clubService,ManagerService managerService,
                       FixtureRepository fixtureRepository) {
        this.statRepository = statRepository;
        this.pitchGridService = pitchGridService;
        this.clubService = clubService;
        this.playerService = playerService;
        this.fellowshipService = fellowshipService;
        this.statNameService = statNameService;
        this.managerService = managerService;

        this.fixtureRepository = fixtureRepository;
    }

    // return all Stats
    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public List<Stat> list(){
        List<Stat> stats = statRepository.findAll();
        if(stats.isEmpty()) new MyMessageResponse("Error: No Stats listed", MessageTypes.WARN);
        return stats;
    }

    // setter injection used to avoid circular dependencies
    @Autowired
    public void setFixtureService(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }


    // return Stat by id

    public Stat findById( StatId id){
        Optional<Stat> stat = statRepository.findById(id);
        if(stat.isEmpty())
            new MyMessageResponse(String.format("Warning: Stat id: %d %s not found", id.getFixtureId(), id.getTimeOccurred()), MessageTypes.WARN);
        return stat.orElse(new Stat());
    }

    public List<Stat> findByFixtureId(Long fixtureId) {
        List<Stat> stats = statRepository.findByFixtureIdAndLastNameNotOpposition(fixtureId).orElse(new ArrayList<>());
        if(stats.isEmpty())
            new MyMessageResponse(String.format("Warning: Stats: No stats found for this fixture Id: %d", fixtureId), MessageTypes.WARN);
        return stats;
    }

    // return Stat by id

    public Result scoreByFixtureDate( Date fixtureDate){
        String FREESCORE  = "FS";
        String POINT      = "SCPT";
        String GOAL       = "SCG";
        String OPPOSITION = "Opposition";
        boolean SUCCESS   = true;

        // gather the StatName objects for each object needed to gather the scores
        StatName freeScore = statNameService.findById( FREESCORE );
        StatName point = statNameService.findById( POINT );
        StatName goal = statNameService.findById( GOAL );

        // get the id of the opposition object to be used to search for opposition results
        long oppositionID = playerService.findByLastname(OPPOSITION).get(0).getId();

        // initialise collectors
        long ownGoals  = 0;
        long ownPoints = 0;
        long oppGoals  = 0;
        long oppPoints = 0;

        // get club id
        // get fixture ID - Judes must have played either a home or away fixture on this date
        Long clubId = clubService.findByName(clubName).getId();
        // find the fixture on a specific date for the club team - St Judes
        // ie on a specific date, the team, is either playing at home or away hence the longer than usual repo call.

        Fixture fixture = fixtureService.findByFixtureDateAndHomeTeamIdOrFixtureDateAndAwayTeamId(fixtureDate, clubId, fixtureDate, clubId);

        if (fixture.getId() == null) {
            new MyMessageResponse(String.format("Score: No Fixture with [%s] found for this date: [%s]", clubName, fixtureDate.toString()), MessageTypes.ERROR);
        }

        Optional<List<Stat>> goals = statRepository.findByFixtureIdAndSuccessAndStatNameOrFixtureIdAndSuccessAndStatName(
                fixture.getId(), SUCCESS, freeScore, fixture.getId(), SUCCESS, goal);

        Optional<List<Stat>> points = statRepository.findByFixtureIdAndSuccessAndStatNameOrFixtureIdAndSuccessAndStatName(
                fixture.getId(), SUCCESS, freeScore, fixture.getId(), SUCCESS, point);

        if(goals.isPresent()) {
            oppGoals = goals.get()
                    .stream()
                    .filter(g -> g.getFellowship().getId() == oppositionID)
                    .count();
            ownGoals = goals.get()
                    .stream()
                    .filter(g -> g.getFellowship().getId() != oppositionID)
                    .count();
        }
        if(points.isPresent()) {
            oppPoints = points.get()
                    .stream()
                    .filter(p -> p.getFellowship().getId() == oppositionID)
                    .count();
            ownPoints = points.get()
                    .stream()
                    .filter(p -> p.getFellowship().getId() != oppositionID)
                    .count();
        }

        long homeGoals;
        long homePoints;
        long awayGoals;
        long awayPoints;

        if(clubId.equals( fixture.getHomeTeam().getId())) {
            homeGoals  = ownGoals;
            homePoints = ownPoints;
            awayGoals  = oppGoals;
            awayPoints = oppPoints;
        } else {
            homeGoals  = oppGoals;
            homePoints = oppPoints;
            awayGoals  = ownGoals;
            awayPoints = ownPoints;
        }

        return Result.builder()
                .homeTeam(fixture.getHomeTeam().getName())
                .awayTeam(fixture.getAwayTeam().getName())
                .homeGoals(homeGoals)
                .homePoints(homePoints)
                .awayGoals(awayGoals)
                .awayPoints(awayPoints)
                .homeScorePoints(homeGoals * 3 + homePoints)
                .awayScorePoints(awayGoals * 3 + awayPoints)
                .homeScoreString(String.format("%d - %d",homeGoals, homePoints))
                .awayScoreString(String.format("%d - %d",awayGoals, awayPoints))
                .homeScoreStringWithPointsTotal(String.format("%d - %d (%d)",homeGoals, homePoints, homeGoals * 3 + homePoints))
                .awayScoreStringWithPointsTotal(String.format("%d - %d (%d)",awayGoals, awayPoints, awayGoals * 3 + awayPoints))
                .build();
    }



    // return Stat by name

    public String findByName( StatModel statModel) {
        return statNameService.findById(statModel.getStatNameId()).getName();
    }

    // add new Stat

    public ResponseEntity<MessageResponse> add(StatModel statModel){

        StatId statId = new StatId();
        statId.setFixtureId(statModel.getFixtureId());
        statId.setTimeOccurred(statModel.getTimeOccurred());

        if(!statRepository.existsById(statId)) {
            Stat stat = statModel.translateModelToStat(fixtureService, playerService, fellowshipService,pitchGridService, statNameService);
            stat.setId(statId);
            statRepository.save(stat);
            return ResponseEntity.ok(new MyMessageResponse("new Stat added", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Stat already exists", MessageTypes.WARN));
        }
    }

    // delete by id

    public  List<Stat> delete(Stat stat){
        StatId id = stat.getId();
        if(statRepository.existsById(id)) {
            statRepository.deleteById(id);
             ResponseEntity.ok(new MyMessageResponse("Stat deleted with id: " + id, MessageTypes.INFO));
        } else {
             ResponseEntity.ok(new MyMessageResponse("Error: Cannot delete Stat with id: " + id, MessageTypes.WARN));
        }
        return list();
    }

    // edit/update a Stat record - only if record with id exists

    public ResponseEntity<MessageResponse> update(StatId id, Stat stat) {

        // check if exists first
        // then update

        if (statRepository.existsById(id)) {
            statRepository.save(stat);
            return ResponseEntity.ok(new MyMessageResponse("Stat record updated", MessageTypes.INFO));
        } else {
            return ResponseEntity.ok(new MyMessageResponse("Error: Id does not exist [" + id + "] -> cannot update record", MessageTypes.WARN));
        }
    }

    public List<List<Stat>> getStatsForFixtures(List<Fixture> fixtures) {
        return fixtures.stream()
                .map(fixture -> findByFixtureId(fixture.getId()))
                .filter(stats -> !stats.isEmpty())
                .collect(Collectors.toList());

    }
    public HashMap<String, Integer> sumStatsForFixtures(List<List<Stat>> stats) {
        // sum each stat for each fixture
        HashMap<String, Integer> statCounts = new HashMap<>();
        for(List<Stat> statsPerWin: stats){
            for(int i = 0; i < statsPerWin.size(); i++){
                String stat = statsPerWin.get(i).getStatName().getName();
                statCounts.put(stat,  statCounts.containsKey(stat)
                        ? statCounts.get(stat) + 1
                        : 1 );
            }
        }
        return statCounts;
    }
    public Map<String, Integer> getStatCountAverages(HashMap<String, Integer> statCounts, Integer fixtureCount) {
        return statCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(), // Use a lambda expression to extract the key
                        entry -> Math.round(entry.getValue() / fixtureCount) // Divide the original value by number of fixtureStat lists
                ));
    }

}





