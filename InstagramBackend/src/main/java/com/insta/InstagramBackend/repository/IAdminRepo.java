package com.insta.InstagramBackend.repository;

import com.insta.InstagramBackend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdminRepo extends JpaRepository<Admin, Long> {

    Admin findFirstByAdminEmail(String newEmail);
}
