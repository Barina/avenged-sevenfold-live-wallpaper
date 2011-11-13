package com.roy.barina.livewallpapernodonate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.anddev.andengine.util.Debug;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static final String IS_BLACK_SETTING = "isBlack", IS_PAUSED_SETTING = "isPaused";
	private LinearLayout linearLayout;
	private CheckBox isBlackCheckBox, pausedCheckBox;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(LiveWallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.wallpaper_settings);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		addContentView(getLinearLayout(), new android.widget.LinearLayout.LayoutParams(LiveWallpaper.CAMERA_WIDTH, LiveWallpaper.CAMERA_HEIGHT));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
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
			linearLayout.addView(getIsBlackCheckBox());
			linearLayout.addView(getPausedCheckBox());
		}
		return linearLayout;
	}

	public CheckBox getIsBlackCheckBox()
	{
		if(isBlackCheckBox == null)
		{
			isBlackCheckBox = new CheckBox(this);
			isBlackCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			isBlackCheckBox.setText("Use black skull.");
			isBlackCheckBox.setTextColor(Color.BLACK);
			isBlackCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			isBlackCheckBox.setChecked(getBooleanSetting(IS_BLACK_SETTING));
			isBlackCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					LiveWallpaper.changeColor(isChecked);
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
					setSetting(IS_PAUSED_SETTING, isChecked);
				}
			});
		}
		return pausedCheckBox;
	}

	private static Context activityContext;

	public static void loadContext(Context context)
	{
		activityContext = context;
	}

	@Deprecated
	public static boolean getIsBlackSetting()
	{
		ObjectInputStream inputStream = null;
		try
		{
			inputStream = new ObjectInputStream(activityContext.openFileInput("isBlack"));
			return inputStream.readBoolean();
		}
		catch(Exception e)
		{
			Debug.w("Faild to load setting." + e.getMessage(), e);
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

	public static boolean getBooleanSetting(String settingName)
	{
		ObjectInputStream inputStream = null;
		try
		{
			inputStream = new ObjectInputStream(activityContext.openFileInput(settingName));
			return inputStream.readBoolean();
		}
		catch(Exception e)
		{
			Debug.w("Faild to load setting." + e.getMessage(), e);
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

	public static void setSetting(String settingName, boolean isBlack)
	{
		ObjectOutputStream outputStream = null;
		try
		{
			outputStream = new ObjectOutputStream(activityContext.openFileOutput(settingName/* "isBlack" */, 0));
			outputStream.writeBoolean(isBlack);
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