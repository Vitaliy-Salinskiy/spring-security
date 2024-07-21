package com.security.auth.controller;

import com.security.auth.dto.RoleRequest;
import com.security.auth.exception.CustomException;
import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;

import com.security.auth.service.impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleServiceImpl roleService;

    @GetMapping()
     public List<Role>  getAllRoles(){
        return roleService.findAllRoles();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Role>  getRoleByName(@PathVariable RoleEnum name){
        try {
            return ResponseEntity.ok(roleService.findFirstByName(name));
        }  catch (CustomException ex){
            throw ex;
        } catch (Exception ex){
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<Role> createRole(@RequestBody RoleRequest roleRequest){
        try {
            Role savedRole = roleService.createRole(roleRequest.getRoleName());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
        } catch (Exception ex){
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
