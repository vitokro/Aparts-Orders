package ua.kiev.prog.orders;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class OrdersDB {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/weedb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    private Connection conn;

    public OrdersDB(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {

//        3. Создать проект «База данных заказов». Создать
//таблицы «Товары» , «Клиенты» и «Заказы».
//Написать код для добавления новых клиентов,
//товаров и оформления заказов.
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)) {
            final OrdersDB ordersDB = new OrdersDB(conn);
            ordersDB.initDB();
            ordersDB.chooseAction();
        } catch (SQLException throwables) {
            System.out.println("Connection failed");
            throwables.printStackTrace();
        }
    }

    private void chooseAction() throws SQLException {
        try (Scanner sc = new Scanner(System.in)) {
            addCustomer(sc);
            addCustomer(sc);
//            generateGoods();
//            addProduct(sc);
//            viewGoods();
//            addOrder(sc);
//            viewGoods();
//            showTable("select * from orders");
            showTable("select * from customers");
//            viewOrders();
        }
    }

    private void addProduct(Scanner sc) throws SQLException {
        System.out.print("Enter product name: ");
        String product = sc.nextLine();
        System.out.print("Enter price: ");
        Integer price = Integer.parseInt(sc.nextLine());
        System.out.print("Enter amount: ");
        Integer amount = Integer.parseInt(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Goods (name, price, amount) " +
                "VALUES(?, ?, ?)");
        ps.setString(1, product);
        ps.setInt(2, price);
        ps.setInt(3, amount);
        ps.executeUpdate();
    }

    private void addCustomer(Scanner sc) throws SQLException {
        System.out.print("Enter customer first name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter customer last name: ");
        String lastName = sc.nextLine();
        System.out.print("Enter customer phone number: ");
        String phone = sc.nextLine();

        insertCustomer(firstName, lastName, phone);
    }

    private void insertCustomer(String firstName, String lastName, String phone) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO Customers (firstName, lastName, phone) " +
                "VALUES(?, ?, ?)");
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setString(3, phone);
        ps.executeUpdate();
    }

    private void initDB() throws SQLException {
        createTables();
        generateGoods();
    }

    private void generateGoods() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO Goods (name, price, amount) " +
                "VALUES(?, ?, ?)");
        try {
            Random rnd = new Random();
            for (int i = 0; i < 30; i++) {
                ps.setString(1, "product" + i);
                ps.setInt(2, rnd.nextInt(1000) + 20);
                ps.setInt(3, rnd.nextInt(100) + 1);
                ps.executeUpdate();
            }
        } finally {
            ps.close();
        }
    }

    private void createTables() {
        try {
            ScriptRunner scriptRunner = new ScriptRunner(conn);
            scriptRunner.setLogWriter(new PrintWriter("initDb.log"));
            scriptRunner.runScript(new FileReader(new File("src/main/java/ua/kiev/prog/orders/sql/InitDB.sql")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addOrder(Scanner sc) throws SQLException {
        System.out.print("Enter product name: ");
        String product = sc.nextLine();
        System.out.print("Enter amount: ");
        Integer amount = Integer.parseInt(sc.nextLine());
        System.out.print("Enter customer first name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter customer last name: ");
        String lastName = sc.nextLine();
        System.out.print("Enter customer phone number: ");
        String phone = sc.nextLine();

        insertCustomer(firstName, lastName, phone);

        PreparedStatement getCustId = conn.prepareStatement(String.format(
                "SELECT id FROM customers as c    " +
                        "where c.firstName = '%s'    " +
                        "and c.lastName = '%s'    " +
                        "and c.phone = %s", firstName, lastName, phone));
        ResultSet rs = getCustId.executeQuery();
        rs.next();
        final int customerId = rs.getInt(1);

        PreparedStatement getGoodsId = conn.prepareStatement(String.format(
                "SELECT id, amount FROM goods " +
                        " where name = '%s'", product));
        rs = getGoodsId.executeQuery();
        rs.next();

        final int goodsId = rs.getInt("id");
        final int amountOld = rs.getInt("amount");

        PreparedStatement ps = conn.prepareStatement("UPDATE Goods SET amount = ? " +
                "WHERE id = " + goodsId);
        ps.setInt(1, amountOld - amount);
        ps.executeUpdate();

        ps = conn.prepareStatement("INSERT INTO Orders (customerId, goodsId, createdAt, amount) " +
                "VALUES(?, ?, ?, ?)");
        ps.setInt(1, customerId);
        ps.setInt(2, goodsId);
        ps.setDate(3, new Date(System.currentTimeMillis()));
        ps.setInt(4, amount);
        ps.executeUpdate();

    }

    private void viewGoods() throws SQLException {
        showTable("SELECT * FROM Goods");
    }

    private void viewOrders() throws SQLException{
        showTable("SELECT * FROM OrdersView");
    }

    private void showTable(String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();

            try {
                ResultSetMetaData md = rs.getMetaData();
                System.out.println("======================================== " + md.getTableName(1) + " ========================================");
                System.out.println("==================================================================================================================================================");
                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();
                System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
                System.out.println("==================================================================================================================================================");
            } finally {
                rs.close(); // rs can't be null according to the docs
            }
        }
    }
}
