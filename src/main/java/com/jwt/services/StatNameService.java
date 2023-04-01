package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.StatName;
import com.jwt.models.StatNameModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repositories.StatNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Service
public class StatNameService {

    StatNameRepository statNameRepository;

    @Autowired
    public StatNameService(StatNameRepository statNameRepository) {
        this.statNameRepository = statNameRepository;
    }

    // return all StatNames

    public List<StatName> list(){
        List<StatName> statNames = statNameRepository.findAll();
        if(statNames.isEmpty()) new MyMessageResponse("Error: No StatNames listed", MessageTypes.WARN);
        return statNames;
    }

    // return StatName by id

    public StatName findById(String id){
        Optional<StatName> statName = statNameRepository.findById(id);
        if(statName.isEmpty())
            new MyMessageResponse(String.format("StatName id: %s not found", id), MessageTypes.ERROR);
        return statName.orElse(new StatName());
    }

    // return StatName by name

    public StatName findByStatName( StatNameModel statNameModel) {
        Optional<StatName> statName = statNameRepository.findByName(statNameModel.getName());
        if(statName.isEmpty()) new MyMessageResponse(String.format("StatName name: %s not found", statNameModel.getName()), MessageTypes.INFO);
        return statName.orElse(new StatName());
    }
    public String findByStatName( String statName) {
        StatName name = statNameRepository.findByName(statName).orElse(new StatName());
        if(name.getId().equals(null)) new MyMessageResponse(String.format("StatName name: %s not found", statName), MessageTypes.INFO);
        return name.getId();
    }



    // add new StatName

    public ResponseEntity<MessageResponse> add(StatNameModel statNameModel){

        if(!statNameRepository.existsById(statNameModel.getAbbrev())) {
            statNameRepository.save(statNameModel.translateModelToStatName(statNameModel.getAbbrev()));
            return ResponseEntity.ok(new MyMessageResponse("new StatName added", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: StatName already exists", MessageTypes.WARN));
        }

    }

    // delete by name

    public  List<StatName> delete(StatName statName){
        if(statNameRepository.existsById(statName.getId())) {
            statNameRepository.deleteById(statName.getId());
             ResponseEntity.ok(new MyMessageResponse("StatName deleted with id: " + statName.getId(), MessageTypes.INFO));
        } else {
             ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Cannot delete StatName with id: " + statName.getId(), MessageTypes.WARN));
        }
        return list();
    }

    // edit/update a StatName record - only if record with id exists

    public ResponseEntity<MessageResponse> update(StatName statName){

        // check if exists first
        // then update

        if(statNameRepository.existsById(statName.getId())) {
            statNameRepository.save(statName);
            return ResponseEntity.ok(new MyMessageResponse("StatName record updated", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Id does not exist [" + statName.getId() + "] -> cannot update record", MessageTypes.WARN));
        }

    }


//    public String getIdByStatName(String statName) {
//         String id = statNameRepository.findIdByStatName(String statName);
//    }
}
