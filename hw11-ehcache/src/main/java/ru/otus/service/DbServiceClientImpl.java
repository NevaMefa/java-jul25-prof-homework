package ru.otus.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.model.Client;
import ru.otus.repository.DataTemplate;
import ru.otus.sessionmanager.TransactionManager;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<String, Client> cache;

    public DbServiceClientImpl(
            TransactionManager transactionManager,
            DataTemplate<Client> clientDataTemplate,
            HwCache<String, Client> cache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            Client managed = (client.getId() == null)
                    ? clientDataTemplate.insert(session, client)
                    : clientDataTemplate.update(session, client);

            log.info("{} client: {}", client.getId() == null ? "created" : "updated", managed);

            cache.put(String.valueOf(managed.getId()), managed);
            return managed;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        String key = String.valueOf(id);
        Client cached = cache.get(key);
        if (cached != null) {
            log.info("cache hit id={}", id);
            return Optional.of(cached);
        }

        return transactionManager.doInReadOnlyTransaction(
                session -> clientDataTemplate.findById(session, id).map(client -> {
                    cache.put(key, client);
                    return client;
                }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> clientDataTemplate.findAll(session));
    }
}
