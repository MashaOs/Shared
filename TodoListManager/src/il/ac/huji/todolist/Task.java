package il.ac.huji.todolist;

import java.text.ParseException;
import java.util.Date;
/**
 * Represents task consisting of title and due date
 */
public class Task {
	// Title of the task
	private String title = null;
	// String representation of due date
	private String dueDate = null;
	/**
	 * Creates a new empty task.
	 */
	protected Task() {}
	/**
	 * Creates new task with given due date and title
	 * @param date date object
	 * @param title title of task
	 */
	public Task(Date date, String title) {
		this.title = title;
		if (date != null) 
			this.dueDate = AppConstants.DATE_FORMAT.format(date);
		else
			this.dueDate = AppConstants.NULL_DATE_MSG;		
	}
	/**
	 * Returns date
	 * @return due date
	 */
	public Date getDate() {
		try {
			return AppConstants.DATE_FORMAT.parse(this.dueDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Returns string representation of due date in the format
	 * defined {@link AppConstants#DATE_FORMAT}
	 * @return string representation of due date
	 */
	public String getDateString() {
		return this.dueDate;
	}
	/**
	 * Returns title of the task
	 * @return title of the task
	 */
	public String getTitle() {
		return this.title;
	}
	
	@Override 
	public boolean equals(Object o) {
		if (o instanceof Task) {
			Task t = (Task) o;
			// the first bit - title, the second one - date
			int thisIsNull = 0, tIsNull = 0; 
			if (t.dueDate == null)
				tIsNull = ((tIsNull + 1) << 1);
			if (t.getTitle() == null)
				tIsNull += 1;
			if (this.dueDate == null)
				thisIsNull = ((thisIsNull + 1) << 1);
			if (this.getTitle() == null)
				thisIsNull += 1;
			// the both tasks are empty
			if ((tIsNull & thisIsNull) == 0x11)
				return true;
			boolean areEqual = true;
			// the both tasks have a date
			if (((tIsNull & thisIsNull) & 0x10) == 0)
				areEqual = areEqual && this.dueDate.equals(t.dueDate);
			// the both tasks have a title
			if (((tIsNull & thisIsNull) & 0x01) == 0)
				areEqual = areEqual && this.title.equals(t.title);
			return areEqual;			
		}
		return false;
	}
	/**
	 * Sets this task's date to the given value.
	 * @param date task's due date
	 */
	public void setDate(Date date) {
		this.dueDate = AppConstants.DATE_FORMAT.format(date);
	}
	/**
	 * Sets this task's date to the given value.
	 * @param date task's due date
	 */
	public void setDate(String date) {
		this.dueDate = date;
		
	}
	/**
	 * Sets this task's title to the given value.
	 * @param title the title of the task
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
