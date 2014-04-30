package il.ac.huji.tipcalculator;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Tip Calculator Application.
 * Tested on Custom Phone 7 - 4.3 - API 18 - 1024x600 by Genymotion.
 * User:	masha_os
 *   ID:	332508373
 */
public class TipCalculatorActivity extends Activity{
	/**
	 * Background color of bill amount box.
	 */
	public static final int BG_COLOR = Color.CYAN;
	/**
	 * Background color of bill amount box in the case, illegal value is entered.
	 */
	public static final int ERROR_BG_COLOR = Color.RED;

	// Tip size
	private static final float TIP_SIZE = 0.1f;
	// Bill amount pattern
	private static final String BILL_PATTERN = "[0-9]+(\\.[0-9]+)?";
	// Stores entered bill amount
	private EditText edtBillAmount;
	// Tip result output
	private TextView txtTipResult;
	// Defines whether to round the tip or not
	private CheckBox chkRound;
	// Calculate button
	private Button btnCalculate;
	
	/*
	 * On creation add two listeners: 'onClick' and 'textChanged'.
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        this.edtBillAmount = (EditText) findViewById(R.id.edtBillAmount);
        this.txtTipResult = (TextView) findViewById(R.id.txtTipResult);
        this.chkRound = (CheckBox) findViewById(R.id.chkRound);
        this.btnCalculate = (Button) findViewById(R.id.btnCalculate);
        addListenerOnClickBtnCalculate();
        addListenerOnChangedBillAmount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tip_calculator, menu);
        return true;
    }

    /**
     * Creates 'onClick' listener for {@link #btnCalculate}
     */
    private void addListenerOnClickBtnCalculate() {
        OnClickListener btnCalculateClicked = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String bill = edtBillAmount.getText().toString();
				String errorString = getErrorMessageForBill(bill);
				if (errorString != null) {
					txtTipResult.setText(errorString);
					return;
				}
				float tip = Float.parseFloat(bill) * TIP_SIZE;
				if (chkRound.isChecked()) {
					tip = Math.round(tip);
				}
				txtTipResult.setText("Your bill: " + bill + "\n");
				txtTipResult.append("Recommended tip: " + tip);
				edtBillAmount.setText("");
			}
		};
		this.btnCalculate.setOnClickListener(btnCalculateClicked);
    }  
    
    /**
     * Creates 'textChanged' listener for {@link #edtBillAmount} that 
     * changes its background color to {@link #BG_COLOR} if entered value
     * is a valid float or integer, to {@link #ERROR_BG_COLOR} otherwise.
     */
    private void addListenerOnChangedBillAmount() {
    	TextWatcher billAmountEdited = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {			
			}

			@Override
			public void beforeTextChanged(CharSequence txt, int start, int before, int c) {
			
			}

			@Override
			public void onTextChanged(CharSequence txt, int start, int before, int count) {
				String bill = txt.toString();
				if (getErrorMessageForBill(bill) != null && bill.length() > 0) 
					edtBillAmount.setBackgroundColor(ERROR_BG_COLOR);
				else
					edtBillAmount.setBackgroundColor(BG_COLOR);
			}
    		
    	};
    	this.edtBillAmount.addTextChangedListener(billAmountEdited);
    }
 
    /**
     * Verifies that given string represents a legal float or integer.
     * @param bill string bill
     * @return error message if bill is illegal, null otherwise.
     */
    private static String getErrorMessageForBill(String bill) {
		if ((bill == null) || (bill.length() == 0)) {
			return "Enter bill amount.";
		}
		if (!bill.matches(BILL_PATTERN)) {
			return "Invalid bill. Allowed characters are digits and \".\".";
		}
    	return null;
    }
}

