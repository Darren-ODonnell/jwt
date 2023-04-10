package com.jwt.controllers;

import com.jwt.models.ClubModel;
import com.jwt.models.Fixture;
import com.jwt.models.FixtureModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.services.FixtureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Darren O'Donnell
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping({"/fixture","/fixtures"})
public class FixtureController {
    public final FixtureService fixtureService;

    @Autowired
    public FixtureController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    // return all Fixtures

    @GetMapping(value={"/","/list" })
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> list(){
        return fixtureService.findAll();
    }

    // return all Fixtures

    @GetMapping(value={"/","/listWithNoTeamsheet" })
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> listWithNoTeamsheet(){
        return fixtureService.findWithMoTeamsheet();
    }



    // return all Upcoming Fixtures

    @GetMapping(value={"/findUpcoming" })
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> listUpcoming(){
        return fixtureService.findUpcoming();
    }

    // return Fixture by id

    @GetMapping(value="/findById")
        @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody Fixture findById(@RequestParam("id") Long id){
        return fixtureService.findById(id);
    }

    // return Fixtures By Club name

    @GetMapping(value="/findByClub")
        @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> findByClub(@ModelAttribute ClubModel clubModel)  {
        return fixtureService.getClubFixtures(clubModel);
    }

    // return next Fixture By Club name

    @GetMapping(value="/findNextByClub")
        @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody Fixture findNextFixture(@ModelAttribute ClubModel clubModel) {
        return fixtureService.getNextClubFixture(clubModel);
    }

    // return Home Fixtures By Club name

    @GetMapping(value="/findByHomeByClub")
        @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> findByHomeByClub(@ModelAttribute ClubModel clubModel) {
        return fixtureService.getClubHomeFixtures(clubModel);
    }

    // locate specific fixture by Competition, HomeTeam, AwayTeam , FixtureDate and Season

    @GetMapping(value="/findByCompetitionHomeTeamAwayTeamFixtureDateSeason")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody Fixture findByCompetitionHomeTeamAwayTeamFixtureDateSeason(@ModelAttribute FixtureModel fixtureModel) {
        return fixtureService.findByCompetitionHomeTeamAwayTeamFixtureDateSeason( fixtureModel);
    }

    // return Away Fixtures By Club name

    @GetMapping(value="/findByAwayByClub")
        @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> findByAwayByClub(@ModelAttribute ClubModel clubModel)  {
        return fixtureService.getClubAwayFixtures(clubModel.getName());
    }

    @GetMapping(value="/findByFixtureDate")
        @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> findByAwayByClub(@ModelAttribute FixtureModel fixtureModel)  {
        return fixtureService.findByFixtureDate(fixtureModel.getFixtureDate());
    }

    // add new fixture

    @PutMapping(value="/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<MessageResponse> add(@RequestBody FixtureModel fixtureModel) {
        ResponseEntity<MessageResponse> response = fixtureService.add(fixtureModel);
        return response;
    }

    // update record

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<MessageResponse> update(@RequestBody Fixture fixture){
        return fixtureService.update(fixture.getId(), fixture);
    }

    // delete by id

    @DeleteMapping(value="/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<Fixture> delete(@RequestBody Fixture fixture){
        return fixtureService.delete(fixture);
    }
}
