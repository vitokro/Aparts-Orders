package ua.kiev.prog.orders.DAOs;

import ua.kiev.prog.AbstractDAO;
import ua.kiev.prog.orders.entities.OrdersView;

import java.sql.Connection;

public class OrdersViewDAO extends AbstractDAO<OrdersView> {

    public OrdersViewDAO(Connection conn) {
        super(conn, OrdersView.class);
    }
}
