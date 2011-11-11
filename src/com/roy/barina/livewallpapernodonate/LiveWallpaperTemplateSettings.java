package com.roy.barina.livewallpapernodonate;

import com.roy.barina.livewallpapernodonate.R;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout.LayoutParams;

public class LiveWallpaperTemplateSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private CheckBox isBlackCheckBox;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(LiveWallpaperTemplate.SHARED_PREFS_NAME);
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
//			isBlackCheckBox.setBackgroundColor(Color.argb(180, 255, 255, 255));
			isBlackCheckBox.setText("Use black skull.");
			isBlackCheckBox.setTextColor(Color.BLACK);
			isBlackCheckBox.setTypeface(Typeface.DEFAULT_BOLD);
			isBlackCheckBox.setChecked(Settings.getIsBlackSetting());
			isBlackCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					LiveWallpaperTemplate.changeColor(isChecked);
				}
			});
		}
		return isBlackCheckBox;
	}
}