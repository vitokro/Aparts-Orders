package ua.kiev.prog.orders;

import org.apache.ibatis.jdbc.ScriptRunner;
import ua.kiev.prog.DAO;
import ua.kiev.prog.DbProperties;
import ua.kiev.prog.orders.DAOs.*;
import ua.kiev.prog.orders.entities.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class OrdersApp {
    private final Connection conn;
    private final DAO<Customers> customersDAO;
    private final DAO<Goods> goodsDAO;
    private final DAO<Orders> ordersDAO;
    private final DAO<OrdersView> ordersViewDAO;

    public OrdersApp(Connection conn) {
        this.conn = conn;
        this.customersDAO = new CustomersDAO(conn);
        this.goodsDAO = new GoodsDAO(conn);
        this.ordersDAO = new OrdersDAO(conn);
        this.ordersViewDAO = new OrdersViewDAO(conn);
    }

    public void chooseAction() throws SQLException {
        initDB();
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("1: add customer");
                System.out.println("2: add product");
                System.out.println("3: generate goods and customers");
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
                        generateGoodsAndCustomers();
                        break;
                    case "4":
                        addOrder(sc);
                        break;
                    case "5":
                        showTable(customersDAO.getAll());
                        break;
                    case "6":
                        showTable(goodsDAO.getAll());
                        break;
                    case "7":
                        showTable(ordersViewDAO.getAll());
                        break;
                    default:
                        return;
                }
            }
        }
    }

    private void initDB() {
        createTables();
    }

    private void createTables() {
        try {
            ScriptRunner scriptRunner = new ScriptRunner(conn);
            scriptRunner.setLogWriter(new PrintWriter("initDb.log"));
            scriptRunner.runScript(new FileReader(new File(new DbProperties().getInitSQL())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addProduct(Scanner sc) {
        System.out.print("Enter product name: ");
        String product = sc.nextLine();
        System.out.print("Enter price: ");
        int price = Integer.parseInt(sc.nextLine());
        System.out.print("Enter amount: ");
        int amount = Integer.parseInt(sc.nextLine());

        goodsDAO.insert(new Goods(product, price, amount));
    }

    private void generateGoodsAndCustomers() throws SQLException {
        Random rnd = new Random();
        conn.setAutoCommit(false); // enable transactions
        try {
            for (int i = 0; i < 20; i++) {
                Goods good = new Goods();
                good.setAmount(rnd.nextInt(20) + 1);
                good.setName("product" + rnd.nextInt(999));
                good.setPrice(rnd.nextInt(100) + 40);
                goodsDAO.insert(good);
            }

            for (int i = 0; i < 5; i++) {
                Customers customer = new Customers();
                customer.setFirstName("customer" + rnd.nextInt(9));
                customer.setLastName("surname" + rnd.nextInt(6));
                customer.setPhone("011" + (rnd.nextInt(888_88_33) + 111_11_11));
                customersDAO.insert(customer);
            }

            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // return to default mode
        }
    }

    private void addCustomer(Scanner sc) {
        System.out.print("Enter customer first name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter customer last name: ");
        String lastName = sc.nextLine();
        System.out.print("Enter customer phone number: ");
        String phone = sc.nextLine();
        customersDAO.insert(new Customers(firstName, lastName, phone));
    }

    private void addOrder(Scanner sc) {
        System.out.print("Enter product name: ");
        String product = sc.nextLine();
        System.out.print("Enter amount: ");
        int amount = Integer.parseInt(sc.nextLine());
        System.out.print("Enter customer id: ");
        int customerId = Integer.parseInt(sc.nextLine());

        try {
            customersDAO.get(customerId).orElseThrow();
        } catch (NoSuchElementException e) {
            System.err.println("Customer with id =  " + customerId + " is not exists");
            return;
        }

        int goodsId = updateGoodsAndGetGoodId(product, amount);
        if (goodsId == -1)
            return;

        ordersDAO.insert(new Orders(customerId, goodsId, amount));
    }

    private int updateGoodsAndGetGoodId(String product, Integer amount) {
        final List<Goods> goods = goodsDAO.getByCondition(String.format("name = '%s'", product));
        if (goods.size() == 0){
            System.err.println("Product " + product + " is not exists");
            return -1;
        }
        final Goods good = goods.get(0);
        good.setAmount(good.getAmount() - amount);
        this.goodsDAO.update(good);
        return good.getId();
    }

    private <T> void showTable(List<T> list)  {
        if (list.size() == 0) {
            System.out.println("No data");
            return;
        }
        final Field[] filedNames = list.get(0).getClass().getDeclaredFields();
        System.out.println("==================================================================================================================================================");
        for (int i = 0; i < filedNames.length; i++)
            System.out.print(filedNames[i].getName() + "\t\t");
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        System.out.println();
        System.out.println("==================================================================================================================================================");

    }

}
