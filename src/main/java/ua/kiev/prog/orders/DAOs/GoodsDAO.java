package ua.kiev.prog.orders.DAOs;

import ua.kiev.prog.AbstractDAO;
import ua.kiev.prog.orders.entities.Goods;

import java.sql.Connection;

public class GoodsDAO extends AbstractDAO<Goods> {

    public GoodsDAO(Connection conn) {
        super(conn, Goods.class);
    }
}
