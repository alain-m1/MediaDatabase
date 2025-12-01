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

        Boolean validChoice = false;
        int choice = 0;
        while(!validChoice) {
            System.out.println("Do you want to login or create an account?");
            System.out.println("1: Login");
            System.out.println("2: Create account");

            choice = 0;
            try {
                choice = scanner.nextInt();

                if (choice < 1 || choice > 2) {
                    System.err.println("Error: Must choose either 1 or 2.");
                } else {
                    validChoice = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer");
            }
        }

        boolean adminFlag = false;
        String username = "";
        switch(choice) {
            case 1: {// login
                boolean loggedIn = false;
                while (!loggedIn) {
                    System.out.println("Are you a user or admin?");
                    System.out.println("1: User");
                    System.out.println("2: Admin");
                    int userType = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter your username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter your password: ");
                    String userPassword = scanner.nextLine();

                    // create resources
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

                        // Print results
                        if (rs.next()) {
                            System.out.println("You have successfully logged into your account.");
                            loggedIn = true;
                        } else {
                            System.out.println("Your username or password is incorrect. Try again.");
                        }

                        // while loop (actually method call)
                        // main menu options
                        // 1
                        // 2
                        // 0 to quit program : break (goes to finally block)
                        if (!adminFlag) {
                            System.out.println("This is a user menu");
                        } else {
                            System.out.println("This is a admin menu");
                        }


                    } catch (SQLException e) {
                        System.err.println("Login Error: " + e.getMessage());
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
                }
                break;
            }
            case 2: {// Create user account
                boolean created = false;
                while (!created) {
                    scanner.nextLine();
                    System.out.println("Enter a username");
                    username = scanner.nextLine();

                    // create resources
                    Connection conn = null;
                    PreparedStatement pstmt = null;
                    PreparedStatement addUser = null;
                    ResultSet rs = null;

                    try {
                        // Make connection with Database
                        conn = DriverManager.getConnection(url, user, password);

                        String sql = "SELECT username " +
                                "FROM USERS " +
                                "WHERE username = ?";

                        // Create prepared statement
                        pstmt = conn.prepareStatement(sql);

                        // Set parameter
                        pstmt.setString(1, username);

                        // Execute the query
                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            System.out.println("That username is taken. Try again");
                            created = false;
                        } else {
                            System.out.println("Enter a password");
                            String userPassword = scanner.nextLine();
                            System.out.println("Enter your full name");
                            String userFullName = scanner.nextLine();

                            String addUserSql = "INSERT INTO USERS (Username, Name, Password) " +
                                    "VALUES (?, ?, ?)";
                            addUser = conn.prepareStatement(addUserSql);

                            addUser.setString(1, username);
                            addUser.setString(2, userFullName);
                            addUser.setString(3, userPassword);

                            int rowsAffected = addUser.executeUpdate();
                            // conn.commit();

                            if (rowsAffected > 0) {
                                System.out.println("Account created successfully.");
                                created = true;
                            } else {
                                System.out.println("Something went wrong while creating the user account");
                            }
                        }

                        System.out.println("This is a user menu");


                        // Display the user main menu
                    } catch (SQLException e) {
                        System.err.println("Account Creation Error: " + e.getMessage());
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
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                        if (addUser != null) {
                            try {
                                addUser.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
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
                }
                break;
            }
        }

    }
}
