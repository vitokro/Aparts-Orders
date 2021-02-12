package ua.kiev.prog.apartments;

public final class ApartmentBuilder {
    private int id;
    private String district;
    private String address;
    private int area;
    private int roomsNumber;
    private int price;

    private ApartmentBuilder() {
    }

    public static ApartmentBuilder anApartment() {
        return new ApartmentBuilder();
    }

    public ApartmentBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ApartmentBuilder withDistrict(String district) {
        this.district = district;
        return this;
    }

    public ApartmentBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public ApartmentBuilder withArea(int area) {
        this.area = area;
        return this;
    }

    public ApartmentBuilder withRoomsNumber(int roomsNumber) {
        this.roomsNumber = roomsNumber;
        return this;
    }

    public ApartmentBuilder withPrice(int price) {
        this.price = price;
        return this;
    }

    public Apartment build() {
        Apartment apartment = new Apartment();
        apartment.setId(id);
        apartment.setDistrict(district);
        apartment.setAddress(address);
        apartment.setArea(area);
        apartment.setRoomsNumber(roomsNumber);
        apartment.setPrice(price);
        return apartment;
    }
}
