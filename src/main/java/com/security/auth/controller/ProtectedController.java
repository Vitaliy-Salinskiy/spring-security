package com.security.auth.controller;

import com.security.auth.annotation.RoleSecured;
import com.security.auth.model.RoleEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping("/user")
    @RoleSecured(roles = {RoleEnum.USER})
    public String user() {
        return "Hello User";
    }

    @RoleSecured(roles = {RoleEnum.ADMIN})
    @GetMapping("/admin")
    public String admin() {
        return "Hello Admin";
    }

    @RoleSecured(roles = {RoleEnum.USER, RoleEnum.ADMIN})
    @GetMapping("Everyone")
    public String both() {
        return "Hello Everyone";
    }

}
