package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.Fellowship;
import com.jwt.models.Player;
import com.jwt.models.PlayerModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repositories.FellowshipRepository;
import com.jwt.repositories.ManagerRepository;
import com.jwt.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    PlayerRepository playerRepository;
    FellowshipRepository fellowshipRepository;

    @Autowired
    public PlayerService(FellowshipRepository fellowshipRepository, PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.fellowshipRepository = fellowshipRepository;
    }

    // return all players - done

    public List<Player> list() {
        return playerRepository.findAll();
    }

    // return player by id

    public Player findById(Long id){
        Optional<Player> player = playerRepository.findById(id);
        if(player.isEmpty())
            new MyMessageResponse("Player not found with id: " + id , MessageTypes.WARN);

        return player.orElse(new Player());
    }

    // return player by email

    public Player findByEmail(String email){
        Optional<Player> player = playerRepository.findByEmail(email);
        if(player.isEmpty())
            new MyMessageResponse("Player not found with email: " + email , MessageTypes.WARN);

        return player.orElse(new Player());
    }

    // return player by firstname + lastname

    public Player findByFirstnameLastname(PlayerModel playerModel) {
        Optional<Player> player = playerRepository.findByFirstnameAndLastname(playerModel.getFirstname(), playerModel.getLastname());
        if(player.isEmpty())
            new MyMessageResponse("Player not found with Firstname: "+playerModel.getFirstname()+", and lastname: "+playerModel.getLastname(), MessageTypes.WARN);

        return player.orElse(new Player());
    }

    // return players with same lastname

    public  List<Player> findByLastname(PlayerModel playerModel) {
        Optional<List<Player>> players = playerRepository.findByLastname(playerModel.getLastname());
        if(players.isEmpty())
            new MyMessageResponse("Player not found with lastname: " + playerModel.getLastname(), MessageTypes.WARN);

        return players.orElse(new ArrayList<>());
    }

    public  List<Player> findByLastname(String lastname) {
        Optional<List<Player>> players = playerRepository.findByLastname(lastname);
        if(players.isEmpty())
            new MyMessageResponse("Player not found with lastname: " + lastname, MessageTypes.WARN);

        return players.orElse(new ArrayList<>());
    }

    // return players with same firstname

    public List<Player> findByFirstname(PlayerModel playerModel) {
        Optional<List<Player>> players = playerRepository.findByFirstname(playerModel.getFirstname());
        if(players.isEmpty())
            new MyMessageResponse("Player not found with Firstname: "+playerModel.getFirstname(), MessageTypes.WARN);

        return players.orElse(new ArrayList<>());
    }

    // add player

    public ResponseEntity<MessageResponse> add( PlayerModel playerModel){
        if(!fellowshipRepository.existsByFirstnameAndLastname(playerModel.getFirstname(), playerModel.getLastname())) {
            fellowshipRepository.save(playerModel.translateModelToFellowship());
            return ResponseEntity.ok(new MyMessageResponse("New PLayer Added", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Player already exists", MessageTypes.WARN));
        }
    }

    // edit/update player

    public ResponseEntity<MessageResponse> update(Long id, Player player){
        Fellowship fellow = new Fellowship();
        fellow.setId(id);
        fellow.setFirstname(player.getFirstname());
        fellow.setLastname(player.getLastname());
        fellow.setFirstnameI(player.getFirstnameI());
        fellow.setLastnameI(player.getLastnameI());
        fellow.setEmail(player.getEmail());
        fellow.setAddress(player.getAddress());
        fellow.setPhone(player.getPhone());
        fellow.setPhoneIce(player.getPhoneIce());
        fellow.setYob(player.getYob());
        fellow.setRegistered(player.getRegistered());
        fellow.setAvailability(player.getAvailability());
        fellow.setGrade(player.getGrade());
        fellow.setFellowType("Player");

        if(fellowshipRepository.existsById(id)) {
            fellowshipRepository.save(fellow);
            return ResponseEntity.ok(new MyMessageResponse("Player record updated", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Player with Id: [" + id + "] -> does not exist - cannot update record", MessageTypes.WARN));
        }

    }

    // delete player

    public List<Player>  delete(Player player){
        Long id = player.getId();
        if(fellowshipRepository.existsById(id)) {
            fellowshipRepository.deleteById(id);
             ResponseEntity.ok(new MyMessageResponse("Player deleted with id: " + id, MessageTypes.INFO));
        } else {
             ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Cannot delete player with id: " + id, MessageTypes.WARN));
        }
        return list();

    }


}
