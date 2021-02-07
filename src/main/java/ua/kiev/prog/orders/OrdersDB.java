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
    private Connection conn;

    public OrdersDB(Connection conn) {
        this.conn = conn;
    }

    public void chooseAction() throws SQLException {
        initDB();
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("1: add customer");
                System.out.println("2: add product");
                System.out.println("3: generate goods");
                System.out.println("4: add order");
                System.out.println("5: view customers");
                System.out.println("6: view goods");
                System.out.println("7: view orders");
                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        addCustomer(sc);
                        break;
                    case "2":
                        addProduct(sc);
                        break;
                    case "3":
                        generateGoods();
                        break;
                    case "4":
                        addOrder(sc);
                        break;
                    case "5":
                        viewCustomers();
                        break;
                    case "6":
                        viewGoods();
                        break;
                    case "7":
                        viewOrders();
                        break;
                    default:
                        return;
                }
            }
        }
    }

    public void initDB()  {
        createTables();
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

    private void addProduct(Scanner sc) throws SQLException {
        System.out.print("Enter product name: ");
        String product = sc.nextLine();
        System.out.print("Enter price: ");
        Integer price = Integer.parseInt(sc.nextLine());
        System.out.print("Enter amount: ");
        Integer amount = Integer.parseInt(sc.nextLine());

        insertGoods(product, price, amount);
    }

    private void insertGoods(String product, Integer price, Integer amount) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO Goods (name, price, amount) " +
                "VALUES(?, ?, ?)");
        ps.setString(1, product);
        ps.setInt(2, price);
        ps.setInt(3, amount);
        ps.executeUpdate();
    }

    private void generateGoods() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO Goods (name, price, amount) " +
                "VALUES(?, ?, ?)");
        try {
            Random rnd = new Random();
            for (int i = 0; i < 20; i++) {
                ps.setString(1, "product" + i);
                ps.setInt(2, rnd.nextInt(1000) + 20);
                ps.setInt(3, rnd.nextInt(100) + 1);
                ps.executeUpdate();
            }
        } finally {
            ps.close();
        }
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

    private void addOrder(Scanner sc) throws SQLException {
        System.out.print("Enter product name: ");
        String product = sc.nextLine();
        System.out.print("Enter amount: ");
        Integer amount = Integer.parseInt(sc.nextLine());
        System.out.print("Enter customer phone number: ");
        String phone = sc.nextLine();

        if (!updateCustomers(phone))
            return;

        PreparedStatement getGoodsId = conn.prepareStatement(String.format(
                "SELECT id, amount FROM goods " +
                        " where name = '%s'", product));
        ResultSet rs = getGoodsId.executeQuery();

        if (!rs.next()) {
            System.err.println("Product " + product + " is not exists");
            return;
        }
        final int goodsId = rs.getInt("id");
        final int amountOld = rs.getInt("amount");

        updateGoods(amount, goodsId, amountOld);

        final int customerId = getCustomerId(phone);
        insertOrders(amount, customerId, goodsId);
    }

    private int getCustomerId(String phone) throws SQLException {
        PreparedStatement getCustId = conn.prepareStatement(String.format(
                "SELECT id FROM customers    " +
                        "WHERE phone = %s", phone));
        ResultSet rs = getCustId.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private void insertOrders(Integer amount, int customerId, int goodsId) throws SQLException {
        PreparedStatement ps;

        ps = conn.prepareStatement("INSERT INTO Orders (customerId, goodsId, createdAt, amount) " +
                "VALUES(?, ?, ?, ?)");
        ps.setInt(1, customerId);
        ps.setInt(2, goodsId);
        ps.setDate(3, new Date(System.currentTimeMillis()));
        ps.setInt(4, amount);
        ps.executeUpdate();
    }

    private void updateGoods(Integer amount, int goodsId, int amountOld) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE Goods SET amount = ? " +
                "WHERE id = " + goodsId);
        ps.setInt(1, amountOld - amount);
        ps.executeUpdate();
    }

    private boolean updateCustomers(String phone) throws SQLException {
        PreparedStatement ps;
        ps = conn.prepareStatement("UPDATE customers SET phone = ? \n" +
                "WHERE phone = ?");
        ps.setString(1, phone);
        ps.setString(2, phone);
        final int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated == 0) {
            System.err.println("Customer with phone number " + phone + " is not exists");
            return false;
        }
        return true;
    }

    private void viewGoods() throws SQLException {
        showTable("SELECT * FROM Goods");
    }

    private void viewOrders() throws SQLException {
        showTable("SELECT * FROM OrdersView");
    }

    private void viewCustomers() throws SQLException {
        showTable("SELECT * FROM customers");
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
