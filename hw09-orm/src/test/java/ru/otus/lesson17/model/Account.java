package ru.otus.lesson17.model;

import ru.otus.lesson17.orm.api.Column;
import ru.otus.lesson17.orm.api.Id;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    @Id
    private Long no;
    @Column("type")
    private String accountType;
    private BigDecimal rest;

    public Account() {
    }

    public Account(Long no, String accountType, BigDecimal rest) {
        this.no = no;
        this.accountType = accountType;
        this.rest = rest;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getRest() {
        return rest;
    }

    public void setRest(BigDecimal rest) {
        this.rest = rest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(no, account.no) &&
            Objects.equals(accountType, account.accountType) &&
            ((rest == null && account.rest == null) || (rest != null && account.rest != null && rest.compareTo(account.rest) == 0));
    }

    @Override
    public int hashCode() {
        return Objects.hash(no, accountType, rest);
    }

    @Override
    public String toString() {
        return "Account{" +
            "no=" + no +
            ", accountType='" + accountType + '\'' +
            ", rest=" + rest +
            '}';
    }
}
