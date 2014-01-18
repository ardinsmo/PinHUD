package com.pinhud.pinhud;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	private NfcAdapter mNfcAdapter;

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
		loadSavedPreferences();

	}

	private void loadSavedPreferences(){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		String pUser = shared.getString("User", "");
		boolean check = shared.getBoolean("stored", false);
		if(check){
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

}
