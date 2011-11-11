package com.roy.barina.livewallpapernodonate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.anddev.andengine.util.Debug;
import android.content.Context;

public class Settings
{
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