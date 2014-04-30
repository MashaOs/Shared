package il.ac.huji.todolist;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A data source.
 * Stores all added to the list items.
 * Implemented by SQLite database, containing a single table, whose name is
 * {@link AppConstants#TABLE_NAME}, as specified in the exercise description.
 */
public class TasksSQLiteDB {
	/**
	 * Version of the database.
	 */
	static final int DB_VER = 1;
	/**
	 * Names and parameters of the table columns, 
	 * specified in the exercise description.
	 */
	static final String DB_CREATE = "create table " + AppConstants.TABLE_NAME + 
											" ( " + AppConstants.ID_COL + 
											" integer primary key autoincrement, " + 
											AppConstants.TITLE_COL + " text, " + 
											AppConstants.DATE_COL + " long);";
	/**
	 * Tag to print in Log.
	 */
	static final String TAG = "SQLiteDBTaskAdapter";
	private SQLiteDatabase db = null;
	private DBHelper dbHelper = null;
	private Context ctx = null;
	
	public TasksSQLiteDB(Context ctx) {
		  this.ctx = ctx;
	}
		 
	public TasksSQLiteDB open() {
		this.dbHelper = new DBHelper(this.ctx);
		this.db = this.dbHelper.getWritableDatabase();
		return this;
	}
		 
	public void close() {
		if (this.dbHelper != null) {
			this.dbHelper.close();
		}
	} 
	/**
	 * Waits until db is unlocked.
	 */
	private void waitWhileLocked() {
		int time = 0;
		while (this.db.isDbLockedByCurrentThread()) {
			time++;
			if (time % 10 == 0)
				Log.i(TAG, "DB locked");
		}
	}
	/**
	 * Adds a new task to the database.
	 * @param t task to add
	 * @return updated database
	 */
	public long addTask(Task t) {
		waitWhileLocked();
		ContentValues task = new ContentValues();
		task.put(AppConstants.TITLE_COL, t.getTitle());
		task.put(AppConstants.DATE_COL, t.getDateString());
		Log.i(TAG, "New task added: " + t.getTitle());
		return this.db.insert(AppConstants.TABLE_NAME, null, task);
	}
		
	/**
	 * Removes all rows in the table.
	 * @return true if number of removed rows is more than 0, false - otherwise.
	 */
	public boolean deleteAll() {
		waitWhileLocked();
		Log.i(TAG, "The table " + AppConstants.TABLE_NAME + " was deleted.");
		int done = this.db.delete(AppConstants.TABLE_NAME, null, null);
		return done != 0;
	}
		 
	/**
	 * Selects rows with the given value in the given column.<br>
	 * For example, for parameters 'title' and 'write' this method returns all rows whose
	 * 'title' column value is 'write'.
	 * @param columnName column name
	 * @param columnValue column value, if null all rows will be returned
	 * @return found rows
	 * @throws SQLException
	 */
	public ArrayList<Task> selectByValue(String columnName, String columnValue) {
		Cursor cursor = null;
		if (columnValue == null)
			return this.selectAll();
		if (columnName != AppConstants.ID_COL)
			columnValue += "'" + columnValue + "'";
		cursor = this.db.query(true, AppConstants.TABLE_NAME,
							   AppConstants.SQLITE_TABLE_COLS, 
							   columnName + " = " + columnValue, 
							   null, null, null, null, null);
		ArrayList<Task> foundRows = getList(cursor);
		if (cursor != null) 
		    cursor.close();
		return foundRows;
	}
	
	/**
	 * Deletes rows with the given value in the given column.<br>
	 * For example, for parameters 'title' and 'write' this method removes all rows whose
	 * 'title' column value is 'write'.
	 * @param columnName column name
	 * @param columnValue column value, if null no row will be removed
	 * @throws SQLException
	 */
	void deleteByValue(String columnName, String columnValue) {
		waitWhileLocked();
		if (columnValue == null)
			return;
		if (columnName != AppConstants.ID_COL)
			columnValue += "'" + columnValue + "'";
	    this.db.delete(AppConstants.TABLE_NAME, columnName + " = " + columnValue, null);
	}	
		 
	/**
	 * Returns all rows containing in a table with the given name in the given database.
	 * @param db an SQLite database
	 * @param tName the name of a table in the database
	 * @return all rows in the table
	 */
	public ArrayList<Task> selectAll() {
		Cursor cursor = db.query(AppConstants.TABLE_NAME, AppConstants.SQLITE_TABLE_COLS, 
								null, null,	null, null, null);
		ArrayList<Task> foundRows = getList(cursor);
		if (cursor != null) 
		    cursor.close();
		return foundRows;
	}
	
	/**
	 * Returns list of tasks. Doesn't close the cursor.
	 * @param cursor found rows.
	 * @return task list.
	 */
	private static ArrayList<Task> getList(Cursor cursor) {
		ArrayList<Task> foundRows = new ArrayList<Task>(); 
		if (cursor != null) {
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Task t = new Task();
		    	t.setTitle(cursor.getString(cursor.getColumnIndex(AppConstants.TITLE_COL)));
		      	t.setDate(cursor.getString(cursor.getColumnIndex(AppConstants.DATE_COL)));
		      	foundRows.add(t);
		      	cursor.moveToNext();
		    }
		}	
		return foundRows;
	}
	
	/**
	 * Deletes the given task from the database.<br>
	 * @param task task to be removed
	 * @throws SQLException
	 */
	public void deleteTask(Task task) {
		waitWhileLocked();
		String clause = AppConstants.TITLE_COL + " = '" + task.getTitle() + "'";
		clause += " AND " + AppConstants.DATE_COL + " = '" + task.getDateString() + "'";
	    this.db.delete(AppConstants.TABLE_NAME, clause, null);
	    Log.i(TAG, "Task deleted: " + task.getTitle());
	}

	/**
	 * Creates a new database with the name {@link #DB_NAME}, version
	 * {@link #DB_VER}. 
	 */
	public class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context) {
			super(context, AppConstants.DB_NAME, null, DB_VER);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			Log.i(DBHelper.class.getName(), "Upgrading db from " + oldVer + " to " + newVer);
			db.execSQL("DROP TABLE IF EXISTS " + AppConstants.TABLE_NAME);
			onCreate(db);
		}
	}		 
}
