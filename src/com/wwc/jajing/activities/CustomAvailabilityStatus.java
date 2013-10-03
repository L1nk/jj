package com.wwc.jajing.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wwc.jajing.R;

public class CustomAvailabilityStatus extends Activity implements OnClickListener{

	Button save;
	EditText customMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_message);

		save = (Button) findViewById(R.id.buttonSaveCustomMessage);
		customMessage = (EditText) findViewById(R.id.editCustomMessage);
		save.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}

	@Override
	public void onClick(View view) {
		
		String message = this.customMessage.getText().toString();

		if(this.isValidCustomMessage(message)) {
			Intent i = new Intent(this, AwayOptions.class);
			i.putExtra("custom", message);
			this.startActivity(i);
		} else {
			Toast.makeText(this, "invalid message", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	private boolean isValidCustomMessage(String aCustomMessage)
	{
		if(aCustomMessage.trim().equalsIgnoreCase("")) 
			return false;
		else 
			return true;
	}

}
