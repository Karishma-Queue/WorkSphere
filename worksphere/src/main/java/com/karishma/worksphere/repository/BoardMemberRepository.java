package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {

}
