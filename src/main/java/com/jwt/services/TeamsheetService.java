package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.*;
import com.jwt.payload.response.MessageResponse;


import com.jwt.repositories.FixtureRepository;
import com.jwt.repositories.PlayerRepository;
import com.jwt.repositories.TeamsheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.util.*;

import java.util.stream.Collectors;


@Service
public class TeamsheetService {
    @Value("${club.name}")
    private String clubName;

    TeamsheetRepository teamsheetRepository;
    FixtureService fixtureService;
    ClubService clubService;
    private final FixtureRepository fixtureRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public TeamsheetService(TeamsheetRepository teamsheetRepository,FixtureService fixtureService, ClubService clubService,
                            FixtureRepository fixtureRepository,
                            PlayerRepository playerRepository) {
        this.teamsheetRepository = teamsheetRepository;
        this.fixtureService = fixtureService;
        this.clubService = clubService;
        this.fixtureRepository = fixtureRepository;
        this.playerRepository = playerRepository;
    }

    // return all Teamsheets

    public List<Teamsheet> list(){
        List<Teamsheet> teamsheets = teamsheetRepository.findAll();
        if(teamsheets.isEmpty()) new MyMessageResponse("Error: No Teamsheets listed", MessageTypes.WARN);
        return teamsheets;
    }

    // return all Teamsheets

    public List<Teamsheet> last() {
        // get teamsheets
        List<Teamsheet> teamsheets = list();

        // sort by fixtureId leaving the most recent teamsheet first in list
        teamsheets.sort(Comparator.comparing(ts -> ts.getFixture().getFixtureDate(), Comparator.reverseOrder()));

        // pick up fixtureId from first teamsheet
        Long fixtureId = teamsheets.get(0).getFixture().getId();

        // filter by this fixtureId
        List<Teamsheet> filteredTeamsheets = teamsheets.stream()
                .filter(ts -> ts.getFixture().getId() == fixtureId)
                .collect(Collectors.toList());

        // check if empty and return
        if(filteredTeamsheets.isEmpty())
            new MyMessageResponse(String.format("No Teamsheets found for this fixture Id: %d ", fixtureId), MessageTypes.ERROR);
        return filteredTeamsheets;
    }

    // return Teamsheet by id

    public Teamsheet findById( TeamsheetId id){
        Optional<Teamsheet> teamsheet = teamsheetRepository.findById(id);
        if(teamsheet.isEmpty())
            new MyMessageResponse(String.format("Teamsheet fixtureid: %d playerid: %dnot found", id.getFixtureId(), id.getPlayerId()), MessageTypes.ERROR);
        return teamsheet.orElse(new Teamsheet());
    }
    public List<Teamsheet> findByFixtureId( Long id){
        Optional<List<Teamsheet>> teamsheets = teamsheetRepository.findByFixtureIdOrderByPosition_Id(id);
        if(teamsheets.isEmpty())
            new MyMessageResponse(String.format("Fixture id: %d not found", id), MessageTypes.ERROR);
        return teamsheets.orElse(new ArrayList<>());
    }
    public List<Teamsheet> findByPlayerId( Long id){
        Optional<List<Teamsheet>> teamsheets = Optional.ofNullable(teamsheetRepository.findByPlayerId(id));
        if(teamsheets.isEmpty())
            new MyMessageResponse(String.format("Player id: %d not found", id), MessageTypes.ERROR);
        return teamsheets.orElse(new ArrayList<>());
    }

    public List<Player> findPlayersByFixtureId( Long id){
        // get teamsheet by fixture id.
        // extract and return the list of players from this list
        List<Teamsheet> teamsheets = teamsheetRepository.findByFixtureIdOrderByPosition_Id(id).orElse(new ArrayList<>());
        List<Player> players = new ArrayList<>();
        for(Teamsheet ts : teamsheets)
            players.add(ts.getPlayer());
        return players;
    }

    // add new Teamsheet

    public ResponseEntity<MessageResponse> add(TeamsheetModel teamsheetModel){
        return addAll(Collections.singletonList(teamsheetModel));
    }

