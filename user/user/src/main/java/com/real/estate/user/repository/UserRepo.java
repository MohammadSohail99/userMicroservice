package com.real.estate.user.repository;

import com.real.estate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    User findByUserName(String userName);

    boolean existsByUserName(String userName);
}
