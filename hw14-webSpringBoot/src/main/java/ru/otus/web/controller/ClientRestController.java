package ru.otus.web.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import ru.otus.crm.service.ClientService;
import ru.otus.web.dto.ClientDto;

@RestController
@RequestMapping("/api/clients")
public class ClientRestController {

    private final ClientService service;

    public ClientRestController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClientDto> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ClientDto byId(@PathVariable Long id) {
        return service.getClientWithPhones(id);
    }
}
