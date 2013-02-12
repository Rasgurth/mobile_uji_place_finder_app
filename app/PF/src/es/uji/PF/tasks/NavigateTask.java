package es.uji.PF.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import es.uji.PF.NavigateActivity;
import es.uji.PF.classes.Office;
import es.uji.PF.classes.QueryOfficeResults;
import es.uji.PF.classes.XMLParser;
import es.uji.PF.classes.Person;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class NavigateTask extends AsyncTask<String, Object, List<Person>> {
	private final NavigateActivity navigateActivity;
	private final float X,Y;
	private final String RN;
	
	public NavigateTask(NavigateActivity navigateActivity, float x, float y,String rN) {
		super();
		this.navigateActivity = navigateActivity;
		this.X=x;
		this.Y=y;
		this.RN=rN;
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
    	navigateActivity.progressDialog.setMessage("Loading the results, please wait...");  
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
	protected List<Person> doInBackground(String... urls) {
		List<Person> faculties = null;

        publishProgress(25);
        synchronized (this){
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(10000 /* milliseconds */);
				connection.setConnectTimeout(15000 /* milliseconds */);
				connection.setRequestMethod("GET");
				connection.setDoInput(true);
				connection.addRequestProperty("Referer",
								urls[0]);
		        publishProgress(25);
				connection.addRequestProperty("Accept", "application/xml");
				connection.connect();
				InputStream is = connection.getInputStream();
				XMLParser xmlParser = new XMLParser();
		        publishProgress(50);
				faculties = xmlParser.parse(is);
		        publishProgress(75);
				is.close();
				connection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		}
		return faculties;
	}

	@Override
	protected void onPostExecute(List<Person> result) {
		super.onPostExecute(result);

		QueryOfficeResults query=new QueryOfficeResults();
		query.setOffices(result);
		if(result.size()==0){
			navigateActivity.setRoom(new Office(RN));
			navigateActivity.showCallout(RN, "---", X, Y);
		}else{
			navigateActivity.setRoom(query.getOffice(0));
			navigateActivity.showCallout(RN, getDesc(query.getOffice(0)), X, Y);
		}
		
		navigateActivity.progressDialog.dismiss();
	}

	protected String getDesc(Office off) {
		String aux="";
		for(Person occ: off.getOccupants())
			aux=aux+"-"+occ.getName()+"<br />";
		return aux;
	}
}
