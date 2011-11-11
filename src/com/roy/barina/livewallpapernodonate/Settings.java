package com.roy.barina.livewallpapernodonate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.anddev.andengine.util.Debug;
import com.roy.barina.livewallpapernodonate.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout.LayoutParams;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private CheckBox isBlackCheckBox;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(LiveWallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.wallpaper_settings);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		addContentView(getIsBlackCheckBox(), new android.widget.LinearLayout.LayoutParams(540, 80));
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

	public CheckBox getIsBlackCheckBox()
	{
		if(isBlackCheckBox == null)
		{
			isBlackCheckBox = new CheckBox(this);
			isBlackCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			isBlackCheckBox.setText("Use black skull.");
			isBlackCheckBox.setTextColor(Color.BLACK);
			isBlackCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			isBlackCheckBox.setChecked(getIsBlackSetting());
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
	
	private static Context activityContext;

	public static void loadContext(Context context)
	{
		activityContext = context;
	}

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

	public static void setSetting(boolean isBlack)
	{
		ObjectOutputStream outputStream = null;
		try
		{
			outputStream = new ObjectOutputStream(activityContext.openFileOutput("isBlack", 0));
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