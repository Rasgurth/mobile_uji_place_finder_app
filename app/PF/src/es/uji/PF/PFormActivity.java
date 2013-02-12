package es.uji.PF;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import es.uji.PF.classes.Person;
import es.uji.PF.classes.QueryOfficeResults;
import es.uji.PF.tasks.OfficesTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class PFormActivity extends Activity {

	//##### Variables #####
	
		int option=0;
		String extra="";
	
		private static List<Person> persons;
		private QueryOfficeResults query=new QueryOfficeResults();
		public ProgressDialog progressDialog; 
		Spinner spinner;


	//##### Activity methods #####
	
	    /** 
	     * Called when the activity is first created.
	     * Set the content view "activity_search",
	     * and set the "extra" value with 
	     * the extra of the intent.
	     */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_search);
	        
	        extra=getIntent().getStringExtra("type");	
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
	     * Set the spinner and the buttons of the activity.
	     */
		protected void onResume() {
			super.onResume(); 
			
	        setSpinner();
	        setButtons();  
		}
	
	//##### Buttons listeners #####
	   
	    /** 
	     * The listener for the return button.
		 */
	    private OnClickListener snd = new OnClickListener() {
	        public void onClick(View v) { 
	        	res();
	        }
		};
		
	    /** 
	     * The listener for the results button.
		 */
	    private OnClickListener rslts = new OnClickListener() {
	        public void onClick(View v) { 
	        	nextScreen();
	        }
		};
		
	//##### Functions #####


	    /** 
	     * Set the query and run it.
	     * Set enabled to true.
		 */
		private void res() {
			Button res = (Button) findViewById(R.id.TextResults);

	    	createQuery();
			
			res.setEnabled(true);
		}
		
		/**
		 * Start the next activity.
		 * Put the extras "use" and "criteria",
		 * start the activity "ResultActivity"
		 * and finish the actual activity.
		 */
		private void nextScreen(){
	    	Intent intent = new Intent(this, ResultActivity.class);

			save();

			intent.putExtra("use", "res");
			intent.putExtra("criteria", getIntent().getStringExtra("type"));
			startActivity(intent);
			finish();
	    }

		/**
		 * Depending on the search option
		 * chosen in the spinner and the option
		 * that was chosen in the search menu,
		 * run the query with a different url.
		 */
		private void createQuery(){
			EditText input= (EditText) findViewById(R.id.InputText);
			String option=spinner.getSelectedItem().toString();
			String text=""+input.getText();
			
			if(extra.equalsIgnoreCase("place")){
				
				//if(option.equals(getString(R.string.RadioName))){
				//	Toast.makeText(getApplicationContext(), getString(R.string.comingSoon).toString(), Toast.LENGTH_LONG).show();
				//} else{
					new OfficesTask(this).execute(buildURL(1, text));
				//}
	    	} else if(extra.equalsIgnoreCase("person")){
	    		
	    		if(option.equalsIgnoreCase(getString(R.string.RadioName))){
					new OfficesTask(this).execute(buildURL(2, text));
					
	    		}else if(option.equals(getString(R.string.RadioTlf))){
					new OfficesTask(this).execute(buildURL(3, text));
					
	    		}else if(option.equals(getString(R.string.RadioTitle))){
					new OfficesTask(this).execute(buildURL(4, text));
					
	    		}
	    		else{
					new OfficesTask(this).execute(buildURL(5, text));
	    		}
	    	}
		}

		/**
		 * Build a url using the option chosen and
		 *  the input writen by the user.
		 *  @param option The option chosen by the user.
		 *  @param input The string writen by the user.
		 *  @return Complete url to use in a search.
		 */
		private String buildURL(int option, String input){
			//Local host from the android virtual device 
			//String url = "http://10.0.2.2:8080/Locations/";
			
			//UJI server 
			String url="http://geotec.dlsi.uji.es:8080/Locations/";
			
			switch (option) {
		        case 1:  url=url+"offices/"+input;
		        		 break;
		        case 2:  url=url+"people/"+input;
       		 			 break;
		        case 3:  url=url+"telephones/"+input;
       		 			 break;
		        case 4:  url=url+"titles/"+input;
       		 			 break;
		        case 5:  url=url+"mails/"+input;
       		 			 break;
		        default: url=url+"offices/"+input;
			}
			url=url.replaceAll(" ", "%20");
			return url;
			
		}

		/** 
		 * Set the spinner and its option list. 
		 */
		private void setSpinner() {
		    TextView title= (TextView) findViewById(R.id.SearchTitle);
		    
			if(extra.equals("place")){
				option=R.array.place_search;
				title.setText(R.string.SearchPlaceTitle);
			} else{
				option=R.array.people_search;
				title.setText(R.string.SearchPeopleTitle);
			}
		    
		    spinner = (Spinner) findViewById(R.id.SearchSpinner);
		    // Create an ArrayAdapter using the string array and a default spinner layout
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		         option, android.R.layout.simple_spinner_item);
		    // Specify the layout to use when the list of choices appears
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    // Apply the adapter to the spinner
		    spinner.setAdapter(adapter);
			
		}

		/** 
		 * Set the button's click listeners. 
		 */
		private void setButtons() {
			Button send = (Button) findViewById(R.id.SendButton);
			Button res = (Button) findViewById(R.id.TextResults);
			
			send.setOnClickListener(snd);
			res.setOnClickListener(rslts);
			
			res.setEnabled(false);
		}

		/** 
		 * Save the query results in a file. 
		 */
		protected void save(){
	    	FileOutputStream fos = null;
	
	    	try {
				fos = openFileOutput("q.xml", MODE_PRIVATE);
	
	    	} catch (Exception e) {
	    		Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
	    	}
	        query.save(fos,query);
		}

		/** 
		 * Load the query results from a file. 
		 */
		protected void load(){
			FileInputStream fin = null;
		
			try {
				fin = openFileInput("q.xml");
			} catch (Exception e) {
	    		Toast.makeText(getApplicationContext(), "file not opened", Toast.LENGTH_LONG).show();
			}
			query=query.load(fin);
		}
	

		/** 
		 * Set the interface to show the results of a query. 
		 * @param persons A list of persons,
		 * representing the results of a query.
		 */
		public void setResult(List<Person> persons) {
			this.persons = persons;

			query.setOffices(persons);
			
			Button res = (Button) findViewById(R.id.TextResults);
	    	
			if(extra.equalsIgnoreCase("place"))
				res.setText(getString(R.string.button_ViewOnTheMap_part1).toString()+" "+query.getOfficesNum()+" "+getString(R.string.button_ViewOnTheMap_part2offices).toString());
			else if(extra.equalsIgnoreCase("person"))
				res.setText(getString(R.string.button_ViewOnTheMap_part1).toString()+" "+persons.size()+" "+getString(R.string.button_ViewOnTheMap_part2offices).toString());
			
			res.setEnabled(true);
		}

}