import java.sql.*;

public class BankDatabase {
    private Connection connection;

    public BankDatabase() {
        try {
            // Connect to PostgreSQL database
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bank_database", "postgres", "0000");
            System.out.println("Connected to the PostgreSQL database.");
            createAdminsTable();
            createAccountsTable();
            initializeAccountsDatabase(); // Insert initial values into the database
            initializeAdminDatabase(); // Insert initial values into the database
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void createAdminsTable() {
        // SQL statement to create the admins table
        String sql = "CREATE TABLE IF NOT EXISTS admins (" +
                "id SERIAL PRIMARY KEY, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL" +
                ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Admins table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating admins table: " + e.getMessage());
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

    private void initializeAdminDatabase(){
        // Insert initial values into the database
        String sql = "INSERT INTO admins (email, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Insert the first admin
            stmt.setString(1, "admin1@example.com");
            stmt.setString(2, "password123");
            stmt.executeUpdate();

            // Insert the second admin
            stmt.setString(1, "admin2@example.com");
            stmt.setString(2, "password456");
            stmt.executeUpdate();

            System.out.println("Initial admin values inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting initial admin values: " + e.getMessage());
        }

    }

    private void initializeAccountsDatabase() {
        // Insert initial values into the database
        String sql = "INSERT INTO accounts (accountNumber, pin, availableBalance, totalBalance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Insert account 1
            stmt.setInt(1, 123456);
            stmt.setInt(2, 654321);
            stmt.setDouble(3, 1000.0);
            stmt.setDouble(4, 1200.0);
            stmt.executeUpdate();

            // Insert account 2
            stmt.setInt(1, 987654);
            stmt.setInt(2, 456789);
            stmt.setDouble(3, 200.0);
            stmt.setDouble(4, 200.0);
            stmt.executeUpdate();

            System.out.println("Initial values inserted into the database.");
        } catch (SQLException e) {
            System.err.println("Error inserting initial values: " + e.getMessage());
        }
    }

    private Admin getAdmin(String emailAddress){
        String sql = "SELECT * FROM admins WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emailAddress);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new Admin(
                        resultSet.getString("email"),
                        resultSet.getString("password")

                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving admin: " + e.getMessage());
        }
        return null; // if no matching account was found return null
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

    public boolean authenticateAdmin(String emailAddress, String password){
        Admin adminAccount = getAdmin(emailAddress);

        if(adminAccount != null){
            return adminAccount.validatePassword(password);
        }else {
            return false;
        }
    }

    public boolean authenticateUser(int userAccountNumber, int userPin) {
        Account userAccount = getAccount(userAccountNumber);

        if (userAccount != null) {
            return userAccount.validatePIN(userPin);
        } else {
            return false;
        }
    }

    public void printAllAccounts() {
        String sql = "SELECT * FROM accounts";
        int i = 1;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("    AccountNumber | PIN | AvailableBalance | TotalBalance");
            while (rs.next()) {
                int accountNumber = rs.getInt("accountNumber");
                int pin = rs.getInt("pin");
                double availableBalance = rs.getDouble("availableBalance");
                double totalBalance = rs.getDouble("totalBalance");

                System.out.println(i + " - " + accountNumber + " | " + pin + " | " + availableBalance + " | " + totalBalance);
                i++;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
        }
    }

    public int findAccount(int index) {
        String sql = "SELECT * FROM accounts";
        int i = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int accountNumber = 0;
            while (i != index) {
                rs.next();
                accountNumber = rs.getInt("accountNumber");
                i++;
            }
            return accountNumber;
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
            return 0;
        }
    }

    public int countAllRows() {
        String sql = "SELECT COUNT(*) AS row_count FROM accounts";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("row_count");
            }
        } catch (SQLException e) {
            System.err.println("Error counting rows: " + e.getMessage());
        }
        return 0;
    }

    public void addAccount(int accountNumber, int pin, double availableBalance, double totalBalance) {
        String sql = "INSERT INTO accounts (accountNumber, pin, availableBalance, totalBalance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            stmt.setInt(2, pin);
            stmt.setDouble(3, availableBalance);
            stmt.setDouble(4, totalBalance);
            stmt.executeUpdate();
            System.out.println("Account added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding account: " + e.getMessage());
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

    public int getPin(int userAccountNumber) {
        Account account = getAccount(userAccountNumber);
        return (account != null) ? account.getPin() : 0;
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

    public void updateAccount(int accountNumber, int newPin, double newAvailableBalance, double newTotalBalance) {
        String sql = "UPDATE accounts SET pin = ?, availableBalance = ?, totalBalance = ? WHERE accountNumber = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newPin);
            stmt.setDouble(2, newAvailableBalance);
            stmt.setDouble(3, newTotalBalance);
            stmt.setInt(4, accountNumber);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account updated successfully.");
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
        }
    }

    // Method to delete an account
    public void deleteAccount(int accountNumber) {
        String sql = "DELETE FROM accounts WHERE accountNumber = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
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