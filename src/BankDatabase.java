import java.sql.*;

public class BankDatabase {
    private Connection connection;

    public BankDatabase() {
        try {
            // Connect to PostgreSQL database
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bank_database", "postgres", "0000");
            System.out.println("Connected to the PostgreSQL database.");
            createAccountsTable();
            initializeDatabase(); // Insert initial values into the database
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void createAccountsTable() {
        // SQL statement to create the accounts table
        String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                "accountNumber INT PRIMARY KEY," +
                "pin INT," +
                "availableBalance DOUBLE PRECISION," +
                "totalBalance DOUBLE PRECISION" +
                ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Accounts table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating accounts table: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        // Insert initial values into the database
        String sql = "INSERT INTO accounts (accountNumber, pin, availableBalance, totalBalance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Insert account 1
            stmt.setInt(1, 12345);
            stmt.setInt(2, 54321);
            stmt.setDouble(3, 1000.0);
            stmt.setDouble(4, 1200.0);
            stmt.executeUpdate();

            // Insert account 2
            stmt.setInt(1, 98765);
            stmt.setInt(2, 56789);
            stmt.setDouble(3, 200.0);
            stmt.setDouble(4, 200.0);
            stmt.executeUpdate();

            System.out.println("Initial values inserted into the database.");
        } catch (SQLException e) {
            System.err.println("Error inserting initial values: " + e.getMessage());
        }
    }

    private Account getAccount(int accountNumber) {
        String sql = "SELECT * FROM accounts WHERE accountNumber = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new Account(
                        resultSet.getInt("accountNumber"),
                        resultSet.getInt("pin"),
                        resultSet.getDouble("availableBalance"),
                        resultSet.getDouble("totalBalance")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving account: " + e.getMessage());
        }
        return null; // if no matching account was found return null
    }

    public boolean authenticateUser(int userAccountNumber, int userPin) {
        Account userAccount = getAccount(userAccountNumber);

        if (userAccount != null) {
            return userAccount.validatePIN(userPin);
        } else {
            return false;
        }
    }

    public double getAvailableBalance(int userAccountNumber) {
        Account account = getAccount(userAccountNumber);
        return (account != null) ? account.getAvailableBalance() : 0.0;
    }

    public double getTotalBalance(int userAccountNumber) {
        Account account = getAccount(userAccountNumber);
        return (account != null) ? account.getTotalBalance() : 0.0;
    }

    public void credit(int userAccountNumber, double amount) {
        Account account = getAccount(userAccountNumber);
        if (account != null) {
            account.credit(amount);
            updateAccount(account);
        }
    }

    public void debit(int userAccountNumber, double amount) {
        Account account = getAccount(userAccountNumber);
        if (account != null) {
            account.debit(amount);
            updateAccount(account);
        }
    }

    private void updateAccount(Account account) {
        String sql = "UPDATE accounts SET availableBalance = ?, totalBalance = ? WHERE accountNumber = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, account.getAvailableBalance());
            stmt.setDouble(2, account.getTotalBalance());
            stmt.setInt(3, account.getAccountNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection to the database closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}