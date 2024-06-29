package com.real.estate.user.repository;

import com.real.estate.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
}
