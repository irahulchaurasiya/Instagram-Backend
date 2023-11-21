package com.insta.InstagramBackend.controller;

import com.insta.InstagramBackend.model.Admin;
import com.insta.InstagramBackend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping("signUp")
    public String adminSignUp(@RequestBody @Valid Admin admin)
    {
        return adminService.adminSignUp(admin);
    }

    @PostMapping("signIn")
    public String adminSignIn(@RequestParam String email , @RequestParam String password){
        return adminService.adminSignIn(email , password);
    }

    @DeleteMapping("signOut")
    public String adminSignOut(@RequestParam String email , @RequestParam String token)
    {
        return adminService.adminSignOut(email , token);
    }

    @PatchMapping("giveBlue-tick")
    public String adminBlueTick(@RequestParam String email,@RequestParam String token , @RequestParam Integer userId){
        return adminService.adminBlueTick(email,token,userId);
    }
}
