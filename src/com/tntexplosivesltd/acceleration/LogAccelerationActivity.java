/*
	This file is part of LogAcceleration.

    LogAcceleration is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    LogAcceleration is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LogAcceleration.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tntexplosivesltd.acceleration;

// Activity and dialog stuff
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
// App mechanics related stuff
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
// OS stuff including power manager
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
// Preference-related stuff
import android.preference.PreferenceManager;
// View-related stuff
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @brief Activity class for actually logging part of app.
 * @details This is the activity automatically started when the app starts. 
 */
public class LogAccelerationActivity extends Activity implements SensorEventListener {
	private boolean _first_run = true;
	private boolean _paused = false;
	private int _time;
	private int _delay = 100;
	private int _prev_delay = 0;
	private String _seperator = ",";
	private String _prev_seperator = "";
	
	private Handler _handler = new Handler();
	private Logger _logger = new Logger();
	private PowerManager _pm = null;
	private PowerManager.WakeLock _wl = null;
	private Runnable _logging_task = null;
	private SensorManager _sensor_manager = null;
	
	// "Constants" 
	/**
	 * @brief Whether or not degug mode is on.
	 * @details In debug mode, a few more Toasts show up with debug info.
	 */
	static final boolean DEBUG = false;
	
	/**
	 * @brief Constant passed to the dialog creator.
	 * @details Corresponds to the "Restart logging?" dialog and associated yes/no actions
	 */
	static final int RESET_DIALOG = 0;
	
