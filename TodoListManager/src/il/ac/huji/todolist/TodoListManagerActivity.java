package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

/**
 * TodoListManager Application.
 * Tested on Custom Phone 7 - 4.3 - API 18 - 1024x600 by Genymotion.
 * Empty text cannot be added.
 * User:	masha_os
 *   ID:	332508373
 */
public class TodoListManagerActivity extends Activity {
	private static int REQ_RESULT_FOR_ADD = 42;
	// list view
	private ListView list;
	// custom list adapter setting alternating colors to items in the list
	MyListAdapter lstAdpr;
	// SQLite database
	TasksSQLiteDB todo_db;
	
	/**
	 * Creates ArrayAdapter for the ListView and registers context menu for the list.
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        this.list = (ListView) findViewById(R.id.lstTodoItems);

        // Top Menu
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.add);
        actionBar.show();
        
        // SQLite Database
        this.todo_db = new TasksSQLiteDB(this);
        this.todo_db.open();
        
        // Initialize the list
        this.lstAdpr = new MyListAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Task>()); 
        this.list.setAdapter(this.lstAdpr); 
        registerForContextMenu(list); 
        // ListView, loads values from the database using AsyncTask
        MyAsyncTask asyncTask = new MyAsyncTask(getApplicationContext(), this);
        asyncTask.execute();        
    }
    
    /**
     * Inflates the menu: adds items to the action bar if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_list_menu, menu);
        return true;
    }
    
    /**
     * Creates context menu enabling removing elements from the list, calling.
     * Invoked on long-clicking on an item.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) { 
    	 super.onCreateContextMenu(menu, v, info);    	 
    	 AdapterView.AdapterContextMenuInfo in = (AdapterView.AdapterContextMenuInfo)info;
    	 String selItemVal = lstAdpr.getItem(in.position).getTitle();
    	 menu.setHeaderTitle(selItemVal);	
    	 if (selItemVal.startsWith(AppConstants.CALL_PREFIX)) 
    		 menu.add(Menu.NONE, R.id.menuItemCall, 1, selItemVal);
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.context_menu, menu);    	 
    } 
    
    /**
     * Invoked on pressing context menu button.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	 String selectedValue = this.lstAdpr.getItem(info.position).getTitle();
    	 switch (item.getItemId()) {
    	 case R.id.menuItemDelete:
    		 Task taskToRemove = this.lstAdpr.getItem(info.position);
    		 removeTask(taskToRemove);
    		 notifyListChanges();
    		 break;
      	 case R.id.menuItemThumbnail:
    		 // not relevant for this exercise 
    		 break;
    	 case R.id.menuItemCall:
    	     Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
    	     phoneIntent.setData(Uri.parse("tel:" + selectedValue));
    	     startActivity(phoneIntent);
    		 break;
    	 default:
    		 break;
    	 }
    	 return true;
    }  
    
    /**
     * Invoked on pressing 'add' button in the main menu.
     * Adds a new item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       	if (item.getItemId() == R.id.menuItemAdd) {
       		Intent intent = new Intent(this, AddNewTodoItemActivity.class); 
       		startActivityForResult(intent, REQ_RESULT_FOR_ADD);
       	}
		return true;
    }    
    
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent result) { 
    	if (reqCode == REQ_RESULT_FOR_ADD && resCode == Activity.RESULT_OK) {
    		if (result == null) {
    			Log.e("onActivityResult", "null result");
    			return;
    		}
    		String txt = result.getStringExtra(AppConstants.ITEM_TITLE_VAR);
   	       	if (txt == null || txt.isEmpty()) 
   	       		return;
    		Date dueDate = (Date) result.getSerializableExtra(AppConstants.DATE_VAR);
    		addTask(new Task(dueDate, txt));
    		notifyListChanges();
    	}
	} 
    
    /**
     * Adds the task to the database.
     * @param newTask task to add.
     */
    private void addTask(Task newTask) {
    	this.todo_db.addTask(newTask);
        this.lstAdpr.add(newTask);        
    }
    /**
     * Removes the task from the database.
     * @param newTask task to remove.
     */
    private void removeTask(Task taskToRemove) {
		 this.lstAdpr.remove(taskToRemove);
		 this.todo_db.deleteTask(taskToRemove);
    }
    
    /**
     * Notifies the attached observers that the list adapter data has been 
     * changed and any View reflecting the data set should refresh itself. 
     */
    void notifyListChanges() {
    	this.lstAdpr.notifyDataSetChanged();
    }
    /**
     * Adds the task to the list adapter.
     * @param newTask task to add.
     */
    void addTaskToAdapter(Task newTask) {
    	 this.lstAdpr.add(newTask); 
    }
    
    /**
     * Helper for doing work in the background and posting progress and results.
     */
    public class MyAsyncTask extends AsyncTask<String, Integer, Long> {
    	/**
    	 * Class name to show in the log.
    	 */
    	private final String CLASS_TAG = "myAsyncTask";    	
    	/**
    	 * Interval of updating notifications. 
    	 */
    	private static final int PUBLISH_PROGRESS_INTERVAL = 1000;
    	/**
    	 * Progress dialog for showing notifications.
    	 */
    	private ProgressDialog progress;
    	
    	private TodoListManagerActivity listActivity;
    	
    	private String notificationContent; 
    	
    	/**
    	 * Constructor.
    	 * @param cntxt application context
    	 * @param listActivity ListManager activity
    	 */
    	public MyAsyncTask(Context cntxt, TodoListManagerActivity listActivity) {
    		this.listActivity = listActivity;    		
    		notificationContent = listActivity.getApplicationContext().
    				getString(R.string.initialNotoficationContentStr);
    	}
    	
    	// Initialize progress dialog
    	protected void onPreExecute() {
    		super.onPreExecute();
    		progress = new ProgressDialog(listActivity);
    		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		showProgress(listActivity.getApplicationContext().
    				getString(R.string.initialNotoficationBeginStr));
    	}

    	// Process files and call publishProgress periodically
    	protected Long doInBackground(String... files) {
    		try {    	        
    			ArrayList<Task> l = listActivity.todo_db.selectAll(); 
    			long startTime = System.currentTimeMillis();
    			long passedTime = 0L;
    			int max = l.size();
                for (int i = 0; i < max; i++) {
                	listActivity.addTaskToAdapter(l.get(i));
                	passedTime = System.currentTimeMillis() - startTime;
                	if (passedTime > PUBLISH_PROGRESS_INTERVAL) {
                		startTime = passedTime;
                		publishProgress((i + 1) / max * 100);
                	}
                }
            }
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		publishProgress(100);   
    		// wait a time interval to let user see the last message
    		try {
    			Thread.sleep(PUBLISH_PROGRESS_INTERVAL);
    		} catch (InterruptedException e) {}
    		return null;    		
    	}
    	// Update progress dialog to progress[0]
    	protected void onProgressUpdate(Integer... progress) {
            // create a new notification
    		showProgress(progress[0] + "% " + notificationContent);
    	}
    	
    	// Dismiss progress dialog and display result
    	protected void onPostExecute(Long result) {
    		if (progress.isShowing())
    			progress.dismiss(); 
    		listActivity.notifyListChanges(); 
    	}
    	
    	/**
    	 * Generates new notification with a given content and shows it.
    	 * @param content message to show
    	 */
    	private void showProgress(String content) {
    		Log.i(CLASS_TAG, "generating notice");
    		progress.setMessage(content);
    		progress.show();
    	}
    }
}
