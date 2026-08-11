package com.frauddetection.ruleengine.controller;

import com.frauddetection.ruleengine.dto.RuleDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.service.RuleManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleManagementService ruleManagementService;

    @GetMapping
    public ResponseEntity<List<Rule>> listAll() {
        return ResponseEntity.ok(ruleManagementService.listAll());
    }

    @PostMapping
    public ResponseEntity<Rule> create(@Valid @RequestBody RuleDTO dto,
                                        @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        Rule created = ruleManagementService.create(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rule> update(@PathVariable("id") String id,
                                        @Valid @RequestBody RuleDTO dto,
                                        @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        return ResponseEntity.ok(ruleManagementService.update(id, dto, username));
    }
}
