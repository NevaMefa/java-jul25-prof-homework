package ru.otus.crm.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("client")
public class Client {
    @Id
    private Long id;

    private String name;

    @Column("address_street")
    private String addressStreet;

    public Client(String name) {
        this.name = name;
    }

    public Client(String name, String addressStreet) {
        this.name = name;
        this.addressStreet = addressStreet;
    }
}
