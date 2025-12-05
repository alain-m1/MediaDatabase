import javax.print.attribute.standard.Media;
import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class MediaDatabase {
    public static Scanner scanner = new Scanner(System.in);
    public static ArrayList<MediaRequest> mediaRequests = new ArrayList<>();

    public static void main(String[] args) throws SQLException {
        if (args.length < 4) {
            System.err.println("\nError: Did not include all necessary arguments.");
            System.err.println("Usage:");
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
            System.err.println("\nError: Did not find the driver you are referencing");
            e.printStackTrace();
            System.exit(1);
        }

        MediaDatabase mdb = new MediaDatabase();
        boolean exitFlag = false;
        while(!exitFlag) {
            System.out.println("\nDo you want to login or create an account?");
            System.out.println("1: Login");
            System.out.println("2: Create account");
            int choice = getValidInteger(1, 2);

            boolean adminFlag = false;
            String username = "";
            switch (choice) {
                case 1: {// login
                    boolean loggedIn = false;
                    while (!loggedIn) {
                        System.out.println("\nAre you a user or admin?");
                        System.out.println("1: User");
                        System.out.println("2: Admin");
                        int userType = getValidInteger(1, 2);
                        System.out.println("\nEnter your username: ");
                        username = scanner.nextLine();
                        System.out.println("\nEnter your password: ");
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
                                System.out.println("\nHello, " + username + ". You have successfully logged into your account.");
                                loggedIn = true;
                                // Menu method calls
                                if (!adminFlag) {
                                    exitFlag = mdb.loadUserMainMenu(conn, username);
                                } else {
                                    exitFlag = mdb.loadAdminMainMenu(conn, username);
                                }
                            } else {
                                System.out.println("\nYour username or password is incorrect. Try again.");
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
                    }// End of while loop
                    break;
                }
                case 2: {// Create user account
                    boolean created = false;
                    while (!created) {
                        System.out.println("\nEnter a username");
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
                                System.out.println("\nThat username is taken. Try again");
                                created = false;
                            } else {
                                System.out.println("\nEnter a password");
                                String userPassword = scanner.nextLine();
                                System.out.println("\nEnter your full name");
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
                                    System.out.println("\nHello, " + username + ". Your account was created successfully.");
                                    created = true;
                                    exitFlag = mdb.loadUserMainMenu(conn, username);
                                } else {
                                    System.out.println("\nSomething went wrong while creating the user account");
                                }
                            }


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
                    }// End of while loop
                    break;
                }
            }
        }
    }

    public static int getValidInteger(int min, int max) {
        boolean validInput = false;
        while (!validInput) {
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());

                if (choice < min || choice > max) {
                    System.out.println("\nInvalid choice. Choose a number between " + min + " and " + max + ".");
                } else {
                    validInput = true;
                    return choice;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInput must be an integer between " + min + " and " + max + ".");
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
    public boolean loadUserMainMenu(Connection conn, String username) throws SQLException {
        boolean exitFlag = false;
        boolean exitType = false;
        while(!exitFlag) {
            System.out.println("\n==== Main Menu ====  (Enter 0 to quit)");
            System.out.println("1: View Shows\n" +
                    "2: View Movies\n" +
                    "3: View Playlists\n" +
                    "4: Request Media\n" +
                    "5: Logout\n" +
                    "0: Quit");

            int choice = getValidInteger(0, 5);
            switch (choice) {
                case 0: {
                    System.out.println("\nGoodbye!");
                    exitFlag = true;
                    exitType = true;
                    break;
                }
                case 1: {
                    loadUserShows(conn);
                    break;
                }
                case 2: {
                    loadUserMovies(conn);
                    break;
                }
                case 3: {
                    viewPlaylists(conn, username);
                    break;
                }
                case 4: {
                    addMediaRequest();
                    break;
                }
                case 5: {
                    exitFlag = true;
                    exitType = false;
                    break;
                }
            }
        }
        return exitType;
    }

    public boolean loadAdminMainMenu(Connection conn, String username) throws SQLException {
        boolean exitFlag = false;
        boolean exitType = false;
        while(!exitFlag) {
            System.out.println("\n==== Main Menu ====  (Enter 0 to quit)");
            System.out.println("1: View Shows\n" +
                    "2. View Movies\n" +
                    "3: Edit Media\n" +
                    "4: Review Media Request\n" +
                    "5: Logout\n" +
                    "0: Quit");

            int choice = getValidInteger(0, 5);
            switch (choice) {
                case 0: {
                    System.out.println("\nGoodbye!");
                    exitFlag = true;
                    exitType = true;
                    break;
                }
                case 1: {
                    loadUserShows(conn);
                    break;
                }
                case 2: {
                    loadUserMovies(conn);
                    break;
                }
                case 3: {
                    editMedia(conn);
                    break;
                }
                case 4: {
                    reviewMediaRequest(conn);
                    break;
                }
                case 5: {// logout
                    exitFlag = true;
                    exitType = false;
                    break;
                }
            }
        }
        return exitType;
    }

    public void loadUserShows(Connection conn) throws SQLException {
        boolean backFlag = false;
        while(!backFlag){
            System.out.println("\n==== Shows ====  (Enter 0 to go back to Main Menu)");
            System.out.println("1: View All\n" +
                    "2: Filter By Year\n" +
                    "3: Filter By Genre\n" +
                    "4: Filter by director\n" +
                    "0: Back");
            int choice = getValidInteger(0, 4);
            switch (choice) {
                case 0: {
                    System.out.println("\nGoing back to main menu.");
                    backFlag = true;
                    break;
                }
                case 1: {
                    // Display all shows

                    Statement showsStmt = null;
                    ResultSet rs = null;

                    String showsSql = "SELECT med.Title, med.Year, d.name, med.Description " +
                            "FROM `SHOW` sho, MEDIA med, DIRECTOR d " +
                            "WHERE sho.Title=med.Title AND sho.Year=med.Year AND med.dID=d.dID";

                    try {
                        showsStmt = conn.createStatement();

                        rs = showsStmt.executeQuery(showsSql);

                        System.out.println("\nDisplaying all shows:\n");
                        System.out.printf("%-14s %-5s %-20s %-33s\n", "Title", "Year", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            //String truncated = description.length() > 30 ? description.substring(0, 30) + "..." : description;
                            System.out.printf("%-14s %-5s %-20s %-30.30s...\n", title, year, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Shows Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (showsStmt != null) {
                            try {
                                showsStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    // Display shows based on year
                    System.out.println("\nWhat year would you like to filter by?");
                    int year = getValidInteger(0, 2026);

                    PreparedStatement showsByYearStmt = null;
                    ResultSet rs = null;

                    String showsByYearSql = "SELECT med.Title, med.Year, d.name, med.Description " +
                            "FROM `SHOW` sho, MEDIA med, DIRECTOR d " +
                            "WHERE sho.Title=med.Title AND sho.Year=med.Year AND med.dID=d.dID AND med.Year=?";

                    try {
                        showsByYearStmt = conn.prepareStatement(showsByYearSql);

                        showsByYearStmt.setInt(1, year);
                        rs = showsByYearStmt.executeQuery();

                        System.out.println("\nDisplaying all shows from the year " +
                                year + ":\n");
                        System.out.printf("%-14s %-5s %-20s %-33.33s\n", "Title", "Year", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int yearOutput = rs.getInt("Year");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            //String truncated = description.length() > 30 ? description.substring(0, 30) + "..." : description;
                            System.out.printf("%-14s %-5s %-20s %-30.30s...\n", title, yearOutput, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Shows Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (showsByYearStmt != null) {
                            try {
                                showsByYearStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    // Display all shows filtered by Genre

                    System.out.println("\nWhat genre would you like to filter shows by: ");
                    String showGenre = scanner.nextLine();

                    PreparedStatement showsByGenreStmt = null;
                    ResultSet rs = null;

                    String showsByGenreSql = "SELECT med.Title, med.Year, d.name, med.Description " +
                            "FROM `SHOW` sho, MEDIA med, DIRECTOR d, GENRE g " +
                            "WHERE sho.Title=med.Title AND sho.Year=med.Year AND med.dID=d.dID AND LOWER(g.Genre)=LOWER(?) AND g.Year=sho.Year AND g.Title=sho.Title";

                    try {
                        showsByGenreStmt = conn.prepareStatement(showsByGenreSql);

                        showsByGenreStmt.setString(1, showGenre);
                        rs = showsByGenreStmt.executeQuery();

                        System.out.println("\nDisplaying shows filtered by Genre:\n");
                        System.out.printf("%-14s %-5s %-20s %-33s\n", "Title", "Year", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            System.out.printf("%-14s %-5s %-20s %-30.30s...\n", title, year, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Shows by Genre Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (showsByGenreStmt != null) {
                            try {
                                showsByGenreStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }
                    break;
                }
                //Filter by Director
                case 4: {
                    System.out.println("What Director's shows would you like to view?");
                    String input = scanner.nextLine();

                    PreparedStatement showsbyDirStmt = null;
                    ResultSet rs = null;

                    String showsByDirSql = "SELECT med.Title, med.Year, d.name, med.Description " +
                            "FROM `SHOW` sho, MEDIA med, DIRECTOR d " +
                            "WHERE sho.Title=med.Title AND sho.Year=med.Year AND med.dID=d.dID AND LOWER(d.NAME) = LOWER(?)";

                    try {
                        showsbyDirStmt = conn.prepareStatement(showsByDirSql);
                        showsbyDirStmt.setString(1, input);

                        rs = showsbyDirStmt.executeQuery();

                        System.out.println("\nDisplaying Shows Filtered by Director " + input + ":\n");
                        System.out.printf("%-14s %-5s %-20s %-33.30s\n", "Title", "Year", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            System.out.printf("%-14s %-5s %-20s %-30.30s...\n", title, year, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Shows by Director Error: " + e.getMessage());
                        throw e;
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        } catch (SQLException e) {
                            System.err.println("Error when closing ResultSet: " + e.getMessage());
                        }
                        if (showsbyDirStmt != null) {
                            try {
                                showsbyDirStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }

                    }
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
            System.out.println("\n==== Movies ====  (Enter 0 to go back to Main Menu)");
            System.out.println("1: View All\n" +
                    "2: Filter By Year\n" +
                    "3: Filter By Genre\n" +
                    "4: Filter by director\n" +
                    "0: Back");
            int choice = getValidInteger(0, 4);
            switch (choice) {
                case 0: {
                    System.out.println("\nGoing back to main menu.");
                    backFlag = true;
                    break;
                }
                case 1: {
                    // Display all movies

                    Statement moviesStmt = null;
                    ResultSet rs = null;

                    String moviesSql = "SELECT med.Title, med.Year, mov.MPA_Rating, d.name, med.Description " +
                            "FROM MOVIE mov, MEDIA med, DIRECTOR d " +
                            "WHERE mov.Title=med.Title AND mov.Year=med.Year AND med.dID=d.dID";

                    try {
                        moviesStmt = conn.createStatement();

                        rs = moviesStmt.executeQuery(moviesSql);

                        System.out.println("\nDisplaying all movies:\n");
                        System.out.printf("%-14s %-5s %-6s %-20s %-33s\n", "Title", "Year", "MPA", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String mpa = rs.getString("MPA_Rating");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            System.out.printf("%-14s %-5s %-6s %-20s %-30.30s...\n", title, year, mpa, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Movies Error: " + e.getMessage());
                        throw e;
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        } catch (SQLException e) {
                            System.err.println("Error when closing ResultSet: " + e.getMessage());
                        }
                        if (moviesStmt != null) {
                            try {
                                moviesStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }

                    }
                    break;
                }
                //Filter by Year
                case 2: {
                    // Display movies based on year
                    System.out.println("\nWhat year would you like to filter by?");
                    int year = getValidInteger(0, 2026);

                    PreparedStatement moviesByYearStmt = null;
                    ResultSet rs = null;

                    String moviesByYearSql = "SELECT med.Title, med.Year, mov.MPA_Rating, d.name, med.Description " +
                            "FROM MOVIE mov, MEDIA med, DIRECTOR d " +
                            "WHERE mov.Title=med.Title AND mov.Year=med.Year AND med.dID=d.dID AND med.Year=?";

                    try {
                        moviesByYearStmt = conn.prepareStatement(moviesByYearSql);

                        moviesByYearStmt.setInt(1, year);
                        rs = moviesByYearStmt.executeQuery();

                        System.out.println("\nDisplaying all movies from the year " +
                                year + ":\n");
                        System.out.printf("%-14s %-5s %-6s %-20s %-33.33s...\n", "Title", "Year", "MPA", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int yearOutput = rs.getInt("Year");
                            String mpa = rs.getString("MPA_Rating");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            System.out.printf("%-14s %-5s %-6s %-20s %-30.30s...\n", title, year, mpa, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Movies Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (moviesByYearStmt != null) {
                            try {
                                moviesByYearStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    // Display all movies filtered by Genre

                    System.out.println("\nWhat genre would you like to filter movies by: ");
                    String movieGenre = scanner.nextLine();

                    PreparedStatement moviesByGenreStmt = null;
                    ResultSet rs = null;

                    String moviesByGenreSql = "SELECT med.Title, med.Year, mov.MPA_Rating, d.name, med.Description " +
                            "FROM MOVIE mov, MEDIA med, DIRECTOR d, GENRE g " +
                            "WHERE mov.Title=med.Title AND mov.Year=med.Year AND med.dID=d.dID AND LOWER(g.Genre)=LOWER(?) AND g.Year=mov.Year AND g.Title=mov.Title";

                    try {
                        moviesByGenreStmt = conn.prepareStatement(moviesByGenreSql);

                        moviesByGenreStmt.setString(1, movieGenre);
                        rs = moviesByGenreStmt.executeQuery();

                        System.out.println("\nDisplaying movies filtered by Genre:\n");
                        System.out.printf("%-14s %-5s %-6s %-20s %-33.30s\n", "Title", "Year", "MPA", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String mpa = rs.getString("MPA_Rating");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            System.out.printf("%-14s %-5s %-6s %-20s %-30.30s...\n", title, year, mpa, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Movies by Genre Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (moviesByGenreStmt != null) {
                            try {
                                moviesByGenreStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }
                    break;
                }
                //Filter by Director
                case 4: {
                    System.out.println("What Director's movies would you like to view?");
                    String input = scanner.nextLine();

                    PreparedStatement moviesbyDirStmt = null;
                    ResultSet rs = null;

                    String moviesByDirSql = "SELECT med.Title, med.Year, mov.MPA_Rating, d.name, med.Description " +
                            "FROM MOVIE mov, MEDIA med, DIRECTOR d " +
                            "WHERE mov.Title=med.Title AND mov.Year=med.Year AND med.dID=d.dID AND LOWER(d.NAME) = LOWER(?)";

                    try {
                        moviesbyDirStmt = conn.prepareStatement(moviesByDirSql);
                        moviesbyDirStmt.setString(1, input);

                        rs = moviesbyDirStmt.executeQuery();

                        System.out.println("\nDisplaying Movies Filtered by Director" + input + ":\n");
                        System.out.printf("%-14s %-5s %-6s %-20s %-33.30s...\n", "Title", "Year", "MPA", "Director", "Description");

                        while (rs.next()) {
                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String mpa = rs.getString("MPA_Rating");
                            String director = rs.getString("Name");
                            String description = rs.getString("Description");
                            //String truncated = description.length() > 30 ? description.substring(0, 30) + "..." : description;
                            System.out.printf("%-14s %-5s %-6s %-20s %-30.30s...\n", title, year, mpa, director, description);
                        }
                    } catch (SQLException e) {
                        System.err.println("Display Movies by Director Error: " + e.getMessage());
                        throw e;
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        } catch (SQLException e) {
                            System.err.println("Error when closing ResultSet: " + e.getMessage());
                        }
                        if (moviesbyDirStmt != null) {
                            try {
                                moviesbyDirStmt.close();
                            } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }

                    }
                    break;
                }

            }
        }
    }

    public void viewPlaylists(Connection conn, String username) throws SQLException {
        boolean backFlag = false;

        while (!backFlag) {
            System.out.println("\n==== Playlists ==== (Enter 0 to go back)");
            System.out.println("1: View all playlists");
            System.out.println("2: View a playlist's contents");
            System.out.println("3: Create a new playlist");
            System.out.println("4: Edit a playlist");
            System.out.println("0: Back");

            int choice = getValidInteger(0, 4);

            switch (choice) {

                case 0: {
                    System.out.println("\nGoing back to main menu.");
                    backFlag = true;
                    break;
                }

                case 1: {
                    // list all the playlists a user has
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;

                    String sql = "SELECT PlaylistName, COUNT(*) AS itemCount " +
                            "FROM USER_PLAYLIST " +
                            "WHERE Username = ? " +
                            "GROUP BY PlaylistName";

                    try {
                        // prepare the statement and insert the username value
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, username);

                        rs = pstmt.executeQuery();
                        System.out.println("\nYour playlists:\n");
                        System.out.printf("%-25s %-10s%n", "PlaylistName", "Items");

                        boolean any = false;
                        while (rs.next()) {
                            any =  true;
                            String playlistName = rs.getString("PlaylistName");
                            int itemCount = rs.getInt("itemCount");
                            System.out.printf("%-25s %-10d%n", playlistName, itemCount);
                        }

                        if (!any) {
                            System.out.println("(You don't have any playlists yet.)");
                        }

                    } catch (SQLException e) {
                        System.err.println("View Playlists Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try{ rs.close(); } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if  (pstmt != null) {
                            try{ pstmt.close(); } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }

                    break;
                }

                case 2: {
                    // view all media inside a specific playlist
                    System.out.println("\nEnter the playlist name: ");
                    String playlistName = scanner.nextLine();

                    PreparedStatement pstmt = null;
                    ResultSet rs = null;

                    String sql =
                            "SELECT up.Title, up.Year, d.name AS Director, med.Description " +
                            "FROM USER_PLAYLIST up " +
                            "JOIN MEDIA med ON up.Title = med.Title AND up.Year = med.Year " +
                            "LEFT JOIN DIRECTOR d ON med.dID = d.dID " +
                            "WHERE up.Username = ? AND up.PlaylistName = ?";

                    try {
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, username); // filter by user
                        pstmt.setString(2, playlistName); // filter by playlist name

                        rs =  pstmt.executeQuery();
                        System.out.println("\nPlaylist: " + playlistName + "\n");
                        System.out.printf("%-14s %-5s %-20s %-33s%n",
                                "Title", "Year", "Director", "Description");

                        boolean any = false;
                        while (rs.next()) {
                            any =  true;

                            String title = rs.getString("Title");
                            int year = rs.getInt("Year");
                            String director = rs.getString("Director");
                            String description = rs.getString("Description");

                            System.out.printf("%-14s %-5d %-20s %-30.30s...%n",
                                    title,
                                    year,
                                    director == null ? "" : director,
                                    description == null ? "" : description);
                        }

                        if (!any) {
                            System.out.println("(No items found in this playlist.)");
                        }

                    } catch (SQLException e) {
                        System.err.println("View Playlist Contents Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try { rs.close(); } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (pstmt != null) {
                            try { pstmt.close(); } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }

                    break;
                }

                case 3: {
                    System.out.println("\n==== Create a new playlist ====");
                    
                    System.out.println("Enter a name for your new playlist: ");
                    String newPlaylistName = scanner.nextLine();
                    if (newPlaylistName.isEmpty()) {
                        System.out.println("Playlist name cannot be empty.");
                        break;
                    }

                    System.out.println("Enter the title of the playlists first media addition: ");
                    String newMediaTitle = scanner.nextLine();

                    System.out.println("Enter the year of the playlists first media addition: ");
                    String newMediaYear = scanner.nextLine();

                    PreparedStatement newPlaylistStmt = null;
                    ResultSet rs = null;

                    String newPlaylistSql = "SELECT COUNT(*) AS cnt FROM USER_PLAYLIST " +
                            "WHERE Username=? AND PlaylistName=?";

                    try {
                        newPlaylistStmt = conn.prepareStatement(newPlaylistSql);
                        newPlaylistStmt.setString(1, username);
                        newPlaylistStmt.setString(2, newMediaTitle);
                        rs =  newPlaylistStmt.executeQuery();

                        if (rs.next() && rs.getInt("cnt") > 0) {
                            System.out.println("Playlist of that name already exists.");
                            break;
                        }

                        rs.close();
                        newPlaylistStmt.close();

                        String insertPlaylistSql = "INSERT INTO USER_PLAYLIST (Username, Title, Year, PlaylistName) VALUES (?, ?, ?, ?)";

                        newPlaylistStmt = conn.prepareStatement(insertPlaylistSql);
                        newPlaylistStmt.setString(1, username);
                        newPlaylistStmt.setString(2, newMediaTitle);
                        newPlaylistStmt.setString(3, newMediaYear);
                        newPlaylistStmt.setString(4, newPlaylistName);
                        newPlaylistStmt.executeUpdate();

                        System.out.println("\nPlaylist '" + newPlaylistName + "' created successfully!");

                    } catch (SQLException e) {
                        System.err.println("View Playlist Creation Error: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try { rs.close(); } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (newPlaylistStmt != null) {
                            try { newPlaylistStmt.close(); } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }

                    break;
                }

                case 4: {
                    System.out.println("\n=== Edit Playlist ===");

                    PreparedStatement listStmt = null;
                    ResultSet rs = null;

                    String listSql =
                            "SELECT PlaylistName, COUNT(*) AS itemCount " +
                            "FROM USER_PLAYLIST " +
                            "WHERE Username = ? " +
                            "GROUP BY PlaylistName";

                    try {
                        listStmt = conn.prepareStatement(listSql);
                        listStmt.setString(1, username);

                        rs =  listStmt.executeQuery();
                        System.out.println("\nYour playlists:\n");
                        System.out.printf("%-25s %-10s%n", "Playlist Name", "Items");
                        boolean any = false;

                        while (rs.next()) {
                            any =  true;
                            String pName = rs.getString("PlaylistName");
                            int count = rs.getInt("itemCount");
                            System.out.printf("%-25s %-10d%n", pName, count);
                        }

                        if (!any) {
                            System.out.println("(You don't have any playlists to edit)");
                            break;
                        }
                    } catch (SQLException e) {
                        System.err.println("Error listing playlists: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rs != null) {
                            try { rs.close(); } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (listStmt != null) {
                            try { listStmt.close(); } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }

                    System.out.print("\nEnter the playlist name to edit (or 0 to cancel): ");
                    String playlistName = scanner.nextLine();
                    if (playlistName.equals("0")) {
                        break;
                    }

                    // we need to verify that the playlist exists
                    PreparedStatement checkStmt = null;
                    ResultSet rsCheck = null;
                    String checkSql = "SELECT COUNT(*) AS cnt FROM USER_PLAYLIST WHERE Username = ? AND PlaylistName = ?";
                    
                    try {
                        checkStmt = conn.prepareStatement(checkSql);
                        checkStmt.setString(1, username);
                        checkStmt.setString(2, playlistName);

                        rsCheck = checkStmt.executeQuery();
                        if (rsCheck.next() && rsCheck.getInt("cnt") == 0) {
                            System.out.println("No playlist named '" + playlistName + "' found.");
                            break;
                        }
                    } catch (SQLException e) {
                        System.err.println("Error checking playlist: " + e.getMessage());
                        throw e;
                    } finally {
                        if (rsCheck != null) {
                            try { rsCheck.close(); } catch (SQLException e) {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (checkStmt != null) {
                            try { checkStmt.close(); } catch (SQLException e) {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }

                    boolean editing = true;
                    while (editing) {
                        System.out.println("\n=== Editing Playlist: " + playlistName + " ===");
                        System.out.println("1: Add media to playlist");
                        System.out.println("2: Remove media from playlist");
                        System.out.println("0: Back");
                        int editChoice = getValidInteger(0, 2);

                        switch (editChoice) {
                            case 0: {
                                editing = false;
                                break;
                            }

                            case 1: {
                                // Add media
                                String checkMediaSql =
                                        "SELECT Title, Year FROM MEDIA WHERE Title = ? AND Year = ?";
                                String insertSql = "INSERT INTO USER_PLAYLIST (Username, Title, Year, PlaylistName) " +
                                        "VALUES (?, ?, ?, ?)";

                                PreparedStatement checkMediaStmt = null;
                                PreparedStatement insertStmt = null;

                                try {
                                    checkMediaStmt = conn.prepareStatement(checkMediaSql);
                                    insertStmt = conn.prepareStatement(insertSql);

                                    System.out.print("Enter media title to add: ");
                                    String title = scanner.nextLine();

                                    System.out.print("Enter year: ");
                                    int year = getValidInteger(0, 2026);

                                    // check media exists
                                    ResultSet rsMedia = null;
                                    try {
                                        checkMediaStmt.setString(1, title);
                                        checkMediaStmt.setInt(2, year);
                                        rsMedia = checkMediaStmt.executeQuery();

                                        if (!rsMedia.next()) {
                                            System.out.println("Media not found in MEDIA table — cannot add.");
                                            break;
                                        }
                                    } finally {
                                        if (rsMedia != null) {
                                            try { rsMedia.close(); } catch (SQLException e) {
                                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                                            }
                                        }
                                    }

                                    //insert USER_PLAYLIST
                                    insertStmt.setString(1, username);
                                    insertStmt.setString(2, title);
                                    insertStmt.setInt(3, year);
                                    insertStmt.setString(4, playlistName);

                                    try {
                                        insertStmt.executeUpdate();
                                        System.out.println("Added '" + title + "' (" + year +
                                                ") to playlist '" + playlistName + "'.");
                                    } catch (SQLException e) {
                                        System.out.println("Already in this playlist or insert error.");
                                    }

                                } catch (SQLException e) {
                                    System.err.println("Error adding media to playlist: " + e.getMessage());
                                    throw e;
                                } finally {
                                    if (checkMediaStmt != null) {
                                        try { checkMediaStmt.close(); } catch (SQLException e) {
                                            System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                                        }
                                    }
                                    if (insertStmt != null) {
                                        try { insertStmt.close(); } catch (SQLException e) {
                                            System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                                        }
                                    }
                                }

                                break;
                            }

                            case 2: {
                                // remove
                                System.out.print("Enter title to remove: ");
                                String title = scanner.nextLine();

                                System.out.print("Enter year: ");
                                int year = getValidInteger(0, 2026);

                                String deleteSql =
                                        "DELETE FROM USER_PLAYLIST " +
                                                "WHERE Username = ? AND PlaylistName = ? AND Title = ? AND Year = ?";

                                PreparedStatement delStmt = null;

                                try {
                                    delStmt = conn.prepareStatement(deleteSql);
                                    delStmt.setString(1, username);
                                    delStmt.setString(2, playlistName);
                                    delStmt.setString(3, title);
                                    delStmt.setInt(4, year);

                                    int rows = delStmt.executeUpdate();
                                    if (rows > 0) {
                                        System.out.println("Removed '" + title + "' (" + year +
                                                ") from playlist '" + playlistName + "'.");
                                    } else {
                                        System.out.println("This media item was not found in the playlist.");
                                    }
                                } catch (SQLException e) {
                                    System.err.println("Error removing media from playlist: " + e.getMessage());
                                    throw e;
                                } finally {
                                    if (delStmt != null) {
                                        try { delStmt.close(); } catch (SQLException e) {
                                            System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                                        }
                                    }
                                }

                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }
    }




    public void addMediaRequest() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n==== Add Media Request ====");

        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        
        System.out.print("Enter Year: ");
        int year = getValidInteger(0, 2026);

        System.out.print("Movie or Show(M/S)?");
        String type = sc.nextLine().toUpperCase();
        String mpa = null;
        if(type.equals("M")) {
            System.out.print("Enter MPA: ");
            mpa = sc.nextLine();
        }

        System.out.print("Enter Director: ");
        String director = sc.nextLine();

        System.out.print("Enter brief description: ");
        String description = sc.nextLine();

        MediaRequest suggest = new MediaRequest(title, year, description, type, director, mpa);

        System.out.print("Enter Genre(s) and enter 0 when you're done: ");
        while(true){
            String genre = sc.nextLine();
            if(genre.equals("0")) break;

            suggest.addGenre(genre);
        }

        mediaRequests.add(suggest);
    }

    public void editMedia(Connection conn) throws SQLException {
        Scanner  sc = new Scanner(System.in);

        System.out.print("==== Edit Media ====");

        System.out.print("Enter title to edit: ");
        String title = sc.nextLine();

        System.out.print("Enter Year: ");
        int year = getValidInteger(0, 2026);

        String selectSql = """
                SELECT Title, Year, Description, dId
                FROM MEDIA
                WHERE Title = ? AND Year =?
                """;

        try(PreparedStatement check = conn.prepareStatement(selectSql)){
            check.setString(1, title);
            check.setInt(2, year);

            ResultSet rs = check.executeQuery();

            if(!rs.next()) {
                System.out.println("No media found");
                return;
            }

            boolean editing = true;
            String newTitle = title;
            int newYear = year;
            //int newdID = rs.getInt("dID");
            String newDescription = rs.getString("Description");

            while(editing){
                System.out.print("""
                        \nChoose something to edit.
                        1. Title
                        2. Year
                        3. Description
                        4. Finish
                        Choice: """);
                int choice = getValidInteger(1, 4);

                switch(choice){
                    case 1: {
                        System.out.print("\nEnter Title: ");
                        newTitle = sc.nextLine();


                        String titleUpdateSql = "UPDATE MEDIA SET Title = ? " +
                                "WHERE LOWER(Title) = LOWER(?) AND Year = ?";

                        try(PreparedStatement titleUpdateStmt = conn.prepareStatement(titleUpdateSql)) {
                            titleUpdateStmt.setString(1, newTitle);
                            titleUpdateStmt.setString(2, title);
                            titleUpdateStmt.setInt(3, year);

                            int rowsAffected = titleUpdateStmt.executeUpdate();

                            //conn.commit();

                            if (rowsAffected > 0) {
                                System.out.println("You have successfully updated the title name.");
                                title = newTitle;
                            }else{
                                System.out.println();
                            }
                        }catch(SQLException e){
                            System.out.println("Something went wrong while editing the title." + e.getMessage());
                        }
                        break;
                    }
                    case 2: {
                        System.out.print("\nEnter Year: ");
                        newYear = getValidInteger(0, 2026);


                        String yearUpdateSql = "UPDATE Media SET Year = ? WHERE Title = ? AND Year = ?";
                        try(PreparedStatement yearUpdateStmt = conn.prepareStatement(yearUpdateSql)){
                            yearUpdateStmt.setInt(1, newYear);
                            yearUpdateStmt.setString(2, title);
                            yearUpdateStmt.setInt(3, year);

                            int rowsAffected = yearUpdateStmt.executeUpdate();

                            //conn.commit();

                            if (rowsAffected > 0) {
                                System.out.println("You have successfully updated the year.");
                            }else{
                                System.out.println("Year was not updated.");
                            }
                        }catch(SQLException e){
                            System.out.println("Something went wrong while editing the year." + e.getMessage());
                        }
                        break;
                    }
                    case 3: {
                        System.out.print("\nEnter Description: ");
                        newDescription = sc.nextLine();

                        String descriptionUpdateSql = "UPDATE Media SET Description = ? WHERE LOWER(Title) = LOWER(?) AND Year = ?";
                        try(PreparedStatement descriptionUpdateStmt = conn.prepareStatement(descriptionUpdateSql)){
                            descriptionUpdateStmt.setString(1, newDescription);
                            descriptionUpdateStmt.setString(2, title);
                            descriptionUpdateStmt.setInt(3, year);

                            int rowsAffected = descriptionUpdateStmt.executeUpdate();

                            //conn.commit();

                            if (rowsAffected > 0) {
                                System.out.println("You have successfully updated the description.");
                            }else {
                                System.out.println("Something went wrong while adding the description.");
                            }
                        }catch(SQLException e){
                            System.out.println("Something went wrong while editing the description." + e.getMessage());
                        }
                        break;
                    }
                    case 4: {
                        editing = false;
                        break;
                    }
                }
            }
        }
    }

    public void reviewMediaRequest(Connection conn) throws SQLException {
        if(mediaRequests.isEmpty())
        {
            System.out.println("No media requests found.");
        }
        else
        {
            Scanner sc = new Scanner(System.in);
            String confirm;
            ResultSet rs = null;
            PreparedStatement suggestStmt = null;
            String countDirSQL = """
                                 SELECT COUNT(*) AS dCount
                                 FROM DIRECTOR 
                                 """;
            String insertSuggestSql;
            Statement countDirStmt = null;

            String title;
            int year;
            String description;
            int director = 0;
            String mpa;
            ArrayList<String> genre;

            System.out.println("\n==== Review Media Requests ====");
            for (int idx = 0; idx < mediaRequests.size(); ) {
                MediaRequest suggest = mediaRequests.get(idx);

                String suggestString = suggest.toString();
                System.out.println(suggestString);
                System.out.println("Confirm or Deny (C/D)");
                confirm = sc.nextLine().toUpperCase();

                if(confirm.equals("D")) {
                    mediaRequests.remove(idx);
                    continue;
                }
                else if (confirm.equals("C")) {


                    //Insert MEDIA
                    insertSuggestSql = "INSERT INTO MEDIA (Title, Year, dID, Description) VALUES (?, ?, ?, ?)";
                    try {
                        suggestStmt = conn.prepareStatement(insertSuggestSql);

                        title = suggest.getTitle();
                        year = suggest.getYear();
                        description = suggest.getDescription();
                        director++;

                        suggestStmt.setString(1, title);
                        suggestStmt.setInt(2, year);
                        suggestStmt.setInt(3, Types.INTEGER);
                        suggestStmt.setString(4, description);

                        suggestStmt.executeUpdate();
                        suggestStmt.close();
                        suggestStmt = null;


                        //Insert Genre
                        genre = suggest.getGenres();
                        insertSuggestSql = "INSERT INTO GENRE (Title, year, Genre) VALUES (?, ?, ?)";
                        for (String g : genre) {
                            suggestStmt = conn.prepareStatement(insertSuggestSql);

                            title = suggest.getTitle();
                            year = suggest.getYear();
                            director++;

                            suggestStmt.setString(1, title);
                            suggestStmt.setInt(2, year);
                            suggestStmt.setString(3, g);

                            suggestStmt.executeUpdate();
                            suggestStmt.close();
                            suggestStmt = null;
                        }

                        //Insert Show
                        if (suggest.getMpa() == null) {
                            insertSuggestSql = "INSERT INTO 'SHOW' (Title, Year) VALUES (?, ?)";

                            suggestStmt = conn.prepareStatement(insertSuggestSql);

                            title = suggest.getTitle();
                            year = suggest.getYear();

                            suggestStmt.setString(1, title);
                            suggestStmt.setInt(2, year);

                            suggestStmt.executeUpdate();
                        }


                        //Insert Movie
                        else {
                            insertSuggestSql = "INSERT INTO MOVIE (Title, Year, MPA_Rating) VALUES (?, ?, ?)";

                            suggestStmt = conn.prepareStatement(insertSuggestSql);

                            title = suggest.getTitle();
                            year = suggest.getYear();
                            mpa = suggest.getMpa();

                            suggestStmt.setString(1, title);
                            suggestStmt.setInt(2, year);
                            suggestStmt.setString(3, mpa);

                            suggestStmt.executeUpdate();
                        }
                        mediaRequests.remove(idx);
                    }
                    catch (SQLException e)
                    {
                        System.out.println("Something went wrong while processing media request: " + e.getMessage());
                        idx++;
                    }
                    finally
                    {
                        if (rs != null)
                        {
                            try
                            {
                                rs.close();
                            }
                            catch (SQLException e)
                            {
                                System.err.println("Error when closing ResultSet: " + e.getMessage());
                            }
                        }
                        if (suggestStmt != null)
                        {
                            try
                            {
                                suggestStmt.close();
                            }
                            catch (SQLException e)
                            {
                                System.err.println("Error when closing PreparedStatement: " + e.getMessage());
                            }
                        }
                    }
                }
            }

     }

    }
}
