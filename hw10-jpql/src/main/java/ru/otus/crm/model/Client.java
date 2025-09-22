package ru.otus.crm.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", unique = true, nullable = false)
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Phone> phones = new ArrayList<>();

    public Client(String name) {
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("this-escape")
    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;

        if (address != null) {
            this.address = address;
            address.setClient(this);
        }

        if (phones != null) {
            for (Phone p : phones) {
                this.phones.add(p);
                p.setClient(this);
            }
        }
    }

    public void setAddress(Address address) {
        if (this.address != null) {
            this.address.setClient(null);
        }
        this.address = address;
        if (address != null) {
            address.setClient(this);
        }
    }

    public void setPhones(List<Phone> phones) {
        this.phones.forEach(p -> p.setClient(null));
        this.phones.clear();
        if (phones != null) {
            phones.forEach(this::addPhone);
        }
    }

    public void addPhone(Phone phone) {
        if (phone == null) return;
        this.phones.add(phone);
        phone.setClient(this);
    }

    public void removePhone(Phone phone) {
        if (phone == null) return;
        this.phones.remove(phone);
        phone.setClient(null);
    }

    @Override
    public Client clone() {
        Client copy = new Client(this.id, this.name);

        if (this.address != null) {
            Address addrCopy = new Address(this.address.getId(), this.address.getStreet());
            copy.setAddress(addrCopy);
        }

        if (this.phones != null) {
            for (Phone p : this.phones) {
                Phone pCopy = new Phone(p.getId(), p.getNumber());
                copy.addPhone(pCopy);
            }
        }
        return copy;
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "'}";
    }
}
