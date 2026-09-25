package ir.maktabsharif.smspanel;

import ir.maktabsharif.smspanel.entity.Transaction;
import ir.maktabsharif.smspanel.entity.User;
import ir.maktabsharif.smspanel.exception.DomainException;
import ir.maktabsharif.smspanel.repository.TransactionRepository;
import ir.maktabsharif.smspanel.repository.UserRepository;
import ir.maktabsharif.smspanel.repository.inmemory.InMemoryTransactionRepository;
import ir.maktabsharif.smspanel.repository.inmemory.InMemoryUserRepository;
import ir.maktabsharif.smspanel.service.TransactionService;
import ir.maktabsharif.smspanel.service.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point of the SMS panel subscription management system.
 * <p>
 * This class is the only place where input is read from the user and output is written back.
 * Every decision is delegated to the service layer.
 */
public class Main {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Scanner SCANNER = new Scanner(System.in);

    private final UserService userService;

    /** Currently authenticated account, {@code null} while nobody is logged in. */
    private User loggedInUser;

    public Main(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        // --- in-memory storage (default): runs without any database ---
        UserRepository userRepository = new InMemoryUserRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();

        // --- PostgreSQL storage: uncomment these two lines instead (and run db.sql first) ---
        // UserRepository userRepository = new JdbcUserRepository();
        // TransactionRepository transactionRepository = new JdbcTransactionRepository();

        TransactionService transactionService = new TransactionService(transactionRepository);
        UserService userService = new UserService(userRepository, transactionService);

        new Main(userService).run();
    }

    // ---------------------------------------------------------------- menus

    private void run() {
        printHeader();
        boolean running = true;
        while (running) {
            if (loggedInUser == null) {
                running = handleGuestMenu();
            } else {
                handleUserMenu();
            }
        }
        SCANNER.close();
    }

    private boolean handleGuestMenu() {
        System.out.println();
        System.out.println("===== SMS Panel | Main Menu =====");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.println("0) Exit");

        int choice = readInt("Choose an option: ");
        switch (choice) {
            case 1 -> register();
            case 2 -> login();
            case 0 -> {
                System.out.println("Goodbye!");
                return false;
            }
            default -> System.out.println("Invalid option, please try again.");
        }
        return true;
    }

    private void handleUserMenu() {
        System.out.println();
        System.out.println("===== SMS Panel | Account of " + loggedInUser.getUsername() + " =====");
        System.out.println("1) View profile");
        System.out.println("2) Increase credit");
        System.out.println("3) Change password");
        System.out.println("4) Transaction history");
        System.out.println("5) Deactivate account");
        System.out.println("0) Logout");

        int choice = readInt("Choose an option: ");
        switch (choice) {
            case 1 -> showProfile();
            case 2 -> increaseCredit();
            case 3 -> changePassword();
            case 4 -> showTransactionHistory();
            case 5 -> deactivateAccount();
            case 0 -> {
                System.out.println("You have been logged out.");
                loggedInUser = null;
            }
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    // ---------------------------------------------------------------- 1. registration

    private void register() {
        System.out.println();
        System.out.println("--- Register ---");
        String name = readLine("Name: ");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        try {
            User user = userService.register(name, username, password);
            System.out.println("Registration successful. Welcome " + user.getName() + "!");
            System.out.println("An initial gift credit of " + format(user.getCredit())
                    + " was added to your account.");
        } catch (DomainException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 2. login

    private void login() {
        System.out.println();
        System.out.println("--- Login ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        try {
            loggedInUser = userService.login(username, password);
            System.out.println("Login successful. Welcome back " + loggedInUser.getName() + "!");
        } catch (DomainException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 3. increase credit

    private void increaseCredit() {
        System.out.println();
        System.out.println("--- Increase Credit ---");

        BigDecimal amount;
        try {
            amount = new BigDecimal(readLine("Amount to add: ").replace(",", ""));
        } catch (NumberFormatException e) {
            System.out.println("The amount must be a valid number.");
            return;
        }

        try {
            userService.increaseCredit(loggedInUser.getId(), amount);
            refreshLoggedInUser();
            System.out.println("Credit increased successfully.");
            System.out.println("Current credit: " + format(loggedInUser.getCredit()));
        } catch (DomainException e) {
            System.out.println("Increase credit failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 4. change password

    private void changePassword() {
        System.out.println();
        System.out.println("--- Change Password ---");
        String currentPassword = readLine("Current password: ");
        String newPassword = readLine("New password: ");

        try {
            userService.changePassword(loggedInUser.getId(), currentPassword, newPassword);
            System.out.println("Your password has been changed successfully.");
        } catch (DomainException e) {
            System.out.println("Change password failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 5. view profile

    private void showProfile() {
        System.out.println();
        System.out.println("--- Profile ---");
        try {
            User user = userService.viewProfile(loggedInUser.getId());
            System.out.println("Name:              " + user.getName());
            System.out.println("Username:          " + user.getUsername());
            System.out.println("Credit:            " + format(user.getCredit()));
            System.out.println("Registration date: " + user.getRegistrationDate().format(DATE_FORMAT));
            System.out.println("Account status:    " + user.getStatus());
        } catch (DomainException e) {
            System.out.println("Could not load the profile: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 6. transaction history

    private void showTransactionHistory() {
        System.out.println();
        System.out.println("--- Transaction History ---");
        try {
            List<Transaction> transactions = userService.viewTransactionHistory(loggedInUser.getId());
            if (transactions.isEmpty()) {
                System.out.println("No transactions have been recorded on this account yet.");
                return;
            }

            System.out.printf("%-4s %-20s %-16s %12s  %s%n", "ID", "Date", "Type", "Amount", "Description");
            System.out.println("-".repeat(95));
            for (Transaction transaction : transactions) {
                System.out.printf("%-4d %-20s %-16s %12s  %s%n",
                        transaction.getId(),
                        transaction.getDate().format(DATE_FORMAT),
                        transaction.getType(),
                        format(transaction.getAmount()),
                        transaction.getDescription() == null ? "-" : transaction.getDescription());
            }
        } catch (DomainException e) {
            System.out.println("Could not load the transaction history: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 7. deactivate account

    private void deactivateAccount() {
        System.out.println();
        System.out.println("--- Deactivate Account ---");
        String confirmation = readLine("Type YES to confirm the deactivation: ");

        if (!"YES".equalsIgnoreCase(confirmation)) {
            System.out.println("Deactivation cancelled.");
            return;
        }

        try {
            userService.deactivateAccount(loggedInUser.getId());
            System.out.println("Your account has been deactivated. You can no longer log in,");
            System.out.println("but your information and transaction history are preserved.");
            loggedInUser = null;
        } catch (DomainException e) {
            System.out.println("Deactivation failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- helpers

    private void refreshLoggedInUser() {
        loggedInUser = userService.viewProfile(loggedInUser.getId());
    }

    private void printHeader() {
        System.out.println("=================================================");
        System.out.println("     SMS PANEL - SUBSCRIPTION MANAGEMENT         ");
        System.out.println("=================================================");
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            return "";
        }
        return SCANNER.nextLine().trim();
    }

    private int readInt(String prompt) {
        String value = readLine(prompt);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    private static String format(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
