package com.frauddetection.graphengine.service;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.graphengine.dto.CyclePathDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Construit le graphe des comptes (noeuds Account, relations TRANSFERRED_TO)
 * a partir des transactions recues, et detecte les cycles de fraude.
 *
 * La detection de cycle s'appuie sur une requete Cypher a "chemin variable"
 * (relationship*2..6) : Neo4j parcourt le graphe en profondeur (equivalent
 * d'un DFS) depuis le compte de depart, et s'arrete des qu'il retombe sur ce
 * meme compte apres 2 a 6 sauts - exactement la definition d'un cycle.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {

    private static final int MIN_CYCLE_LENGTH = 2;
    private static final int MAX_CYCLE_LENGTH = 6;

    private final Driver neo4jDriver;

    public void recordTransaction(TransactionDTO transaction) {
        if (transaction.getSourceAccountId() == null || transaction.getDestinationAccountId() == null) {
            return;
        }

        try (Session session = neo4jDriver.session()) {
            session.run(
                    "MERGE (source:Account {id: $sourceId}) " +
                            "MERGE (dest:Account {id: $destId}) " +
                            "CREATE (source)-[:TRANSFERRED_TO {" +
                            "  transactionId: $transactionId, " +
                            "  amount: $amount, " +
                            "  currency: $currency, " +
                            "  timestamp: $timestamp" +
                            "}]->(dest)",
                    Map.of(
                            "sourceId", transaction.getSourceAccountId(),
                            "destId", transaction.getDestinationAccountId(),
                            "transactionId", transaction.getTransactionId(),
                            "amount", transaction.getAmount() != null ? transaction.getAmount().doubleValue() : 0.0,
                            "currency", transaction.getCurrency() != null ? transaction.getCurrency() : "",
                            "timestamp", transaction.getTimestamp() != null ? transaction.getTimestamp().toString() : ""
                    )
            );
            log.debug("Graphe mis a jour : {} -> {}", transaction.getSourceAccountId(), transaction.getDestinationAccountId());
        }
    }

    public List<CyclePathDTO> detectCycles(String accountId) {
        List<CyclePathDTO> cycles = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            var result = session.run(
                    "MATCH path = (a:Account {id: $accountId})" +
                            "-[:TRANSFERRED_TO*" + MIN_CYCLE_LENGTH + ".." + MAX_CYCLE_LENGTH + "]->(a) " +
                            "RETURN path LIMIT 10",
                    Map.of("accountId", accountId)
            );

            for (Record record : result.list()) {
                Path path = record.get("path").asPath();
                List<String> accountIds = new ArrayList<>();
                for (Node node : path.nodes()) {
                    accountIds.add(node.get("id").asString());
                }
                cycles.add(CyclePathDTO.builder()
                        .accountPath(accountIds)
                        .length(accountIds.size() - 1)
                        .build());
            }
        }

        if (!cycles.isEmpty()) {
            log.warn("Cycle(s) de fraude detecte(s) pour le compte [{}] : {} cycle(s)", accountId, cycles.size());
        }

        return cycles;
    }
}
