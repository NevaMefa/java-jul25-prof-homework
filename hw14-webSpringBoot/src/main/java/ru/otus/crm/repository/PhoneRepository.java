package ru.otus.crm.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
    List<Phone> findByClientId(Long clientId);
}
