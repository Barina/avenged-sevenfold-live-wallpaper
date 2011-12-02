package com.roy.barina.livewallpapernodonate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;
import org.anddev.andengine.util.Debug;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.roy.barina.livewallpapernodonate.NumericPicker.IOnValueChangedListener;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, SettingsConstants
{
	private static boolean isBlack, paused, drawTitle, drawBg;
	private static int logoDistance, titleDistance, logoCenterDistance, titleCenterDistance;
	private static float titleTextSize;
	private static String titleString;

	private LinearLayout linearLayout;
	private CheckBox isBlackCheckBox, pausedCheckBox, drawTitleCheckBox, drawBgCheckBox;
	private SeekBar titleSeekBar, logoSeekBar, titleCenterSeekBar, logoCenterSeekBar;
	private TextView titleDistanceTextView, logoDistanceTextView, titleCenterDistanceTextView, logoCenterDistanceTextView;
	private Button resetButton;
	private EditText titleEditText;
	private NumericPicker titleSizePicker;
	
	private AdView adView;// non donation

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(LiveWallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.wallpaper_settings);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		final ScrollView view = new ScrollView(this);
		view.addView(getLinearLayout());
		addContentView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		saveSettings();
		LiveWallpaper.changeColor(isBlack);
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		getAdView().destroy();// non donation
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{}

	public static boolean getSettingAsBoolean(String settingName)
	{
		if(settingName.equals(IS_BLACK_SETTING))
			return isBlack;
		if(settingName.equals(IS_PAUSED_SETTING))
			return paused;
		if(settingName.equals(DRAW_TITLE_SETTING))
			return drawTitle;
		throw new InvalidParameterException("Bad boolean setting request.");
	}

	public static int getSettingAsInt(String settingName)
	{
		if(settingName.equals(TITLE_TOP_DISTANCE_SETTING))
			return titleDistance;
		if(settingName.equals(LOGO_TOP_DISTANCE_SETTING))
			return logoDistance;
//		if(settingName.equals(TITLE_CENTER_DISTANCE_SETTING))
//			return titleCenterDistance;
//		if(settingName.equals(LOGO_CENTER_DISTANCE_SETTING))
//			return logoCenterDistance;
		{// TODO: temp code..
			if(settingName.equals(TITLE_CENTER_DISTANCE_SETTING) || settingName.equals(LOGO_CENTER_DISTANCE_SETTING))
				return 0;
		}
		return -1;
	}

	public static float getSettingAsFloat(String settingName)
	{
		if(settingName.equals(TITLE_TEXT_SIZE_SETTING))
			return titleTextSize;
		return -1;
	}

	public static String getSettingAsString(String settingName)
	{
		if(settingName.equals(TITLE_STRING_SETTING))
			return titleString;	
		throw new InvalidParameterException("Bad string setting request.");
	}
	
	public LinearLayout getLinearLayout()
	{
		if(linearLayout == null)
		{
			linearLayout = new LinearLayout(this);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.addView(getAdView()); // non donation
			TextView textView = new TextView(this);
			textView.setText("Please donate.. :)");// non donation
			textView.setTextColor(Color.RED);// non donation
			textView.setTextSize(20);
			textView.setBackgroundColor(Color.argb(190, 0, 0, 0));
			linearLayout.addView(textView);
			linearLayout.addView(getTitleEditText());			
			textView = new TextView(this);
			textView.setText("Title text size:");
			textView.setTextColor(Color.WHITE);
			textView.setBackgroundColor(Color.argb(190, 0, 0, 0));
			linearLayout.addView(textView);			
			linearLayout.addView(getTitleSizePicker());			
			linearLayout.addView(getIsBlackCheckBox());
			linearLayout.addView(getPausedCheckBox());
			linearLayout.addView(getDrawTitleCheckBox());
			linearLayout.addView(getDrawBgCheckBox());
			linearLayout.addView(getTitleDistanceTextView());
			linearLayout.addView(getTitleSeekBar());
			linearLayout.addView(getLogoDistanceTextView());
			linearLayout.addView(getLogoSeekBar());
//			linearLayout.addView(getTitleCenterDistanceTextView());	// cause problems..
//			linearLayout.addView(getTitleCenterSeekBar());			//
//			linearLayout.addView(getLogoCenterDistanceTextView());	//
//			linearLayout.addView(getLogoCenterSeekBar());			//
			linearLayout.addView(getResetButton());
			getAdView().loadAd(new AdRequest());// non donation
		}
		return linearLayout;
	}

	public AdView getAdView()// non donation
	{
		if(adView == null)
			adView = new AdView(this, AdSize.BANNER, "a14ebf22b8e1ba6");
		return adView;
	}

	public CheckBox getIsBlackCheckBox()
	{
		if(isBlackCheckBox == null)
		{
			isBlackCheckBox = new CheckBox(this);
			isBlackCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			isBlackCheckBox.setText("Invert colors.");
			isBlackCheckBox.setTextColor(Color.BLACK);
			isBlackCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			isBlackCheckBox.setChecked(getBooleanSetting(IS_BLACK_SETTING));
			isBlackCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					LiveWallpaper.changeColor(isChecked);
					isBlack = isChecked;
				}
			});
		}
		return isBlackCheckBox;
	}

	public CheckBox getPausedCheckBox()
	{
		if(pausedCheckBox == null)
		{
			pausedCheckBox = new CheckBox(this);
			pausedCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			pausedCheckBox.setText("Pause animation.");
			pausedCheckBox.setTextColor(Color.BLACK);
			pausedCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			pausedCheckBox.setChecked(getBooleanSetting(IS_PAUSED_SETTING));
			pausedCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					LiveWallpaper.pauseScene(isChecked);
					paused = isChecked;
				}
			});
		}
		return pausedCheckBox;
	}

	public CheckBox getDrawTitleCheckBox()
	{
		if(drawTitleCheckBox == null)
		{
			drawTitleCheckBox = new CheckBox(this);
			drawTitleCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			drawTitleCheckBox.setText("Draw title.");
			drawTitleCheckBox.setTextColor(Color.BLACK);
			drawTitleCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			drawTitleCheckBox.setChecked(getBooleanSetting(DRAW_TITLE_SETTING));
			drawTitleCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					LiveWallpaper.drawTitle(isChecked);
					drawTitle = isChecked;
				}
			});
		}
		return drawTitleCheckBox;
	}

	public CheckBox getDrawBgCheckBox()
	{
		if(drawBgCheckBox == null)
		{
			drawBgCheckBox = new CheckBox(this);
			drawBgCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			drawBgCheckBox.setText("Draw custom background.");
			drawBgCheckBox.setTextColor(Color.BLACK);
			drawBgCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			drawBgCheckBox.setChecked(getBooleanSetting(DRAW_BG_SETTING));
			drawBgCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					LiveWallpaper.drawBg(isChecked);
					drawBg = isChecked;
				}
			});
		}
		return drawBgCheckBox;
	}

	public TextView getTitleDistanceTextView()
	{
		if(titleDistanceTextView == null)
		{
			titleDistanceTextView = new TextView(this);
			titleDistanceTextView.setText("Title distance from ceiling: " + titleDistance);
			titleDistanceTextView.setTextColor(Color.WHITE);
			titleDistanceTextView.setBackgroundColor(Color.argb(190, 0, 0, 0));
		}
		return titleDistanceTextView;
	}

	public TextView getLogoDistanceTextView()
	{
		if(logoDistanceTextView == null)
		{
			logoDistanceTextView = new TextView(this);
			logoDistanceTextView.setText("Logo distance from ceiling: " + logoDistance);
			logoDistanceTextView.setTextColor(Color.WHITE);
			logoDistanceTextView.setBackgroundColor(Color.argb(190, 0, 0, 0));
		}
		return logoDistanceTextView;
	}

	public TextView getTitleCenterDistanceTextView()
	{
		if(titleCenterDistanceTextView == null)
		{
			titleCenterDistanceTextView = new TextView(this);
			titleCenterDistanceTextView.setText("Title distance from center: " + titleCenterDistance);
			titleCenterDistanceTextView.setTextColor(Color.WHITE);
			titleCenterDistanceTextView.setBackgroundColor(Color.argb(190, 0, 0, 0));
		}
		return titleCenterDistanceTextView;
	}

	public TextView getLogoCenterDistanceTextView()
	{
		if(logoCenterDistanceTextView == null)
		{
			logoCenterDistanceTextView = new TextView(this);
			logoCenterDistanceTextView.setText("Logo distance from center: " + logoCenterDistance);
			logoCenterDistanceTextView.setTextColor(Color.WHITE);
			logoCenterDistanceTextView.setBackgroundColor(Color.argb(190, 0, 0, 0));
		}
		return logoCenterDistanceTextView;
	}

	public SeekBar getTitleSeekBar()
	{
		if(titleSeekBar == null)
		{
			titleSeekBar = new SeekBar(this);
			titleSeekBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			titleSeekBar.setMax(CAMERA_HEIGHT);
			int distance = getSettingAsInt(TITLE_TOP_DISTANCE_SETTING);
			titleSeekBar.setProgress(distance <= -1 ? 100 : distance);
			titleSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
					LiveWallpaper.changeColor(isBlack);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					if(fromUser){
						titleDistance = progress;
					getTitleDistanceTextView().setText("Title distance from ceiling: " + progress);}
				}
			});
		}
		return titleSeekBar;
	}

	public SeekBar getLogoSeekBar()
	{
		if(logoSeekBar == null)
		{
			logoSeekBar = new SeekBar(this);
			logoSeekBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			logoSeekBar.setMax(CAMERA_HEIGHT);
			int distance = getSettingAsInt(LOGO_TOP_DISTANCE_SETTING);
			logoSeekBar.setProgress(distance <= -1 ? 400 : distance);
			logoSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
					LiveWallpaper.changeColor(isBlack);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					if(fromUser){
						logoDistance = progress;
					getLogoDistanceTextView().setText("Logo distance from ceiling: " + progress);}
				}
			});
		}
		return logoSeekBar;
	}

	public SeekBar getTitleCenterSeekBar()
	{
		if(titleCenterSeekBar == null)
		{
			titleCenterSeekBar = new SeekBar(this);
			titleCenterSeekBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			titleCenterSeekBar.setMax(CAMERA_WIDTH * 3);
			titleCenterSeekBar.setProgress(getSettingAsInt(TITLE_CENTER_DISTANCE_SETTING) + (int)((CAMERA_WIDTH * 3) * 0.5f));
			titleCenterSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
					LiveWallpaper.changeColor(isBlack);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					if(fromUser){
						titleCenterDistance = progress - (int)((CAMERA_WIDTH * 3) * 0.5f);
					getTitleCenterDistanceTextView().setText("Title distance from center: " + titleCenterDistance);}
				}
			});
		}
		return titleCenterSeekBar;
	}

	public SeekBar getLogoCenterSeekBar()
	{
		if(logoCenterSeekBar == null)
		{
			logoCenterSeekBar = new SeekBar(this);
			logoCenterSeekBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			logoCenterSeekBar.setMax(CAMERA_WIDTH * 3);
			logoCenterSeekBar.setProgress(getSettingAsInt(LOGO_CENTER_DISTANCE_SETTING) + (int)((CAMERA_WIDTH * 3) * 0.5f));
			logoCenterSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
					LiveWallpaper.changeColor(isBlack);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					if(fromUser){
						logoCenterDistance = progress - (int)((CAMERA_WIDTH * 3) * 0.5f);
					getLogoCenterDistanceTextView().setText("Logo distance from center: " + logoCenterDistance);}
				}
			});
		}
		return logoCenterSeekBar;
	}

	public Button getResetButton()
	{
		if(resetButton == null)
		{
			resetButton = new Button(this);
			resetButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			resetButton.setText("Reset to defaults");
			resetButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
					builder.setMessage("Are you sure you want to reset all your settings to default?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
								public void onClick(DialogInterface dialog, int id)
								{
									resetSettings();
								}
							}).setNegativeButton("No", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int id)
								{
									dialog.cancel();
								}
							});
					builder.create().show();
				}
			});
		}
		return resetButton;
	}

	public EditText getTitleEditText()
	{
		if(titleEditText == null)
		{
			titleEditText = new EditText(this);
			titleEditText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			titleEditText.setText(titleString);
			titleEditText.setSingleLine();
			titleEditText.setOnKeyListener(new View.OnKeyListener()
			{
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event)
				{
					String tmp = getTitleEditText().getText().toString();
					if(tmp.length() >= MAX_TITLE_LENGTH)
						titleString = tmp.substring(0, MAX_TITLE_LENGTH);
					else
						titleString = tmp;
					LiveWallpaper.updateTitleText();
					return false;
				}
			});
		}
		return titleEditText;
	}

	public NumericPicker getTitleSizePicker()
	{
		if(titleSizePicker==null)	
		{
			titleSizePicker= new NumericPicker(this, 0, 5,0.10f, titleTextSize);
			titleSizePicker.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			titleSizePicker.setOnValueChangedListener(new IOnValueChangedListener()
			{
				@Override
				public void ValueChangedListener(float size)
				{
					titleTextSize = size;
					LiveWallpaper.updateTitleSize();
				}
			});
		}
		return titleSizePicker;
	}
	
	private static Context activityContext;

	public static void loadContext(Context context)
	{
		activityContext = context;
		loadSettings();
	}

	private static void loadSettings()
	{//TODO: add or remove settings
		isBlack = getBooleanSetting(IS_BLACK_SETTING);
		paused = getBooleanSetting(IS_PAUSED_SETTING);
		drawTitle = getBooleanSetting(DRAW_TITLE_SETTING);
		drawBg = getBooleanSetting(DRAW_BG_SETTING);
		if((logoDistance = getIntSetting(LOGO_TOP_DISTANCE_SETTING)) <= -1)
			logoDistance = 400;
		if((titleDistance = getIntSetting(TITLE_TOP_DISTANCE_SETTING)) <= -1)
			titleDistance = 100;
		logoCenterDistance = getIntSetting(LOGO_CENTER_DISTANCE_SETTING);
		titleCenterDistance = getIntSetting(TITLE_CENTER_DISTANCE_SETTING);
		if((titleString = getStringSetting(TITLE_STRING_SETTING)) == null || titleString.equals(""))
			titleString = activityContext.getResources().getString(R.string.default_title_string);
		if((titleTextSize = getFloatSetting(TITLE_TEXT_SIZE_SETTING)) == -1f)
			titleTextSize = 1;
	}

	private static void saveSettings()
	{//TODO: add or remove settings
		setSetting(IS_BLACK_SETTING, isBlack);
		setSetting(IS_PAUSED_SETTING, paused);
		setSetting(DRAW_TITLE_SETTING, drawTitle);
		setSetting(DRAW_BG_SETTING, drawBg);
		setSetting(LOGO_TOP_DISTANCE_SETTING, logoDistance);
		setSetting(TITLE_TOP_DISTANCE_SETTING, titleDistance);
		setSetting(LOGO_CENTER_DISTANCE_SETTING, logoCenterDistance);
		setSetting(TITLE_CENTER_DISTANCE_SETTING, titleCenterDistance);
		setSetting(TITLE_STRING_SETTING, titleString);
		setSetting(TITLE_TEXT_SIZE_SETTING, titleTextSize);
	}

	private void resetSettings()
	{
		isBlack = false;
		paused = false;
		drawTitle = true;
		drawBg = true;
		logoDistance = 400;
		titleDistance = 100;
		logoCenterDistance = 0;
		titleCenterDistance = 0;
		titleTextSize = 1;
		saveSettings();
		finish();
		LiveWallpaper.changeColor(isBlack);
	}

	private static boolean getBooleanSetting(String settingName)
	{
		Boolean result;
		try
		{
			result = (Boolean)getSetting(settingName);
		}
		catch(ClassCastException e)
		{
			result = false;
		}
		if(result == null)
			result = false;
		return result;
	}

	private static int getIntSetting(String settingName)
	{
		Integer result = -1;
		try
		{
			result = (Integer)getSetting(settingName);
		}
		catch(ClassCastException e)
		{
			return -1;
		}
		if(result == null)
			result = -1;
		return result;
	}

	private static float getFloatSetting(String settingName)
	{
		Float result = -1f;
		try
		{
			result = (Float)getSetting(settingName);
		}
		catch(ClassCastException e)
		{
			return -1;
		}
		if(result == null)
			result = -1f;
		return result;
	}

	private static String getStringSetting(String settingName)
	{
		try
		{
			return (String)getSetting(settingName);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	private static Object getSetting(String settingName)
	{
		ObjectInputStream inputStream = null;
		try
		{
			inputStream = new ObjectInputStream(activityContext.openFileInput(settingName));
			return inputStream.readObject();
		}
		catch(Exception e)
		{
			Debug.w("Faild to load setting. " + e.getMessage(), e);
		}
		finally
		{
			if(inputStream != null)
				try
				{
					inputStream.close();
				}
				catch(IOException e)
				{
					Debug.w(e.getMessage(), e);
				}
		}
		return false;
	}

	private static void setSetting(String settingName, Object value)
	{
		ObjectOutputStream outputStream = null;
		try
		{
			outputStream = new ObjectOutputStream(activityContext.openFileOutput(settingName, 0));
			outputStream.writeObject(value);
			outputStream.flush();
			outputStream.close();
		}
		catch(Exception e)
		{
			Debug.w("Faild to save setting. " + e.getMessage(), e);
		}
		finally
		{
			if(outputStream != null)
				try
				{
					outputStream.close();
				}
				catch(IOException e)
				{
					Debug.w(e.getMessage(), e);
				}
		}
	}
}