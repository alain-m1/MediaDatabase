import java.sql.*;
import java.io.IOException;

public class MediaDatabase {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Error: Did not include all necessary arguments.");
            System.err.println("Usage (choose from below):");
            System.err.println("query2: java Main <url> <user> <pwd> <driver>");
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
        System.out.println("Do you want to login or create an account?");
        System.out.println("1: Login");
        System.out.println("2: Create account");


        Boolean done = false;
        while(!done){// Main menu

        }
    }
}
