package com.jwt.controllers;

import com.jwt.models.Firstname;
import com.jwt.models.StatName;
import com.jwt.models.StatNameModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.services.StatNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Darren O'Donnell
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping({"/statname","/statnames"})
public class StatNameController {
    public final StatNameService statNameService;

    @Autowired
    public StatNameController(StatNameService statNameService) {
        this.statNameService = statNameService;
    }

    // return all StatNames

    @GetMapping(value={"/","/list"} )
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody List<StatName> list(){
        return statNameService.list();
    }

    // return StatName by id

    @GetMapping(value="/findById")
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody StatName findById(@RequestParam("abbrev")  String id){
        return statNameService.findById(id);
    }

    // return StatName by name

    @GetMapping(value="/findByName")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public @ResponseBody  StatName findByName(@RequestBody StatNameModel statNameModel) {
        return statNameService.findByName(statNameModel);
    }

    // add new StatName

    @PutMapping(value="/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> add(@RequestBody StatNameModel statNameModel){
        return statNameService.add(statNameModel);
    }

    // edit/update a StatName record - only if record with id exists

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> update(@RequestBody StatName statName) {
        return statNameService.update( statName.getId(), statName);
    }

    // delete by id

    @DeleteMapping(value="/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> delete(@RequestBody StatName statName){
        return statNameService.delete(statName);
    }
}
