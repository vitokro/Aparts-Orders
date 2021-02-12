package ua.kiev.prog;

import ua.kiev.prog.apartments.ApartmentsApp;
import ua.kiev.prog.orders.OrdersApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DbProperties props = new DbProperties();
        try (Connection conn = DriverManager.getConnection(props.getUrl(), props.getUser(), props.getPassword());
             Scanner sc = new Scanner(System.in)) {
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
                        new ApartmentsApp(conn).chooseAction();
                        break;
                    case "2":
//        3. Создать проект «База данных заказов». Создать
//таблицы «Товары» , «Клиенты» и «Заказы».
//Написать код для добавления новых клиентов,
//товаров и оформления заказов.
                        new OrdersApp(conn).chooseAction();
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException throwables) {
            System.out.println("Connection failed");
            throwables.printStackTrace();
        }

    }
}
