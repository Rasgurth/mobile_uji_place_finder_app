package es.uji.PF;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//Ready
public class TitleActivity extends Activity {
	
	//########################## Activity methods ##########################
	
	    /** 
	     * Called when the activity is first created.
	     * Set the content view: 
	     * The activity "activity_title" 
	     */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_title);
	    }
	
	    /** 
	     * Called when the activity is destroyed. 
	     */
		protected void onDestroy() { 
			super.onDestroy();
		}
	
	    /** 
	     * Called when the activity is paused. 
	     */
		protected void onPause() {
			super.onPause();
		}
	
	    /** 
	     * Called when the activity is resumed. 
	     * Set the buttons of the activity.
	     */
		protected void onResume() {
			super.onResume(); 
			
			setButtons();
		}
	 
	//########################## Buttons listeners ##########################
	
	    /** 
	     * The listener for the return button. 
	     * Start the next activity: The menu.
	     */
	    private OnClickListener m1 = new OnClickListener() {
	        public void onClick(View v) { 
	        	nextScreen();
	        }
		};
	
	//########################## Functions ##########################

		/** 
		 * Set the button's click listeners. 
		 */
		private void setButtons(){
	        ImageButton title = (ImageButton) findViewById(R.id.TitleButton);
	
	    	title.setOnClickListener(m1);
	    }
		
		/** 
		 * Start the next activity, the menu.
		 */
		private void nextScreen(){
	    	Intent intent = new Intent(this, NavigateActivity.class);
	    	
	        startActivity(intent);
	    }
}