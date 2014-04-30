package il.ac.huji.todolist;

import java.text.SimpleDateFormat;

import android.graphics.Color;

/**
 * Constants used in application, specified in exercises descriptions.
 */
public class AppConstants {
	/**
	 * When user types text starting with "Call ", additional 
	 * menu item, enabling dialing a number, is shown 
	 */
	public static final String CALL_PREFIX = "Call ";
	/**
	 * Phone pattern
	 */
	public static final String PHONE_PATTERN = "[\\d-]+";
	/**
	 * Name used for passing list item title from the {@link AddNewTodoItemActivity}
	 * to {@link TodoListManagerActivity} 
	 */
	public static final String ITEM_TITLE_VAR = "title";
	/**
	 * Name used for passing typed date from the {@link AddNewTodoItemActivity}
	 * to {@link TodoListManagerActivity} 
	 */
	public static final String DATE_VAR = "dueDay";
	/**
	 * Message shown when no date is chosen
	 */
	public static final String NULL_DATE_MSG = "No due date";
	/**
	 * Date format
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	/**
	 * Default color of list items
	 */
	public static final int DEFAULT_ITEM_COLOR = Color.BLACK;
	/**
	 * Color of overdue list items
	 */
	public static final int EXPIRED_TASK_COLOR = Color.RED;
	/**
	 * Database name, specified in the exercise description.
	 */
	public static final String DB_NAME = "todo_db";	
	/**
	 * Table name, specified in the exercise description.
	 * When storing data on the cloud, this name is used as a class name.
	 */
	public static final String TABLE_NAME = "todo";
	
	/**
	 * In SQLite database: ID column name.
	 */
	public static final String ID_COL = "_id";
	/**
	 * In a database: title column name.
	 */
	public static final String TITLE_COL = "title";
	/**
	 * In a database: due date column name.
	 */
	public static final String DATE_COL = "due";
	/**
	 * In SQLite database: table columns names.<br>
	 * Include {@link #ID_COL}, {@link #TITLE_COL}, {@link #DATE_COL}.
	 */
	public static final String SQLITE_TABLE_COLS[] = {ID_COL, TITLE_COL, DATE_COL};
	/**
	 * In Parse object: fields names.<br>
	 * Include {@link #TITLE_COL}, {@link #DATE_COL}.
	 */
	public static final String PARSE_FIELDS[] = {TITLE_COL, DATE_COL};
}
