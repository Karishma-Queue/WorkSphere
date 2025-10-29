package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {


}
