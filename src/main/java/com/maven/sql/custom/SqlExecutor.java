package com.maven.sql.custom;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * SQL executor plugin to execute all the sql files in a given directory
 * 
 * Goal which runs a sql file.
 *
 * @goal sql-execute
 * 
 * @phase process-sources
 */
public class SqlExecutor  extends AbstractMojo
{
	
	/**
	* Any Object to print out.
	* @parameter
	*   expression="${sql-execute.driver}"
	*/
	private String driver;
	
	/**
	* Any Object to print out.
	* @parameter
	*   expression="${sql-execute.url}"
	*/
	private String url;
	
	/**
	* Any Object to print out.
	* @parameter
	*   expression="${sql-execute.username}"
	*/
	private String username;
	
	/**
	* Any Object to print out.
	* @parameter
	*   expression="${sql-execute.password}"
	*/
	private String password;
	
	/**
	* Any Object to print out.
	* @parameter
	*   expression="${sql-execute.srcFile}"
	*/
	private String srcFile;
	
	final String delimeterRegex = "DELIMITER [$&+,:=?@#|'<>.^*()%!-]*";
	final Pattern pattern = Pattern.compile(delimeterRegex);
	
	
	public void execute() throws MojoExecutionException	{
			
		//Load JDBC Driver
		loadDriver();
		
		//GetConnection
		final Connection con = getConnection();
		
		try {
			// Give the input file to Reader
			final Path sourceDir = Paths.get(srcFile);
			Files.walkFileTree(sourceDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
		     new SimpleFileVisitor<Path>() {
		  
		        @Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		        	if(getExtension(file.toFile().getName()).equalsIgnoreCase("sql"))
		        		executeSQLScript(file,pattern,con);
		            return FileVisitResult.CONTINUE;
		        }
	    });
		} catch (Exception e) {
			System.err.println("Failed to Execute" + srcFile
					+ " The error is " + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				System.err.println("Failed to get Connection "
						+ " The error is " + e.getMessage());
			}
		}
		
	}
	
	
	private void executeSQLScript(Path file, final Pattern pattern, final Connection con) {
		FileInputStream fis = null;
		BufferedReader br = null;
		String changedDelimeter = "";
		Statement stmt = null;
		StringBuilder builder = new StringBuilder();
		try {
		fis = new FileInputStream(file.toFile());
		br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		
		while ((line = br.readLine()) != null) {
			if ("DELIMITER ;".contains(line)) {
			} else if (pattern.matcher(line).find()) {
				changedDelimeter = extractDelimiter(line).trim();
			} else {
				builder.append(line);
				builder.append("\n");
			}
		}

		List<String> queries = buildQueries(builder.toString());
		stmt = con.createStatement();
		int sqlcount=0;
		for (String query : queries) {
			if (query != null && !query.isEmpty() && !query.matches("\\s+")) {
				sqlcount++;
				stmt.execute(query);
			}
		}
		getLog().info(sqlcount +" of " +sqlcount+" SQL Statements Executed Successfully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				br.close();
				stmt.close();
				} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadDriver() {
		try {
			Class.forName(driver);
		} catch (Exception e) {
			System.err.println("Failed to load driver class" + driver
					+ " The error is " + e.getMessage());
		}
	}
	
	private Connection getConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(url,username,password);
		} catch (SQLException e) {
			System.err.println("Failed to load driver class" + driver
					+ " The error is " + e.getMessage());
		}
		return con;
	}
	
	private static List<String> buildQueries(String sqlQuery) {
		return Arrays.asList(sqlQuery.split("\\$\\$"));
	}

	private static String extractDelimiter(String str) {
		return str.substring(str.indexOf(" ") + 1, str.length());
	}
	
	private static String getExtension(String str) {
		return str.substring(str.lastIndexOf(".") + 1, str.length());
	}
}
