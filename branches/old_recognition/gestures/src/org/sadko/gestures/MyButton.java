package org.sadko.gestures;


import android.content.Context;
import android.widget.Button;

public class MyButton extends Button{
	@Override
	public void setOnTouchListener(OnTouchListener l) {
		
		super.setOnTouchListener(l);
	}
	public MyButton(Context context) {
		super(context);
		setBackgroundResource(R.drawable.my_button_on);
	}

}
