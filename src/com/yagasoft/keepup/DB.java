/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/DB.java
 *
 *			Modified: 23-Jun-2014 (23:26:28)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yagasoft.logger.Logger;


/**
 * The Class DB.
 */
public final class DB
{

	/**
	 * A connection to the database, which can be used to control it.
	 */
	private static Connection	connection;

	/**
	 * Used to send an SQL statement to the {@link Database#connection}.
	 */
	private static Statement	statement;

	/** Flag to be tested on program startup. */
	public static boolean		ready;

	/**
	 * Table names as an enumeration.
	 */
	public static enum Table
	{

		/** Options. */
		options,

		/** Backup path. */
		backup_path,

		/** Backup status. */
		backup_status,

		/** Backup revisions. */
		backup_revisions
	}

	/** Options columns. */
	public static String[]	optionsColumns			= new String[] { "category", "option", "value" };

	/** Backup path columns. The 'remote' is the remote parent, not full path. */
	public static String[]	backupPathColumns		= new String[] { "path", "remote" };

	/** Backup status columns. */
	public static String[]	backupStatusColumns		= new String[] { "path", "status" };

	/** Backup revisions columns. */
	public static String[]	backupRevisionsColumns	= new String[] { "path", "revision", "date" };

	/**
	 * <p>
	 * Creates an SQLite database file and connection. Only creates a connection if the file already exists.
	 * </p>
	 *
	 * <p>
	 * It also sets auto-commit to false; so that communication becomes faster. Committing is done after each large operation is
	 * finished.
	 * </p>
	 */
	static
	{
		Logger.info("initialising DB ...");

		try
		{
			Class.forName("org.sqlite.JDBC");		// initialise SQLite JDBC.

			connection = DriverManager.getConnection("jdbc:sqlite:"
					+ System.getProperty("user.dir") + "/var/db.dat");		// load/create database file.
			connection.setAutoCommit(false);		// make bulk operations run faster.

			statement = connection.createStatement();		// used to pass queries to db.

			initTables();

			ready = true;

			Logger.info("DB is ready.");
		}
		catch (ClassNotFoundException | SQLException e)
		{
			Logger.error("Problem with the database!");
			Logger.except(e);
			e.printStackTrace();
		}
	}

	/**
	 * Initialises the tables.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private static void initTables() throws SQLException
	{
		// create the options table.
		createTable(Table.options, optionsColumns, "category, option");

		// create the files path table.
		createTable(Table.backup_path, backupPathColumns, backupPathColumns[0]);

		// create the files status table.
		createTable(Table.backup_status, backupStatusColumns
				, backupStatusColumns[0], "(path) REFERENCES backup_path(path)");

		// create the files revisions table.
		createTable(Table.backup_revisions, backupRevisionsColumns
				, "path, revision", "(path) REFERENCES backup_path(path)");
	}

	/**
	 * Creates a new table in the database, or makes sure the table is already
	 * created.
	 *
	 * @param table
	 *            the table to be created.
	 * @param columnsArray
	 *            an array of the names of columns in the table in the form <br>
	 * @param primaryKey
	 *            primary key column
	 * @param foreignKeysArray
	 *            an array of names of columns to be used as foreign keys, in
	 *            the form<br>
	 *            <code>([column-name]) REFERENCES [table]([column-name-there])</code>
	 * @throws SQLException
	 *             the SQL exception
	 */
	private static void createTable(Table table, String[] columnsArray, String primaryKey, String... foreignKeysArray)
			throws SQLException
	{
		// String to form the columns portion of the SQL statement.
		String columns = "";

		if (columnsArray != null)
		{
			// form the columns.
			for (int i = 0; i < columnsArray.length; i++)
			{
				columns += columnsArray[i] + " TEXT" + ((i + 1) < columnsArray.length ? ", " : "");
			}
		}

		// String to form the foreign keys portion of the SQL statement.
		String foreignKeys = "";

		if (foreignKeysArray.length > 0)
		{
			foreignKeys = ", FOREIGN KEY ";

			// form the columns.
			for (int i = 0; i < foreignKeysArray.length; i++)
			{
				foreignKeys += foreignKeysArray[i] + ((i + 1) < foreignKeysArray.length ? ", " : "");
			}
		}

		String createStatement = "CREATE TABLE " + table + "(" + columns + ", PRIMARY KEY (" + primaryKey + ")"
				+ foreignKeys + ")";

		// read meta-data of the database.
		DatabaseMetaData dbMetaData = connection.getMetaData();

		// reads database tables.
		ResultSet result = dbMetaData.getTables(null, null, table.toString(), null);

		// if there's no such table, create.
		if ( !result.next())
		{
			Logger.info("Creating table in DB: " + table);

			statement.executeUpdate(createStatement);
			connection.commit();		// save changes to db file
		}
	}

