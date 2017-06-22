## OREELY

## Contributors
Lukas Schneider

Felix Rauchenwald

Markus Wolf


## Load Distribution
Felix Rauchenwald: Code Owner and Backend

Markus Wolf: Frontend

Lukas Schneider: Backend


## Release Information
Sadly not all features that were initialy planned made it into the project due to timecontraints. If we would have to start again we would do some things differently. However, we are very proud of what we achieved and learned in the process.

## Setup Instructions
Prerequired Software:
-Eclipse IDE
-MySQL database
-Tomcat Server

1) Clone this repository to your local computer

2) After that, you need to create two files in the /src folder.

  a) config.properties
  
  This file has to contain your personal API Key for the MovieDB. A Key can be obtained from their website.
  The API Key has to be saved following this schema: 
      
      apiKey=*yourAPIKey*
      
  b) db.properties
  
  This file has to contain your credentials of the MySQL database. 
  The information has to be saved using the following schema:
      
      db.url=jdbc:mysql://0.0.0.0:0000/*yourSchema*
      db.username=*yourUsername*
      db.password=*yourPassword*
      
  It is necessary that the database scheme is completly empty befor running the server for the first time.
      
3) In Eclipse click File->Import...>Maven>Existing Maving Projects

      Choose as Root Directory the folder where you have cloned this repository and click Finish
           
4) After this you should be able to run the application on the tomcat server.

## TROUBLESHOOTING

Because we store a lot of data when the server starts for the first time it might be necessary to increase the timeout timer of your tomcat server. Depending on your hardware and internet connection a value of up to 300 seconds might be required.

If you run into any kind of errors resulting from database errors it might be necessary to to drop your database schema and create it from screatch.

Also if you want to restart your tomcat server it might be necessary to no only restart the server but also delete it and create it new. We have run into errors where the previous session was still active even after restartng the server.

Please note: it might be necessary to add these repositories to your pom.xml file:

    <repositories>
      <repository>
          <id>jcenter</id>
          <name>JCenter</name>
          <url>http://jcenter.bintray.com/</url>
      </repository>
      <repository>
         <id>maven</id>
          <name>maven</name>
          <url>http://repo.maven.apache.org/maven2</url>
      </repository>
    </repositories>

Due to issues with a broken library we could not test this.
