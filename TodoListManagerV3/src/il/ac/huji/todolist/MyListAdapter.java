package il.ac.huji.todolist;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Custom array adapter, setting alternating colors to added strings.
 */
public class MyListAdapter extends ArrayAdapter<Task> {
	private final static long MILLISECONDS_PER_DAY = 1000L*60*60*24;
	/*
	 * Class used for holding list adapter consisting of two TextView
	 */
	class RowView {
		TextView title;
		TextView dueDate;
	}
    Activity activity;
    
    public MyListAdapter(Activity activity, int txtViewResID , List<Task> l) {
        super(activity.getApplicationContext(), txtViewResID, l);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
    	View view = v;
        RowView row;
        if (view == null) {
            // Get a new instance of the row layout view
            LayoutInflater inflater = this.activity.getLayoutInflater();
            view = inflater.inflate(R.layout.list_row, null);
            row = new RowView();
            // Hold the view objects in an object, that way the don't need to be "re-  finded"
            row.dueDate = (TextView) view.findViewById(R.id.txtTodoDueDate);
            row.title = (TextView) view.findViewById(R.id.txtTodoTitle);            
            view.setTag(row);
        } else {
            row = (RowView) view.getTag();
        }
        // Set data to views
        Task item = getItem(position);
        row.title.setText(item.getTitle());
        row.dueDate.setText(item.getDateString());
        Date currentDate = new Date();
        long curDateInDays = currentDate.getTime() / MILLISECONDS_PER_DAY;
        long taskDateInDays = item.getDate().getTime() / MILLISECONDS_PER_DAY;;
        long tmp = curDateInDays - taskDateInDays;
        int c = (tmp > 0) ? AppConstants.EXPIRED_TASK_COLOR : AppConstants.DEFAULT_ITEM_COLOR;
        row.title.setTextColor(c);
        row.dueDate.setTextColor(c);   
        return view;
    }
}	
