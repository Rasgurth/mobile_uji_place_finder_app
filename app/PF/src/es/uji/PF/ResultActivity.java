package es.uji.PF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;


import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.ags.ArcGISFeatureLayer.Options;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import es.uji.PF.classes.Office;
import es.uji.PF.classes.Person;
import es.uji.PF.classes.QueryOfficeResults;
import es.uji.PF.tasks.RoomTask;

public class ResultActivity extends Activity  implements LocationListener, SensorEventListener {

	//##### Variables #####
	
	//----- ArcGIS APK ----- 
	
	final static double SEARCH_RADIUS = 5;
	MapView map = null;
	
	String mapURL = "http://geotec.dlsi.uji.es:6080/arcgis/rest/services";
	
	ArcGISTiledMapServiceLayer basemap = null;
	ArcGISTiledMapServiceLayer floorplan = null;
	ArcGISFeatureLayer ujiFL;
	static GraphicsLayer [] gl = null;//pos 0 the floor, pos 1 the room.
	Graphic[] floorGraphics;
	
	double m_tapx;
	double m_tapy;
	Callout callout;
	Drawable jobIcon;
	FeatureSet fset;
	
	SimpleFillSymbol floorSS = new SimpleFillSymbol(Color.GRAY);
	SimpleFillSymbol roomSS = new SimpleFillSymbol(Color.BLUE);

	LocationService ls = null;

	//----- location and orientation ----- 
	Location actLoc=new Location("GPS");
	float az=0;
	private LocationManager locationManager;
	private String provider;
	private SensorManager mSensorManager;
	private Sensor mAcc;
	private Sensor mMag;
    private float[] Rotation_data = new float[9];
	private float[] Inclination_data = new float[9];
    private float[] acc_data=new float[3];
	private float[] mag_data=new float[3];
	private float[] angles=new float[3];
	
	//----- aux variables -----

	private static QueryOfficeResults query=new QueryOfficeResults();
	private List<Office> pageResults=null;
	public ProgressDialog progressDialog;
	private String extra="";
	private boolean favorites=false;
	public float x, y;
	private int selected=0;

	//##### Activity methods #####

	
    /** 
     * Called when the activity is first created.
     * */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //----- ArcGIS APK ----- 
        
			// Retrieve the map and initial extent from XML layout
			map = (MapView)findViewById(R.id.map);
	
			// Add featured layer to MapView
			Options o = new Options();
			o.mode = MODE.ONDEMAND;
			//o.outFields=new String[] {"Shape","SPACEID","BUILDING","FLOOR"};
			ujiFL = new ArcGISFeatureLayer(
					mapURL+"/UJI/UJIBuildingInterior/MapServer/1",
					o);
			floorSS.setOutline(new SimpleLineSymbol(Color.YELLOW, 2));
			ujiFL.setSelectionSymbol(floorSS);
			map.addLayer(ujiFL);
			
			// Add tiled layer to MapView
			basemap = new ArcGISTiledMapServiceLayer(mapURL+"/UJI/viscaUJI_desaturado/MapServer");
			map.addLayer(basemap);
			
			
			gl=new GraphicsLayer[2];
			gl[0]=new GraphicsLayer();
			gl[1]=new GraphicsLayer();
	
			map.addLayer(gl[0]);
			map.addLayer(gl[1]);
			
			ls = map.getLocationService();
			ls.setAccuracyCircleOn(true);
			ls.setAutoPan(true);
			
