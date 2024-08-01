public class AdminRun {
    private boolean adminAuthenticated; // whether admin is authenticated
    private String currentEmailAddress; // current administrator's email address
    private Screen screen; // ATM's screen
    private Keypad keypad; // ATM's keypad
    private BankDatabase bankDatabase; // account information database

    // constants corresponding menu options
    private static final int EXIT = 0;
    private static final int FINISH = 0;

    // non-argument constructor initializes variables
    public AdminRun() {
        adminAuthenticated = false;
        currentEmailAddress = null;
        screen = new Screen();
        keypad = new Keypad();
        bankDatabase = new BankDatabase();
    }

    // start ATM
    public void run() {
        // welcome and authenticate user; perform transaction
        while (true) {
            while (!adminAuthenticated) {
                screen.displayMessageLine("\nWelcome!");
                authenticateUser();
            }// end while

            performAction();

            adminAuthenticated = false;
            currentEmailAddress = "";
            screen.displayMessageLine("\nThank You! Goodbye!");
        }// end while
    } // end method run

    public void authenticateUser() {
        screen.displayMessage("\nPlease enter your email address: ");
        String emailAddress = keypad.getStr();
        screen.displayMessage("\nPlease enter your password: ");
        String password = keypad.getStr();

        // set userAuthenticated to boolean value set by database
        adminAuthenticated = bankDatabase.authenticateAdmin(emailAddress, password);

        // check whether authentication succeed
        if (adminAuthenticated) {
            currentEmailAddress = emailAddress;
        } else {
            screen.displayMessage("\nInvalid email address or password. Please try again.");
        }
    }

    private void performAction() {
        boolean userExited = false; // user has not chosen exit

        // loop while user has not chosen exit
        while (!userExited) {
            // show menu and get admin selection
            int mainMenuSelection = displayMainMenu();

            if (1 <= mainMenuSelection && mainMenuSelection <= bankDatabase.countAllRows()) {
                currentAction(mainMenuSelection);
            } else if (mainMenuSelection == (bankDatabase.countAllRows() + 1)) {
                addAccount();
            } else if (mainMenuSelection == EXIT) {
                screen.displayMessage("\nExiting system...");
                userExited = true;
            } else {
                screen.displayMessage("\nYou have not entered a valid selection. Try again.");
            }
        }
    }

    private int displayMainMenu() {
        screen.displayMessageLine("\nMain Menu:");
        bankDatabase.printAllAccounts();
        screen.displayMessageLine((bankDatabase.countAllRows() + 1) + " - Add acount");
        screen.displayMessageLine("0 - Exit\n");
        return keypad.getInput();
    }

    private void currentAction(int mainMenuSelection) {
        int accountNumber = bankDatabase.findAccount(mainMenuSelection);

        screen.displayMessageLine("\nMain Menu:");
        screen.displayMessageLine("1 - delete account");
        screen.displayMessageLine("2 - edited account\n");
        int choice = keypad.getInput();


        switch (choice) {
            case 1:
                screen.displayMessage("\nDelete account was successful... :)\n");
                bankDatabase.deleteAccount(accountNumber);
                break;
            case 2:
                screen.displayMessageLine("\nPlease enter details");
                boolean finishEnter = false;
                int newPin = bankDatabase.getPin(accountNumber);
                double newAvailableBalance = bankDatabase.getAvailableBalance(accountNumber);
                double newTotalBalance = bankDatabase.getTotalBalance(accountNumber);

                while (!finishEnter) {
                    int mainUpdateSelection = displayUpdateDetails();

                    switch (mainUpdateSelection) {
                        case 1:
                            screen.displayMessage("\nPlease enter PIN: ");
                            newPin = keypad.getInput();
                            break;
                        case 2:
                            screen.displayMessage("\nPlease enter Available Balance: ");
                            newAvailableBalance = keypad.getInputDouble();
                            break;
                        case 3:
                            screen.displayMessage("\nPlease enter Total Balance: ");
                            newTotalBalance = keypad.getInputDouble();
                            break;
                        case FINISH:
                            if (newPin == 0 || newAvailableBalance == 0.0 || newTotalBalance == 0.0) {
                                screen.displayMessage("\nYou didn't add all the details, please add all details\n");
                                break;
                            } else if (newAvailableBalance > newTotalBalance) {
                                screen.displayMessage("\nAvailable Balance cannot be greater than Total Balance\n");
                                break;
                            } else {
                                screen.displayMessage("\nEdited account was successful... :)\n");
                                bankDatabase.updateAccount(accountNumber, newPin, newAvailableBalance, newTotalBalance);
                                finishEnter = true;
                                break;
                            }
                        default:
                            screen.displayMessage("\nYou have not entered a valid selection. Try again.\n");
                            break;
                    }

                    screen.displayMessageLine("\nAccountNumber | PIN | AvailableBalance | TotalBalance");
                    screen.displayMessageLine(accountNumber + " | " + newPin + " | " + newAvailableBalance + " | " + newTotalBalance + "\n");
                }
                break;
            default:
                screen.displayMessage("\nYou have not entered a valid selection. Try again.\n");
                break;
        }


    }

    private void addAccount() {
        screen.displayMessageLine("\nPlease enter details");
        boolean finishEnter = false;
        int newAccountNumber = 0;
        int newPin = 0;
        double newAvailableBalance = 0;
        double newTotalBalance = 0;
        while (!finishEnter) {

            int mainMenuSelection = displayEnterDetails();

            switch (mainMenuSelection) {
                case 1:
                    screen.displayMessage("\nPlease enter Account Number: ");
                    newAccountNumber = keypad.getInput();
                    break;
                case 2:
                    screen.displayMessage("\nPlease enter PIN: ");
                    newPin = keypad.getInput();
                    break;
                case 3:
                    screen.displayMessage("\nPlease enter Available Balance: ");
                    newAvailableBalance = keypad.getInputDouble();
                    break;
                case 4:
                    screen.displayMessage("\nPlease enter Total Balance: ");
                    newTotalBalance = keypad.getInputDouble();
                    break;
                case FINISH:
                    if (newAccountNumber == 0 || newPin == 0 || newAvailableBalance == 0.0 || newTotalBalance == 0.0) {
                        screen.displayMessage("\nYou didn't add all the details, please add all details\n");
                        break;
                    } else if (newAvailableBalance > newTotalBalance) {
                        screen.displayMessage("\nAvailable Balance cannot be greater than Total Balance\n");
                        break;
                    } else {
                        screen.displayMessage("\nAdd account was successful... :)\n");
                        bankDatabase.addAccount(newAccountNumber, newPin, newAvailableBalance, newTotalBalance);
                        finishEnter = true;
                        break;
                    }
                default:
                    screen.displayMessage("\nYou have not entered a valid selection. Try again.\n");
                    break;
            }

            screen.displayMessageLine("\nAccountNumber | PIN | AvailableBalance | TotalBalance");
            screen.displayMessageLine(newAccountNumber + " | " + newPin + " | " + newAvailableBalance + " | " + newTotalBalance + "\n");
        }
    }

    private int displayEnterDetails() {
        screen.displayMessageLine("Main Menu:");
        screen.displayMessageLine("1 - Account Number");
        screen.displayMessageLine("2 - Pin");
        screen.displayMessageLine("3 - Available Balance");
        screen.displayMessageLine("4 - Total Balance");
        screen.displayMessageLine("0 - Finish\n");
        return keypad.getInput();
    }

    private int displayUpdateDetails() {
        screen.displayMessageLine("Main Menu:");
        screen.displayMessageLine("1 - Pin");
        screen.displayMessageLine("2 - Available Balance");
        screen.displayMessageLine("3 - Total Balance");
        screen.displayMessageLine("0 - Finish\n");
        return keypad.getInput();
    }
}
