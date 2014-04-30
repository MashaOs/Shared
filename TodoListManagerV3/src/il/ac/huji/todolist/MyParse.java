package il.ac.huji.todolist;

import java.util.List;
import android.content.Context;
import android.util.Log;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Class used for storing the application data in the Parse cloud.
 * Location: https://www.parse.com/apps/todolistmanager--30/collections
 */
public class MyParse {
	
	/**
	 * Class tag
	 */
	private static final String TAG = "PARSE";
	/**
	 * Initializes the Parse.
	 * @param ctx application context
	 */
	public MyParse(Context ctx) {
        // Associate with the anonymous user 
    	ParseUser.enableAutomaticUser();
        Parse.initialize(ctx, "vowh0UTBzIZpbFbC9hP6WQ3qzomeek0x5LVSwozS", "cTUm5xVxs2upKPNyhbKsDTFEnohVfYFu0F0mnwPl");
		ParseACL.setDefaultACL(new ParseACL(), true);
	}
	/**
	 * Saves the given task to the server.
	 * @param task task to save
	 */
	public void addTask(Task task) {
//		ParseUser.enableAutomaticUser();
		ParseObject newTask = new ParseObject(AppConstants.TABLE_NAME);
		newTask.put(AppConstants.DATE_COL, task.getDateString());
		newTask.put(AppConstants.TITLE_COL, task.getTitle());
		newTask.saveInBackground();
		Log.i(TAG, "Parse object added: " + newTask.toString());
	}

	/**
	 * Removes the given task.
	 * @param task task to remove
	 */
	public void removeTask(Task task) {
//		ParseUser.enableAutomaticUser();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(AppConstants.TABLE_NAME); 
		query.whereEqualTo(AppConstants.DATE_COL, task.getDateString());
		query.whereEqualTo(AppConstants.TITLE_COL, task.getTitle());
		query.findInBackground(new FindCallback<ParseObject>() {		
			@Override
			public void done(List<ParseObject> l, ParseException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage());
				}
				else {
					if (l.size() == 0)
						Log.i(TAG, "Delete: parse objects not found.");
					for (ParseObject o : l) {
						try {
							Log.i(TAG, "Parse object deleted: " + o.toString());
							o.delete();							
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
}
