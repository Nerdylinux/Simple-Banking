import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class App {

    public static void main(String[] args) throws Exception {
        try {
            String accNo = null;
            boolean f = false;
            int check = 0;

            check = JOptionPane.showConfirmDialog(
                null,
                "Do Have An Account Already ?",
                "Welcome :)",
                JOptionPane.YES_NO_OPTION
            );

            try {
                if (check == 1) {
                    addAccount();
                } else if (check == 0) {
                    do {
                        accNo = JOptionPane.showInputDialog(
                            "Enter your Account No.:-"
                        );
                        if (accNo == null) {
                            int choice = JOptionPane.showConfirmDialog(
                                null,
                                "Do you want to exit?",
                                "Exit Confirmation",
                                JOptionPane.YES_NO_OPTION
                            );
                            if (choice == JOptionPane.YES_OPTION) {
                                System.exit(0);
                            }
                        } else if (!accNo.matches("\\d{10}")) {
                            JOptionPane.showMessageDialog(
                                null,
                                "Invalid account no. [Please Enter a 10-digit no.]",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } while (
                        accNo != null && accNo.matches("\\d{10}") == false
                    );
                } else {
                    System.exit(0);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Error getting the account no.: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            try (
                BufferedReader br = new BufferedReader(
                    new FileReader("lib/AccountList.txt")
                )
            ) {
                String line;
                do {
                    line = br.readLine();
                    if (accNo.equals(line)) {
                        checkSavings(accNo);
                        f = true;
                        break;
                    }
                } while (line != null);
            }

            if (!f) {
                JOptionPane.showMessageDialog(
                    null,
                    "No Account Found",
                    "Opps!",
                    JOptionPane.ERROR_MESSAGE
                );
                if (
                    JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to Open 1 ?",
                        "Be our new member",
                        JOptionPane.YES_NO_OPTION
                    ) ==
                    0
                ) {
                    addAccount();
                } else {
                    JOptionPane.showMessageDialog(
                        null,
                        "Thank You for visiting. \uD83D\uDE4F",
                        "Have a Great Day",
                        JOptionPane.PLAIN_MESSAGE
                    );
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "An unexpected error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    static void checkSavings(String accountNumber) throws IOException {
        String savings = null;
        try (
            BufferedReader accountReader = new BufferedReader(
                new FileReader("lib/AccountList.txt")
            );
            BufferedReader savingsReader = new BufferedReader(
                new FileReader("lib/savingsList.txt")
            )
        ) {
            String accountLine;
            String savingsLine;
            while (
                (accountLine = accountReader.readLine()) != null &&
                (savingsLine = savingsReader.readLine()) != null
            ) {
                if (accountLine.equals(accountNumber)) {
                    savings = savingsLine;
                    break;
                }
            }
        }

        if (savings != null) {
            JOptionPane.showMessageDialog(
                null,
                "Your Account Balance is " + savings + " ",
                "Your Savings",
                JOptionPane.PLAIN_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No details Found for account " + accountNumber + "! Sorry 🙏",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    static String generateNewAccountNumber() throws IOException {
        long lastNumber = 0;
        try (
            BufferedReader reader = new BufferedReader(
                new FileReader("lib/AccountList.txt")
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    lastNumber = Math.max(lastNumber, Long.parseLong(line));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid account No. found: " + line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error reading account list: " + e.getMessage(),
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        return String.format("%010d", lastNumber + 1);
    }

    static void addAccount() throws IOException {
        String accountNo = generateNewAccountNumber();

        JOptionPane.showMessageDialog(
            null,
            "Your Account Number for the new Account is " + accountNo,
            "Welcome new Member",
            JOptionPane.PLAIN_MESSAGE
        );
        JOptionPane.showMessageDialog(
            null,
            "Please Remeber your account number is " + accountNo,
            "Be Responsiable",
            JOptionPane.WARNING_MESSAGE
        );

        String name;
        do {
            name = JOptionPane.showInputDialog("Enter Your Name :-");
            if (name == null) {
                int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to continue Account Creation ?",
                    "Cancel Conformation",
                    JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Creation Cancelled",
                        "Cancelled",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            } else if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Name cannot be empty",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } while (name == null || name.trim().isEmpty());

        String dob = JOptionPane.showInputDialog(
            "Enter your Date of Birth [DD/MM/YYYY]"
        );
        while (!dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(
                null,
                "Invalid date format. Please use DD/MM/YYYY",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
            dob = JOptionPane.showInputDialog(
                "Enter your Date of Birth [DD/MM/YYYY]"
            );
        }

        String address = JOptionPane.showInputDialog("Enter your address :-");

        String saving = JOptionPane.showInputDialog(
            "Enter the Amount of 1st Deposit "
        );

        while (!saving.matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(
                null,
                "Invalid amount. Please enter a valid number",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
            saving = JOptionPane.showInputDialog(
                "Enter the Amount of 1st Deposit "
            );
        }

        try (
            PrintWriter accp = new PrintWriter(
                new FileWriter("lib/AccountList.txt", true)
            );
            PrintWriter namep = new PrintWriter(
                new FileWriter("lib/name.txt", true)
            );
            PrintWriter dobp = new PrintWriter(
                new FileWriter("lib/dob.txt", true)
            );
            PrintWriter addressp = new PrintWriter(
                new FileWriter("lib/address.txt", true)
            );
            PrintWriter savingp = new PrintWriter(
                new FileWriter("lib/savingsList.txt", true)
            )
        ) {
            accp.println(accountNo);
            namep.println(name);
            dobp.println(dob);
            addressp.println(address);
            savingp.println(saving);
        }
    }
}
