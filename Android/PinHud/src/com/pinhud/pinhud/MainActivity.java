package com.pinhud.pinhud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

	private NfcAdapter mNfcAdapter;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
	private String storeResult;
	private JSONObject request;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(mNfcAdapter == null){
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		if (!mNfcAdapter.isEnabled()){
			new AlertDialog.Builder(this).setTitle("NFC Disabled")
			.setMessage("Please enable your NFC to use this app.")
			.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					// do nothing
				}
			})
			.show();

		}else{
			handleFragments(getIntent());
		}
	}

	private void handleFragments(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);
			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
		loadSavedPreferences();

	}

	/**
	 * Background task for reading the data. Do not block the UI thread while reading.
	 *
	 * @author Ralf Wondratschek
	 *
	 */
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];
			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}
			NdefMessage ndefMessage = ndef.getCachedNdefMessage();
			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}
			return null;
		}
		private String readText(NdefRecord record) throws UnsupportedEncodingException {
			byte[] payload = record.getPayload();
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			int languageCodeLength = payload[0] & 0063;

			return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		}
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				savePreferences(result);
				//System.out.println("Read content: " + result);
				//mTextView.setText("Read content: " + result);
			}
		}
	}
	
	private class AuthRequestAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			String user = params[0];
			int authCheck = 0;
			// Creating HTTP client
			HttpClient httpClient = new DefaultHttpClient();
			// Creating HTTP Post TODO
			HttpPost httpPost = new HttpPost("");
			JSONObject passData = new JSONObject();
			try {
				passData.put("Username", user);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			String message = passData.toString();
			// Url Encoding the POST parameters
			try {
				httpPost.setEntity(new StringEntity(message, "UTF8"));
				httpPost.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				// writing error to Log
				e.printStackTrace();
			}

			// Making HTTP Request
			try {
				HttpResponse response = httpClient.execute(httpPost);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				//JSONTokener tokener = new JSONTokener(builder.toString());
				//JSONArray finalResult = new JSONArray(tokener);
				request = new JSONObject(builder.toString());
				// writing response to log
				Log.d("Http Response:", response.toString());
			} catch (ClientProtocolException e) {
				// writing exception to log
				e.printStackTrace();
			} catch (IOException e) {
				// writing exception to log
				e.printStackTrace();

			} catch (JSONException e) {
				authCheck = 1;
				e.printStackTrace();
			}
			return authCheck;
		}


	}
	
	private void savePreferences(String result){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

		Editor edit = shared.edit();
		edit.putString("Store", result);
		edit.commit();
	}
	
	private void loadSavedPreferences(){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		String pUser = shared.getString("User", "");
		String storeName = shared.getString("Store", "");
		System.out.println(storeName);
		boolean check = shared.getBoolean("stored", false);
		if(check){
			asyncCheck(pUser);
			FeedFragment fFrag = new FeedFragment();
			fFrag.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction()
			.add(R.id.fragment_container, fFrag).commit();
		}else{
			LoginFragment lFrag = new LoginFragment();
			lFrag.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction()
			.add(R.id.fragment_container, lFrag).commit();
		}
	}

	private void asyncCheck(String pUser) {
		AuthRequestAsync theRun = new AuthRequestAsync();
		int result = 0;
		try {
			result = theRun.execute(pUser).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result == 0){
			for(int i = 0; i < request.length(); i++){
				
			}
		}
		
	}

}
