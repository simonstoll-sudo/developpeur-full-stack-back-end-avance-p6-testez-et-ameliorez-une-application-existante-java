package fr.norlys.mobilite.service;

import fr.norlys.mobilite.entity.Agent;
import fr.norlys.mobilite.entity.AgentRole;
import fr.norlys.mobilite.exception.BusinessRuleException;
import fr.norlys.mobilite.exception.ResourceNotFoundException;
import fr.norlys.mobilite.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des comptes des agents de terrain : création, mot de passe, désactivation.
 */
@Service
@Transactional
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<Agent> findAll() {
        return agentRepository.findAll();
    }

    public Agent findById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable : " + id));
    }

    public Agent findByUsername(String username) {
        return agentRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable : " + username));
    }

    public Agent create(Agent agent) {
        if (agent.getUsername() == null || agent.getUsername().isBlank()) {
            throw new BusinessRuleException("L'identifiant est obligatoire");
        }
        if (agentRepository.existsByUsername(agent.getUsername())) {
            throw new BusinessRuleException("Identifiant déjà utilisé : " + agent.getUsername());
        }
        if (agent.getEmail() != null && agentRepository.existsByEmail(agent.getEmail())) {
            throw new BusinessRuleException("Adresse e-mail déjà utilisée : " + agent.getEmail());
        }

        agent.setId(null);
        agent.setActive(true);
        if (agent.getRole() == null) {
            agent.setRole(AgentRole.TERRAIN);
        }
        return agentRepository.save(agent);
    }

    public Agent changePassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessRuleException("Le mot de passe est obligatoire");
        }
        Agent agent = findById(id);
        agent.setPassword(newPassword);
        return agentRepository.save(agent);
    }

    public void deactivate(Long id) {
        Agent agent = findById(id);
        agent.setActive(false);
        agentRepository.save(agent);
    }
}
