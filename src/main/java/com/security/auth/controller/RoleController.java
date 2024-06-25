package com.security.auth.controller;

import com.security.auth.dto.RoleRequest;
import com.security.auth.exception.CustomException;
import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;
import com.security.auth.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping()
     public List<Role>  getAllRoles(){
        return roleRepository.findAll();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Role>  getRoleByName(@PathVariable String name){
        try {
            RoleEnum roleEnum = RoleEnum.valueOf(name.toUpperCase());
            return ResponseEntity.ok(roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new CustomException("Error: Role: " + roleEnum + " is not found.", HttpStatus.NOT_FOUND)));
        }  catch (CustomException ex){
            throw ex;
        } catch (Exception ex){
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<Role> createRole(@RequestBody RoleRequest roleRequest){
        try {
            RoleEnum roleEnum = RoleEnum.valueOf(roleRequest.getRoleName().toUpperCase());
            Role role = new Role();
            role.setName(roleEnum);

            Role savedRole = roleRepository.save(role);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
        } catch (Exception ex){
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
