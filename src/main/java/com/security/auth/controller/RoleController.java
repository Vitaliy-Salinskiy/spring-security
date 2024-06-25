package com.security.auth.controller;

import com.security.auth.dto.RoleRequest;
import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;
import com.security.auth.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Role getRoleByName(@PathVariable String name){
        RoleEnum roleEnum = RoleEnum.valueOf(name.toUpperCase());
        return roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    @PostMapping()
    public Role createRole(@RequestBody RoleRequest roleRequest){
        RoleEnum roleEnum = RoleEnum.valueOf(roleRequest.getRoleName().toUpperCase());
        Role role = new Role();
        role.setName(roleEnum);

        return roleRepository.save(role);
    }

}
