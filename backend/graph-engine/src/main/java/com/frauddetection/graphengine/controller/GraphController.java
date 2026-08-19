package com.frauddetection.graphengine.controller;

import com.frauddetection.graphengine.dto.CyclePathDTO;
import com.frauddetection.graphengine.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService graphService;

    @GetMapping("/accounts/{accountId}/cycles")
    public ResponseEntity<List<CyclePathDTO>> detectCycles(@PathVariable("accountId") String accountId) {
        return ResponseEntity.ok(graphService.detectCycles(accountId));
    }
}
