package org.un.myworld;

import org.un.myworld.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MyWorldLauncher extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	public static String [] LANGUAGE_CODE,LANGUAGE_NAME,LANGUAGE_FLAG;
	private static final String TAG="MyWorldLauncher";
	private int pos=0;
    private LanguageAdapter adapter;
    private ListView contentView;
    private Button btnUseLanguage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		//initialize sharePrefs variable
		Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		setContentView(R.layout.activity_my_world_launcher);
		setupActionBar();
		
		 //initialize pref editor
		 Preferences.edit = Preferences.sharedPrefs.edit();

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		btnUseLanguage=(Button)findViewById(R.id.use_language_button);
		//final View contentView = findViewById(R.id.fullscreen_content);
		//final ListView contentView;// = (ListView)findViewById(R.id.list);
		
		LANGUAGE_NAME=this.getResources().getStringArray(R.array.pref_language_item);
		LANGUAGE_CODE=this.getResources().getStringArray(R.array.pref_language_value);
		LANGUAGE_FLAG=this.getResources().getStringArray(R.array.pref_language_value);
		
		contentView=(ListView)findViewById(R.id.listview);
        adapter = new LanguageAdapter(LANGUAGE_NAME,this);
        //setListAdapter(mAdapter);
        contentView.setAdapter(adapter);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.use_language_button).setOnTouchListener(
				mDelayHideTouchListener);
		Log.i(TAG,"Button touched");
	}
	
	//register use language click listener
	public void btnUseLanguage_clicked(View view){
		//start home activity
		Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intentHome);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	//view items holder
	 private static class LanguagesViewHolder {
	    	public TextView text_code,text_title;
			public ImageView img_language_flag;
			
	    }
	
   //inner class to define our language adapter
	private class LanguageAdapter extends BaseAdapter {

        private String[] mData;
        
      //variables
	    private Activity activity;
	
	

        public LanguageAdapter(String[] data,Activity a) {
            mData = data;
            activity = a;
        }

        public void changeData(String[] data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LanguagesViewHolder viewHolder = null;

            if (convertView == null) {
            	LayoutInflater inflator = activity.getLayoutInflater();
				convertView = inflator.inflate(R.layout.language_row, null);
              

                viewHolder = new LanguagesViewHolder();
				viewHolder.text_code = (TextView) convertView.findViewById(R.id.language_code);
				viewHolder.text_title = (TextView) convertView.findViewById(R.id.language_title);
				viewHolder.img_language_flag = (ImageView) convertView.findViewById(R.id.language_flag);
				
                
               // ((TextView) convertView.findViewById(R.id.mdg_description)).setOnClickListener(mVoteCheckboxClickListener);

                convertView.setTag(viewHolder);
            } else {
            	viewHolder = (LanguagesViewHolder) convertView.getTag();
            }

           

            viewHolder.text_code.setText(LANGUAGE_CODE[position]);
			viewHolder.text_title.setText(LANGUAGE_NAME[position]);
			viewHolder.img_language_flag.setImageResource(getResources().getIdentifier(LANGUAGE_FLAG[position] , "drawable", getPackageName()));
			//viewHolder.checkbox.setChecked(list.get(position).isSelected());
			
			//register an on click listener
			((TextView) convertView.findViewById(R.id.language_title)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 final int position = contentView.getPositionForView(v);
					// TODO check if mdg_code is visible
					Log.i(TAG,"Language Picked: "+LANGUAGE_CODE[position]);
					if (TOGGLE_ON_CLICK) {
						mSystemUiHider.toggle();
						
						btnUseLanguage.setText(LANGUAGE_NAME[position]);
						Preferences.languagePrefix=LANGUAGE_CODE[position];
						Log.i(TAG,"Key: "+Preferences.KEY_LANGUAGE_LIST_PREFERENCE);
						Log.i(TAG,"Value: "+Preferences.languagePrefix);
						Preferences.storeSharedPrefs(Preferences.KEY_LANGUAGE_LIST_PREFERENCE, Preferences.languagePrefix);
						
					} else {
						mSystemUiHider.show();
						
					}
					
				}
			});

            return convertView;
        }
    }
    }
