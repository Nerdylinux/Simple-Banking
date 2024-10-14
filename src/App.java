import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            System.out.println(
                "An unexpected error occurred: " + e.getMessage()
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
            );
            BufferedReader passwordReader = new BufferedReader(
                new FileReader("lib/Passwords.txt")
            );
        ) {
            String accountLine;
            String savingsLine;
            String passwordLine;
            String passwordEntry;
            try {
                while (
                    (accountLine = accountReader.readLine()) != null &&
                    (savingsLine = savingsReader.readLine()) != null &&
                    (passwordLine = passwordReader.readLine()) != null
                ) {
                    if (accountLine.equals(accountNumber)) {
                        for (int attempts = 3; attempts > 0; attempts--) {
                            passwordEntry = JOptionPane.showInputDialog(
                                "Enter Your Password (Attempts left: " +
                                attempts +
                                "):"
                            );
                            if (passwordEntry == null) {
                                return;
                            }
                            if (
                                passwordLine.equals(
                                    passwordEncrpyter(passwordEntry)
                                )
                            ) {
                                savings = savingsLine;
                                break;
                            } else {
                                JOptionPane.showMessageDialog(
                                    null,
                                    "Wrong Password! Please try again.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            System.out.println("Unexpected error occured :" + e.getMessage());
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

        String passString = "";
        try {
            passString = passwordEncrpyter(passwordValidator());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            );
            PrintWriter passwordp = new PrintWriter(
                new FileWriter("lib/Passwords.txt", true)
            );
        ) {
            accp.println(accountNo);
            namep.println(name);
            dobp.println(dob);
            addressp.println(address);
            savingp.println(saving);
            passwordp.println(passString);
        }
    }

    static String passwordValidator() {
        String passcode;
        final int NO_OF_UPPERCASE = 1;
        final int NO_OF_LOWERCASE = 1;
        final int NO_OF_DIGITS = 1;
        final int NO_OF_SPECIAL = 1;
        final int MAX_LENGTH = 30;
        final int MIN_LENGTH = 8;
        final int SPACE_ERROR = 0;
        int lowerCount = 0;
        int digitCount = 0;
        int lenghtCount = 0;
        int upperCount = 0;
        int specialCount = 0;
        int spaceCount = 0;
        char ch;

        do {
            lowerCount = 0;
            digitCount = 0;
            lenghtCount = 0;
            upperCount = 0;
            specialCount = 0;
            spaceCount = 0;

            passcode = JOptionPane.showInputDialog("Enter your new password");
            passcode = passcode.trim();
            lenghtCount = passcode.length();
            for (int i = 0; i < lenghtCount; i++) {
                ch = passcode.charAt(i);
                if (Character.isUpperCase(ch)) upperCount++;
                if (Character.isLowerCase(ch)) lowerCount++;
                if (Character.isDigit(ch)) digitCount++;
                if (
                    !Character.isLetterOrDigit(ch) &&
                    !Character.isWhitespace(ch)
                ) specialCount++;
                if (Character.isWhitespace(ch)) spaceCount++;
            }
            if (
                upperCount == NO_OF_UPPERCASE &&
                lowerCount == NO_OF_LOWERCASE &&
                digitCount == NO_OF_DIGITS &&
                specialCount == NO_OF_SPECIAL &&
                lenghtCount >= MIN_LENGTH &&
                lenghtCount <= MAX_LENGTH &&
                spaceCount == SPACE_ERROR
            ) {
                break;
            } else {
                if (upperCount < NO_OF_UPPERCASE) JOptionPane.showMessageDialog(
                    null,
                    "Password needs to have atleast 1 Uppercase Letter",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
                if (lowerCount < NO_OF_LOWERCASE) JOptionPane.showMessageDialog(
                    null,
                    "Password needs to have atleast 1 Lowercase Letter",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
                if (digitCount < NO_OF_DIGITS) JOptionPane.showMessageDialog(
                    null,
                    "Password needs to have atleast 1 Number",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
                if (specialCount < NO_OF_SPECIAL) JOptionPane.showMessageDialog(
                    null,
                    "Password needs to have atleast 1 Special Character (e.g.- @ , - , _ , etc)",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
                if (
                    lenghtCount > MAX_LENGTH || lenghtCount < MIN_LENGTH
                ) JOptionPane.showMessageDialog(
                    null,
                    "Password needs to be between 8 to 30 characters",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
                if (spaceCount != SPACE_ERROR) JOptionPane.showMessageDialog(
                    null,
                    "Password must not contain any blank spaces",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } while (
            upperCount < NO_OF_UPPERCASE ||
            lowerCount < NO_OF_LOWERCASE ||
            digitCount < NO_OF_DIGITS ||
            specialCount < NO_OF_SPECIAL ||
            lenghtCount < MIN_LENGTH ||
            lenghtCount > MAX_LENGTH ||
            spaceCount != SPACE_ERROR
        );

        return passcode;
    }

    static String passwordEncrpyter(String s) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(s.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
