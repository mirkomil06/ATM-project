//Class Withdrawal represents an ATM withdrawal transaction

public class Withdrawal extends Transaction {
    private int amount; // amount to withdrawn
    private Keypad keypad; // reference to keypad
    private CashDispenser cashDispenser; // reference to cash dispenser

    // constant corresponding menu option to cancel
    private final static int CANCELED = 7;


    //non-argument constructor
    public Withdrawal(int userAccountNumber, Screen atmScreen, BankDatabase atmBankDatabase, Keypad atmKeypad, CashDispenser atmCashDispenser) {
        super(userAccountNumber, atmScreen, atmBankDatabase);

        keypad = atmKeypad;
        cashDispenser = atmCashDispenser;
    }

    // perform transaction
    @Override
    public void execute() {
        boolean cashDispensed = false;
        double availableBalance;

        //get references to bank database and screen
        BankDatabase bankDatabase = getBankDatabase();
        Screen screen = getScreen();

        //loop unitl cash dispenses or user cancels
        do {
            // obtain withdrawal amount
            amount = displayMenuOfAmounts();

            // check whether user choose withdrawal amount or canceled
            if (amount != CANCELED) {
                // get available balance of account ivolved
                availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());

                if (amount <= availableBalance) {
                    if (cashDispenser.isSufficientCashAvailable(amount)) {
                        bankDatabase.debit(getAccountNumber(), amount);

                        cashDispenser.dispenseCash(amount);
                        cashDispensed = true;

                        //instruct user to take cash
                        screen.displayMessageLine("\nYour cash has been" + " dispensed. Please take your cash now.");
                    } // end if
                    else {
                        screen.displayMessageLine("\nInsufficient cash available in the ATM." + "\n\nPlease choose a smaller amount.");
                    }
                }// end if
                else {
                    screen.displayMessageLine("\nInsufficient funds in your account." + "\n\nPlease choose a smaller amount.");
                } // end else
            }// end if
            else {
                screen.displayMessageLine("\nCanceling transaction...");
                return;
            } // end else
        } while (!cashDispensed);
    } // end method

    private int displayMenuOfAmounts() {
        int userChoice = 0;

        Screen screen = getScreen();

        int[] amounts = {0, 5, 10, 20, 50, 100, 200};

        while (userChoice == 0) {
            // display the menu
            screen.displayMessageLine("\nWithdrawal menu:");
            screen.displayMessageLine("1 - $5");
            screen.displayMessageLine("2 - $10");
            screen.displayMessageLine("3 - $20");
            screen.displayMessageLine("4 - $50");
            screen.displayMessageLine("5 - $100");
            screen.displayMessageLine("6 - $200");
            screen.displayMessageLine("7 - Cancel transaction");
            screen.displayMessage("\nChoose a withdrawal amount: ");

            int input = keypad.getInput();

            switch (input) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    userChoice = amounts[input];
                    break;
                case CANCELED:
                    userChoice = CANCELED;
                    break;
                default:
                    screen.displayMessage("\nInvalid selection. Try again. ");
            } // end switch
        } // end while
        return userChoice;
    }
} // end class