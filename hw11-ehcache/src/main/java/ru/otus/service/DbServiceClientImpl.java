package ru.otus.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.model.Client;
import ru.otus.repository.DataTemplate;
import ru.otus.sessionmanager.TransactionManager;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    private final HwCache<Long, Client> cache = new MyCache<>();

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            Client managed = (client.getId() == null)
                    ? clientDataTemplate.insert(session, client)
                    : clientDataTemplate.update(session, client);
            log.info("{} client: {}", client.getId() == null ? "created" : "updated", managed);

            if (managed.getAddress() != null) managed.getAddress().getStreet();
            managed.getPhones().size();

            Client copy = managed.clone();
            cache.put(copy.getId(), copy);
            return copy;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cached = cache.get(id);
        if (cached != null) {
            log.info("cache hit id={}", id);
            return Optional.of(cached.clone());
        }

        return transactionManager.doInReadOnlyTransaction(
                session -> clientDataTemplate.findById(session, id).map(client -> {
                    if (client.getAddress() != null) client.getAddress().getStreet();
                    client.getPhones().size();
                    Client copy = client.clone();
                    cache.put(id, copy);
                    return copy.clone();
                }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> clientDataTemplate.findAll(session).stream()
                .map(c -> {
                    if (c.getAddress() != null) c.getAddress().getStreet();
                    c.getPhones().size();
                    return c.clone();
                })
                .toList());
    }
}