	/**
	 * Uses the 'INSERT' statement to add a new row into the database under the
	 * table passed.<br />
	 * This method passes only 'TEXT' to the database.
	 *
	 * @param table
	 *            The table the row is going to be added to.
	 * @param values
	 *            An array with column values at each index. MUST be in order of
	 *            columns of the table.
	 * @return True if operation was successful.
	 */
	public static boolean insertRecord(Table table, String[] values)
	{
		// flag for the success of the insertion operation.
		boolean success = false;

		// String to form the 'VALUES' portion of the SQL statement.
		String formattedValues = "'";

		// form 'VALUES'.
		for (int i = 0; i < values.length; i++)
		{
			formattedValues += values[i] + ((i + 1) < values.length ? "', '" : "'");
		}

		String query = "INSERT INTO " + table + " VALUES(" + formattedValues + ");";

		// execute INSERT.
		try
		{
			Logger.info(query);

			// execute the statement using the values built, and store the success status.
			success = statement.executeUpdate(query) > 0;

			connection.commit();	// make sure data is saved lest a crash occurs and data is lost.

			return success;
		}
		catch (Exception e)
		{
			Logger.error("Failed to insert record: " + query);
			Logger.except(e);
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Uses the 'INSERT' statement to add new rows into the database under the
	 * table passed.<br />
	 * This method passes only 'TEXT' to the database.
	 *
	 * @param table
	 *            The table the rows are going to be added to.
	 * @param array
	 *            A 2D array of the column values to be passed. Each entry in
	 *            the array is an array of values (row), in the order of columns.
	 * @return True if operation was successful.
	 */
	public static boolean batchInsertRecords(Table table, String[][] array)
	{
		// execute INSERT.
		try
		{
			// String to form the 'VALUES' portion of the SQL statement.
			String values = "'";

			String query;

			// form 'VALUES'.
			for (String[] element : array)
			{
				for (int i = 0; i < element.length; i++)
				{
					values += element[i] + ((i + 1) < element.length ? "', '" : "'");
				}

				query = "INSERT INTO " + table + " VALUES(" + values + ");";

				Logger.info("Batch add: " + query);

				statement.addBatch(query);

				values = "'";
			}

			connection.commit();		// save changes to db file

			Logger.info("Executing batch ...");

			// submit the batch, then check if any rows were affected.
			return statement.executeBatch()[0] > 0;
		}
		catch (Exception e)
		{
			Logger.error("Failed to insert records into table: " + table);
			Logger.except(e);
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Update record.
	 *
	 * @param table
	 *            Table.
	 * @param columns
	 *            Columns.
	 * @param values
	 *            Values.
	 * @param condition
	 *            Condition.
	 * @return true, if successful
	 */
	public static boolean updateRecord(Table table, String[] columns, String[] values, String... condition)
	{
		// flag for the success of the insertion operation.
		boolean success = false;

		// String to form the 'SET' portion of the SQL statement.
		String formattedValues = "";

		// form 'SET'.
		for (int i = 0; i < columns.length; i++)
		{
			formattedValues += columns[i] + " = '" + values[i] + ((i + 1) < values.length ? "', " : "'");
		}

		String query = "UPDATE " + table + " SET " + formattedValues
				+ (condition.length > 0 ? " WHERE " + condition[0] : "") + ";";

		// execute UPDATE.
		try
		{
			Logger.info(query);

			// execute the statement using the values built, and store the success status.
			success = statement.executeUpdate(query) > 0;

			connection.commit();	// make sure data is saved lest a crash occurs and data is lost.

			return success;
		}
		catch (Exception e)
		{
			Logger.error("Failed to update record: " + query);
			Logger.except(e);
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Delete record.
	 *
	 * @param table
	 *            Table.
	 * @param condition
	 *            Condition.
	 * @return true, if successful
	 */
	public static boolean deleteRecord(Table table, String condition)
	{
		// flag for the success of the insertion operation.
		boolean success = false;

		String query = "DELETE FROM " + table + " WHERE " + condition + ";";

		// execute DELETE.
		try
		{
			Logger.info(query);

			// execute the statement using the values built, and store the success status.
			success = statement.executeUpdate(query) > 0;

			connection.commit();	// make sure data is saved lest a crash occurs and data is lost.

			return success;
		}
		catch (Exception e)
		{
			Logger.error("Failed to delete record: " + query);
			Logger.except(e);
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Insert or update a record.
	 *
	 * @param table
	 *            Table.
	 * @param columns
	 *            Columns.
	 * @param values
	 *            Values.
	 * @param primaryKeysIndexes
	 *            Primary keys indexes (zero-based), which will be used to form the condition.
	 * @return true, if successful
	 */
	public static boolean insertOrUpdate(Table table, String[] columns, String[] values, int[] primaryKeysIndexes)
	{
		if (insertRecord(table, values))
		{
			return true;
		}
		else
		{
			String condition = "";

			// form the condition
			for (int i = 0; i < primaryKeysIndexes.length; i++)
			{
				condition += columns[i] + " = '" + values[i] + ((i + 1) < primaryKeysIndexes.length ? "' AND " : "'");
			}

			return updateRecord(table, columns, values, condition);
		}
	}

	/**
	 * Uses 'SELECT' SQL statement to query the database for rows matching the
	 * criteria.
	 *
	 * @param table
	 *            The table to search in.
	 * @param columns
	 *            An array of column names to search in. '*' can be used to
	 *            search in all columns.
	 * @param condition
	 *            An SQL formatted condition. Optional.
	 * @return A 2D array of rows. Each entry in the array is an array of values
	 *         (row).
	 */
	public static String[][] getRecord(Table table, String[] columns, String... condition)
	{
		// used to form the columns criteria.
		String columnsString = "";

		// form the columns criteria.
		for (int i = 0; i < columns.length; i++)
		{
			columnsString += columns[i] + ((i + 1) < columns.length ? ", " : "");
		}

		String query = "SELECT " + columnsString + " FROM " + table
				+ (condition.length > 0 ? " WHERE " + condition[0] : "") + ";";

		Logger.info(query);

		// execute SELECT.
		try
		{
			// execute the statement, and assign the result.
			ResultSet result = statement.executeQuery(query);

			// list to store rows returned
			List<String[]> resultList = new ArrayList<String[]>();
			// array to form the row
			String[] tempRow = new String[result.getMetaData().getColumnCount()];

			// fetch each row and add it to the list
			while (result.next())
			{
				// form the row as an array
				for (int i = 0; i < tempRow.length; i++)
				{
					tempRow[i] = result.getString(i + 1);		// columns start at '1', not '0'!
				}

				// add the row to the list
				resultList.add(tempRow);
			}

			return resultList.toArray(
					new String[resultList.size()][]);
		}
		catch (Exception e)
		{
			Logger.error("Couldn't fetch rows for: " + query);
			Logger.except(e);
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Close db.
	 */
	public static void closeDB()
	{
		try
		{
			if ((connection != null) && !connection.isClosed())
			{
				connection.commit();
				connection.close();

				Logger.info("Closed DB connection.");
			}
		}
		catch (SQLException e)
		{
			Logger.error("Failed to close DB connection!");
			Logger.except(e);
			e.printStackTrace();
		}
	}

	/**
	 * Singleton.
	 */
	private DB()
	{}

}
