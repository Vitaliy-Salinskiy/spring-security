package com.security.auth.service.impl;

import com.security.auth.exception.CustomException;
import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;
import com.security.auth.repository.RoleRepository;
import com.security.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findFirstByName(RoleEnum name) {
        return roleRepository.findFirstByName(name)
                .orElseThrow(() -> new CustomException("Role with name: " + name + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(RoleEnum name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }
}
