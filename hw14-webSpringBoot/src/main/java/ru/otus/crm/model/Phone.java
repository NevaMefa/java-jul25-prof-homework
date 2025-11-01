package ru.otus.crm.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("phone")
public class Phone {
    @Id
    private Long id;

    private String number;

    @Column("client_id")
    private Long clientId;

    public Phone(String number, Long clientId) {
        this.number = number;
        this.clientId = clientId;
    }
}
