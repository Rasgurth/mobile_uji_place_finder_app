package es.uji.PF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
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
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

import es.uji.PF.classes.Office;
import es.uji.PF.classes.QueryOfficeResults;
import es.uji.PF.tasks.FloorTask;
import es.uji.PF.tasks.NavigateTask;


public class NavigateActivity extends Activity  implements LocationListener, SensorEventListener {
	
	//##### Variables #####
	
		//----- ArcGIS APK -----
			final static double SEARCH_RADIUS = 5;
			MapView map = null;
			
			private final static String MAP_URL = "http://geotec.dlsi.uji.es:6080/arcgis/rest/services";
			
			ArcGISTiledMapServiceLayer basemap = null;
			ArcGISTiledMapServiceLayer floorplan = null;
			ArcGISFeatureLayer ujiFL;
			GraphicsLayer floorGL = null;
			
			double m_tapx;
			double m_tapy;
			Callout callout;
			Drawable jobIcon;
			FeatureSet fset;
			
			SimpleFillSymbol floorSS = new SimpleFillSymbol(Color.GRAY);
			
			CallbackListener<FeatureSet> callback = new CallbackListener<FeatureSet>() {
				public void onCallback(FeatureSet fSet) {
					floorGL.addGraphics(ujiFL.getSelectedFeatures());
				}
				public void onError(Throwable arg0) {
					floorGL.removeAll();
				}
			};
		
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
			private int floor=0;
			private String building="";
			public ProgressDialog progressDialog;  
			private Office room=new Office();

	//##### Activity methods #####

