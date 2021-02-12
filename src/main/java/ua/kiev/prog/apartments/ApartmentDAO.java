package ua.kiev.prog.apartments;

import ua.kiev.prog.AbstractDAO;

import java.sql.Connection;

public class ApartmentDAO extends AbstractDAO<Apartment> {

    public ApartmentDAO(Connection conn) {
        super(conn, Apartment.class);
    }

}
