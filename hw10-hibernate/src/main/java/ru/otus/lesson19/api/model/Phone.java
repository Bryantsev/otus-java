package ru.otus.lesson19.api.model;

import javax.persistence.*;

@Entity
@Table(name = "phone")
public class Phone {

    @Id
    @SequenceGenerator(name = "phoneGen", sequenceName = "phone_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phoneGen")
    @Column(name = "phone_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, unique = true)
    private String number;

    public Phone() {
    }

    public Phone(User user, String number) {
        this.user = user;
        this.number = number;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
