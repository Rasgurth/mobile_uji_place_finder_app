package es.uji.PF.tasks;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.SELECTION_METHOD;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.ags.query.Query;

import es.uji.PF.ResultActivity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;

public class RoomTask extends AsyncTask<ArcGISFeatureLayer, Object, GraphicsLayer []> {
	private final ResultActivity resultActivity;
	private final String roomid;
	private final String building;
	private final int floor;
	private GraphicsLayer[] gl;
	private ArcGISFeatureLayer ujiFL=null;
	SimpleFillSymbol roomSS = new SimpleFillSymbol(Color.BLUE);
	SimpleFillSymbol floorSS = new SimpleFillSymbol(Color.GRAY);
	Envelope Env = new Envelope();
	
	public RoomTask(ResultActivity navigateActivity,String building,int floor,String roomid) {
		super();
		this.resultActivity = navigateActivity;
		this.roomid=roomid;
		this.building=building;
		this.floor=floor;
		this.gl=new GraphicsLayer[2];
		gl[0]=new GraphicsLayer();
		gl[1]=new GraphicsLayer();
	}

	//Before running code in separate thread  
    @Override  
    protected void onPreExecute()  
    {  
        //Create a new progress dialog  
        resultActivity.progressDialog = new ProgressDialog(resultActivity);  
        //Set the progress dialog to display a horizontal progress bar  
        resultActivity.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        //Set the dialog title to 'Loading...'  
        resultActivity.progressDialog.setTitle("Loading...");  
        //Set the dialog message to 'Loading application View, please wait...'  
        resultActivity.progressDialog.setMessage("Loading room, please wait...");  
        //This dialog can't be canceled by pressing the back key  
        resultActivity.progressDialog.setCancelable(false);  
        //This dialog isn't indeterminate  
        resultActivity.progressDialog.setIndeterminate(false);  
        //The maximum number of items is 100  
        resultActivity.progressDialog.setMax(100);  
        //Set the current progress to zero  
        resultActivity.progressDialog.setProgress(0);  
        //Display the progress dialog  
        resultActivity.progressDialog.show();  
    }  

	
	@Override
	protected  GraphicsLayer[] doInBackground(ArcGISFeatureLayer... FL) {   
        synchronized (this)  
        {  
    		ujiFL=FL[0];
            publishProgress(25);
            showFloor(this.building,this.floor);
            publishProgress(75);
        }  
		return this.gl;
	}

	@Override
	protected void onPostExecute(GraphicsLayer[] result) {
		super.onPostExecute(result);
		
		resultActivity.progressDialog.dismiss();
		
		resultActivity.setResult(result);
	}
	
	
	private void showFloor(String bldng,int flr){
		Query q = new Query();
		q.setWhere("BUILDING = '"+bldng+"' AND FLOOR = '"+flr+"'");

		floorSS.setOutline(new SimpleLineSymbol(Color.YELLOW, 1));
		ujiFL.setSelectionSymbol(floorSS);
		ujiFL.selectFeatures(q, SELECTION_METHOD.NEW, callback1);
	}
	
	private void showRoom(String room){
		Query q = new Query();
		q.setWhere("SPACEID = '"+room+"'");

		roomSS.setOutline(new SimpleLineSymbol(Color.YELLOW, 2));
		ujiFL.setSelectionSymbol(roomSS);
		ujiFL.selectFeatures(q, SELECTION_METHOD.NEW, callback2);
	}
	
	CallbackListener<FeatureSet> callback1 = new CallbackListener<FeatureSet>() {
		public void onCallback(FeatureSet fSet) {
			gl[0].addGraphics(ujiFL.getSelectedFeatures());
			calculateEnvelope();
            showRoom(roomid);
			resultActivity.zoomTo(Env);
	        System.out.println("Result 0 in the callback: "+gl[0].getNumberOfGraphics());
		}
		public void onError(Throwable arg0) {
			gl[0].removeAll();
		}
	};
	

	CallbackListener<FeatureSet> callback2 = new CallbackListener<FeatureSet>() {
		public void onCallback(FeatureSet fSet) {
			gl[1].addGraphics(ujiFL.getSelectedFeatures());
	        System.out.println("Result 1 in the callback: "+gl[1].getNumberOfGraphics());
	        if(gl[1].getNumberOfGraphics()==0){
	        	resultActivity.otherScreen(4);
	        }
		}
		public void onError(Throwable arg0) {
			gl[1].removeAll();
		}
	};
	

	public void calculateEnvelope(){
    	Envelope auxEnv = new Envelope();
    	Env = new Envelope();
    	int [] aux=gl[0].getGraphicIDs();
		if(gl[0].getNumberOfGraphics()>0){
			for (int i:aux){
	    		Polygon p = (Polygon) gl[0].getGraphic(i).getGeometry();
	    		p.queryEnvelope(auxEnv);
	    		Env.merge(auxEnv);
	    	}
		}
	}
	
}
