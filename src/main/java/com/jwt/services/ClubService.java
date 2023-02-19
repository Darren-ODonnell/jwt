package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.Club;
import com.jwt.models.ClubModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repositories.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    ClubRepository clubRepository;

    @Autowired
    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    // return all Clubs

    public List<Club> list(){
        List<Club> clubs = clubRepository.findAll();
        if(clubs.isEmpty()) new MyMessageResponse("Error: No Clubs listed", MessageTypes.WARN);
        return clubs;
    }

    public Long getIdByName(String name){
        Club club = clubRepository.getByName(name).orElse(new Club());
        if (!club.getName().equals(name)) new MyMessageResponse(String.format("Club name: %d not found", name), MessageTypes.ERROR);
        return club.getId();
    }


    // return Club by id

    public Club findById( Long id){
        Optional<Club> club = clubRepository.findById(id);
        if(club.isEmpty()) new MyMessageResponse(String.format("Club id: %d not found", id), MessageTypes.ERROR);
        return club.orElse(new Club());
    }

    // return Club by name

    public Club findByName( ClubModel clubModel) {
        Optional<Club> club = clubRepository.findByName(clubModel.getName());
        if(club.isEmpty()) new MyMessageResponse(String.format("Club name: %s not found", clubModel.getName()), MessageTypes.INFO);
        return club.orElse(new Club());
    }
    public Club findByName( String name) {
        Optional<Club> club = clubRepository.findByName(name);
        if(club.isEmpty()) new MyMessageResponse(String.format("Club name: %s not found", name), MessageTypes.INFO);
        return club.orElse(new Club());
    }

    // add new Club

    public ResponseEntity<MessageResponse> add(ClubModel clubModel) {
        if (!clubRepository.existsByName(clubModel.getName())) {
            clubRepository.save(clubModel.translateModelToClub());
            return ResponseEntity.ok(new MyMessageResponse("new Club added", MessageTypes.INFO));
        } else  {
            return ResponseEntity.ok(new MyMessageResponse("Error: Club already exists", MessageTypes.WARN));
        }
    }

    // delete by id

    public ResponseEntity<MessageResponse> delete( Club club){
        Long id = club.getId();
        if(clubRepository.existsById(id)) {
            clubRepository.deleteById(id);
            return ResponseEntity.ok(new MyMessageResponse("Club deleted with id: " + id, MessageTypes.INFO));
        } else {
            return ResponseEntity.ok(new MyMessageResponse("Error: Cannot delete Club with id: " + id, MessageTypes.WARN));
        }

    }

    // edit/update a Club record - only if record with id exists

    public ResponseEntity<MessageResponse> update(Long id, Club club){

        // check if exists first
        // then update

        if(clubRepository.existsById(id)) {
            clubRepository.save(club);
            return ResponseEntity.ok(new MyMessageResponse("Club record updated", MessageTypes.INFO));
        } else {
            return ResponseEntity.ok(new MyMessageResponse("Error: Id does not exist [" + id + "] -> cannot update record", MessageTypes.WARN));
        }
    }


}
