package fr.norlys.mobilite.repository;

import fr.norlys.mobilite.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Agent> findByActiveTrue();
}
