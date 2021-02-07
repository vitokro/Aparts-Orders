package ua.kiev.prog;

import ua.kiev.prog.apartments.Apartments;
import ua.kiev.prog.orders.OrdersDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/weedb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    static Connection conn;


    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    System.out.println("1: ApartmentsDB");
                    System.out.println("2: OrdersDB");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
//        2. Спроектировать базу «Квартиры». Каждая запись
//в базе содержит данные о квартире (район,
//адрес, площадь, кол. комнат, цена). Сделать
//возможность выборки квартир из списка по
//параметрам.
                            new Apartments(conn).chooseAction();
                            break;
                        case "2":
//        3. Создать проект «База данных заказов». Создать
//таблицы «Товары» , «Клиенты» и «Заказы».
//Написать код для добавления новых клиентов,
//товаров и оформления заказов.
                            new OrdersDB(conn).chooseAction();
                            break;
                        default:
                            return;
                    }
                }
            }
        } catch (SQLException throwables) {
            System.out.println("Connection failed");
            throwables.printStackTrace();
        }

    }
}