			callout=new Callout(map);
			
			   
	    	// Sets 'OnSingleTapListener' so that when single tap
			// happens, Callout would show 'SQMI' information associated
			// with tapped 'Graphic'
			map.setOnSingleTapListener(new OnSingleTapListener() {
	
				private static final long serialVersionUID = 1L;
	
				public void onSingleTap(float x, float y) {
	
					if (!map.isLoaded())
						return;
					int[] uids = gl[1].getGraphicIDs(x, y, 2);
					if (uids != null && uids.length > 0) {
	
						int targetId = uids[0];
						Graphic gr = gl[1].getGraphic(targetId);
					    callout = map.getCallout();
					    
					    // Sets Callout style
					   callout.setStyle(R.xml.pop);
					   String Name = (String) gr.getAttributeValue("SPACEID");
					   String Desc = getDesc(Name);
					   
					    // Sets custom content view to Callout
					   callout.setContent(loadView(Name, Desc));
					   callout.show(map.toMapPoint(new Point(x, y)));
					}else {
						if (callout != null && callout.isShowing()) {
							callout.hide();
						}
					}
	
				}
			});
		
		//----- location -----
	        // Get the location manager
	        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        // Define the criteria how to select the location provider -> use
	        // default
	        Criteria criteria = new Criteria();
	        provider = locationManager.getBestProvider(criteria, false);
	        Location location = locationManager.getLastKnownLocation(provider);
	
	        // Initialize the location fields
	        if (location != null) {
	          System.out.println("Provider " + provider + " has been selected.");
	          onLocationChanged(location);
	        } else {
	        	//  latituteField.setText("Location not available");
	        	//  longitudeField.setText("Location not available");
	        }

        //----- orientation -----
	        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        mMag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    

        //----- Get the query -----

