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

import es.uji.PF.NavigateActivity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;

public class FloorTask extends AsyncTask<ArcGISFeatureLayer, Object, GraphicsLayer> {
	private final NavigateActivity navigateActivity;
	private final String building;
	private final int floor;
	private GraphicsLayer floorGL=null;
	private ArcGISFeatureLayer ujiFL=null;
	SimpleFillSymbol floorSS = new SimpleFillSymbol(Color.GRAY);
	Envelope Env = new Envelope();
	
	public FloorTask(NavigateActivity navigateActivity,String building,int floor) {
		super();
		this.navigateActivity = navigateActivity;
		this.building=building;
		this.floor=floor;
	}

	//Before running code in separate thread  
    @Override  
    protected void onPreExecute()  
    {  
        //Create a new progress dialog  
        navigateActivity.progressDialog = new ProgressDialog(navigateActivity);  
        //Set the progress dialog to display a horizontal progress bar  
        navigateActivity.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        //Set the dialog title to 'Loading...'  
        navigateActivity.progressDialog.setTitle("Loading...");  
        //Set the dialog message to 'Loading application View, please wait...'  
        navigateActivity.progressDialog.setMessage("Loading "+building+"'s floor Nº"+floor+" map, please wait...");  
        //This dialog can't be canceled by pressing the back key  
        navigateActivity.progressDialog.setCancelable(false);  
        //This dialog isn't indeterminate  
        navigateActivity.progressDialog.setIndeterminate(false);  
        //The maximum number of items is 100  
        navigateActivity.progressDialog.setMax(100);  
        //Set the current progress to zero  
        navigateActivity.progressDialog.setProgress(0);  
        //Display the progress dialog  
        navigateActivity.progressDialog.show();  
    }  

	
	@Override
	protected GraphicsLayer doInBackground(ArcGISFeatureLayer... FL) {  
        //Get the current thread's token  
        synchronized (this)  
        {  
    		ujiFL=FL[0];
            publishProgress(33);
            showFloor(this.building,this.floor);
            publishProgress(66);
        }  
		return this.floorGL;
	}

	@Override
	protected void onPostExecute(GraphicsLayer result) {
		super.onPostExecute(result);
        //close the progress dialog  
		navigateActivity.progressDialog.dismiss();
		navigateActivity.setResult(result);
	}
	
	
	private void showFloor(String bldng,int flr){
		floorGL = new GraphicsLayer();
		Query q = new Query();
		q.setWhere("BUILDING = '"+bldng+"' AND FLOOR = '"+flr+"'");

		floorSS.setOutline(new SimpleLineSymbol(Color.YELLOW, 1));
		ujiFL.setSelectionSymbol(floorSS);
		ujiFL.selectFeatures(q, SELECTION_METHOD.NEW, callback);
	}
	

	CallbackListener<FeatureSet> callback = new CallbackListener<FeatureSet>() {
		public void onCallback(FeatureSet fSet) {
			floorGL.addGraphics(ujiFL.getSelectedFeatures());
			calculateEnvelope();
			navigateActivity.zoomTo(Env);
		}
		public void onError(Throwable arg0) {
			floorGL.removeAll();
		}
	};
	
	public void calculateEnvelope(){
    	Envelope auxEnv = new Envelope();
    	Env = new Envelope();
    	int [] aux=floorGL.getGraphicIDs();
		if(floorGL.getNumberOfGraphics()>0){
			for (int i:aux){
	    		Polygon p = (Polygon) floorGL.getGraphic(i).getGeometry();
	    		p.queryEnvelope(auxEnv);
	    		Env.merge(auxEnv);
	    	}
		}
	}
}
