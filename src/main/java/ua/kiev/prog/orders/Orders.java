package ua.kiev.prog.orders;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Orders {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/weedb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    private Connection conn;

    public Orders(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {
//        3. Создать проект «База данных заказов». Создать
//таблицы «Товары» , «Клиенты» и «Заказы».
//Написать код для добавления новых клиентов,
//товаров и оформления заказов.
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)) {
            new Orders(conn).chooseAction();
        } catch (SQLException throwables) {
            System.out.println("Connection failed");
            throwables.printStackTrace();
        }
    }

    private void chooseAction() throws SQLException {
        try (Scanner sc = new Scanner(System.in)) {
            initDB();
            viewGoods();
            addOrder(sc);
        }
    }

    private void addOrder(Scanner sc) throws SQLException {
        System.out.print("Enter product's name: ");
        String product = sc.nextLine();
        System.out.print("Enter amount: ");
        Integer amount = Integer.parseInt(sc.nextLine());
        System.out.print("Enter customer's first name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter customer's last name: ");
        String lastName = sc.nextLine();
        System.out.print("Enter customer's phone number: ");
        String phone = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Customers (firstName, lastName, phone) " +
                "VALUES(?, ?, ?)");
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setString(3, phone);
        ps.executeUpdate();

        PreparedStatement getCustId = conn.prepareStatement("SELECT id FROM customers as c" +
                "    where c.firstName = " + firstName +
                "    and c.lastName = " + lastName +
                "    and c.phone = " + phone);
        ResultSet rs = getCustId.executeQuery();
        final int customerId = rs.getInt(1);

        PreparedStatement getGoodsId = conn.prepareStatement("SELECT id FROM goods as c" +
                "    where c.name = " + product);
        rs = getGoodsId.executeQuery();
        final int goodsId = rs.getInt(1);

        ps = conn.prepareStatement("INSERT INTO Orders (customerId, goodsId, createdAt) " +
                "VALUES(?, ?, ?)");
        ps.setInt(1, customerId);
        ps.setInt(2, goodsId);
        ps.setDate(3, new Date(System.currentTimeMillis()));


    }

    private void initDB() throws SQLException {
        Statement st1 = conn.createStatement();
        createTables(st1);
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

    private void createTables(Statement st1) throws SQLException {
        st1.execute("DROP TABLE IF EXISTS Orders");
        st1.execute("DROP TABLE IF EXISTS Goods");
        st1.execute("CREATE TABLE Goods (id INT NOT NULL " +
                "AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(128) NOT NULL, " +
                "price INT NOT NULL," +
                "amount INT)");

        st1.execute("DROP TABLE IF EXISTS Customers");
        st1.execute("CREATE TABLE Customers (id INT NOT NULL " +
                "AUTO_INCREMENT PRIMARY KEY, " +
                "firstName VARCHAR(20) NOT NULL," +
                "lastName VARCHAR(20) NOT NULL," +
                "phone VARCHAR(20) NOT NULL UNIQUE)");

        st1.execute("CREATE TABLE Orders (id INT NOT NULL " +
                "AUTO_INCREMENT PRIMARY KEY, " +
                "    customerId INT," +
                "    goodsId INT," +
                "    createdAt Date," +
                "    FOREIGN KEY (goodsId)  REFERENCES Goods(id)," +
                "    FOREIGN KEY (customerId)  REFERENCES Customers(id))");
    }

    private void viewGoods() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Goods")) {
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();

            try {
                System.out.println("======================================== G  O  O  D  S ========================================");

                System.out.println("==================================================================================================================================================");
                // can be used to get information about the types and properties of the columns in a ResultSet object
                ResultSetMetaData md = rs.getMetaData();
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
