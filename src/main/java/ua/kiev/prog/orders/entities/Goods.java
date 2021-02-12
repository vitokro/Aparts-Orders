package ua.kiev.prog.orders.entities;

import ua.kiev.prog.Id;

import java.util.Objects;

public class Goods {
    @Id
    private int id;
    private String name;
    private int price;
    private int amount;

    public Goods() {
    }

    public Goods(String name, int price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return id == goods.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d\t\t%s\t\t%d\t\t%d", id, name, price, amount);
    }
}
