package com.security.auth.service;

import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;

import java.util.List;

public interface RoleService {
    List<Role> findAllRoles();
    Role findFirstByName(RoleEnum name);
    Role  createRole(RoleEnum name);
}
