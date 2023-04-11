package com.jwt.controllers;

import com.jwt.models.Player;
import com.jwt.models.Teamsheet;
import com.jwt.models.TeamsheetId;
import com.jwt.models.TeamsheetModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.services.TeamsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

/**
 * @author Darren O'Donnell
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping({"/teamsheet","/teamsheets"})
public class TeamsheetController {

    public final TeamsheetService teamsheetService;

    @Autowired
    public TeamsheetController( TeamsheetService teamsheetService) {
        this.teamsheetService = teamsheetService;
    }

    // return all Teamsheets

    @GetMapping(value={"/","/list"} )
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Teamsheet> list(){
        return teamsheetService.list();
    }

    // return the last Teamsheet in the past from todays date

    @GetMapping(value={"/","/last"} )
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Teamsheet> last(){

        return teamsheetService.last();
    }

    // return Teamsheet by id

    @GetMapping(value="/findById")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody Teamsheet findById(@RequestParam("id") TeamsheetId id){
        return teamsheetService.findById(id);
    }

    // return Teamsheet by id

    @GetMapping(value="/findByFixtureId")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Teamsheet> findByFixtureId(@RequestParam("id") Long fixtureId){
        return teamsheetService.findByFixtureId(fixtureId);
    }
    @GetMapping(value="/findPlayersByFixtureId")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Player> findPlayersByFixtureId(@RequestParam("id") Long id){
        return teamsheetService.findPlayersByFixtureId(id);
    }

    @GetMapping(value="/findPlayersByFixtureDate")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Teamsheet> findPlayersByFixtureDate(@RequestParam("fixture_date") Date fixtureDate){
        return teamsheetService.findPlayersByFixtureDate(fixtureDate);
    }

    @GetMapping(value="/findByPlayerId")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Teamsheet> findByPlayerId(@RequestParam("id") Long playerId){
        return teamsheetService.findByPlayerId(playerId);
    }

    // add new Teamsheet

    @PutMapping(value="/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')") 
    public ResponseEntity<MessageResponse> add(@RequestBody TeamsheetModel teamsheetModel){
        return teamsheetService.add(teamsheetModel);
    }

    // add new Teamsheet

    @PutMapping(value="/addAll")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<MessageResponse> addAll(@RequestBody List<TeamsheetModel> teamsheetModels){
        return teamsheetService.addAll(teamsheetModels);
    }

    // edit/update a Teamsheet record - only if record with id exists

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public List<Teamsheet> update(@RequestBody Teamsheet teamsheet) {
        return teamsheetService.updateAll(Collections.singletonList(teamsheet));
    }

    // edit/update Teamsheet records - only if individual teamsheet records with id exist.

    @PostMapping(value="/updateAll")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Teamsheet> update(@RequestBody List<Teamsheet> teamsheets) {
        return teamsheetService.updateAll(teamsheets);
    }

    // delete by id

    @DeleteMapping(value="/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')") 
    public @ResponseBody List<Teamsheet> delete(@RequestBody Teamsheet teamsheet){
        return teamsheetService.delete(teamsheet);
    }
}
