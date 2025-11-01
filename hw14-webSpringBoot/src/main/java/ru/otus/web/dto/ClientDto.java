package ru.otus.web.dto;

import java.util.List;
import lombok.Data;
import ru.otus.crm.model.Phone;

@Data
public class ClientDto {
    private Long id;
    private String name;
    private String addressStreet;
    private List<Phone> phones;

    public ClientDto(Long id, String name, String addressStreet, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.addressStreet = addressStreet;
        this.phones = phones;
    }
}
