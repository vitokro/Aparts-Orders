DROP VIEW IF EXISTS OrdersView;

DROP TABLE IF EXISTS Orders;

DROP TABLE IF EXISTS Goods;

CREATE TABLE Goods
(
    id     INT          NOT NULL
        AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(128) NOT NULL,
    price  INT          NOT NULL,
    amount INT
);

DROP TABLE IF EXISTS Customers;

CREATE TABLE Customers
(
    id        INT         NOT NULL
        AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(20) NOT NULL,
    lastName  VARCHAR(20) NOT NULL,
    phone     VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE Orders
(
    id         INT NOT NULL
        AUTO_INCREMENT PRIMARY KEY,
    customerId INT,
    goodsId    INT,
    amount     INT,
    createdAt  DATE,
    FOREIGN KEY (goodsId) REFERENCES Goods (id),
    FOREIGN KEY (customerId) REFERENCES Customers (id)
);

CREATE VIEW OrdersView
AS
SELECT g.name,
       g.price,
       o.amount,
       c.firstName,
       c.lastName,
       c.phone,
       o.createdAt,
       o.amount * g.price as totalPrice
FROM Goods g,
     Customers c,
     Orders o
WHERE o.customerId = c.id
  AND o.goodsId = g.id;
