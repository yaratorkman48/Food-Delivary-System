# Food Delivery System

A Java prototype of a food ordering and delivery management system, built across four
assignments for the **Object-Oriented Programming** course, Department of Information
Systems, University of Haifa.

The system manages customers, restaurants, restaurant managers, riders and orders —
from placing an order through assigning a rider to marking it delivered, with credit
balances, ratings and reporting along the way.

---

## Repository structure

```
HW1/src/FoodDeliverySystem/   OOP foundations — classes, inheritance, polymorphism
HW2/src/FoodDeliverySystem/   Collections — the central DeliveryDataBase
HW3/src/FoodDeliverySystem/   Comparable/Comparator, lambdas, Stream API, exceptions
HW4/src/FoodDeliverySystem/   JavaFX GUI + text-file persistence
```

Each folder is a complete, runnable version of the system at that stage. HW4 is the
final one.

---

## What each stage adds

### HW1 — Object-oriented foundations
The class model and the inheritance hierarchy.

- `Customer`, `Rider`, `Admin`, `RestAdmin`, `Order`
- `Restaurant` as a base class, with `FastFoodRestaurant` and `PremiumRestaurant`
  extending it — final order price is computed polymorphically per restaurant type
- `InputHelper` for validation (email format, phone, non-empty strings)
- Console menu driven by `Main`

### HW2 — Collections
A single central data store, `DeliveryDataBase`, replacing the arrays from HW1.

- `ArrayList` collections for customers, restaurants (polymorphic), riders,
  restaurant managers and orders
- `HashMap<Integer, ArrayList<Order>>` — every order placed by a given customer
- `Hashtable<Integer, ArrayList<Restaurant>>` — the restaurants a customer ordered from
- `HashMap<Integer, Double>` — total amount each customer has paid
- Query methods: active orders per rider, premium restaurants per customer, top
  customer by order count, top rider by deliveries, open restaurants filtered by
  cuisine type
- Duplicate prevention on every `add` operation

### HW3 — Advanced Java
Sorting, functional style and a custom exception hierarchy.

- `Customer implements Comparable` — sorted by credit balance, high to low
- `RestaurantRatingComparator`, `OrderPriceComparator`
- Lambda-expression sorts: riders by delivery count, customers by first name,
  orders by date
- Stream API and method references in `SystemReports`
- Checked exceptions: `CustomerNotFoundException`, `RestaurantNotFoundException`,
  `InsufficientBalanceException`, `DeliveryPersonUnavailableException`

### HW4 — GUI and persistence
Full migration from console to JavaFX, plus saving and loading.

- `FoodDeliveryApp` owns one `Stage` and one shared `DeliveryDataBase`; navigation
  swaps the root node of a single reused `Scene`
- Screens: opening, admin login, admin hub, customer / restaurant / rider / order /
  restaurant-manager management, reports, user login
- Tables backed by `ObservableList`, so a single `setAll(...)` redraws after a change
- `FileManager` — text-file persistence using `File`, `BufferedReader` and
  `BufferedWriter` with try-with-resources; one file per collection, `|` as the field
  delimiter, type tags (`BASE` / `FASTFOOD` / `PREMIUM`) so polymorphic restaurants
  reconstruct correctly, and codes rather than object references so live objects are
  re-linked on load
- No `Scanner`, no console I/O

---

## Running it

**HW1–HW3** (console):

```bash
cd HW1/src
javac FoodDeliverySystem/*.java
java FoodDeliverySystem.Main
```

**HW4** (JavaFX) needs the JavaFX SDK on the module path — JavaFX is not bundled with
the JDK from Java 11 onward:

```bash
cd HW4/src
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls \
      FoodDeliverySystem/*.java
java  --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls \
      FoodDeliverySystem.FoodDeliveryApp
```

In Eclipse or IntelliJ, add the JavaFX library to the project and run
`FoodDeliveryApp` as the main class.

Default admin credentials (created by the `DeliveryDataBase` constructor):
username `admin`, password `12345`.

The system starts with seeded sample data; save and load write `customers.txt`,
`restaurants.txt`, `riders.txt`, `orders.txt` and `restAdmins.txt` into the working
directory.

---

## Built with

Java · JavaFX · Java Collections Framework · Stream API
