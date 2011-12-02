package com.roy.barina.livewallpapernodonate;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NumericPicker extends LinearLayout
{
	private IOnValueChangedListener valueChangedListener;
	private final Float MINIMUM, MAXIMUM, STEP_SIZE;
	private Float value;
	private final long REPEAT_DELAY = 50;
	private Button decrement;
	private Button increment;
	private EditText valueText;
	private Handler repeatUpdateHandler = new Handler();
	private boolean autoIncrement = false;
	private boolean autoDecrement = false;

	public NumericPicker(Context context, float minimum, float maximum, float stepSize, float value)
	{
		super(context);
		MINIMUM = new Float(minimum);
		MAXIMUM = new Float(maximum);
		STEP_SIZE = stepSize;
		this.value = new Float(value);
		addView(getDecrement(), new LayoutParams(75, LayoutParams.WRAP_CONTENT));
		addView(getValueText(), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		addView(getIncrement(), new LayoutParams(75, LayoutParams.WRAP_CONTENT));
		setValue(value);
	}

	public void setOnValueChangedListener(IOnValueChangedListener valueChangedListener)
	{
		this.valueChangedListener = valueChangedListener;
	}

	private EditText getValueText()
	{
		if(valueText == null)
		{
			valueText = new EditText(getContext());
			valueText.setTextSize(25);
			valueText.setText(value.toString());
			valueText.setOnKeyListener(new View.OnKeyListener()
			{
				public boolean onKey(View v, int arg1, KeyEvent event)
				{
					float backupValue = value;
					try
					{
						value = Float.parseFloat(((EditText)v).getText().toString());
					}
					catch(NumberFormatException nfe)
					{
						value = backupValue;
					}
					valueChangeed();
					return false;
				}
			});
			valueText.setOnFocusChangeListener(new View.OnFocusChangeListener()
			{
				public void onFocusChange(View v, boolean hasFocus)
				{
					if(hasFocus)
					{
						((EditText)v).selectAll();
					}
				}
			});
			valueText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			valueText.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		return valueText;
	}

	private void valueChangeed()
	{
		if(this.valueChangedListener != null)
			this.valueChangedListener.ValueChangedListener(getValue());
	}

	private Button getIncrement()
	{
		if(increment == null)
		{
			increment = new Button(getContext());
			increment.setTextSize(25);
			increment.setText("+");
			increment.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					increment();
				}
			});
			increment.setOnLongClickListener(new View.OnLongClickListener()
			{
				public boolean onLongClick(View arg0)
				{
					autoIncrement = true;
					repeatUpdateHandler.post(new RepetetiveUpdater());
					return false;
				}
			});
			increment.setOnTouchListener(new View.OnTouchListener()
			{
				public boolean onTouch(View v, MotionEvent event)
				{
					if(event.getAction() == MotionEvent.ACTION_UP && autoIncrement)
						autoIncrement = false;
					return false;
				}
			});
		}
		return increment;
	}

	private Button getDecrement()
	{
		if(decrement == null)
		{
			decrement = new Button(getContext());
			decrement.setTextSize(25);
			decrement.setText("-");
			decrement.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					decrement();
				}
			});
			decrement.setOnLongClickListener(new View.OnLongClickListener()
			{
				public boolean onLongClick(View arg0)
				{
					autoDecrement = true;
					repeatUpdateHandler.post(new RepetetiveUpdater());
					return false;
				}
			});
			decrement.setOnTouchListener(new View.OnTouchListener()
			{
				public boolean onTouch(View v, MotionEvent event)
				{
					if(event.getAction() == MotionEvent.ACTION_UP && autoDecrement)
						autoDecrement = false;
					return false;
				}
			});
		}
		return decrement;
	}

	public void increment()
	{
		if(value < MAXIMUM)
		{
			setValue(value + STEP_SIZE);
			valueText.setText(value.toString());
			valueChangeed();
		}
	}

	public void decrement()
	{
		if(value > MINIMUM)
		{
			setValue(value - STEP_SIZE);
			valueText.setText(value.toString());
			valueChangeed();
		}
	}

	public float getValue()
	{
		return value;
	}

	public void setValue(float value)
	{
		if(value > MAXIMUM)
			value = MAXIMUM;
		if(value < 0)
			this.value = 0f;
		if(value >= 0)
		{
			this.value = value;
			valueText.setText(this.value.toString());
		}
		valueChangeed();
	}

	class RepetetiveUpdater implements Runnable
	{
		public void run()
		{
			if(autoIncrement)
			{
				increment();
				repeatUpdateHandler.postDelayed(new RepetetiveUpdater(), REPEAT_DELAY);
			}
			else
				if(autoDecrement)
				{
					decrement();
					repeatUpdateHandler.postDelayed(new RepetetiveUpdater(), REPEAT_DELAY);
				}
		}
	}

	public interface IOnValueChangedListener
	{
		void ValueChangedListener(float size);
	}
}