package com.frauddetection.ruleengine.repository;

import com.frauddetection.ruleengine.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, String> {

    List<Rule> findByActiveTrue();
}
