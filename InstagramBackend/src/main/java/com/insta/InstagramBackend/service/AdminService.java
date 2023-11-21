package com.insta.InstagramBackend.service;

import com.insta.InstagramBackend.model.Admin;
import com.insta.InstagramBackend.model.AdminAuthToken;
import com.insta.InstagramBackend.repository.IAdminRepo;
import com.insta.InstagramBackend.service.utility.emailUtility.EmailHandler;
import com.insta.InstagramBackend.service.utility.hashingUtility.PasswordEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class AdminService {

    @Autowired
    IAdminRepo adminRepo;

    @Autowired
    AdminAuthService adminAuthService;

    @Autowired
    UserService userService;

    public String adminSignUp(Admin admin) {

        String newEmail = admin.getAdminEmail();
        Admin existingAdmin = adminRepo.findFirstByAdminEmail(newEmail);

        if(existingAdmin != null)
        {
            return "Email is already registered. Try Logging in!!!";
        }

        String signUpPassword = admin.getAdminPassword();

        try{
            String encryptedPassword = PasswordEncrypter.encryptPassword(signUpPassword);
            admin.setAdminPassword(encryptedPassword);
            adminRepo.save(admin);
            return "Admin Registered Successfully!!!";
        } catch (NoSuchAlgorithmException e) {
            return "Internal Server Error";
        }

    }

    public String adminSignIn(String email, String password) {

        Admin existingAdmin = adminRepo.findFirstByAdminEmail(email);

        if (existingAdmin == null)
        {
            return "Email not registered , SignUp first";
        }

        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(password);

            if (existingAdmin.getAdminPassword().equals(encryptedPassword))
            {
                AdminAuthToken token = new AdminAuthToken(existingAdmin);

                if(EmailHandler.sendEmail(email,"OTP after login: ",token.getAdminTokenValue()))
                {
                    adminAuthService.createToken(token);
                    return "check emails for otp/token!!!";
                }
                else {
                    return "error while generating token!!!";
                }
            }
            else {
                return "Invalid Credentials!!!";
            }
        }
        catch (NoSuchAlgorithmException e){
            return "Internal Server issue while saving password , try again later!!!";
        }
    }

    public String adminSignOut(String email, String token) {
        if (adminAuthService.authenticate(email,token)){
            adminAuthService.deleteToken(token);
            return "Sign Out Successful!!!";
        }
        else {
            return "Un Authenticated Access!!!";
        }
    }

    public String adminBlueTick(String email, String token, Integer userId) {
        if (adminAuthService.authenticate(email,token)){
            return userService.addBlueTick(userId);
        }
        else {
            return "Un Authenticated Access!!!";
        }
    }
}
