package com.jwt.services;

// identify the basic crud services which should be included here
// as stats_view is basically a View - there are no edit/update/add or delete operations here just Get operations
// these operations occur in the real table - Stats

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.*;
import com.jwt.repositories.FixtureRepository;
import com.jwt.repositories.StatsViewRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
public class StatsViewService {
    @Value("${club.name}")
    String clubName;

    final int KEY = 0;
    final int COUNT = 1;

    private final FixtureRepository fixtureRepository;

    StatsViewRepository statsViewRepository;
    StatNameService statNameService;
    StatService statService;
    ClubService clubService;
    FixtureService fixtureService;


    @Autowired
    public StatsViewService(StatsViewRepository statsViewRepository, ClubService clubService, StatNameService statNameService,
                            FixtureRepository fixtureRepository) {
        this.statsViewRepository = statsViewRepository;
        this.clubService = clubService;
        this.statNameService = statNameService;
        this.fixtureRepository = fixtureRepository;


    }

    @Autowired
    public void setFixtureService(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }
    @Autowired
    public void setStatService(StatService statService) {
        this.statService = statService;
    }

    // return all Stats
    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public List<StatsView> list(){
        List<StatsView> statsView = statsViewRepository.findAll();
        if(statsView.isEmpty()) new MyMessageResponse("Error: No Stats listed", MessageTypes.WARN);
        return statsView;
    }

    public Date stringToDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return Date.valueOf(date);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> List<Key<T, BigInteger>>  mapData3(List<Object[]> seasons, Class T) {
        ArrayList<Key<T, BigInteger>> stats = new ArrayList<>();

        for(Object[] obj : seasons) {
            Key<T, BigInteger> stat = new Key<>();

            stat.setKey((T) obj[KEY]);
            stat.setCount((BigInteger) obj[COUNT]);

            stats.add(stat);
        }
        return stats;
    }


    // convert the List<Object[]> into List<StatViewCounts>
    // most of the conversion is done in the StatViewCount constructor
    private List<StatViewCounts> mapData(List<Object[]> seasons) {

        List<StatViewCounts> counts = new ArrayList<>();
        for(Object[] record : seasons) {
            StatViewCounts stat = new StatViewCounts(record);
            counts.add(stat);
        }
        return counts;
    }

