# Media Database
This document will explain how to run the Media Database program using Gradle. This document will also provide a backup run option if Gradle does not work for you.

## Setting up the database

The file 322test1.sql is located in the root folder. Use this file to create the database in MySQL.

## Using Gradle to run the Media Database 

First, you must save your MySQL credentials in properties.gradle file. The steps to do so are as follows.

1. Create a file called 'properties.gradle' in the root directory of this project (In the same folder as this README). You can do this by copying the 'gradle.properties.example' file and removing the '.example' extension from the file name.
2. Once the file is created, open it.
3. Enter the following 2 lines into the properties.gradle file:
	
	mysqlUser=YOUR_USERNAME
	
	mysqlPass=YOUR_PASSWORD
	
4. Replace YOUR_USERNAME with your MySQL username. 
	
	Ex: mysqlUser=root
	
5. Replace YOUR_PASSWORD with your MySQL password. 
	
	Ex: mysqlPass=Pass123
	
6. Save your changes and close the gradle.properties file.

Once you have saved your MySQL credentials to the gradle.properties file, you are ready to run the program. To run the program, you can run the following command:
	
	gradle runDatabase -q --console=plain
	
This command runs a Gradle task with preset arguments for the program. The task also passes in your MySQL username and password as arguments to the program. 


## Using the Java compiler to run the Media Database

If Gradle does not work for you for whatever reason, you can still run this program with the following Java compiler commands. 

First, you need to compile the program using the following command:

	javac -d . src/main/java/MediaDatabase.java


Next, you can run the program with the following command. (The command is slightly difference for Windows and MacOS)

Windows:

	java -cp ".;mysql-connector-j-9.5.0.jar" MediaDatabase "jdbc:mysql://localhost:3306/mediadatabase" MYSQL_USERNAME MYSQL_PASSWORD "com.mysql.cj.jdbc.Driver"

MacOS:

	java -cp ".:mysql-connector-j-9.5.0.jar" MediaDatabase "jdbc:mysql://localhost:3306/mediadatabase" MYSQL_USERNAME MYSQL_PASSWORD "com.mysql.cj.jdbc.Driver"

Be sure to replace MYSQL_USERNAME with your MySQL username, and replace MYSQL_PASSWORD with your MySQL password.