    // add new Teamsheet
    public ResponseEntity<MessageResponse> addAll(List<TeamsheetModel> teamsheetModels){
        List<Teamsheet> teamsheetsToSave = new ArrayList<>();

        for (TeamsheetModel teamsheetModel : teamsheetModels) {
            if(!teamsheetRepository.existsByFixtureId(teamsheetModel.getFixture().getId())) {
                Teamsheet teamsheet = teamsheetModel.translateModelToTeamsheet();
                teamsheetsToSave.add(teamsheet);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Teamsheet already exists", MessageTypes.WARN));
            }
        }
        teamsheetRepository.saveAll(teamsheetsToSave);
        return ResponseEntity.ok(new MyMessageResponse("new Teamsheets added", MessageTypes.INFO));
    }

    // add new Teamsheet
    public ResponseEntity<MessageResponse> addAll2(List<Teamsheet> teamsheets){
        List<Teamsheet> teamsheetsToSave = new ArrayList<>();

        for (Teamsheet teamsheet : teamsheets) {
            // skip over null objects created by the deserialisation process
            if(teamsheet.getFixture() == null || teamsheet.getPlayer() == null) continue;

            // the manual json deserialisation does not create the id object
            // this is created here before saving to the db
            TeamsheetId id = new TeamsheetId( teamsheet.getFixture().getId(), teamsheet.getPlayer().getId() );
            teamsheet.setId(id);

            Fixture fixture = fixtureRepository.findById(teamsheet.getFixture().getId()).orElse(new Fixture());
            Player player = playerRepository.findById(teamsheet.getPlayer().getId()).orElse(new Player());

            teamsheet.setPlayer(player);
            teamsheet.setFixture(fixture);

            if(!teamsheetRepository.existsById(teamsheet.getId()))
                teamsheetsToSave.add(teamsheet);
             else // id exists - so update
                 teamsheetRepository.save(teamsheet);

        }
        teamsheetRepository.saveAll(teamsheetsToSave);
        return ResponseEntity.ok(new MyMessageResponse("new Teamsheets added for Date: " + teamsheets.get(0).getFixture().getFixtureDate() , MessageTypes.INFO));
    }

    // delete by id

    public List<Teamsheet> delete(Teamsheet teamsheet){
        TeamsheetId id = teamsheet.getId();
        if(teamsheetRepository.existsById(id)) {
            teamsheetRepository.deleteById(id);
             ResponseEntity.ok(new MyMessageResponse("Teamsheet deleted with id: " + id, MessageTypes.INFO));
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Cannot delete Teamsheet with id: " + id, MessageTypes.WARN));
        }
        return list();
    }

    // edit/update a Teamsheet record - only if record with id exists

    public List<Teamsheet> update(Teamsheet teamsheet){
        return updateAll(Collections.singletonList(teamsheet));
    }

    // edit/update all Teamsheets records - only if record with id exists

    public List<Teamsheet> updateAll(List<Teamsheet> teamsheets){
        // this method assumes that the list of updates are all against the same fixture.

        List<Teamsheet> teamsheetsToUpdate = new ArrayList<>();
        teamsheets.stream()
                .forEach(teamsheet -> teamsheet.setId(new TeamsheetId(teamsheet.getFixture().getId(), teamsheet.getPlayer().getId())));

        for (Teamsheet teamsheet : teamsheets) {
            if (teamsheetRepository.existsById(teamsheet.getId())) {
                teamsheetsToUpdate.add(teamsheet);
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MyMessageResponse("Error: Teamsheet with ID " + teamsheet.getId() + " not found", MessageTypes.WARN));
            }
        }
        teamsheetRepository.saveAll(teamsheetsToUpdate);
        return findByFixtureId(teamsheets.get(0).getFixture().getId());
    }


    public List<Teamsheet> findPlayersByFixtureDate(Date fixtureDate) {

        Long teamId = clubService.getIdByName(clubName);
        List<Fixture> fixtures = fixtureService.findByFixtureDate(fixtureDate);

        return  fixtures.stream()
                .filter(f -> f.getHomeTeam().getId().equals(teamId)  || f.getAwayTeam().getId().equals(teamId))
                .flatMap(f -> teamsheetRepository.findByFixtureIdOrderByPosition_Id(f.getId()).orElse(new ArrayList<Teamsheet>()).stream())
                .collect(Collectors.toList());
    }
}