	    /** 
	     * Called when the activity is first created.
	     * Set the content view "activity_navigate",
	     * and accomplish the required actions for the proper
	     * behavior of ArcGis APK, the location 
	     * and the orientation functionalities.
	     */
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_navigate);
	
	        iniAPK();
	        iniLocation();
	        iniOrientation();	
	        iniTapListener();
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
			map.pause();
	
	    	locationManager.removeUpdates(this);
		}

	    /** 
	     * Called when the activity is resumed. 
	     * Unpause the map view, activate the location, 
	     * accelerometer and magnetometer sensors, and
	     * set the spinner and the buttons of the activity.
	     */
		protected void onResume() {
			super.onResume(); 
			map.unpause();
			
	        locationManager.requestLocationUpdates(provider, 400, 1, this);
	        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
	        mSensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
	        
	        setSpinner();
	        
	        setButtons();
		}
		
	    /** 
	     * Called when the activity is stoped. 
	     * Deactivate the sensor manager.
	     */
		protected void onStop() {
	    	super.onStop();
	    	
	        mSensorManager.unregisterListener(this);
	    }
    

	//##### Buttons listeners #####
		/** 
		 * The listener for the current location button. 
		 */
	    private OnClickListener loc = new OnClickListener() {
	        public void onClick(View v) { 
	        	ls.start();
	        }
		};
		
	    /** 
	     * The listener for the person button.
	     */
	    private OnClickListener fnd = new OnClickListener() {
	        public void onClick(View v) { 
	        	otherScreen(1);
	        }
		};
		
		/** 
		 * The listener for the favorites button.
		 */
		private OnClickListener fv = new OnClickListener() {
		    public void onClick(View v) { 
	        	otherScreen(2);
		    }
		};
		
		/** The listener for the next page button.
		*/
		private OnClickListener toFav = new OnClickListener() {
			public void onClick(View v) { 
				toFav();
			}
		};
	
		/** 
		 * The listener for the up floor button.
		 */
		private OnClickListener p = new OnClickListener() {
			public void onClick(View v) { 
				upFloor();
			}
		};
	
	
		/** 
		 * The listener for the dwn floor button. 
		 */
		private OnClickListener dwn = new OnClickListener() {
			public void onClick(View v) { 
				downFloor();
			}
		};
		

		/** The listener for the button to include in favorites.
		*/
		private OnClickListener toInf = new OnClickListener() {
			public void onClick(View v) { 
	        	otherScreen(3);
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
	     * Initialize the attributes for
	     * the ArcGIS APK: the map, the layers, 
	     * the location and the callout.
	     */
		private void iniAPK() {
			// Retrieve the map and initial extent from XML layout
			map = (MapView)findViewById(R.id.map);
		
			// Add featured layer to MapView
			Options o = new Options();
			o.mode = MODE.ONDEMAND;
			//o.outFields=new String[] {"Shape","SPACEID","BUILDING","FLOOR"};
			ujiFL = new ArcGISFeatureLayer(
					MAP_URL+"/UJI/UJIBuildingInterior/MapServer/1",
					o);
			floorSS.setOutline(new SimpleLineSymbol(Color.YELLOW, 2));
			ujiFL.setSelectionSymbol(floorSS);
			map.addLayer(ujiFL);
			
			// Add tiled layer to MapView
			basemap = new ArcGISTiledMapServiceLayer(
					MAP_URL+"/UJI/viscaUJI_desaturado/MapServer");
			map.addLayer(basemap);
			
			floorGL = new GraphicsLayer();
			map.addLayer(floorGL);
			
			ls = map.getLocationService();
			ls.setAccuracyCircleOn(true);
			ls.setAutoPan(true);
			
			callout=new Callout(map);
		}

	    /** 
	     * Set the sensor to get data about
	     * location.
	     */
		private void iniLocation() {
		    // Get the location manager
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);
		
		    // Initialize the location fields
		    if (location != null) {
		      System.out.println("Provider " + provider + " has been selected.");
		      onLocationChanged(location);
		    }
		}


	    /** 
	     * Sets the sensors to get data about
	     * orientation.
	     */
		private void iniOrientation() {
		    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		    mMag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}
		

	    /** 
	     * Sets 'OnSingleTapListener' so that when 
	     * single tap happens, Callout would show
	     * 'SQMI' information associated with 
	     * tapped 'Graphic'.
	     */
	    private void iniTapListener(){

			map.setOnSingleTapListener(new OnSingleTapListener() {

				private static final long serialVersionUID = 1L;

				public void onSingleTap(float x, float y) {

					if (!map.isLoaded())
						return;
					int[] uids = floorGL.getGraphicIDs(x, y, 2);
					if (uids != null && uids.length > 0) {

						int targetId = uids[0];
						Graphic gr = floorGL.getGraphic(targetId);
					    callout = map.getCallout();
					    
					   callout.setStyle(R.xml.pop);
					   String Name = (String) gr.getAttributeValue("SPACEID");
					   
					   getDesc(Name,x,y);
					   
					}else {
						if (callout != null && callout.isShowing()) {
							callout.hide();
						}
					}

				}
			});
	    }

		/** 
		 * Set the button's click listeners. 
		 */
		private void setButtons() { 
		    ImageButton pos = (ImageButton) findViewById(R.id.PositionButton);
		    ImageButton fav = (ImageButton) findViewById(R.id.FavouritesButton);
		    ImageButton find = (ImageButton) findViewById(R.id.FindButton);
		    ImageButton up = (ImageButton) findViewById(R.id.UpButton);
		    ImageButton down = (ImageButton) findViewById(R.id.DownButton);

			pos.setOnClickListener(loc);
			fav.setOnClickListener(fv);
			find.setOnClickListener(fnd);
			up.setOnClickListener(p);
			down.setOnClickListener(dwn);
			
			down.setEnabled(false);
		}

		/** 
		 * Set the spinner and its option list. 
		 */
		private void setSpinner() {
		    final Spinner spinner = (Spinner) findViewById(R.id.BuildingSpinner);
		     // Create an ArrayAdapter using the string array and a default spinner layout
		     ArrayAdapter<CharSequence> adapter = 
		    		 ArrayAdapter.createFromResource(this, R.array.buildings, android.R.layout.simple_spinner_item);
		     // Specify the layout to use when the list of choices appears
		     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		     // Apply the adapter to the spinner
		     spinner.setAdapter(adapter);
		
		     spinner.setOnItemSelectedListener(
	    		 new AdapterView.OnItemSelectedListener() {
	    			 public void onItemSelected(AdapterView<?> adapterView, View view,int i, long l) { 
	    		        ImageButton up = (ImageButton) findViewById(R.id.UpButton);
	    		        ImageButton down = (ImageButton) findViewById(R.id.DownButton);
	    		    	
	    		        building = spinner.getSelectedItem().toString();
	    		        if(!building.equalsIgnoreCase("select one")){
	    		        	up.setEnabled(true);
	    		    		down.setEnabled(false);
	    		        	floor=0;
	    		        	showFloor(building,floor);
	    		        }
	    		    } 
		
					public void onNothingSelected(AdapterView<?> adapterView) {
	    		       return;
	    		    }
				}
	    	); 
		}
		

		/** 
		 * Start other activity.
		 * The parameter provided indicate the next activity:
		 * search menu (1), favorites visualization (2) or
		 * place info (3). 
		 * @param option The option to select the next screen.
		 */
	    private void otherScreen(int option){
			Intent intent = new Intent(this, TitleActivity.class);
			switch (option) {
		        case 1:  intent = new Intent(this, MenuActivity.class);
		                 break;
		        case 2:  intent = new Intent(this, ResultActivity.class); 
			 			 intent.putExtra("use", "fav");
			 			 break;
		        case 3:  intent = new Intent(this, InfoActivity.class); 
		        		 intent.putExtra("origin", "nav");
		 			 	 intent.putExtra("Office", room);
		 			 	 break;
		        default: intent = new Intent(this, TitleActivity.class);
		                 break;
			}
		    startActivity(intent);
		}


		/** 
		 * Increase the floor value, and actualize the buttons and the
		 * map taking into account the new floor value. 
		 */
		private void upFloor() {
		    ImageButton up = (ImageButton) findViewById(R.id.UpButton);
		    ImageButton down = (ImageButton) findViewById(R.id.DownButton);
		    
			floor++;
		
			down.setEnabled(true);
			if(floor==5){
				up.setEnabled(false);
			}else{
				up.setEnabled(true);
			}
		
			showFloor(building,floor);
		}

		/** 
		 * Decrease the floor value, and actualize the buttons and the
		 * map taking into account the new floor value. 
		 */
		private void downFloor() {
		    ImageButton up = (ImageButton) findViewById(R.id.UpButton);
		    ImageButton down = (ImageButton) findViewById(R.id.DownButton);
		    
			floor--;
			
			up.setEnabled(true);
			if(floor==0){
				down.setEnabled(false);
			}else{
				down.setEnabled(true);
			}
			showFloor(building,floor);
		}

		/** 
		 * Add the office to the favorite list.
		 * Open the favorites file, read the favorites list,
		 * add the office to the list
		 * or actualize it if is already in,
		 * and rewrite the favorite file with
		 * the favorite list.
		 */
		protected void toFav() {
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
				
				int pos=aux.isIn(room);
				
				if(pos==-1){
					aux.addOffice(room);
		    		Toast.makeText(getApplicationContext(), 
		    				getString(R.string.button_FavoritesAddDel_part1).toString()
		    				+room.getRoomNumber()
		    				+getString(R.string.button_FavoritesAddDel_part2added).toString(), Toast.LENGTH_LONG).show();
				}else{
					aux.delOffice((Office) room);
					aux.addOffice((Office) room);
					Toast.makeText(getApplicationContext(), 
							getString(R.string.button_FavoritesAddDel_part1actualized).toString()
							+room.getRoomNumber()
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
		 * Set the room with the values of an office.
		 * @param Room office to put in room. 
		 */
		public void setRoom(Office Room){
			room=Room;
		}


		/** 
		 * Query the description of the room with 
		 * the proporcionated room number and 
		 * in the screen position (x, y).
		 * @param roomNumber room number of a room 
		 * to search its description. 
		 * @param x screen coordinate x. 
		 * @param y screen coordinate y. 
		 */
		protected void getDesc(String roomNumber,float x,float y){
	    	//String url = "http://10.0.2.2:8080/Locations-0.0.1-SNAPSHOT/offices/" + input.getText().toString();					
	    	//String url = "http://localhost:8080/Locations-0.0.1-SNAPSHOT/offices/" + input.getText().toString();
			String url = "http://geotec.dlsi.uji.es:8080/Locations/offices/" + roomNumber;
			url=url.replaceAll(" ", "%20");
			
			new NavigateTask(this,x,y,roomNumber).execute(url);
	    }

		/** 
		 * Show the callout in the screen position (x, y), 
		 * with the proporcionated title and description.
		 * @param Title The title to show in the callout.
		 * @param Description The description 
		 * to show in the callout.
		 * @param x screen coordinate x. 
		 * @param y screen coordinate y. 
		 */
	    public void showCallout(String Title, String Description, float x, float y){
		   callout.setContent(loadView(Title, Description));
		   callout.show(map.toMapPoint(new Point(x, y)));
	    }

		/** 
		 * Creates custom content view with 'Graphic' attributes.
		 * @param Name The title to show in the callout.
		 * @param Desc The description 
		 * to show in the callout. 
		 */
		private View loadView(String Name, String Desc) {
			View view = LayoutInflater.from(NavigateActivity.this).inflate(
					R.layout.callout, null);

			final ImageButton toFavorites = (ImageButton) view.findViewById(R.id.fav_button);
			final ImageButton toInfo = (ImageButton) view.findViewById(R.id.InfoButton);
			final TextView roomID = (TextView) view.findViewById(R.id.name);
			final TextView info = (TextView) view.findViewById(R.id.Info);
			
			toFavorites.setOnClickListener(toFav);
			toInfo.setOnClickListener(toInf);

			roomID.setText("RoomID: "+Name);
			info.setText(Html.fromHtml(Desc));
			
			if(Desc.equalsIgnoreCase("---")){
				toInfo.setEnabled(false);
				toInfo.setImageResource(R.drawable.trans);
			}

			return view;

		}

		/** 
		 * Query the corresponding floor to show
		 * in the map.
		 * @param bldng The building 
		 * where is the searched floor. 
		 * @param flr The floor number
		 * of the searched floor. 
		 */
		private void showFloor(String bldng,int flr){
			new FloorTask(this,bldng,flr).execute(ujiFL);

			if (callout != null && callout.isShowing()) {
				callout.hide();
			}
	    	
	    	showBuildingImage(5,floor);
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
		 * Set the building image to show the showed floor
		 * and the number of floors of the building.
		 * @param max The number of floors of the building.
		 * @param act The floor number to show. 
		 */
		private void showBuildingImage(int max, int act){
			ImageView build = (ImageView) findViewById(R.id.BuildingImage);
			
	    	build.setImageResource(useBuildingImage(max, act));
		}

		/** 
		 * Chose the building image to represent the chosed floor.
		 * @param max The number of floors of the building.
		 * @param act The floor number to show. 
		 */
		private int useBuildingImage(int max, int act){
			if(max==5){
				if(act==0){
					return R.drawable.building5_b;
				}else if(act==1){
					return R.drawable.building5_1;
				}else if(act==2){
					return R.drawable.building5_2;
				}else if(act==3){
					return R.drawable.building5_3;
				}else if(act==4){
					return R.drawable.building5_4;
				}else{
					return R.drawable.building5_5;
				}
			}else if (max==4){
				if(act==0){
					return R.drawable.building4_b;
				}else if(act==1){
					return R.drawable.building4_1;
				}else if(act==2){
					return R.drawable.building4_2;
				}else if(act==3){
					return R.drawable.building4_3;
				}else{
					return R.drawable.building4_4;
				}
			}else if (max==3){
				if(act==0){
					return R.drawable.building3_b;
				}else if(act==1){
					return R.drawable.building3_1;
				}else if(act==2){
					return R.drawable.building3_2;
				}else{
					return R.drawable.building3_3;
				}
				
			}else if (max==2){
				if(act==0){
					return R.drawable.building2_b;
				}else if(act==1){
					return R.drawable.building2_1;
				}else{
					return R.drawable.building2_2;
				}
				
			}else{
				if(act==0){
					return R.drawable.building1_b;
				}else{
					return R.drawable.building1_1;
				}
			}
		}

		/** 
		 * Using a graphics layer with the floor features,
		 * add a copy of this layer to the map.
		 * @param flrGL The graphics layer with a floor. 
		 */
		public void setResult(GraphicsLayer flrGL) {
			map.removeLayer(floorGL);
			floorGL=flrGL;
			map.addLayer(floorGL);
		}
		
		
}