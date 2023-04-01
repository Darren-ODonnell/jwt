package com.jwt.controllers;

import com.jwt.models.StatName;
import com.jwt.models.StatNameModel;
import com.jwt.payload.response.MessageResponse;
import com.jwt.services.StatNameService;
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
@RequestMapping({"/statname","/statnames"})
public class StatNameController {
    public final StatNameService statNameService;

    @Autowired
    public StatNameController(StatNameService statNameService) {
        this.statNameService = statNameService;
    }

    // return all StatNames

    @GetMapping(value={"/","/list"} )
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody List<StatName> list(){
        return statNameService.list();
    }

    // return StatName by id

    @GetMapping(value="/findById")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody StatName findById(@RequestParam("abbrev")  String id){
        return statNameService.findById(id);
    }

    // return StatName by name

    @GetMapping(value="/findByName")
    @PreAuthorize("hasRole('ROLE_PLAYER')  or hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public @ResponseBody  StatName findByName(@ModelAttribute StatNameModel statNameModel) {
        return statNameService.findByStatName(statNameModel);
    }

    // add new StatName

    @PutMapping(value="/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')") 
    public ResponseEntity<MessageResponse> add(@RequestBody StatNameModel statNameModel){
        return statNameService.add(statNameModel);
    }

    // edit/update a StatName record - only if record with id exists

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')") 
    public ResponseEntity<MessageResponse> update(@RequestBody StatName statName) {
        return statNameService.update( statName);
    }

    // delete by id

    @DeleteMapping(value="/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')") 
    public @ResponseBody List<StatName> delete(@RequestBody StatName statName){
        return statNameService.delete(statName);
    }
}
