package ua.kiev.prog.apartments;

import ua.kiev.prog.Id;

import java.util.Objects;

public class Apartment {
    @Id
    private int id;
    private String district;
    private String address;
    private int area;
    private int roomsNumber;
    private int price;

    public Apartment() {
    }

    public Apartment(String district, String address, int area, int roomsNumber, int price) {
        this.district = district;
        this.address = address;
        this.area = area;
        this.roomsNumber = roomsNumber;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getRoomsNumber() {
        return roomsNumber;
    }

    public void setRoomsNumber(int roomsNumber) {
        this.roomsNumber = roomsNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return area == apartment.area &&
                roomsNumber == apartment.roomsNumber &&
                price == apartment.price &&
                Objects.equals(district, apartment.district) &&
                Objects.equals(address, apartment.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(district, address, area, roomsNumber, price);
    }

    @Override
    public String toString() {
        return id + "\t\t" + district + "\t\t" + address +
                "\t\t" + area + "\t\t\t" + roomsNumber + "\t\t" + price;
    }
}
