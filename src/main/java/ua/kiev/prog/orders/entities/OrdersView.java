package ua.kiev.prog.orders.entities;

import java.sql.Date;
import java.util.Objects;

public class OrdersView {
    private String name;
    private int price;
    private int amount;
    private String firstName;
    private String lastName;
    private String phone;
    private java.sql.Date createdAt;
    private long totalPrice;

    public OrdersView() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdersView that = (OrdersView) o;
        return price == that.price &&
                amount == that.amount &&
                totalPrice == that.totalPrice &&
                name.equals(that.name) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                phone.equals(that.phone) &&
                createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, amount, firstName, lastName, phone, createdAt, totalPrice);
    }

    @Override
    public String toString() {
        return String.format("%s\t\t%d\t\t%d\t\t%s\t\t%s\t\t%s\t\t%s\t\t%d",
                name, price, amount, firstName, lastName, phone, createdAt, totalPrice);
    }
}
