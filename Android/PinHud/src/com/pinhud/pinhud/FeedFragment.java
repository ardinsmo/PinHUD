package com.pinhud.pinhud;

import java.util.Locale;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

public class FeedFragment extends Fragment {

		private CardLayout layout;
		private RelativeLayout newCard;
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        View view = inflater.inflate(R.layout.feed_layout, container,
	                false);
	        
	        layout = (CardLayout) view.findViewById(R.id.theLayout);
	        newCard = (RelativeLayout) view.findViewById(R.id.card_rel);
	        newCard = (RelativeLayout) View.inflate(this.getActivity(), R.layout.main_list_card, null);
	        layout.addView(newCard);
			newCard.startAnimation(AnimationUtils.loadAnimation(newCard.getContext(),
					R.anim.slide_right));
			
	        return view;
	    }
}
