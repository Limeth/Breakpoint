package com.fijistudios.jordan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author Jordan Wiggins
 * 
 */
/*
 * Example use:
 * 
 * FruitSQL sql = new FruitSQL("host", "port", "database", "username",
 * "password"); //Connect to database sql.createTable("users"); //Create the
 * table 'users'
 */
public class FruitSQL
{
	private Connection connection = null;
	private final String host, database, username, password;
	private final int port;
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 */
	public FruitSQL(String host, int port, String database, String username, String password)
	{
		long start = 0;
		long end = 0;
		try
		{
			start = System.currentTimeMillis();
			System.out.println("Attempting to establish a connection the MySQL server!");
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			end = System.currentTimeMillis();
			System.out.println("Connection to MySQL server established! (" + host + ":" + port + ")");
			System.out.println("Connection took " + ((end - start)) + "ms!");
		}
		catch(SQLException e)
		{
			System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("JDBC Driver not found!");
		}
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	/**
	 * 
	 * @return
	 */
	public synchronized boolean isConnected()
	{
		if(connection != null)
			return true;
		return false;
	}

	/**
*
*/
	public synchronized void closeConnection()
	{
		if(connection == null)
			try
			{
				connection.close();
				System.out.println("MySQL Connection closed");
			}
			catch(SQLException e)
			{
				System.out.println("Couldn't close connection");
			}
	}

	/**
*
*/
	public synchronized void refreshConnection()
	{
		PreparedStatement st = null;
		ResultSet valid = null;
		try
		{
			st = connection.prepareStatement("SELECT 1 FROM Dual");
			valid = st.executeQuery();
			if(valid.next())
				return;
			closeQuietly(valid);
		}
		catch(SQLException e2)
		{
			System.out.println("Connection is idle or terminated. Reconnecting...");
		}
		finally
		{
			closeQuietly(valid);
		}
		long start = 0;
		long end = 0;
		try
		{
			start = System.currentTimeMillis();
			System.out.println("Attempting to establish a connection the MySQL server!");
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			end = System.currentTimeMillis();
			System.out.println("Connection to MySQL server established! (" + host + ":" + port + ")");
			System.out.println("Connection took " + ((end - start)) + "ms!");
		}
		catch(SQLException e)
		{
			System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("JDBC Driver not found!");
		}
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public synchronized boolean execute(String sql)
	{
		refreshConnection();
		boolean st = false;
		try
		{
			Statement statement = connection.createStatement();
			st = statement.execute(sql);
			statement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return st;
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public synchronized ResultSet executeQuery(String sql)
	{
		refreshConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return rs;
	}
	
	public synchronized ResultSetMetaData getMetaData(String table) throws SQLException
	{
		ResultSet rs = executeQuery("SELECT * FROM `" + table + "` LIMIT 1");
		ResultSetMetaData rsmd = rs.getMetaData();
		
		closeQuietly(rs);
		
		return rsmd;
	}

	public static synchronized void closeQuietly(ResultSet rs)
	{
		PreparedStatement ps = null;
		try
		{
			ps = (PreparedStatement) rs.getStatement();
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs != null)
				try
				{
					rs.close();
				}
				catch(SQLException e)
				{
					// Ignore... nothing we can do about this here
				}
			if(ps != null)
				try
				{
					ps.close();
				}
				catch(SQLException e)
				{
					// Ignore... nothing we can do about this here
				}
		}
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public synchronized int executeUpdate(String sql)
	{
		refreshConnection();
		int st = 0;
		try
		{
			Statement statement = connection.createStatement();
			st = statement.executeUpdate(sql);
			statement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return st;
	}

	/**
	 * @param statements
	 *            - the list of statements
	 * @param batchSize
	 *            - the size of each batch to avoid an outOfMemory exception,
	 *            set to 0 if you want to ignore
	 */
	public synchronized void executeBatch(String[] statements, int batchSize)
	{
		refreshConnection();
		int count = 0;
		Statement statement = null;
		try
		{
			statement = connection.createStatement();
			for(String query : statements)
			{
				statement.addBatch(query);
				if(batchSize != 0)
					if(count % batchSize == 0)
						statement.executeBatch(); // execute batch.
						// batch size limit has been
						// reached
				count++;
			}
			statement.executeBatch(); // execute the remaining batches
			statement.close();
			statement = null;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(statement != null)
				try
				{
					statement.close();
				}
				catch(SQLException e)
				{
					// Ignore this, nothing we can do about this now
				}
		}
	}

	/**
	 * 
	 * @param tablename
	 * @param values
	 */
	public synchronized void createTable(String tablename, String[] values, String additionalCommands)
	{
		refreshConnection();
		StringBuilder stmt = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tablename).append("(");
		for(int i = 0; i < values.length; i++)
			if(i == values.length - 1)
				stmt.append(values[i]);
			else
				stmt.append(values[i] + ", ");
		stmt.append(')');
		
		if(additionalCommands != null)
			stmt.append(' ').append(additionalCommands);
		
		stmt.append(';');
		
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(stmt.toString());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param tablename
	 */
	public synchronized void deleteTable(String tablename)
	{
		refreshConnection();
		String sql = "DROP TABLE IF EXISTS " + tablename;
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param tablename
	 * @param values
	 */
	public synchronized void resetTable(String tablename, String[] values)
	{
		refreshConnection();
		deleteTable(tablename);
		createTable(tablename, values, null);
	}

	/**
	 * 
	 * @param column
	 * @param value
	 */
	public synchronized void insertInto(String table, String[] columns, Object[] values)
	{
		refreshConnection();
		
		StringBuilder statement = new StringBuilder("INSERT INTO ").append(table).append('(');
		
		for(int i = 0; i < columns.length; i++)
			if(i == columns.length - 1)
				statement.append('`').append(columns[i]).append('`');
			else
				statement.append('`').append(columns[i]).append("`, ");
		
		statement.append(')').append(" VALUES").append('(');
		
		for(int i = 0; i < values.length; i++)
			if(i == columns.length - 1)
			{
				if(values[i] instanceof String)
					statement.append('\'').append(values[i]).append('\'');
				else
					statement.append(values[i]);
			}
			else if(values[i] instanceof String)
				statement.append('\'').append(values[i]).append("\', ");
			else
				statement.append(values[i]).append(", ");
		
		statement.append(") ON DUPLICATE KEY UPDATE ");
		
		for(int i = 0; i < columns.length; i++)
		{
			statement.append('`').append(columns[i]).append("` = ");
			if(i == columns.length - 1)
			{
				if(values[i] instanceof String)
					statement.append('\'').append(values[i]).append('\'');
				else
					statement.append(values[i]);
			}
			else if(values[i] instanceof String)
				statement.append('\'').append(values[i]).append("', ");
			else
				statement.append(values[i]).append(", ");
		}
		
		statement.append(';');
		
		String builtStatement = statement.toString();
		
		executeUpdate(builtStatement);
	}

	/***
	 * @Deprecated This method is deprecated - it has not been edited to support special characters yet
	 */
	public synchronized void insertIntoWithoutPrimaryKey(String table, String[] columns, Object[] values)
	{
		refreshConnection();
		String statement = "INSERT INTO " + table;
		String c = "(";
		for(int i = 0; i < columns.length; i++)
			if(i == columns.length - 1)
				c = c + columns;
			else
				c = c + columns + ",";
		c = c + ")";
		String v = "(";
		for(int i = 0; i < values.length; i++)
			if(i == columns.length - 1)
			{
				if(values[i] instanceof String)
					v = v + "'" + values[i] + "'";
				else
					v = v + values[i];
			}
			else if(values[i] instanceof String)
				v = v + "'" + values[i] + "', ";
			else
				v = v + values[i] + ", ";
		v = v + ")";
		statement = statement + c + " VALUES" + v + " ON DUPLICATE KEY UPDATE ";
		for(int i = 1; i < columns.length; i++)
		{
			statement = statement + columns + "=";
			if(i == columns.length - 1)
			{
				if(values[i] instanceof String)
					statement = statement + "'" + values[i] + "'";
				else
					statement = statement + values[i];
			}
			else if(values[i] instanceof String)
				statement = statement + "'" + values[i] + "', ";
			else
				statement = statement + values[i] + ", ";
		}
		statement = statement + ";";
		executeUpdate(statement);
	}

	/**
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public synchronized ResultSet getRowByColumn(String table, String column, String value)
	{
		refreshConnection();
		ResultSet rs = executeQuery("SELECT * FROM " + table + " WHERE " + column + " = " + value);
		return rs;
	}

	/**
	 * 
	 * @param table
	 * @return
	 */
	public synchronized int getRowCount(String table)
	{
		refreshConnection();
		ResultSet rs = executeQuery("SELECT * FROM " + table);
		int count = 0;
		try
		{
			while(rs.next())
				count++;
			
			closeQuietly(rs);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 
	 * @return
	 */
	public synchronized Connection getConnection()
	{
		return connection;
	}
}