	        if(getIntent().getStringExtra("use").equalsIgnoreCase("res")){
	        	favorites=false;
	        	extra=getIntent().getStringExtra("criteria");
	        	load(1);
	        }else if(getIntent().getStringExtra("use").equalsIgnoreCase("fav")){
	        	favorites=true;
	        	load(2);
	        }
    }
  

    /** 
     * Called when the activity is first created.
     * */ 
	protected void onDestroy() { 
		super.onDestroy();
	}

    /** 
     * Called when the activity is first created.
     * */
	protected void onPause() {
		super.onPause();
		map.pause();

    	locationManager.removeUpdates(this);
    }

    /** 
     * Called when the activity is first created.
     * */
	protected void onResume() {
		super.onResume(); 
		map.unpause();
		
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);

        setButtons();
        showResults();
        actualizePage();
        if(favorites)
        	setFavoritesInterface();
	}

    /** 
     * Called when the activity is first created.
     * */
	protected void onStop() {
    	super.onStop();
    	
        mSensorManager.unregisterListener(this);
        
        //ls.stop();
    }
	
	//##### Buttons listeners #####
	
	/** 
	 * The listener for the current location button.
	 */
    private OnClickListener loc = new OnClickListener() {
        public void onClick(View v) { 
        	locationShifter();
        }
	};
	
    /** 
     * The listener for the find button.
	 */
    private OnClickListener fnd = new OnClickListener() {
        public void onClick(View v) { 
        	otherScreen(1);
        }
	};
	
	
    /** 
     * The listener for the find button.
	 */
    private OnClickListener nvt = new OnClickListener() {
        public void onClick(View v) { 
        	otherScreen(2);
        }
	};
	
	/** 
	 * The listener for the favorites button.
	 * */
	private OnClickListener fv = new OnClickListener() {
	    public void onClick(View v) { 
        	otherScreen(3);
	    }
	};


	/** The listener for the result 1 button.
	*/
	private OnClickListener res1 = new OnClickListener() {
		public void onClick(View v) { 
			select(1);
		}
	};
	
	/** 
	 * The listener for the result 2 buttons.
	 * */
	private OnClickListener res2 = new OnClickListener() {
		public void onClick(View v) { 
			select(2);
		}
	};
	
	/** 
	 * The listener for the result 3 buttons.
	*/
	private OnClickListener res3 = new OnClickListener() {
		public void onClick(View v) { 
			select(3);
		}
	};
	
	/** The listener for the result 4 button.
	*/
	private OnClickListener res4 = new OnClickListener() {
		public void onClick(View v) { 
			select(4);
		}
	};
	
	/** The listener for the previous page button.
	*/
	private OnClickListener prv = new OnClickListener() {
		public void onClick(View v) { 
			prevPage();
		}
	};

	/** The listener for the next page button.
	*/
	private OnClickListener pst = new OnClickListener() {
		public void onClick(View v) { 
			nextPage();
		}
	};

	/** The listener for the next page button.
	*/
	private OnClickListener toFav = new OnClickListener() {
		public void onClick(View v) { 
			toFav(selected);
		}
	};
	
	/** The listener for the next page button.
	*/
	private OnClickListener delFav = new OnClickListener() {
		public void onClick(View v) { 
			delFav(selected);
		}
	};
	

	/** The listener for the next page button.
	*/
	private OnClickListener toInf = new OnClickListener() {
		public void onClick(View v) { 
        	otherScreen(4);
		}
	};
	
	//##### Sensors functions #####
	
	public void onLocationChanged(Location location) {
		actLoc=location;
	}
	

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acc_data[0]=event.values[0];
            acc_data[1]=event.values[1];
            acc_data[2]=event.values[2];
        }else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mag_data[0]=event.values[0];
            mag_data[1]=event.values[1];
            mag_data[2]=event.values[2];
        }

		SensorManager.getRotationMatrix(Rotation_data, Inclination_data, acc_data, mag_data);
		SensorManager.getOrientation(Rotation_data, angles);

        rotate();
	}
	

	//##### Functions #####

		
	/** 
	 * The creation of the options menu, 
	 * accessible from the menu button of the device.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
	    return true;
	}

	/** 
	 * The different functionalities for the options menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent(this, TitleActivity.class);
	    switch (item.getItemId()) {
	        case R.id.Building:
	        	startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	/** 
	 * Set the favorites interface. 
	 */
	private void setFavoritesInterface(){
	    ImageButton fav = (ImageButton) findViewById(R.id.FavouritesButton);
	    TextView title = (TextView) findViewById(R.id.PageTitle);
	    
		fav.setEnabled(false);
		fav.setBackgroundResource(R.drawable.trans);
		fav.setImageResource(R.drawable.trans);
		
		title.setText(getText(R.string.favorites));
	}

	/** 
	 * Set the button's click listeners. 
	 */
	protected void setButtons(){
	    ImageButton pos = (ImageButton) findViewById(R.id.PositionButton);
	    ImageButton find = (ImageButton) findViewById(R.id.FindButton);
	    ImageButton nav = (ImageButton) findViewById(R.id.NavigateButton);
	    ImageButton fav = (ImageButton) findViewById(R.id.FavouritesButton);
	    ImageButton prev = (ImageButton) findViewById(R.id.PrevButton);
	    ImageButton post = (ImageButton) findViewById(R.id.PostButton);
	    
		ImageButton bnum1 = (ImageButton) findViewById(R.id.ButtonNum1);
		ImageButton bid1 = (ImageButton) findViewById(R.id.ButtonID1);
		ImageButton bnum2 = (ImageButton) findViewById(R.id.ButtonNum2);
		ImageButton bid2 = (ImageButton) findViewById(R.id.ButtonID2);
		ImageButton bnum3 = (ImageButton) findViewById(R.id.ButtonNum3);
		ImageButton bid3 = (ImageButton) findViewById(R.id.ButtonID3);
		ImageButton bnum4 = (ImageButton) findViewById(R.id.ButtonNum4);
		ImageButton bid4 = (ImageButton) findViewById(R.id.ButtonID4);
	
		pos.setOnClickListener(loc);
		find.setOnClickListener(fnd);
		nav.setOnClickListener(nvt);
		fav.setOnClickListener(fv);
		prev.setOnClickListener(prv);
		post.setOnClickListener(pst);
		
		bnum1.setOnClickListener(res1);
		bnum2.setOnClickListener(res2);
		bnum3.setOnClickListener(res3);
		bnum4.setOnClickListener(res4);
		bid1.setOnClickListener(res1);
		bid2.setOnClickListener(res2);
		bid3.setOnClickListener(res3);
		bid4.setOnClickListener(res4);
		
		prev.setEnabled(false);
	}

	/** 
	 * Increase the page number and
	 * actualize the page. 
	 */
	private void prevPage() {
		query.decrPage();
		actualizePage();
	}
	
	/** 
	 * Decrease the page number and
	 * actualize the page. 
	 */
	private void nextPage() {
		query.incrPage();
		actualizePage();
	}


	/** 
	 * Actualize the page with the results list. 
	 */
	private void actualizePage(){
	    ImageButton prev = (ImageButton) findViewById(R.id.PrevButton);
	    ImageButton post = (ImageButton) findViewById(R.id.PostButton);
	    TextView numeration = (TextView) findViewById(R.id.PageText);
	    
	    numeration.setText(query.getPage()+"/"+query.getPagesNum());
	    
		if(query.getPage()==1){
			prev.setEnabled(false);
		}else{
			prev.setEnabled(true);
		}
		if(query.getPage()==query.getPagesNum()){
			post.setEnabled(false);
		}else{
			post.setEnabled(true);
		}
		
		select(-1);
		
		prepareResults();
		
		showResults();
	}

	/** 
	 * Extract the results to show in the actual page
	 * from the query results. 
	 */
	private void prepareResults() {
		pageResults=query.getResultsActualPage(); 	
	}

	/** 
	 * Select one of the 4 results.
	 * If i is different of 1,2,3 or 4,
	 * none is selected.
	 * @param i The result to selected
	 */
	private void select(int i){
		String bldng;
		int flr;
		String rmd=null;

		callout.hide();
		
		selected=i;
		
		ImageButton bnum1 = (ImageButton) findViewById(R.id.ButtonNum1);
		ImageButton bid1 = (ImageButton) findViewById(R.id.ButtonID1);
		ImageButton bnum2 = (ImageButton) findViewById(R.id.ButtonNum2);
		ImageButton bid2 = (ImageButton) findViewById(R.id.ButtonID2);
		ImageButton bnum3 = (ImageButton) findViewById(R.id.ButtonNum3);
		ImageButton bid3 = (ImageButton) findViewById(R.id.ButtonID3);
		ImageButton bnum4 = (ImageButton) findViewById(R.id.ButtonNum4);
		ImageButton bid4 = (ImageButton) findViewById(R.id.ButtonID4);
		
		if(i==1){
			rmd=""+pageResults.get(0).getRoomNumber();

			if(rmd.equalsIgnoreCase("null")){
				showBuildingImage(0, 0);
	    		Toast.makeText(getApplicationContext(), getString(R.string.noOffice).toString(), Toast.LENGTH_LONG).show();
	    		otherScreen(4);
			}else{
				bldng=""+rmd.charAt(0)+rmd.charAt(1);
				flr=Character.getNumericValue(rmd.charAt(3));
				showRoom(bldng, flr, rmd);
				showBuildingImage(5, flr);
			}
			bnum1.setSelected(true);
			bid1.setSelected(true);
			bnum2.setSelected(false);
			bid2.setSelected(false);
			bnum3.setSelected(false);
			bid3.setSelected(false);
			bnum4.setSelected(false);
			bid4.setSelected(false);
		}else if(i==2){
			bnum1.setSelected(false);
			bid1.setSelected(false);
			bnum2.setSelected(true);
			bid2.setSelected(true);
			bnum3.setSelected(false);
			bid3.setSelected(false);
			bnum4.setSelected(false);
			bid4.setSelected(false);
			
			rmd=""+pageResults.get(1).getRoomNumber();

			if(rmd.equalsIgnoreCase("null")){
				showBuildingImage(0, 0);
	    		Toast.makeText(getApplicationContext(), getString(R.string.noOffice).toString(), Toast.LENGTH_LONG).show();
	    		otherScreen(4);
			}else{
				bldng=""+rmd.charAt(0)+rmd.charAt(1);
				flr=Character.getNumericValue(rmd.charAt(3));
				showRoom(bldng, flr, rmd);
				showBuildingImage(5, flr);
			}	
		}else if(i==3){
			bnum1.setSelected(false);
			bid1.setSelected(false);
			bnum2.setSelected(false);
			bid2.setSelected(false);
			bnum3.setSelected(true);
			bid3.setSelected(true);
			bnum4.setSelected(false);
			bid4.setSelected(false);

			rmd=""+pageResults.get(2).getRoomNumber();

			if(rmd.equalsIgnoreCase("null")){
				showBuildingImage(0, 0);
	    		Toast.makeText(getApplicationContext(), getString(R.string.noOffice).toString(), Toast.LENGTH_LONG).show();
	    		otherScreen(4);
			}else{
				bldng=""+rmd.charAt(0)+rmd.charAt(1);
				flr=Character.getNumericValue(rmd.charAt(3));
				showRoom(bldng, flr, rmd);
				showBuildingImage(5, flr);
			}
		}else if(i==4){
			bnum1.setSelected(false);
			bid1.setSelected(false);
			bnum2.setSelected(false);
			bid2.setSelected(false);
			bnum3.setSelected(false);
			bid3.setSelected(false);
			bnum4.setSelected(true);
			bid4.setSelected(true);
			
			rmd=""+pageResults.get(3).getRoomNumber();

			if(rmd.equalsIgnoreCase("null")){
				showBuildingImage(0, 0);
	    		Toast.makeText(getApplicationContext(), getString(R.string.noOffice).toString(), Toast.LENGTH_LONG).show();
	    		otherScreen(4);
			}else{
				bldng=""+rmd.charAt(0)+rmd.charAt(1);
				flr=Character.getNumericValue(rmd.charAt(3));
				showRoom(bldng, flr, rmd);
				showBuildingImage(5, flr);
			}
		}else{
			bnum1.setSelected(false);
			bid1.setSelected(false);
			bnum2.setSelected(false);
			bid2.setSelected(false);
			bnum3.setSelected(false);
			bid3.setSelected(false);
			bnum4.setSelected(false);
			bid4.setSelected(false);

			showBuildingImage(0, 0);
		}
	}

	/** 
	 * Rotate the compass image to point to the north. 
	 */
	private void rotate(){
		ImageView compass = (ImageView) findViewById(R.id.CompassImage);
		
				
		Matrix matrix=new Matrix();
		compass.setScaleType(ScaleType.MATRIX);   //required
		matrix.postRotate((float) - Math.toDegrees(angles[0]),30,30);
		compass.setImageMatrix(matrix);
		compass.setImageResource(R.drawable.compass);		//imagAprox()=compass imagAprox2()=proximity
	}

	/** 
	 * Start other activity.
	 * The parameter passed indicate the next activity:
	 * the menu (1), the favorites screen (2). 
	 * @param option The option to select the next screen.
	 */
	public void otherScreen(int option){
		Intent intent;
		switch (option) {
	        case 1:  intent = new Intent(this, MenuActivity.class);
	                 break;
	        case 2:  intent = new Intent(this, NavigateActivity.class);
   		 			 break;
	        case 3:  intent = new Intent(this, ResultActivity.class); 
	        		 intent.putExtra("use", "fav");
	                 break;
	        case 4:  intent = new Intent(this, InfoActivity.class); 
   		 			 intent.putExtra("origin", getIntent().getStringExtra("use"));
   		 			 intent.putExtra("Office", pageResults.get(selected-1));
   		 			 break;
	        default: intent = new Intent(this, TitleActivity.class);
	                 break;
		}
	    startActivity(intent);
	}


	/** 
	 * Query the corresponding room to show
	 * in the map.
	 * @param building The building 
	 * where is the searched room. 
	 * @param floor The floor number
	 * of the searched room. 
	 * @param roomid The room id
	 * of the searched room. 
	 */
	private void showRoom(String building,int floor,String roomid){
			map.removeLayer(gl[0]);
			map.removeLayer(gl[1]);
			new RoomTask(this,building,floor,roomid).execute(ujiFL);
		}
	
	/** 
	 * Set the building image to show the showed floor
	 * and the number of floors of the building.
	 * @param max The number of floors of the building.
	 * @param act The floor number to show. 
	 */
	private void showBuildingImage(int max, int act){
		ImageView build = (ImageView) findViewById(R.id.FloorView);
		
		build.setImageResource(useBuildingImage(max, act));
	}
	
	/** 
	 * Chose the building image to represent the chosed floor.
	 * @param max The number of floors of the building.
	 * @param act The floor number to show. 
	 */
	private int useBuildingImage(int max, int act){
		if(max==5)			if		(act==0)	return R.drawable.building5_b;
							else if	(act==1)	return R.drawable.building5_1;
							else if	(act==2)	return R.drawable.building5_2;
							else if	(act==3)	return R.drawable.building5_3;
							else if	(act==4)	return R.drawable.building5_4;
							else				return R.drawable.building5_5;
		
		else if (max==4)	if		(act==0)	return R.drawable.building4_b;
							else if	(act==1)	return R.drawable.building4_1;
							else if	(act==2)	return R.drawable.building4_2;
							else if	(act==3)	return R.drawable.building4_3;
							else				return R.drawable.building4_4;
		
		else if (max==3)	if		(act==0)	return R.drawable.building3_b;
							else if	(act==1)	return R.drawable.building3_1;
							else if	(act==2)	return R.drawable.building3_2;
							else				return R.drawable.building3_3;
			
		else if (max==2)	if		(act==0)	return R.drawable.building2_b;
							else if	(act==1)	return R.drawable.building2_1;
							else				return R.drawable.building2_2;
		
		else if (max==1)	if		(act==0)	return R.drawable.building1_b;
							else				return R.drawable.building1_1;
		
		else									return R.drawable.trans;
	}

    /** 
     * Save the object query in a file. The target file depends on
     * the parameter provided: the query results (1) or the favorites list (2).
     * @param i The option chosen. 
     */
	protected void save(int i){
    	FileOutputStream fos = null;

    	try {
			if(i==1)
				fos = openFileOutput("q.xml", MODE_PRIVATE);
			else if(i==2)
				fos = openFileOutput("f.xml", MODE_PRIVATE);

    	} catch (Exception e) {
    		Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    	}
        query.save(fos,query);
	}
	
    /** 
     * Load the object query from a file. The target file depends on
     * the parameter provided: the query results (1) or the favorites list (2).
     * @param i The option chosen. 
     */
	protected void load(int i){
		FileInputStream fin = null;
	
		try {
			if(i==1){
				fin = openFileInput("q.xml");
			}else if(i==2){
				fin = openFileInput("f.xml");
			}
		} catch (Exception e) {
		}
		query=query.load(fin);
		
		pageResults=query.getResultsActualPage();
	}

    /** 
     * Set the text of the text views of 
     * the result number "i".
     * @param num The text view to write the number. 
     * @param id The text view to write the id. 
     * @param i The result chosen. 
     */
	private void setTextResults(TextView num,TextView id,int i){
		num.setText("Nº"+(((query.getPage()-1)*4)+i));
		if(extra.equalsIgnoreCase("place")){
			id.setText(pageResults.get(i-1).getRoomNumber());
		}else if(extra.equalsIgnoreCase("person")){
			id.setText(pageResults.get(i-1).getOccupant(0).getName());
		}else if(pageResults.get(i-1).getOccNum()!=0){
			if((pageResults.get(i-1).getRoomNumber()+"").equalsIgnoreCase("null")){
				id.setText("--------: "+pageResults.get(i-1).getOccupant(0).getName());
			}else{
				id.setText(pageResults.get(i-1).getRoomNumber()+": "+pageResults.get(i-1).getOccupant(0).getName());
			}
		}else{
			id.setText(pageResults.get(i-1).getRoomNumber()+";");
		}
	}

    /** 
     * Disable the buttons of 
     * one result number.
     * @param num The text view to write the number. 
     * @param id The text view to write the id. 
     * @param bnum The button of the number. 
     * @param bid The button of the id. 
     */
	private void disableResults(TextView num,TextView id,ImageButton bnum,ImageButton bid){
		num.setText("");
		id.setText("");
		bnum.setEnabled(false);
		bid.setEnabled(false);
	}

    /** 
     * Set the results part of the screen 
     * to show the results list.
     */
	protected void showResults(){
		TextView num1 = (TextView) findViewById(R.id.TextNum1);
		TextView id1 = (TextView) findViewById(R.id.TextID1);
		ImageButton bnum1 = (ImageButton) findViewById(R.id.ButtonNum1);
		ImageButton bid1 = (ImageButton) findViewById(R.id.ButtonID1);
		
		TextView num2 = (TextView) findViewById(R.id.TextNum2);
		TextView id2 = (TextView) findViewById(R.id.TextID2);
		ImageButton bnum2 = (ImageButton) findViewById(R.id.ButtonNum2);
		ImageButton bid2 = (ImageButton) findViewById(R.id.ButtonID2);
		
		TextView num3 = (TextView) findViewById(R.id.TextNum3);
		TextView id3 = (TextView) findViewById(R.id.TextID3);
		ImageButton bnum3 = (ImageButton) findViewById(R.id.ButtonNum3);
		ImageButton bid3 = (ImageButton) findViewById(R.id.ButtonID3);
		
		TextView num4 = (TextView) findViewById(R.id.TextNum4);
		TextView id4 = (TextView) findViewById(R.id.TextID4);
		ImageButton bnum4 = (ImageButton) findViewById(R.id.ButtonNum4);
		ImageButton bid4 = (ImageButton) findViewById(R.id.ButtonID4);
		
		if(pageResults.size()>0){
			setTextResults(num1,id1,1);
			bnum1.setEnabled(true);
			bid1.setEnabled(true);
			if(pageResults.size()>1){
				setTextResults(num2,id2,2);
				bnum2.setEnabled(true);
				bid2.setEnabled(true);
				if(pageResults.size()>2){
					setTextResults(num3,id3,3);
					bnum3.setEnabled(true);
					bid3.setEnabled(true);
					if(pageResults.size()>3){
						setTextResults(num4,id4,4);
						bnum4.setEnabled(true);
						bid4.setEnabled(true);
					}else{
						disableResults(num4,id4,bnum4,bid4);
					}
				}else{
					disableResults(num3,id3,bnum3,bid3);
					disableResults(num4,id4,bnum4,bid4);
				}
			}else{
				disableResults(num2,id2,bnum2,bid2);
				disableResults(num3,id3,bnum3,bid3);
				disableResults(num4,id4,bnum4,bid4);
			}
		}else{
			disableResults(num1,id1,bnum1,bid1);
			disableResults(num2,id2,bnum2,bid2);
			disableResults(num3,id3,bnum3,bid3);
			disableResults(num4,id4,bnum4,bid4);
		}	
	}

    /** 
     * Get the description of a room 
     * with the provided room number.
     * @param roomN The room number of 
     * the room of the expected description. 
     */
	protected String getDesc(String roomN) {
		String aux=null;
		String offRN="";
		for(Office off: pageResults){
			offRN=""+off.getRoomNumber();
			if (offRN.equalsIgnoreCase(roomN)){
				aux="";
				for(Person occ: off.getOccupants())
					aux=aux+"-"+occ.getName()+"<br />";
			}
		}
		return aux;
	}

	/** 
	 * Set the map extent with the provided envelope.
	 * @param enve The envelope with 
	 * the extent will be set. 
	 */
	public void zoomTo(Envelope enve){
    	map.setExtent(enve);
	}

	/** 
	 * Using graphics layers with the floor features 
	 * and the room, add copies of this layers to the map.
	 * @param flrGL The graphics layers with a floor (flrGL[0]) and a room (flrGL[0]). 
	 */
	public void setResult(GraphicsLayer[] flrGL) {
		this.gl=flrGL;
		map.addLayer(gl[0]);
		map.addLayer(gl[1]);
	}
	

	/** 
	 * Creates custom content view with 'Graphic' attributes.
	 * @param Name The title to show in the callout.
	 * @param Desc The description 
	 * to show in the callout. 
	 */
	private View loadView(String Name, String Desc) {
		View view = LayoutInflater.from(ResultActivity.this).inflate(
				R.layout.callout, null);

		final ImageButton toFavorites = (ImageButton) view.findViewById(R.id.fav_button);
		final ImageButton toInfo = (ImageButton) view.findViewById(R.id.InfoButton);
		final TextView roomID = (TextView) view.findViewById(R.id.name);
		final TextView info = (TextView) view.findViewById(R.id.Info);
		
		if(favorites){
			toFavorites.setImageResource(R.drawable.buttonstar_del_min);
			toFavorites.setOnClickListener(delFav);
		}else{
			toFavorites.setOnClickListener(toFav);
		}
		toInfo.setOnClickListener(toInf);
		
		roomID.setText("RoomID: "+Name);
		info.setText(Html.fromHtml(Desc));

		return view;
	}
	

	/** 
	 * Add the office to the favorite list.
	 * Open the favorites file, read the favorites list,
	 * add the office to the list
	 * or actualize it if is already in,
	 * and rewrite the favorite file with
	 * the favorite list.
	 */
	protected void toFav(int selected) {
		
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
			
			int pos=aux.isIn(pageResults.get(selected-1));
			
			if(pos==-1){
				aux.addOffice(pageResults.get(selected-1));
	    		Toast.makeText(getApplicationContext(), 
	    				getString(R.string.button_FavoritesAddDel_part1).toString()
	    				+pageResults.get(selected-1).getRoomNumber()
	    				+getString(R.string.button_FavoritesAddDel_part2added).toString(), Toast.LENGTH_LONG).show();
			}else{
				aux.delOffice(pageResults.get(selected-1));
				aux.addOffice(pageResults.get(selected-1));
				Toast.makeText(getApplicationContext(), 
						getString(R.string.button_FavoritesAddDel_part1actualized).toString()
						+pageResults.get(selected-1).getRoomNumber()
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
	 * Delete the office from the favorite list.
	 * Open the favorites file, read the favorites list,
	 * delete the office from the list,
	 * and rewrite the favorite file with
	 * the favorite list.
	 */
	protected void delFav(int selected) {
		
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
					+pageResults.get(selected-1).getRoomNumber()
					+getString(R.string.button_FavoritesAddDel_part2deleted).toString(), Toast.LENGTH_LONG).show();
			
			callout.hide();
			gl[1].removeAll();
			aux.delOffice(pageResults.get(selected-1));
			query=aux;
			prepareResults();
			showResults();
			
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
	 * Start the location service of the map if 
	 * is not running, or stop it if is running.
	 */
	private void locationShifter() {
	    ImageButton pos = (ImageButton) findViewById(R.id.PositionButton);
		callout.hide();
		if(!ls.isStarted() && !pos.isSelected()){
			pos.setSelected(true);
			ls.start();
		}else{
			pos.setSelected(false);
			ls.stop();
		}
	}

}