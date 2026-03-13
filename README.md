# Description
This is the repository for my ClientApp Project for CSCE 548. It is a web app that allows someone to create a client, view their stored data, update their data, and delete it if desired. 

# Requirements
- [Git](https://git-scm.com/)
- [Java JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html#:~:text=(sha256%20)-,Windows%20x64%20Installer,-153.92%20MB)
- [Maven](https://maven.apache.org/download.cgi#:~:text=Apache%20Maven-,Apache%20Maven%203.9.14,-Apache%20Maven%203.9.14)
- [Node.js 20.x](https://nodejs.org/en/download) & npm
### If running locally
* [MySQL](https://dev.mysql.com/downloads/installer/)
* SQL Server (AN option when installing MySQL)
### If deploying to Render
- [Render](https://render.com/) account
- [PostgreSQL](https://www.postgresql.org/download/#:~:text=Packages%20and%20Installers)
- [CORS](https://www.npmjs.com/package/cors#:~:text=Original%20Author-,Installation,%24%20npm%20install%20cors,-Usage) (Only if your frontend Render URL differs from the backend config)

# Running Locally
## Database
Since it's not possible to clone my local database I've included a schema and test_data file so that you can copy the schema to your database and upload randomly generated test data. These can be located in "./clientapp/src/main/java/com/example/sql". You'll first have to get an SQL server running on your computer, and then create a new user.
  1. Ensure that your SQL server is up and running
  2. Create a new database or choose one you already have set up if desired
  3. Open MySQL as an admin
  4. In a terminal, create a new user using the following:
		```
	    CREATE DATABASE IF NOT EXISTS clientdb;
		CREATE USER IF NOT EXISTS 'choose_a_username'@'localhost'
		IDENTIFIED BY 'choose_a_password';
		
		GRANT ALL PRIVILEGES ON clientdb.* TO 'myapp_user'@'localhost';
		FLUSH PRIVILEGES;
	    ```
  5. Run the schema and seed files:
		```
		mysql -u root -p < "C:\path\to\CSCE548\clientapp\src\main\java\com\example\sql\schema.sql"
		mysql -u root -p clientdb < "C:\path\to\CSCE548\clientapp\src\main\java\com\example\sql\send_test_data.sql"
		```
  6. Navigate to the backend file, "/clientapp/src/main/resources/application.properites" and set:
		```
		spring.datasource.username=<database_user_username>
		spring.datasource.password=<database_user_password>
		```
## Backend
 1. `cd clientapp`
 2. `mvn spring-boot:run`
 ## Frontend
 1. `cd clientapp-frontend`
 2. `npm ci`
 3. `npm start`
 
 # Deploying to Render
 - First you have to sign into a Render account and make your way to the dashboard.
 - You'll need to create a Database, Backend, and Frontend service. Feel free to put these all into the same project to help with organization.
 ## Creating a Database
 First, create the database
  1. "+ New"
  2. "Postgres"
  3. Give it a name
  4. Ensure the "free" storage version is selected
  5. "Create Database"
Return this part when you have created the backend. Once done you'll need to flood the DB with a schema and data.
1. Open up a PowerShell terminal
2. Run:
	```
 	psql "<RENDER_EXTERNAL_DATABASE_URL>" -f "...\clientapp\src\main\java\com\example\sql\schema(postgres_version).sql"
	psql "<RENDER_EXTERNAL_DATABASE_URL>" -f "...\clientapp\src\main\java\com\example\sql\send_test_data(postgres_version).sql"
 	```
## Backend
1. "+ New"
2. "Web Service"
3. Language: "Docker"
	- There is currently a `Dockerfile` in the backend. You may need to make edits to it if it does not work.
4. Auto-Deploy "On Commit"
5. "Create"
6. Navigate to "Environment -> Environment Variables" and create the following:
	- `SRPING_PROFILES_ACTIVE=prod`
	- `SPRING_DATASOURCE_URL=jdbc:postgresql://<your_database_hostname_here>:<your_database_port_here>/<your_database_name_here>`
	- `SPRING_DATASOURCE_USERNAME=<your_database_users_username_here>`
	- `SPRING_DATASOURCE_PASSWORD=<your_database_users_password_here>`
7. Manually deploy if needed.
## Frontend
1. "+ New"
2. "Static Site"
3. "Build Command" = `npm run build`
4. "Auto-Deploy" = "On Commit"
5. "Create"
6. Manually deploy if needed
