package com.insta.InstagramBackend.repository;

import com.insta.InstagramBackend.model.AdminAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdminAuthRepo extends JpaRepository<AdminAuthToken , Long> {

    AdminAuthToken findFirstByAdminTokenValue(String tokenValue);
}
