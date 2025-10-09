package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            Client managed;
            if (client.getId() == null) {
                managed = clientDataTemplate.insert(session, client);
                log.info("created client: {}", managed);
            } else {
                managed = clientDataTemplate.update(session, client);
                log.info("updated client: {}", managed);
            }

            if (managed.getAddress() != null) {
                managed.getAddress().getStreet();
            }
            managed.getPhones().size();

            return managed.clone();
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(
                session -> clientDataTemplate.findById(session, id).map(client -> {
                    if (client.getAddress() != null) {
                        client.getAddress().getStreet();
                    }
                    client.getPhones().size();
                    return client.clone();
                }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> clientDataTemplate.findAll(session).stream()
                .map(client -> {
                    if (client.getAddress() != null) {
                        client.getAddress().getStreet();
                    }
                    client.getPhones().size();
                    return client.clone();
                })
                .toList());
    }
}
