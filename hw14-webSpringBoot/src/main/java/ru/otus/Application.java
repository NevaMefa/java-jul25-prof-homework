package ru.otus;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.ClientService;
import ru.otus.web.dto.ClientDto;

@SpringBootApplication
public class Application {

    private final ClientService clientService;

    public Application(ClientService clientService) {
        this.clientService = clientService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void init() {
        if (clientService.findAllClientsOnly().isEmpty()) {
            clientService.save(new Client("Vasya"));
            clientService.save(new Client("Petya"));
            clientService.save(new Client("Masha"));
        }

        List<ClientDto> clients = clientService.findAll();
        System.out.println("=== SERVER STARTED ===");
        clients.forEach(c -> System.out.println("Client: " + c.getId() + " | " + c.getName()));
    }
}