    public List<StatViewCounts> countAllPlayerStat() {
        List<Object[]> data = statsViewRepository.countAllPlayerStat();
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerFixtureByStatName(String statName) {
        List<Object[]> data = statsViewRepository.countAllPlayerFixtureByStatName(statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerStatNameByFixtureDate(String fixtureDate) {
        List<Object[]> data = statsViewRepository.countAllPlayerStatNameByFixtureDate(fixtureDate);
        return mapData(data);
    }

    public List<StatViewCounts> countAllStatNameFixtureByPlayer(String firstname, String lastname) {
        List<Object[]> data = statsViewRepository.countAllStatNameFixtureByPlayer(firstname, lastname);
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerByFixtureStatName(String fixtureDate, String statName) {
        List<Object[]> data = statsViewRepository.countAllPlayerByFixtureStatName(fixtureDate, statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllFixtureByPlayerStatName(String firstname, String lastname, String statName) {
        List<Object[]> data = statsViewRepository.countAllFixtureByPlayerStatName(firstname, lastname, statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllStatsByPlayerFixtureDate(String firstname, String lastname, String fixtureDate) {
        List<Object[]> data = statsViewRepository.countAllStatsByPlayerFixtureDate(firstname, lastname, fixtureDate);
        return mapData(data);
    }

    public List<StatViewCounts> countStat(String firstname, String lastname, String fixtureDate, String statName) {
        List<Object[]> data = statsViewRepository.countStat(firstname, lastname, fixtureDate, statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerStatHeatMap() {
        List<Object[]> data = statsViewRepository.countAllPlayerStatHeatMap();
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerFixtureByStatNameHeatMap(String statName) {
        List<Object[]> data = statsViewRepository.countAllPlayerFixtureByStatNameHeatMap(statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerStatNameByFixtureDateHeatMap(String fixtureDate) {
        List<Object[]> data = statsViewRepository.countAllPlayerStatNameByFixtureDateHeatMap(fixtureDate);
        return mapData(data);
    }

    public List<StatViewCounts> countAllStatNameFixtureByPlayerHeatMap(String firstname, String lastname) {
        List<Object[]> data = statsViewRepository.countAllStatNameFixtureByPlayerHeatMap(firstname, lastname);
        return mapData(data);
    }

    public List<StatViewCounts> countAllPlayerByFixtureStatNameHeatMap(String fixtureDate, String statName) {
        List<Object[]> data = statsViewRepository.countAllPlayerByFixtureStatNameHeatMap(fixtureDate, statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllFixtureByPlayerStatNameHeatMap(String firstname, String lastname, String statName) {
        List<Object[]> data = statsViewRepository.countAllFixtureByPlayerStatNameHeatMap(firstname, lastname, statName);
        return mapData(data);
    }

    public List<StatViewCounts> countAllStatsByPlayerFixtureDateHeatMap(String firstname, String lastname, String fixtureDate) {
        List<Object[]> data = statsViewRepository.countAllStatsByPlayerFixtureDateHeatMap(firstname, lastname, fixtureDate);
        return mapData(data);
    }

    public List<StatViewCounts> countStatHeatMap(String firstname, String lastname, String fixtureDate, String statName) {
        List<Object[]> data = statsViewRepository.countStatHeatMap(firstname, lastname, fixtureDate, statName);
        return mapData(data);
    }

    public List<StatViewCounts> countStatsPlayerAnalysis(String firstname, String lastname) {
        List<Object[]> data = statsViewRepository.countStatsPlayerAnalysis(firstname, lastname);
        return mapData(data);
    }

    public List<StatViewCounts> countStatsAllPlayerAnalysis() {
        List<Object[]> data = statsViewRepository.countStatsAllPlayerAnalysis();
        return mapData(data);
    }


    public List<StatViewCounts> countAllPlayerStatNameByFixtureDateGroupSuccess(String fixtureDate) {
        List<Object[]> data = statsViewRepository.countAllPlayerStatNameByFixtureDateGroupSuccess(fixtureDate);
        return mapData(data);
    }

    public List<StatViewCounts> averageScoreByOpposition(String clubName) {
        List<Object[]> data = statsViewRepository.averageScoreByOpposition(clubName);
        return mapData(data);
    }

    public List<StatViewCounts> averageByStatNameByOpposition( String opposition) {
        List<Object[]> data = statsViewRepository.averageByStatNameByOpposition( clubName, opposition);
        return mapData(data);
    }

    public List<Fixture> findLossesByOpposition(String team) {
        //Get all fixtures where judes are playing against opposition where home/ opposition is judes AND home/opposition is team
        Long clubId = clubService.findByName(team).getId();
        List<Fixture> fixturesVsOpponent = fixtureService.findByOppositionId(clubId);
        return fixturesVsOpponent.stream() // convert to stream
                .filter(fixture -> !fixtureWon(fixture)) // select fixtures not won
                .collect(Collectors.toList()); // collect to and return list of fixtures
    }

    private boolean fixtureWon(Fixture fixture) {
        Long id = clubService.findByName(clubName).getId();
        Result result = statService.scoreByFixtureDate(fixture.getFixtureDate());
        return fixture.getHomeTeam().getId().equals(id)
                ? result.getHomeScorePoints() > result.getAwayScorePoints()
                : result.getHomeScorePoints() < result.getAwayScorePoints();
    }

    public List<Fixture> getWins(List<Fixture> fixtures) {
        return fixtures.stream()
                .filter(this::fixtureWon) // return stream based on fixtures Won passing in this = {current fixture)
                .collect(Collectors.toList()); // finally collect results into List<Fixture>
    }

    public List<Fixture> getLosses(List<Fixture> fixtures) {
        return fixtures.stream()
                .filter(fixture -> !fixtureWon(fixture)) // return stream nased on fixtures Won passing in this = {current fixture)
                .collect(Collectors.toList()); // finally collect results into List<Fixture>
    }

    public Map<String, Integer> getStatCountAverages(HashMap<String, Integer> statCounts, Integer fixtureCount) {
        return statCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(), // Use a lambda expression to extract the key
                        entry -> Math.round(entry.getValue() / fixtureCount) // Divide the original value by number of fixtureStat lists
                ));
    }

    public List<Fixture> findWinsByOpposition(String team) {
        Long clubId = clubService.findByName(team).getId();
        return fixtureService.findByOppositionId(clubId)
                .stream()
                .filter(this::fixtureWon) // returns a new stream based on fixtureWon passing in this = (current fixture)
                .collect(Collectors.toList()); // finnally collect results into a List<Fixture>
    }

    private List<StatViewCounts> mapStatCountToStatViewCount( Map<String, Integer> averageStatCounts ) {
        return averageStatCounts.entrySet().stream()
                .map(entry -> new StatViewCounts(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StatViewCounts::getCount).reversed())
                .collect(Collectors.toList());
    }

    private List<StatViewCounts> getStatsAverageStatsForFixtures(List<Fixture> fixtures) {
        List<List<Stat>> statsForEachFixture = statService.getStatsForFixtures(fixtures);
        HashMap<String, Integer> statCounts = statService.sumStatsForFixtures( statsForEachFixture );
        Map<String, Integer> averageStatCounts = statService.getStatCountAverages(statCounts, statsForEachFixture.size());

        // take the two values Stat - Value/Count and map to StatViewCount for return to client
        return mapStatCountToStatViewCount(averageStatCounts);
    }

    public List<StatViewCounts> getAvgStatsForWinsByOpponent(String team){
        // get the fixtures won and the stats for those fixtures
        List<Fixture> fixturesWon = findWinsByOpposition(team);
        return getStatsAverageStatsForFixtures(fixturesWon);
    }

    public List<StatViewCounts> getAvgStatsForLossesByOpponent(String team){
        // get the fixtures won and the stats for those fixtures
        List<Fixture> fixturesLost = findLossesByOpposition(team);
        return getStatsAverageStatsForFixtures(fixturesLost);
    }

    public List<StatViewCounts> getStatsForLastFiveFixturesWon() {
        List<Fixture> fixtures = fixtureService.findLastFive();
        fixtures = getWins(fixtures);
        List<StatViewCounts> statViewCounts = getStatsAverageStatsForFixtures(fixtures);
        return statViewCounts;
    }

    public List<StatViewCounts> getStatsForLastFiveFixturesLost() {
        List<Fixture> fixtures = fixtureService.findLastFive();
        fixtures = getLosses(fixtures);
        List<StatViewCounts> statViewCounts = getStatsAverageStatsForFixtures(fixtures);
        return statViewCounts;
    }
}
