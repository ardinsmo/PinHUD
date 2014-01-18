package com.pinhud.pinhud;

import java.util.Locale;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	private Button login;
	private String getUser;
	private String getName;
	private EditText username;
	private EditText name;
	private LoginFragment thisThing = this;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_layout, container,
                false);
        login = (Button) view.findViewById(R.id.login);
        
        login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				getUser = username.getText().toString().toLowerCase(Locale.US);
				getName = name.getText().toString();
				savePreferences("stored", true);
				savePreferences("User", getUser);
				savePreferences("Name", getName);
				Fragment fragment = new FeedFragment();
			    FragmentTransaction transaction = thisThing.getFragmentManager().beginTransaction();
			    //transaction.add(R.id.container, fragment, "second");
			    transaction.addToBackStack(null);
			    transaction.commit();
			}

		});
        
        return view;
    }
	
	private void savePreferences(String key, String value){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

		Editor edit = shared.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private void savePreferences(String key, boolean value){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

		Editor edit = shared.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

}
