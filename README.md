# maven-custom-sql-plugin

About

Custom SQL Plugin for Maven is a plugin that will run the sql scrips from a given directory. 
It runs recursively all the directories and sub-directories that contains .SQL files

Setup

Build the project using the maven command mvn clean install

Usage

Add the following in plugin details to the maven build in pom.xml with required fields. 
        
        srcFile - The path to the directory contains
        
        <build>
        				<plugins>
        					<plugin>
        						<!-- ( 1 ) plugin info -->
        						<groupId>com.maven.custom.sql.plugin</groupId>
        						<artifactId>custom-sql-plugin</artifactId>
        						<version>0.0.1-SNAPSHOT</version>
        
        						<!-- ( 2 ) jdbc dirver -->
        						<dependencies>
        							<dependency>
        								<groupId>mysql</groupId>
        								<artifactId>mysql-connector-java</artifactId>
        								<version>5.1.9</version>
        							</dependency>
        						</dependencies>
        						
        						<configuration>
        							<driver>com.mysql.jdbc.Driver</driver>
        							<url>jdbc:mysql://localhost:3306/databasename</url>
        							<username>root</username>
        							<password>root</password>
        							<srcFile>/home/Nagendra/Downloads/</srcFile>
        						</configuration>
        
        						<executions>
        							<execution>
        								<id>first-execution</id>
        								<phase>generate-resources</phase>
        								<goals>
        									<goal>sql-execute</goal>
        								</goals>
        								<configuration>
        									<driver>com.mysql.jdbc.Driver</driver>
        									<url>jdbc:mysql://nagendra:3306/mule</url>
        									<username>root</username>
        									<password>root</password>
        									<srcFile>/home/Nagendra/Downloads/</srcFile>
        								</configuration>
        							</execution>
                        </executions>
        						<!-- ( 3 ) connection settings -->
        						</plugin>
        				</plugins>
        			</build>

Command to Run:

mvn com.maven.custom.sql.plugin:custom-sql-plugin:sql-execute

If enclosed in profile then run with the following command

mvn com.maven.custom.sql.plugin:custom-sql-plugin:sql-execute -Pcustom_schema

custom_schema is the id of the profile

sql-execute is the custom goal name for running the SQL scripts through maven

Future Plans
          My grand vision is to implement maven sql plugin for custom executions which cannot be done but existing plugin.
            
