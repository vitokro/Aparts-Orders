package ua.kiev.prog.orders.entities;

import ua.kiev.prog.Id;

import java.util.Objects;

public class Customers {
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String phone;

    public Customers() {
    }

    public Customers(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customers customers = (Customers) o;
        return Objects.equals(firstName, customers.firstName) &&
                Objects.equals(lastName, customers.lastName) &&
                phone.equals(customers.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phone);
    }

    @Override
    public String toString() {
        return String.format("%d\t\t%s\t\t%s\t\t%s", id, firstName, lastName, phone);
    }
}
