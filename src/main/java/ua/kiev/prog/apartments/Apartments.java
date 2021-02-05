package ua.kiev.prog.apartments;


import java.sql.*;
import java.util.*;

public class Apartments {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/weedb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    private Connection conn;


    
    public Apartments(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            new Apartments(conn).chooseAction();
        } catch (SQLException throwables) {
            System.out.println("Connection failed");
            throwables.printStackTrace();
        }
    }

    public void chooseAction() {
        initDB();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("1: add apartment");
                System.out.println("2: add random apartments");
                System.out.println("3: delete apartment");
                System.out.println("4: change apartment");
                System.out.println("5: view apartments");
                System.out.println("6: select by district");
                System.out.println("7: select by address");
                System.out.println("8: select by area");
                System.out.println("9: select by number of rooms");
                System.out.println("0: select by price");
                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        addApartment(sc);
                        break;
                    case "2":
                        insertRandomApartments(sc);
                        break;
                    case "3":
                        deleteApartment(sc);
                        break;
                    case "4":
                        changeApartment(sc);
                        break;
                    case "5":
                        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments")) {
                            viewApartments(ps);
                        }
                        break;
                    case "6":
                        selectByDistrict(sc);
                        break;
                    case "7":
                        selectByAddress(sc);
                        break;
                    case "8":
                        selectByArea(sc);
                        break;
                    case "9":
                        selectByRooms(sc);
                        break;
                    case "0":
                        selectByPrice(sc);
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initDB() {
        try (Statement st1 = conn.createStatement()) {
            st1.execute("DROP TABLE IF EXISTS Apartments");
            st1.execute("CREATE TABLE Apartments (id INT NOT NULL " +
                    "AUTO_INCREMENT PRIMARY KEY, district VARCHAR(128), " +
                    "address VARCHAR(128)," +
                    "area SMALLINT," +
                    "RoomsNumber TINYINT," +
                    "price INT NOT NULL)");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addApartment(Scanner sc) throws SQLException {
        System.out.print("Enter apartments' district: ");
        String district = sc.nextLine();
        System.out.print("Enter apartments' address: ");
        String address = sc.nextLine();
        int price;
        int area;
        int roomsNumber;
        try {
            System.out.print("Enter apartments' area: ");
            area = Integer.parseInt(sc.nextLine());
            System.out.print("Enter apartments' RoomsNumber: ");
            roomsNumber = Integer.parseInt(sc.nextLine());
            System.out.print("Enter apartments' price: ");
            price = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Input integer values!");
            return;
        }

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                    "INSERT INTO Apartments (district, address, area, roomsNumber, price) " +
                            "VALUES(?, ?, ?, ?, ?)");
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setInt(3, area);
            ps.setInt(4, roomsNumber);
            ps.setInt(5, price);
            ps.executeUpdate();
        } finally {
                ps.close();
        }
    }

    private void deleteApartment(Scanner sc) throws SQLException {
        System.out.print("Enter apartments id: ");
        int id = Integer.parseInt(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM Apartments WHERE id = ?");
        try {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void changeApartment(Scanner sc) throws SQLException {
        System.out.print("Enter id: ");
        final int id = Integer.parseInt(sc.nextLine());
        final List<String> params = List.of("district", "address", "area", "rooms", "price");
        String param;
        do {
            System.out.print("Type name of param you want to change(district, address, area, rooms, price): ");
            param = sc.nextLine();
        } while (!params.contains(param));
        System.out.print("Enter value: ");
        String value = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "UPDATE Apartments SET "+ param +" = ? " +
                        "WHERE id = ?");
        try {
            if (params.indexOf(value) > 2)
                ps.setInt(1, Integer.parseInt(value));
            else
                ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void insertRandomApartments(Scanner sc) throws SQLException {
        System.out.print("Enter apartments count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);
        Random rnd = new Random();

        conn.setAutoCommit(false); // enable transactions
        try {
            try {
                PreparedStatement ps =
                        conn.prepareStatement(
                                "INSERT INTO Apartments (district, address, area, roomsNumber, price) " +
                                        "VALUES(?, ?, ?, ?, ?)");
                try {
                    for (int i = 0; i < count; i++) {
                        ps.setString(1, "District" + i % 10);
                        ps.setString(2, "address" + rnd.nextInt(100));
                        ps.setInt(3, rnd.nextInt(100) + 20);
                        ps.setInt(4, rnd.nextInt(5) + 1);
                        ps.setInt(5, rnd.nextInt(1000000) + 10000);
                        ps.executeUpdate();
                    }
                    conn.commit();
                } finally {
                    ps.close();
                }
            } catch (Exception ex) {
                conn.rollback();
            }
        } finally {
            conn.setAutoCommit(true); // return to default mode
        }
    }

    private void viewApartments(PreparedStatement ps) throws SQLException {

            // table of data representing a database result set,
        ResultSet rs = ps.executeQuery();

        try {
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

    private void selectByDistrict(Scanner sc) throws SQLException {
        System.out.print("District is like: ");
        String districtLike = sc.nextLine();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Apartments" +
                        " WHERE district LIKE '%" + districtLike + "%'")) {
            viewApartments(ps);
        }
    }

    private void selectByAddress(Scanner sc) throws SQLException {
        System.out.print("Address is like: ");
        String addressLike = sc.nextLine();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Apartments" +
                        " WHERE address LIKE '%" + addressLike + "%'")) {
            viewApartments(ps);
        }
    }

    private void selectByArea(Scanner sc) throws SQLException {
        System.out.print("Area more then: ");
        int areaIs = Integer.parseInt(sc.nextLine());
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Apartments" +
                        " WHERE area > " + areaIs)) {
            viewApartments(ps);
        }
    }

    private void selectByRooms(Scanner sc) throws SQLException {
        System.out.print("Number of rooms is: ");
        int rooms = Integer.parseInt(sc.nextLine());
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Apartments" +
                        " WHERE roomsNumber = " + rooms)) {
            viewApartments(ps);
        }
    }

    private void selectByPrice(Scanner sc) throws SQLException {
        System.out.print("Price is less then: ");
        int priceIs = Integer.parseInt(sc.nextLine());
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Apartments" +
                        " WHERE price < " + priceIs)) {
            viewApartments(ps);
        }
    }
}
