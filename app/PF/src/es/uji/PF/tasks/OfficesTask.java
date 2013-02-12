package es.uji.PF.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import es.uji.PF.PFormActivity;
import es.uji.PF.classes.XMLParser;
import es.uji.PF.classes.Person;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class OfficesTask extends AsyncTask<String, Object, List<Person>> {
	private final PFormActivity pFormActivity;
	
	public OfficesTask(PFormActivity pFormActivity) {
		super();
		this.pFormActivity = pFormActivity;
	}
	

	//Before running code in separate thread  
    @Override  
    protected void onPreExecute()  
    {  
        //Create a new progress dialog  
    	pFormActivity.progressDialog = new ProgressDialog(pFormActivity);  
        //Set the progress dialog to display a horizontal progress bar  
    	pFormActivity.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        //Set the dialog title to 'Loading...'  
    	pFormActivity.progressDialog.setTitle("Loading...");  
        //Set the dialog message to 'Loading application View, please wait...'  
    	pFormActivity.progressDialog.setMessage("Loading the results, please wait...");  
        //This dialog can't be canceled by pressing the back key  
    	pFormActivity.progressDialog.setCancelable(false);  
        //This dialog isn't indeterminate  
    	pFormActivity.progressDialog.setIndeterminate(false);  
        //The maximum number of items is 100  
    	pFormActivity.progressDialog.setMax(100);  
        //Set the current progress to zero  
    	pFormActivity.progressDialog.setProgress(0);  
        //Display the progress dialog  
    	pFormActivity.progressDialog.show();  
    }  

	@Override
	protected List<Person> doInBackground(String... urls) {
		List<Person> faculties = null;

        publishProgress(25);
        synchronized (this){
			try {
				URL url = new URL(urls[0]);
				System.out.println(urls[0]);
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
		pFormActivity.setResult(result);
//		TextView text = (TextView) pFormActivity.findViewById(R.id.TextResults);
//		query.setOffices(persons);
//		text.setText("Results in list: "+result.size());
//		text.setText("Results dispached ");
		pFormActivity.progressDialog.dismiss();
	}
	
}
