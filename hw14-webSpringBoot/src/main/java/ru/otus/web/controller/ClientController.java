package ru.otus.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.ClientService;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", service.findAll());
        return "clients";
    }

    @PostMapping
    public String add(
            @RequestParam String name,
            @RequestParam(required = false, name = "address") String address,
            @RequestParam(required = false, name = "phone") String phone) {

        Client client = new Client(name, address);
        Client savedClient = service.save(client);

        if (phone != null && !phone.isBlank()) {
            Phone phoneEntity = new Phone(phone, savedClient.getId());
            service.savePhone(phoneEntity);
        }

        return "redirect:/clients";
    }
}
