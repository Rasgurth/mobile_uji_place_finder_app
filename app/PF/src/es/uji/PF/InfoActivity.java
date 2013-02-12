package es.uji.PF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import es.uji.PF.classes.Office;
import es.uji.PF.classes.QueryOfficeResults;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends Activity {
	private Office off;
	
	//##### Activity methods #####
	
	    /** 
	     * Called when the activity is first created. 
	     * Set the content view: 
	     * The activity "activity_info"
	     */
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_info);
	        
	        off=(Office) getIntent().getSerializableExtra("Office");
	        
	        setText();
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
	     * The listener for the "add to favorites" button. 
	     * Include the office in the favorite list.
	     */
	    private OnClickListener addFav = new OnClickListener() {
	        public void onClick(View v) { 
	        	toFav(off);
	        }
		};
		
		/**
	     * The listener for the "delete from favorites" button. 
	     * Delete the office from the favorite list.
	     */
	    private OnClickListener delFav = new OnClickListener() {
	        public void onClick(View v) { 
	        	delFav(off);
	        }
		};
		   

	//##### Functions #####

		/** 
		 * Set the button's click listeners. 
		 * If the previous activity shown 
		 * the favorite list, the button will be
		 * the "delete button". In other case,
		 * the button will be the "add to favorites".
		 */
		private void setButtons() { 
	        ImageButton favorites = (ImageButton) findViewById(R.id.fav_button);
	        
	        if(getIntent().getStringExtra("origin").equalsIgnoreCase("fav")){
	        	favorites.setImageResource(R.drawable.buttonstar_del_small);
	        	favorites.setOnClickListener(delFav);
	        }else{
	        	favorites.setOnClickListener(addFav);
	        }
	        
		}


		/** 
		 * Set the text with the office information. 
		 */
		private void setText(){
			TextView info = (TextView) findViewById(R.id.Info);
			
			info.setText(Html.fromHtml(off.toString()));
		}


		/** 
		 * Add the office to the favorite list.
		 * Open the favorites file, read the favorites list,
		 * add the office to the list
		 * or actualize it if is already in,
		 * and rewrite the favorite file with
		 * the favorite list.
		 */
		protected void toFav(Office off) {
			
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
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				int pos=aux.isIn(off);
				
				if(pos==-1){
					aux.addOffice(off);
		    		Toast.makeText(getApplicationContext(), 
		    				getString(R.string.button_FavoritesAddDel_part1).toString()
		    				+off.getRoomNumber()
		    				+getString(R.string.button_FavoritesAddDel_part2added).toString(), Toast.LENGTH_LONG).show();
				}else{
					aux.delOffice(off);
					aux.addOffice(off);
					Toast.makeText(getApplicationContext(), 
							getString(R.string.button_FavoritesAddDel_part1actualized).toString()
							+off.getRoomNumber()
							+getString(R.string.button_FavoritesAddDel_part2added).toString(), Toast.LENGTH_LONG).show();
				}

				try {
					fos = openFileOutput("f.xml", MODE_PRIVATE);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(aux);
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}

		/** 
		 * Delete the office from favorite list.
		 * Open the favorites file, read the favorites list,
		 * delete the office from the list,
		 * and rewrite the favorite file with
		 * the favorite list. After the delete action,
		 * start the activity "resultactivity" with 
		 * the "fav" use.
		 */
		protected void delFav(Office off) {
			Intent intent;

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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			Toast.makeText(getApplicationContext(), 
					getString(R.string.button_FavoritesAddDel_part1).toString()
					+off.getRoomNumber()
					+getString(R.string.button_FavoritesAddDel_part2deleted).toString(), Toast.LENGTH_LONG).show();
			
			aux.delOffice(off);
			
			try {
				fos = openFileOutput("f.xml", MODE_PRIVATE);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(aux);
				oos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			intent = new Intent(this, ResultActivity.class); 
   		 	intent.putExtra("use", "fav");
   		 	
   		    startActivity(intent);
	}
}