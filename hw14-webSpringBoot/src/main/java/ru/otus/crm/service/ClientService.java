package ru.otus.crm.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.repository.ClientRepository;
import ru.otus.crm.repository.PhoneRepository;
import ru.otus.web.dto.ClientDto;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final PhoneRepository phoneRepository;

    public List<ClientDto> findAll() {
        List<Client> clients = (List<Client>) clientRepository.findAll();
        return clients.stream()
                .map(client -> {
                    List<Phone> phones = phoneRepository.findByClientId(client.getId());
                    return new ClientDto(client.getId(), client.getName(), client.getAddressStreet(), phones);
                })
                .collect(Collectors.toList());
    }

    public List<Client> findAllClientsOnly() {
        return (List<Client>) clientRepository.findAll();
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void savePhone(Phone phone) {
        phoneRepository.save(phone);
    }

    public List<Phone> findPhonesByClientId(Long clientId) {
        return phoneRepository.findByClientId(clientId);
    }

    public Client getById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public ClientDto getClientWithPhones(Long id) {
        Client client = getById(id);
        List<Phone> phones = findPhonesByClientId(id);
        return new ClientDto(client.getId(), client.getName(), client.getAddressStreet(), phones);
    }

    public boolean hasClients() {
        return clientRepository.count() > 0;
    }
}
