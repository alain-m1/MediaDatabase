import java.sql.*;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MediaDatabase {

    public static void main(String[] args) throws SQLException {
        if (args.length < 4) {
            System.err.println("Error: Did not include all necessary arguments.");
            System.err.println("Usage (choose from below):");
            System.err.println("Enter: java Main <url> <user> <pwd> <driver>");
            System.exit(1);
        }

        String url = args[0];
        String user = args[1];
        String password = args[2];
        String driver = args[3];

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Did not find the driver you are referencing");
            e.printStackTrace();
            System.exit(1);
        }

        MediaDatabase mdb = new MediaDatabase();

        // login or register
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to login or create an account?");
        System.out.println("1: Login");
        System.out.println("2: Create account");

        int choice = 0;
        boolean adminFlag = false;
        try{
            choice = scanner.nextInt();
        } catch(InputMismatchException e) {
            System.out.println("Input must be an integer");
        }

        String username = "";
        switch(choice){
            case 1:
                boolean loggedIn = false;
                while(!loggedIn) {
                    System.out.println("Are you a user or admin?");
                    System.out.println("1: User");
                    System.out.println("2: Admin");
                    int userType = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter your username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter your password: ");
                    String userPassword = scanner.nextLine();

                    Connection conn = null;
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;

                    try {
                        // Make connection with Database
                        conn = DriverManager.getConnection(url, user, password);

                        // query to join EMP and DEPT tables
                        String sql = "";
                        if (userType == 1) {
                            sql = "SELECT u.username, u.password " +
                                    "FROM USERS u " +
                                    "WHERE u.username = ? AND u.password = ?";
                            pstmt = conn.prepareStatement(sql);
                            adminFlag = false;
                        } else {
                            sql = "SELECT a.username, a.password " +
                                    "FROM ADMIN a " +
                                    "WHERE a.username = ? AND a.password = ?";
                            pstmt = conn.prepareStatement(sql);
                            adminFlag = true;
                        }
                        // Create prepared statement
                        pstmt = conn.prepareStatement(sql);

                        // Set parameters
                        pstmt.setString(1, username);
                        pstmt.setString(2, userPassword);


                        // Execute the query
                        rs = pstmt.executeQuery();

                        // Print attribute names
                        System.out.printf("%-10s %-15s\n", "Username", "Password");

                        // Print results
                        if (rs.next()) {
                            System.out.println("You have successfully logged into your account.");
                            loggedIn = true;
                        } else {
                            System.out.println("Your username or password is incorrect. Try again.");
                        }


                    } catch (SQLException e) {
                        System.err.println("SQL Error: " + e.getMessage());
                        throw e;
                    } finally {
                        // Close resources properly
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (pstmt != null) {
                            try {
                                pstmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing Statement: " + e.getMessage());
                            }
                        }
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing Connection: " + e.getMessage());
                            }
                        }
                    }
                    break;
                }
            case 2:
                System.out.println("Enter a username");
                System.out.println("Enter a password");
        }


        /*Boolean done = false;
        while(!done){// Main menu

        }*/
    }


}
