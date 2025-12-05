import java.sql.*;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MediaDatabase {
    public static Scanner scanner = new Scanner(System.in);

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

        System.out.println("\nDo you want to login or create an account?");
        System.out.println("1: Login");
        System.out.println("2: Create account");
        int choice = getValidInteger(1, 2);

        boolean adminFlag = false;
        String username = "";
        switch(choice) {
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
                                mdb.loadUserMainMenu(conn, username);
                            } else {
                                mdb.loadAdminMainMenu(conn, username);
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
                                mdb.loadUserMainMenu(conn, username);
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
    public void loadUserMainMenu(Connection conn, String username) throws SQLException {
        boolean exitFlag = false;
        while(!exitFlag) {
            System.out.println("\n==== Main Menu ====  (Enter 0 to quit)");
            System.out.println("1: View Shows\n" +
                    "2: View Movies\n" +
                    "3: View Playlists\n" +
                    "4: Request Media\n" +
                    "0: Quit");

            int choice = getValidInteger(0, 4);
            switch (choice) {
                case 0: {
                    System.out.println("\nGoodbye!");
                    exitFlag = true;
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
            }
        }
    }

    public void loadAdminMainMenu(Connection conn, String username) throws SQLException {
        boolean exitFlag = false;
        while(!exitFlag) {
            System.out.println("\n==== Main Menu ====  (Enter 0 to quit)");
            System.out.println("1: View Shows\n" +
                    "2. View Movies\n" +
                    "3: Edit Media\n" +
                    "4: Review Media Request\n" +
                    "0: Quit");

            int choice = getValidInteger(0, 3);
            switch (choice) {
                case 0: {
                    System.out.println("\nGoodbye!");
                    exitFlag = true;
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
                            System.out.printf("%-14s %-5s %-20s %-30.30s...\n", title, year, mpa, director, description);
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

    }

    public void addMediaRequest() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Media Add Request ===");

        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        
        System.out.print("Enter Year: ");
        int year = getValidInteger(0, 2026);

        System.out.print("Movie or Show(M/S)?");
        String type = sc.nextLine().toUpperCase();
        if(type.equals("M")) {
            System.out.print("Enter MPA: ");
            String mpa = sc.nextLine();
        };

        System.out.print("Enter Director: ");
        String director = sc.nextLine();




    }

    public void editMedia() {

    }

    public void reviewMediaRequest() {

    }
}
