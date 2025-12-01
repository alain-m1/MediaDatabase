import java.sql.*;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MediaDatabase {
    public static Scanner scanner = new Scanner(System.in);

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

        System.out.println("Do you want to login or create an account?");
        System.out.println("1: Login");
        System.out.println("2: Create account");
        int choice = getValidInteger(1, 2);

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
                            // Menu method calls
                            if (!adminFlag) {
                                mdb.loadUserMainMenu(conn, username);
                            } else {
                                mdb.loadAdminMainMenu(conn, username);
                            }
                        } else {
                            System.out.println("Your username or password is incorrect. Try again.");
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

                        mdb.loadUserMainMenu(conn, username);


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

    public static int getValidInteger(int min, int max) {
        boolean validInput = false;
        while (!validInput) {
            int choice = 0;
            try {
                choice = scanner.nextInt();

                if (choice < min || choice > max) {
                    System.out.println("Invalid choice. Choose a number between " + min + " and " + max + ".");
                } else {
                    validInput = true;
                    return choice;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer.");
            }
        }
        return 0;
    }

    /*
    * The user main menu will display the following options
    * 1. View shows
    * 2. View movies
    * 3. View playlists
    * 4. Request media
    * */
    public void loadUserMainMenu(Connection conn, String username) throws SQLException {
        boolean exitFlag = false;
        while(!exitFlag) {
            System.out.println("==== Main Menu ====");
            System.out.println("1: View Shows\n" +
                    "2: View Movies\n" +
                    "3: View Playlists\n" +
                    "4: Request Media\n" +
                    "0: Quit");

            int choice = getValidInteger(0, 4);
            switch (choice) {
                case 0: {
                    System.out.println("Goodbye!");
                    break;
                }
                case 1: {
                    loadUserShows();
                    break;
                }
                case 2: {
                    loadUserMovies(conn);
                    break;
                }
                case 3: {
                    viewPlaylists();
                    break;
                }
                case 4: {
                    addMediaRequest();
                    break;
                }
            }
        }
    }

    public void loadAdminMainMenu(Connection conn, String username) throws SQLException {
        boolean exitFlag = false;
        while(!exitFlag) {
            System.out.println("==== Main Menu ====");
            System.out.println("1. View Shows\n" +
                    "2. View Movies\n" +
                    "3: Edit Media\n" +
                    "4: Review Media Request\n" +
                    "0: Quit");

            int choice = getValidInteger(0, 3);
            switch (choice) {
                case 0: {
                    System.out.println("Goodbye!");
                    break;
                }
                case 1: {
                    loadUserShows();
                    break;
                }
                case 2: {
                    loadUserMovies(conn);
                    break;
                }
                case 3: {
                    editMedia();
                    break;
                }
                case 4: {
                    reviewMediaRequest();
                    break;
                }
            }
        }
    }

    public void loadUserShows() {
        boolean backFlag = false;
        while(!backFlag){
            System.out.println("==== Shows ====");
            System.out.println("1. View All\n" +
                    "2. Filter By Year\n" +
                    "3. Filter By Genre\n" +
                    "4. Filter by director\n" +
                    "0. Back");
            int choice = getValidInteger(0, 4);
            switch (choice) {
                case 0: {
                    System.out.println("Going back to main menu.");
                    backFlag = true;
                    break;
                }
                case 1: {
                    // Display all movies
                    System.out.println("Displaying all movies:\n");

                    System.out.printf("%-10s %6-s $-10s");
                    break;
                }
            }
        }
    }

    /* The following method will display all the available shows for the user.
    * The user will have the following softing options:
    * 1. View All
    * 2. Filter By Year
    * 3. Filter By Genre
    * 4. Filter by director*/
    public void loadUserMovies(Connection conn) throws SQLException {
        boolean backFlag = false;
        while(!backFlag){
            System.out.println("==== Movies ====");
            System.out.println("1. View All\n" +
                    "2. Filter By Year\n" +
                    "3. Filter By Genre\n" +
                    "4. Filter by director\n" +
                    "0. Back");
            int choice = getValidInteger(0, 4);
            switch (choice) {
                case 0: {
                    System.out.println("Going back to main menu.");
                    backFlag = true;
                    break;
                }
                case 1: {
                    // Display all movies

                    Statement moviesStmt = null;
                    ResultSet rs = null;

                    String moviesSql = "SELECT med.Title, med.Year, mov.MPA_Rating,  d.name, med.Description " +
                            "FROM MOVIE mov, MEDIA med, DIRECTOR d " +
                            "WHERE mov.Title=med.Title AND mov.Year=med.Year AND med.dID=d.dID";

                    try {
                        moviesStmt = conn.createStatement();

                        rs = moviesStmt.executeQuery(moviesSql);

                        System.out.println("Displaying all movies:\n");
                        System.out.printf("%-14s %-5s %-6s %-14s %-33s\n", "Title", "Year", "MPA", "Director", "Description");

                        while(rs.next()){
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String mpa = rs.getString("MPA_Rating");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            //String truncated = description.length() > 30 ? description.substring(0, 30) + "..." : description;
                            System.out.printf("%-14s %-5s %-6s %-14s %-30.30s...\n", title, year, mpa, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Movies Error: " + e.getMessage());
                        throw e;
                    } finally {

                    }
                    break;
                }
            }
        }
    }

    public void viewPlaylists() {

    }

    public void addMediaRequest() {

    }

    public void editMedia() {

    }

    public void reviewMediaRequest() {

    }
}
