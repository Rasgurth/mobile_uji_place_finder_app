package es.uji.PF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import es.uji.PF.classes.QueryOfficeResults;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MenuActivity extends Activity {
	
	//##### Activity methods #####
	
	    /** 
	     * Called when the activity is first created. 
	     * Set the content view: 
	     * The activity "activity_menu"
	     */
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_menu);
	        
	        favoritesInit();
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

	//##### Buttons listeners #####
	
	    /**
	     * The listener for the place button. 
	     * Start the next activity: Search places.
	     */
	    private OnClickListener m1 = new OnClickListener() {
	        public void onClick(View v) { 
	        	otherScreen(1);
	        }
		};
		   
	    /** 
	     * The listener for the person button. 
	     * Start the next activity: Search people.
	     */
	    private OnClickListener m2 = new OnClickListener() {
	        public void onClick(View v) { 
	        	otherScreen(2);
	        }
		};
		   
	    /** 
	     * The listener for the facilities button. 
	     * Start the next activity: Search facilities.
	     */
	    private OnClickListener m3 = new OnClickListener() {
	        public void onClick(View v) { 
	        	otherScreen(3);
	        }
		};
	

	//##### Functions #####

		/** 
		 * Set the button's click listeners. 
		 */
		private void setButtons() { 
	        ImageButton place = (ImageButton) findViewById(R.id.menuPlaceImageButton);
	        ImageButton person = (ImageButton) findViewById(R.id.menuPersonImageButton);
	        ImageButton facilities = (ImageButton) findViewById(R.id.menuFacilitiesImageButton);
	        
	    	place.setOnClickListener(m1);
	    	person.setOnClickListener(m2);
	    	facilities.setOnClickListener(m3);
		}

		/** 
		 * If don't exist the file to store
		 * the favorites of the user,
		 *  initialize it. 
		 */
		private void favoritesInit(){
			FileInputStream fin = null;
			FileOutputStream fos = null;
			QueryOfficeResults aux = new QueryOfficeResults();
			ObjectInputStream is = null;
			ObjectOutputStream oos=null;
			
			try {
				fin = openFileInput("f.xml");
				is = new ObjectInputStream(fin);
				aux= (QueryOfficeResults) is.readObject();
				is.close();
			} catch (FileNotFoundException e) {
				try {
					fos = openFileOutput("f.xml", MODE_PRIVATE);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(aux);
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		/** 
		 * Start other activity.
		 * The parameter provided indicate the next activity:
		 * search places (1), people (2) or facilities (3). 
		 * @param option The option to select the next screen.
		 */
		private void otherScreen(int option){
	    	Intent intent = null;
	    	switch (option) {
		        case 1:  intent = new Intent(this, PFormActivity.class);
		        		 intent.putExtra("type", "place");
		        		 startActivity(intent);
		                 break;
		        case 2:  intent = new Intent(this, PFormActivity.class);
       		 			 intent.putExtra("type", "person");
		        		 startActivity(intent);
		                 break;
		        case 3:  Toast.makeText(getApplicationContext(), getString(R.string.comingSoon).toString(), Toast.LENGTH_LONG).show();
		        		 //intent = new Intent(this, FFormActivity.class);
       		 			 //startActivity(intent);
		                 break;
		        default: intent = new Intent(this, TitleActivity.class);
		        		 startActivity(intent);
		                 break;
	    	}
	    }
}