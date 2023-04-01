package com.jwt.services;

import com.jwt.enums.MessageTypes;
import com.jwt.exceptions.MyMessageResponse;
import com.jwt.models.Fellowship;
import com.jwt.models.FellowshipModel;
import com.jwt.models.Manager;
import com.jwt.models.ManagerModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repositories.FellowshipRepository;
import com.jwt.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {
    ManagerRepository managerRepository;
    FellowshipRepository fellowshipRepository;
    FellowshipService fellowshipService;

    @Autowired
    public ManagerService(ManagerRepository managerRepository,FellowshipService fellowshipService) {
        this.managerRepository = managerRepository;
        this.fellowshipService = fellowshipService;
    }

    // return all managers - done

    public List<Manager> list() {
        return managerRepository.findAll();
    }


    // return manager by id

    public Manager findById(Long id){
        Optional<Manager> manager = managerRepository.findById(id);
        if(manager.isEmpty())
            new MyMessageResponse("Manager not found with id: " + id , MessageTypes.WARN);

        return manager.orElse(new Manager());
    }

    // return manager by firstname + lastname

    public Manager findByFirstnameLastname(ManagerModel managerModel) {
        Optional<Manager> manager = managerRepository.findByFirstnameAndLastname(managerModel.getFirstname(), managerModel.getLastname());
        if(manager.isEmpty())
            new MyMessageResponse("Manager not found with Firstname: "+managerModel.getFirstname()+", and lastname: "+managerModel.getLastname(), MessageTypes.WARN);

        return manager.orElse(new Manager());
    }

    // return manager by Email

    public Manager findByEmail(String email) {
        Optional<Manager> manager = managerRepository.findByEmail(email);
        if(manager.isEmpty())
            new MyMessageResponse("Manager not found with Email: " + email + ", and lastname: " + email, MessageTypes.WARN);

        return manager.orElse(new Manager());
    }

    // return managers with same lastname

    public  List<Manager> findByLastname(ManagerModel managerModel) {
        Optional<List<Manager>> managers = managerRepository.findByLastname(managerModel.getLastname());
        if(managers.isEmpty())
            new MyMessageResponse("Manager not found with lastname: " + managerModel.getLastname(), MessageTypes.WARN);

        return managers.orElse(new ArrayList<>());
    }

    public  List<Manager> findByLastname(String lastname) {
        Optional<List<Manager>> managers = managerRepository.findByLastname(lastname);
        if(managers.isEmpty())
            new MyMessageResponse("Manager not found with lastname: " + lastname, MessageTypes.WARN);

        return managers.orElse(new ArrayList<>());
    }

    // return managers with same firstname

    public List<Manager> findByFirstname(ManagerModel managerModel) {
        Optional<List<Manager>> managers = managerRepository.findByFirstname(managerModel.getFirstname());
        if(managers.isEmpty())
            new MyMessageResponse("Manager not found with Firstname: "+managerModel.getFirstname(), MessageTypes.WARN);

        return managers.orElse(new ArrayList<>());
    }

    // add manager

    public ResponseEntity<MessageResponse> add( ManagerModel managerModel){


        if(!managerRepository.existsByFirstnameAndLastname(managerModel.getFirstname(), managerModel.getLastname())) {
            fellowshipRepository.save(managerModel.translateModelToFellowship());
            return ResponseEntity.ok(new MyMessageResponse("New Manager Added", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Manager already exists", MessageTypes.WARN));
        }
    }

    // edit/update manager

    public ResponseEntity<MessageResponse> update(Long id, Manager manager){
        if(fellowshipRepository.existsById(id)) {
            Fellowship fellow = new Fellowship();
            fellow.setId(id);
            fellow.setFirstname(manager.getFirstname());
            fellow.setLastname(manager.getLastname());
            fellow.setFirstnameI(manager.getFirstnameI());
            fellow.setLastnameI(manager.getLastnameI());
            fellow.setEmail(manager.getEmail());
            fellow.setAddress(manager.getAddress());
            fellow.setPhone(manager.getPhone());
            fellow.setFellowType("Manager");

            fellowshipRepository.save(fellow);
            return ResponseEntity.ok(new MyMessageResponse("Manager record updated", MessageTypes.INFO));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Manager with Id: [" + id + "] -> does not exist - cannot update record", MessageTypes.WARN));
        }

    }

    // delete manager

    public List<Manager>  delete(Manager manager){
        Long id = manager.getId();
        if(managerRepository.existsById(id)) {
            managerRepository.deleteById(id);
             ResponseEntity.ok(new MyMessageResponse("Manager deleted with id: " + id, MessageTypes.INFO));
        } else {
             ResponseEntity.status(HttpStatus.CONFLICT).body(new MyMessageResponse("Error: Cannot delete manager with id: " + id, MessageTypes.WARN));
        }
        return list();

    }


}
