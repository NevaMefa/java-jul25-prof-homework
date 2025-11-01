package ru.otus.crm.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Optional<Client> findById(Long id);

    long count();
}
