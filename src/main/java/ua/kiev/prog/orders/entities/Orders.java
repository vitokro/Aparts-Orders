package ua.kiev.prog.orders.entities;

import ua.kiev.prog.Id;

import java.util.Objects;

public class Orders {
    @Id
    private int id;
    private int customerId;
    private int goodsId;
    private int amount;
    private java.sql.Date createdAt = new java.sql.Date(System.currentTimeMillis());

    public Orders(int customerId, int goodsId, int amount) {
        this.customerId = customerId;
        this.goodsId = goodsId;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public java.sql.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orders orders = (Orders) o;
        return customerId == orders.customerId &&
                goodsId == orders.goodsId &&
                amount == orders.amount &&
                Objects.equals(createdAt, orders.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, goodsId, amount, createdAt);
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", goodsId=" + goodsId +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
