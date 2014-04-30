package il.ac.huji.todolist;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Dialog activity, used for adding new items into the list.
 */
public class AddNewTodoItemActivity extends Activity{

	// Pressing OK button leads to passing input to the caller
	private Button okBtn;
	// Pressing Cancel button leads to stopping this activity
	private Button cancelBtn;
	// Date input field, whose values are passed to the main activity
	private DatePicker datePkr;
	// List item text, passed to the main activity
	private EditText itemTxt;
	// Instance
	private AddNewTodoItemActivity instance;
	
	/**
	 * Creates Dialog for adding new items to the list.
	 */
	@Override
	public void onCreate(Bundle unused) {
		super.onCreate(unused);
		setContentView(R.layout.add_item);		
		this.okBtn = (Button) findViewById(R.id.btnOK);
		this.cancelBtn = (Button) findViewById(R.id.btnCancel);
		this.datePkr = (DatePicker) findViewById(R.id.datePicker);
		this.itemTxt = (EditText) findViewById(R.id.edtNewItem);	
		this.instance = this;
		OnClickListener buttonListener = new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				Button btn = (Button) v;
				if (btn.getId() == okBtn.getId()) {
					Intent result = new Intent();
					result.putExtra(AppConstants.ITEM_TITLE_VAR, itemTxt.getText().toString());
					Calendar c = Calendar.getInstance();
					c.set(datePkr.getYear(), datePkr.getMonth(), datePkr.getDayOfMonth());
					Date dueDate = c.getTime();		
					result.putExtra(AppConstants.DATE_VAR, dueDate);
					setResult(RESULT_OK, result);
				}
				else if (btn.getId() == cancelBtn.getId()) 
					setResult(RESULT_CANCELED);
				instance.finish();   
		    }
		};
		this.okBtn.setOnClickListener(buttonListener);
		this.cancelBtn.setOnClickListener(buttonListener);
	}
}
