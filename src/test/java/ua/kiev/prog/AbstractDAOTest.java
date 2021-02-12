package ua.kiev.prog;

import org.junit.jupiter.api.*;
import ua.kiev.prog.apartments.Apartment;
import ua.kiev.prog.apartments.ApartmentDAO;

import java.sql.*;
import java.util.List;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AbstractDAOTest {
    static Connection conn;
    static DAO<Apartment> apartmentDAO;
    static Apartment ap1 = new Apartment("ddd333", "addr1", 100, 4, 100_00);
    static Apartment ap2 = new Apartment("aa", "ss", 22, 22, 22);
    static Apartment ap3 = new Apartment("zz", "xx", 33, 33, 11);
    static Apartment ap4 = new Apartment("qq", "ww", 11, 11, 11);

    @BeforeAll
    public static void prepare() {
        DbProperties props = new DbProperties();
        try {
            conn = DriverManager.getConnection(props.getUrl(), props.getUser(), props.getPassword());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        apartmentDAO = new ApartmentDAO(conn);
        apartmentDAO.create();
    }

    @AfterAll
    public static void finish() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
//    @Order(1)
    void create() {
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Apartment");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals(rs.getInt(1), 0);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    @Test
//    @Order(2)
    void insert() {
        apartmentDAO.insert(ap1);
        final List<Apartment> list = apartmentDAO.getAll();
        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(ap1, list.get(0));
    }

    @Test
//    @Order(3)
    void update() {
        ap1.setAddress("new Addr");
        ap1.setArea(999);
        ap1.setId(1);
        apartmentDAO.update(ap1);
        final List<Apartment> list = apartmentDAO.getAll();
        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(ap1, list.get(0));
    }

    @Test
//    @Order(4)
    void delete() {
        apartmentDAO.delete(1);
        final List<Apartment> list = apartmentDAO.getAll();
        Assertions.assertEquals(list.size(), 0);
    }

    @Test
//    @Order(5)
    void getAll() {
        apartmentDAO.insert(ap4);
        apartmentDAO.insert(ap2);
        apartmentDAO.insert(ap3);
        final List<Apartment> all = apartmentDAO.getAll();
        Assertions.assertEquals(all, List.of(ap4, ap2, ap3));
    }


    @Test
//    @Order(6)
    void get() {
        final Apartment apartment = apartmentDAO.get(1).get();
        Assertions.assertEquals(apartment, ap2);
    }

    @Test
//    @Order(7)
    void getByCondition() {
        final List<Apartment> aaDistr = apartmentDAO.getByCondition("district = 'aa'");
        Assertions.assertEquals(aaDistr.get(0), ap2);
        final List<Apartment> area = apartmentDAO.getByCondition("area > 15");
        Assertions.assertEquals(area, List.of(ap1, ap2, ap3));
        final List<Apartment> priceArea = apartmentDAO.getByCondition("price = 11 AND area = 11 ");
        Assertions.assertEquals(aaDistr.get(0), ap4);
    }
}