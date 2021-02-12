package ua.kiev.prog.orders.DAOs;

import ua.kiev.prog.AbstractDAO;
import ua.kiev.prog.orders.entities.Orders;

import java.sql.Connection;

public class OrdersDAO extends AbstractDAO<Orders> {

    public OrdersDAO(Connection conn) {
        super(conn, Orders.class);
    }
}
