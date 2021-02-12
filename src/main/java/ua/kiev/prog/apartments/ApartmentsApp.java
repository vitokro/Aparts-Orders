package ua.kiev.prog.apartments;


import ua.kiev.prog.DAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ApartmentsApp {

    private Connection conn;
    private DAO<Apartment> apartmentDAO;

    public ApartmentsApp(Connection conn) {
        this.conn = conn;
    }

    public void chooseAction() {
        apartmentDAO = new ApartmentDAO(conn);
        apartmentDAO.create();
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println();
                System.out.println("1: add apartment");
                System.out.println("2: add random apartments");
                System.out.println("3: delete apartment");
                System.out.println("4: change apartment");
                System.out.println("5: view apartments");
                System.out.println("6: select by condition");
                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        addApartment(sc);
                        break;
                    case "2":
                        generateApartments(sc);
                        break;
                    case "3":
                        deleteApartment(sc);
                        break;
                    case "4":
                        updateApartment(sc);
                        break;
                    case "5":
                        viewApartments(apartmentDAO.getAll());
                        break;
                    case "6":
                        selectByCondition(sc);
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addApartment(Scanner sc) {
        final Apartment ap = getApartment(sc);
        if (ap == null)
            return;
        apartmentDAO.insert(ap);
    }

    private Apartment getApartment(Scanner sc) {
        System.out.print("Enter apartment district: ");
        String district = sc.nextLine();
        System.out.print("Enter apartment address: ");
        String address = sc.nextLine();

        int price;
        int area;
        int roomsNumber;
        try {
            System.out.print("Enter apartment area: ");
            area = Integer.parseInt(sc.nextLine());
            System.out.print("Enter apartment number of rooms: ");
            roomsNumber = Integer.parseInt(sc.nextLine());
            System.out.print("Enter apartment price: ");
            price = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Enter integer values!");
            return null;
        }
        final Apartment ap = new Apartment(district, address, area, roomsNumber, price);
        return ap;
    }

    private void deleteApartment(Scanner sc) {
        System.out.print("Enter apartments id: ");
        final int id = Integer.parseInt(sc.nextLine());
        apartmentDAO.delete(id);
    }

    private void updateApartment(Scanner sc) {
        System.out.print("Enter id of apartment you want to update: ");
        final int id = Integer.parseInt(sc.nextLine());

        System.out.println();
        viewApartments(List.of(apartmentDAO.get(id).get()));
        System.out.println("Enter new values for this apartment: ");
        final Apartment ap = getApartment(sc);
        if (ap == null)
            return;
        ap.setId(id);

        apartmentDAO.update(ap);
    }

    private void generateApartments(Scanner sc) throws SQLException {
        System.out.print("Enter apartments count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);
        Random rnd = new Random();

        conn.setAutoCommit(false); // enable transactions
        try {
            for (int i = 0; i < count; i++) {
                final Apartment ap = ApartmentBuilder.anApartment()
                        .withAddress("addr" + rnd.nextInt(100))
                        .withDistrict("district" + rnd.nextInt(10))
                        .withArea(rnd.nextInt(100) + 40)
                        .withRoomsNumber(rnd.nextInt(4) + 1)
                        .withPrice(rnd.nextInt(1_000_000) + 10_000)
                        .build();
                apartmentDAO.insert(ap);
            }
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // return to default mode
        }
    }

    private <T> void viewApartments(List<T> aps) {
        if (aps.size() == 0) {
            System.out.println("No data");
            return;
        }
        final Field[] filedNames = aps.get(0).getClass().getDeclaredFields();
        System.out.println("==================================================================================================================================================");
        for (int i = 0; i < filedNames.length; i++)
            System.out.print(filedNames[i].getName() + "\t\t");
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < aps.size(); i++) {
            System.out.println(aps.get(i));
        }
        System.out.println();
        System.out.println("==================================================================================================================================================");
    }

    private void selectByCondition(Scanner sc) {
        System.out.print("Enter condition(for example: district = 'distr2' AND area > 100): ");
        String cond = sc.nextLine();
        viewApartments(apartmentDAO.getByCondition(cond));
    }

}
