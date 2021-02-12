package ua.kiev.prog.orders.DAOs;

import ua.kiev.prog.AbstractDAO;
import ua.kiev.prog.orders.entities.Customers;

import java.sql.Connection;

public class CustomersDAO extends AbstractDAO<Customers> {

    public CustomersDAO(Connection conn) {
        super(conn, Customers.class);
    }
}
