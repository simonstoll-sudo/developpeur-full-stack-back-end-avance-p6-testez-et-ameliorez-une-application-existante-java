package fr.norlys.mobilite.controller;

import fr.norlys.mobilite.entity.Agent;
import fr.norlys.mobilite.service.AgentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@Tag(name = "Agents", description = "Comptes des agents de terrain et des superviseurs")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public List<Agent> findAll() {
        return agentService.findAll();
    }

    @GetMapping("/{id}")
    public Agent findById(@PathVariable Long id) {
        return agentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agent create(@RequestBody Agent agent) {
        return agentService.create(agent);
    }

    @PutMapping("/{id}/password")
    public Agent changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequest request) {
        return agentService.changePassword(id, request.password());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        agentService.deactivate(id);
    }
}
