package com.insta.InstagramBackend.service;

import com.insta.InstagramBackend.model.AdminAuthToken;
import com.insta.InstagramBackend.repository.IAdminAuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    @Autowired
    IAdminAuthRepo adminAuthRepo;

    public void createToken(AdminAuthToken token) {
        adminAuthRepo.save(token);
    }

    public void deleteToken(String tokenValue){
        AdminAuthToken token = adminAuthRepo.findFirstByAdminTokenValue(tokenValue);
        adminAuthRepo.delete(token);
    }

    public boolean authenticate(String email, String token) {

        AdminAuthToken tokenValue = adminAuthRepo.findFirstByAdminTokenValue(token);
        if (tokenValue != null){
            return tokenValue.getAdmin().getAdminEmail().equals(email);
        }
        else {
            return false;
        }
    }
}
