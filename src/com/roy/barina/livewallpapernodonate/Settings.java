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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static final String IS_BLACK_SETTING = "isBlack", IS_PAUSED_SETTING = "isPaused", DRAW_TITLE_SETTING = "drawTitle", LOGO_TOP_DISTANCE_SETTING = "logoDistance",
			TITLE_TOP_DISTANCE_SETTING = "titleDistance", LOGO_CENTER_DISTANCE_SETTING = "logoCenterDistance", TITLE_CENTER_DISTANCE_SETTING = "titleCenterDistance";
	private static boolean isBlack, paused, drawTitle;
	private static int logoDistance, titleDistance, logoCenterDistance, titleCenterDistance;

	public static boolean getSettingAsBoolean(String settingName)
	{
		if(settingName.equals(IS_BLACK_SETTING))
			return isBlack;
		if(settingName.equals(IS_PAUSED_SETTING))
			return paused;
		if(settingName.equals(DRAW_TITLE_SETTING))
			return drawTitle;
		throw new InvalidParameterException("Bad request.");
	}

	public static int getSettingAsInt(String settingName)
	{
		if(settingName.equals(TITLE_TOP_DISTANCE_SETTING))
			return titleDistance;
		if(settingName.equals(LOGO_TOP_DISTANCE_SETTING))
			return logoDistance;
		if(settingName.equals(TITLE_CENTER_DISTANCE_SETTING))
			return titleCenterDistance;
		if(settingName.equals(LOGO_CENTER_DISTANCE_SETTING))
			return logoCenterDistance;
		return -1;
	}

	private LinearLayout linearLayout;
	private CheckBox isBlackCheckBox, pausedCheckBox, drawTitleCheckBox;
	private SeekBar titleSeekBar, logoSeekBar;
	private TextView titleDistanceTextView, logoDistanceTextView;
	private Button resetButton;
	private AdView adView;// non donation

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(LiveWallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.wallpaper_settings);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		addContentView(getLinearLayout(), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		getAdView().destroy();// non donation
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{}

	public LinearLayout getLinearLayout()
	{
		if(linearLayout == null)
		{
			linearLayout = new LinearLayout(this);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.addView(getAdView()); // non donation
			final TextView textView = new TextView(this);
			textView.setText("Please donate.. :)");// non donation
			textView.setTextColor(Color.RED);// non donation
			textView.setTextSize(20);
			textView.setBackgroundColor(Color.argb(190, 0, 0, 0));
			linearLayout.addView(textView);
			linearLayout.addView(getIsBlackCheckBox());
			linearLayout.addView(getPausedCheckBox());
			linearLayout.addView(getDrawTitleCheckBox());
			linearLayout.addView(getTitleDistanceTextView());
			linearLayout.addView(getTitleSeekBar());
			linearLayout.addView(getLogoDistanceTextView());
			linearLayout.addView(getLogoSeekBar());
			linearLayout.addView(getResetButton());
			getAdView().loadAd(new AdRequest());// non donation
		}
		return linearLayout;
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

	public AdView getAdView()// non donation
	{
		if(adView == null)
			adView = new AdView(this, AdSize.BANNER, "a14ebf22b8e1ba6");
		return adView;
	}

	public TextView getTitleDistanceTextView()
	{
		if(titleDistanceTextView == null)
		{
			titleDistanceTextView = new TextView(this);
			titleDistanceTextView.setText("Title distance from ceiling: " + getTitleSeekBar().getProgress());
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
			logoDistanceTextView.setText("Logo distance from ceiling: " + getLogoSeekBar().getProgress());
			logoDistanceTextView.setTextColor(Color.WHITE);
			logoDistanceTextView.setBackgroundColor(Color.argb(190, 0, 0, 0));
		}
		return logoDistanceTextView;
	}

	public SeekBar getTitleSeekBar()
	{
		if(titleSeekBar == null)
		{
			titleSeekBar = new SeekBar(this);
			titleSeekBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			titleSeekBar.setMax(LiveWallpaper.CAMERA_HEIGHT);
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
					if(fromUser)
						titleDistance = progress;
					getTitleDistanceTextView().setText("Title distance from ceiling: " + progress);
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
			logoSeekBar.setMax(LiveWallpaper.CAMERA_HEIGHT);
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
					if(fromUser)
						logoDistance = progress;
					getLogoDistanceTextView().setText("Logo distance from ceiling: " + progress);
				}
			});
		}
		return logoSeekBar;
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
					builder.setMessage("Are you sure you want to reset all your settings?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
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

	private static Context activityContext;

	public static void loadContext(Context context)
	{
		activityContext = context;
		loadSettings();
	}

	private static void loadSettings()
	{
		isBlack = getBooleanSetting(IS_BLACK_SETTING);
		paused = getBooleanSetting(IS_PAUSED_SETTING);
		drawTitle = getBooleanSetting(DRAW_TITLE_SETTING);
		if((logoDistance = getIntSetting(LOGO_TOP_DISTANCE_SETTING)) <= -1)
			logoDistance = 400;
		if((titleDistance = getIntSetting(TITLE_TOP_DISTANCE_SETTING)) <= -1)
			titleDistance = 100;
		if((logoCenterDistance = getIntSetting(LOGO_CENTER_DISTANCE_SETTING)) <= -1)
			logoCenterDistance = 0;
		if((titleCenterDistance = getIntSetting(TITLE_CENTER_DISTANCE_SETTING)) <= -1)
			titleCenterDistance = 0;
	}

	private static void saveSettings()
	{
		setSetting(IS_BLACK_SETTING, isBlack);
		setSetting(IS_PAUSED_SETTING, paused);
		setSetting(DRAW_TITLE_SETTING, drawTitle);
		setSetting(LOGO_TOP_DISTANCE_SETTING, logoDistance);
		setSetting(TITLE_TOP_DISTANCE_SETTING, titleDistance);
		setSetting(LOGO_CENTER_DISTANCE_SETTING, logoCenterDistance);
		setSetting(TITLE_CENTER_DISTANCE_SETTING, titleCenterDistance);
	}

	private void resetSettings()
	{
		setSetting(IS_BLACK_SETTING, isBlack = false);
		setSetting(IS_PAUSED_SETTING, paused = false);
		setSetting(DRAW_TITLE_SETTING, drawTitle = true);
		setSetting(LOGO_TOP_DISTANCE_SETTING, logoDistance = 400);
		setSetting(TITLE_TOP_DISTANCE_SETTING, titleDistance = 100);
		setSetting(LOGO_CENTER_DISTANCE_SETTING, logoCenterDistance = 0);
		setSetting(TITLE_CENTER_DISTANCE_SETTING, titleCenterDistance = 0);
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
			// no need to handle..
		}
		if(result == null)
			result = -1;
		return result;
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