    /** 
     * @brief Called when the activity is first created.
     * @details sets up all the variables that need initialisation.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        _wl = _pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "LogAcceleration");
        _sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.main);
    }
    
    /**
     * @brief Gets called when app Resumes.
     * @details Re-registers the accelerometer as a sensor, gets the wake-lock and manages setting values from the preferences. 
     */
    @Override
    protected void onResume()
    {
    	super.onResume();
    	_wl.acquire();
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	String delay_preference_string = preferences.getString("log_delay_pref", "100");
    	String seperator_preference_string = preferences.getString("seperator_pref", ",");
    	_delay = Integer.parseInt(delay_preference_string);
    	_seperator = seperator_preference_string;
    	_logger.set_seperator(_seperator);
    	
    	if (ColourManager.was_reset)
    	{
    		SharedPreferences.Editor editor = preferences.edit();
	    	editor.putString("bg_pref", "14");
	    	editor.putString("box_pref", "5");
	    	editor.putString("circle_pref", "2");
	    	editor.putString("grid_pref", "6");
	    	editor.putString("minmax_pref", "15");
	    	editor.putString("text_pref", "0");
	    	editor.putString("x_pref", "12");
	    	editor.putString("y_pref", "9");
	    	editor.putString("z_pref", "2");
	    	editor.commit();
    		ColourManager.was_reset = false;
    	}
    	else
    	{
    		// Set the colours from the preferences
	    	ColourManager.set_colours(ColourManager.palette[Integer.parseInt(preferences.getString("bg_pref", "14"))], ColourManager.palette[Integer.parseInt(preferences.getString("box_pref", "5"))], ColourManager.palette[Integer.parseInt(preferences.getString("circle_pref", "2"))], ColourManager.palette[Integer.parseInt(preferences.getString("grid_pref", "6"))], ColourManager.palette[Integer.parseInt(preferences.getString("minmax_pref", "15"))], ColourManager.palette[Integer.parseInt(preferences.getString("text_pref", "0"))], ColourManager.palette[Integer.parseInt(preferences.getString("x_pref", "12"))], ColourManager.palette[Integer.parseInt(preferences.getString("y_pref", "9"))], ColourManager.palette[Integer.parseInt(preferences.getString("z_pref", "2"))]);
	    	Panel.refresh_colours();
    	}
    	
    	if (_first_run)
    	{
    		_prev_delay = _delay;
    		_prev_seperator = _seperator;
    	}
    	else
    	{
    		if ((_delay != _prev_delay) || (_seperator != _prev_seperator))
    		{
    			if (_logger.is_logging())
    			{
    				showDialog(RESET_DIALOG);
    				_paused = true;
    			}
    			_prev_delay = _delay;
    			_prev_seperator = _seperator;
    		}
    	}
    	_first_run = false;
    	if (DEBUG)
    	{
    		Toast.makeText(getApplicationContext(), preferences.getString("circle_pref", "2"), Toast.LENGTH_LONG).show();
    		Toast.makeText(getApplicationContext(), delay_preference_string, Toast.LENGTH_SHORT).show();
    		Toast.makeText(getApplicationContext(), seperator_preference_string, Toast.LENGTH_SHORT).show();
    	}
    	_sensor_manager.registerListener(this, _sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }
    
    /**
     * @brief Gets called when app Stops.
     * Unregisters accelerometer as sensor, and removes the wake lock.
     */
    @Override
    protected void onStop()
    {
    	super.onStop();
    	_wl.release();
    	_sensor_manager.unregisterListener(this, _sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }
    
    /**
     * @brief Gets called when sensor value changes.
     * @details This is where the logging takes place, and the value is changed for the graphs.
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
    	if (!_paused)
    	{
	    	synchronized (this)
	    	{
	    		switch (event.sensor.getType())
	    		{
	    		case Sensor.TYPE_ACCELEROMETER:
	
	    			GraphData.x = event.values[0];
	    			GraphData.y = event.values[1];
	    			GraphData.z = event.values[2];
	    			
	    			if (!_logger.is_busy())
	    			{
	    				if (_logger.is_logging())
	    				{
	    					_logger.set_busy(true);
	    					_logging_task = new Runnable()
	    					{
	    						public void run()
	    						{
	    							if (_logger.is_logging())
	    							{
	    								if (!_logger.log(new float[]{(float)_time,GraphData.x,GraphData.y,GraphData.z}))
	    								{
	    									_logger.set_logging(false);
	    									Toast.makeText(getApplicationContext(), "Could not wrote to log. Logging is now off.", Toast.LENGTH_LONG).show();
	    								}
	    								else
	    								{
	    									_time += _delay;
	    								}
	    								_logger.set_busy(false);
	    							}
	    						}
	    					};
	    					synchronized (_handler)
	    					{
		    					_handler.postDelayed(_logging_task, _delay);
	    					}
	    				}
	    			}
	    			
	    			if (GraphData.x > GraphData.max_x)
	    				GraphData.max_x = GraphData.x;
	    			else if (GraphData.x < GraphData.min_x)
	    				GraphData.min_x = GraphData.x;
	
	    			if (GraphData.y > GraphData.max_y)
	    				GraphData.max_y = GraphData.y;
	    			else if (GraphData.y < GraphData.min_y)
	    				GraphData.min_y = GraphData.y;
	    			
	    			if (GraphData.z > GraphData.max_z)
	    				GraphData.max_z = GraphData.z;
	    			else if (GraphData.z < GraphData.min_z)
	    				GraphData.min_z = GraphData.z;
	    			
	    			synchronized(GraphData.data_x)
	    			{
	    				GraphData.data_x.addLast(GraphData.x);
	    				if (GraphData.data_x.size() > GraphData.max_data)
	    					GraphData.data_x.poll();
	    			}
	    			
	    			synchronized(GraphData.data_y)
	    			{
	    				GraphData.data_y.addLast(GraphData.y);
	    				if (GraphData.data_y.size() > GraphData.max_data)
	    					GraphData.data_y.poll();
	    			}
	    			
	    			synchronized(GraphData.data_z)
	    			{
	    				GraphData.data_z.addLast(GraphData.z);
	    				if (GraphData.data_z.size() > GraphData.max_data)
	    					GraphData.data_z.poll();
	    			}
	    		}
	    	}
    	}
    }

    /**
     * @brief Empty, needed for overriding.
     */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	
    /**
     * @brief Called when user presses "Menu" key.
     * @details Inflates the options menu.
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
    /**
     * @brief Gets called when one of the options menu items is selected.
     * @details Handles which item was pressed, and invokes actions based on that. 
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.orientation:
			if (GraphData.orientation == 0)
			{
				GraphData.orientation = 1;
				item.setTitle(R.string.orientation_upright);
			}
			else
			{
				GraphData.orientation = 0;
				item.setTitle(R.string.orientation_flat);
			}
			return true;
		case R.id.mode:
			if (GraphData.mode == 0)
			{
				GraphData.mode = 1;
				item.setTitle(R.string.mode_graph);
			}
			else
			{
				GraphData.mode = 0;
				item.setTitle(R.string.mode_circle);
			}
			return true;
		case R.id.logging:
			if (_logger.is_logging())
			{
				_logger.set_logging(false);
				item.setTitle(R.string.logging_off);
				_time = 0;
		        Toast.makeText(getApplicationContext(), "Logging is now off.", Toast.LENGTH_LONG).show();
			}
			else
			{
				if (start_logging())
		        	item.setTitle(R.string.logging_on);
			}
			return true;
		case R.id.settings:
			startActivity(new Intent(LogAccelerationActivity.this, PreferencesActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * @brief Handles which dialog to display.
	 * @param id The numerical ID of the dialog to show.
	 * @return A Dialog object which holds the correct dialog to display.
	 */
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog;
		switch(id)
		{
		case RESET_DIALOG:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Restart logging to new file?")
	    		   .setCancelable(false)
	    		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	    	{
				public void onClick(DialogInterface dialog, int which)
				{
					// Reset logging, and clear all waiting tasks.
					synchronized (_handler)
					{
						_handler.removeCallbacks(_logging_task);
						start_logging();
					}
					_paused = false;
				}
			});
	    	builder.setNegativeButton("No", new DialogInterface.OnClickListener()
	    	{
				public void onClick(DialogInterface dialog, int which)
				{
					_paused = false;
				}
			});
	    	dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	/**
	 * @brief Starts logging acceleration values to a new file.
	 * @return Whether or not the new log opened properly. 
	 */
	private boolean start_logging()
	{
		_logger.set_logging(true);
		String log_message = _logger.initialize();
        Toast.makeText(getApplicationContext(), log_message, Toast.LENGTH_LONG).show();
    	_logger.log_header();
        if (_logger.is_logging())
        {
        	_time = 0;
        	return true;
        }
        return false;
	}
}