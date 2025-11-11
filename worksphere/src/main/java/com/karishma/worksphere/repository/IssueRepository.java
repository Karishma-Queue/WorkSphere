package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
}
