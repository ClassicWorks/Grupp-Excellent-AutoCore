
import com.wac.autocore.service.GarageSystem;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GarageSystem garageSystem = new GarageSystem();

    public static void main(String[] args) {

        boolean running = true;

        printHeader();

        while (running) {

            printMenu();

            int choice = readInt("Select option: ");

            switch (choice) {

                case 1:
                    garageSystem.showCustomers();
                    break;

                case 2:
                    createCustomer();
                    break;

                case 3:
                    garageSystem.showVehicles();
                    break;

                case 4:
                    createVehicle();
                    break;

                case 5:
                    garageSystem.showBookings();
                    break;

                case 6:
                    createBooking();
                    break;

                case 7:
                    garageSystem.showServiceItems();
                    break;

                case 8:
                    garageSystem.showMechanics();
                    break;

                case 9:
                    garageSystem.showWorkOrders();
                    break;

                case 10:
                    createWorkOrder();
                    break;

                case 11:
                    startWorkOrder();
                    break;

                case 12:
                    completeWorkOrder();
                    break;

                case 13:
                    garageSystem.showInvoices();
                    break;

                case 14:
                    createInvoice();
                    break;

                case 15:
                    garageSystem.showPayments();
                    break;

                case 16:
                    processPayment();
                    break;

                case 0:
                    running = false;
                    break;

                default:
                    System.out.println("Unknown menu option.");
                    break;
            }
        }

        System.out.println();
        System.out.println("Shutting down Wigell AutoCore...");
        System.out.println("Goodbye!");

        scanner.close();
    }

    private static void printHeader() {
        System.out.println("===========================================");
        System.out.println("            WIGELL AUTOCORE");
        System.out.println("         A Wigell Group Company");
        System.out.println("===========================================");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=============== MAIN MENU =================");
        System.out.println(" 1. Show customers");
        System.out.println(" 2. Create customer");
        System.out.println(" 3. Show vehicles");
        System.out.println(" 4. Create vehicle");
        System.out.println(" 5. Show bookings");
        System.out.println(" 6. Create booking");
        System.out.println(" 7. Show services");
        System.out.println(" 8. Show mechanics");
        System.out.println(" 9. Show work orders");
        System.out.println("10. Create work order");
        System.out.println("11. Start work order");
        System.out.println("12. Complete work order");
        System.out.println("13. Show invoices");
        System.out.println("14. Create invoice");
        System.out.println("15. Show payments");
        System.out.println("16. Process payment");
        System.out.println(" 0. Exit");
        System.out.println("===========================================");
    }

    private static void createCustomer() {

        System.out.println();
        System.out.println("=== CREATE CUSTOMER ===");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        garageSystem.createCustomer(name, phone, email);
    }

    private static void createVehicle() {

        System.out.println();
        System.out.println("=== CREATE VEHICLE ===");

        garageSystem.showCustomers();

        int customerId = readInt("Customer ID: ");

        System.out.print("Registration number: ");
        String registrationNumber = scanner.nextLine();

        System.out.print("Brand: ");
        String brand = scanner.nextLine();

        System.out.print("Model: ");
        String model = scanner.nextLine();

        int year = readInt("Year: ");

        garageSystem.createVehicle(
                registrationNumber,
                brand,
                model,
                year,
                customerId
        );
    }

    private static void createBooking() {

        System.out.println();
        System.out.println("=== CREATE BOOKING ===");

        garageSystem.showVehicles();

        int vehicleId = readInt("Vehicle ID: ");

        LocalDate date = readDate("Date (YYYY-MM-DD): ");

        System.out.print("Description: ");
        String description = scanner.nextLine();

        garageSystem.createBooking(
                vehicleId,
                date,
                description
        );
    }

    private static void createWorkOrder() {

        System.out.println();
        System.out.println("=== CREATE WORK ORDER ===");

        garageSystem.showBookings();
        int bookingId = readInt("Booking ID: ");

        garageSystem.showMechanics();
        int mechanicId = readInt("Mechanic ID: ");

        garageSystem.showServiceItems();

        System.out.println();
        System.out.println("Enter service IDs separated by comma.");
        System.out.println("Example: 1,3,4");
        System.out.print("Services: ");

        String input = scanner.nextLine();

        String[] parts = input.split(",");
        int[] serviceItemIds = new int[parts.length];

        try {

            for (int i = 0; i < parts.length; i++) {
                serviceItemIds[i] = Integer.parseInt(parts[i].trim());
            }

            garageSystem.createWorkOrder(
                    bookingId,
                    mechanicId,
                    serviceItemIds
            );

        } catch (NumberFormatException e) {
            System.out.println("Invalid service ID.");
        }
    }

    private static void startWorkOrder() {

        System.out.println();
        System.out.println("=== START WORK ORDER ===");

        garageSystem.showWorkOrders();

        int workOrderId = readInt("Work order ID: ");

        garageSystem.startWorkOrder(workOrderId);
    }

    private static void completeWorkOrder() {

        System.out.println();
        System.out.println("=== COMPLETE WORK ORDER ===");

        garageSystem.showWorkOrders();

        int workOrderId = readInt("Work order ID: ");

        garageSystem.completeWorkOrder(workOrderId);
    }

    private static void createInvoice() {

        System.out.println();
        System.out.println("=== CREATE INVOICE ===");

        garageSystem.showWorkOrders();

        int workOrderId = readInt("Work order ID: ");

        System.out.print("Discount code (leave empty for none): ");
        String discountCode = scanner.nextLine();

        garageSystem.createInvoice(
                workOrderId,
                discountCode
        );
    }

    private static void processPayment() {

        System.out.println();
        System.out.println("=== PROCESS PAYMENT ===");

        garageSystem.showInvoices();

        int invoiceId = readInt("Invoice ID: ");

        System.out.println();
        System.out.println("Available payment types:");
        System.out.println("CARD");
        System.out.println("SWISH");
        System.out.println("CASH");

        System.out.print("Payment type: ");
        String paymentType = scanner.nextLine();

        garageSystem.processPayment(
                invoiceId,
                paymentType
        );
    }

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static LocalDate readDate(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine();

            try {
                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use format YYYY-MM-DD.");
            }
        }
    }
}