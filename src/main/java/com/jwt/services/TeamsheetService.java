package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.*;
import com.jwt.payload.response.MessageResponse;


import com.jwt.repositories.TeamsheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TeamsheetService {
    @Value("${club.name}")
    private String clubName;

    TeamsheetRepository teamsheetRepository;
    FixtureService fixtureService;
    ClubService clubService;

    @Autowired
    public TeamsheetService(TeamsheetRepository teamsheetRepository,FixtureService fixtureService, ClubService clubService) {
        this.teamsheetRepository = teamsheetRepository;
        this.fixtureService = fixtureService;
        this.clubService = clubService;
    }

    // return all Teamsheets

    public List<Teamsheet> list(){
        List<Teamsheet> teamsheets = teamsheetRepository.findAll();
        if(teamsheets.isEmpty()) new MyMessageResponse("Error: No Teamsheets listed", MessageTypes.WARN);
        return teamsheets;
    }

    // return Teamsheet by id

    public Teamsheet findById( TeamsheetId id){
        Optional<Teamsheet> teamsheet = teamsheetRepository.findById(id);
        if(teamsheet.isEmpty())
            new MyMessageResponse(String.format("Teamsheet fixtureid: %d playerid: %dnot found", id.getFixtureId(), id.getPlayerId()), MessageTypes.ERROR);
        return teamsheet.orElse(new Teamsheet());
    }
    public List<Teamsheet> findByFixtureId( Long id){
        Optional<List<Teamsheet>> teamsheets = teamsheetRepository.findByFixtureId(id);
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
        List<Teamsheet> teamsheets = teamsheetRepository.findByFixtureIdOrderByPosition(id);
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
                .flatMap(f -> teamsheetRepository.findByFixtureId(f.getId()).orElse(new ArrayList<Teamsheet>()).stream())
                .collect(Collectors.toList());
    }
}
