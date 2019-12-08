package ru.otus.lesson19.api.model;

import javax.persistence.*;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @SequenceGenerator(name = "addressGen", sequenceName = "address_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressGen")
    @Column(name = "address_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String house;
    private String flat;

    public Address() {
    }

    public Address(User user, String street, String house, String flat) {
        this.user = user;
        this.street = street;
        this.house = house;
        this.flat = flat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }
}
