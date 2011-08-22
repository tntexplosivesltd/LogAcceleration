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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
// Hardware/accelerometer imports
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
// OS stuff including power manager
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
// View-related stuff
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @brief Activity class for actually logging part of app
 * @details This is the activity automatically started when the app starts 
 */
public class LogAccelerationActivity extends Activity implements SensorEventListener {
	
	// "Primitive" types
	/**
	 * @brief Number to write out with the corresponding point
	 */
	int data_num;
	
	/**
	 * @brief Power manager to get access to WakeLock
	 */
	PowerManager pm = null;
	
	/**
	 * @brief Wakelock so we can keep the screen on
	 */
	PowerManager.WakeLock wl = null;
	
	/**
	 * @brief Sensor manager gives us access to the accelerometer
	 */
	SensorManager sensor_manager = null;
	
	/**
	 * @brief Logger object to get access to all the logging functionalities
	 */
	Logger logger = new Logger();
	
    /** 
     * @brief Called when the activity is first created.
     * @details sets up all the variables that need initialisation
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "LogAcceleration");
        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.main);
    }
    
    /**
     * @brief Gets called when app Resumes. Re-registers accelerometer as sensor
     */
    @Override
    protected void onResume()
    {
    	wl.acquire();
    	sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    	super.onResume();
    }
    
    /**
     * @brief Gets called when app Stops. Unregisters accelerometer as sensor
     */
    @Override
    protected void onStop()
    {
    	wl.release();
    	sensor_manager.unregisterListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    	super.onStop();
    }
    
    /**
     * @brief Gets called when sensor value changes.
     * @details This is where the logging takes place, and the value is changed for the graphs
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
    	synchronized (this)
    	{
    		switch (event.sensor.getType())
    		{
    		case Sensor.TYPE_ACCELEROMETER:

    			GraphData.x = event.values[0];
    			GraphData.y = event.values[1];
    			GraphData.z = event.values[2];
    			
    			if (!logger.is_busy())
    			{
    				if (logger.is_logging())
    				{
    					logger.set_busy(true);
    					Handler handler = new Handler();
    					handler.postDelayed(new Runnable()
    					{
    						public void run()
    						{
    							//logger.log(to_log);
    							if (!logger.log(new float[]{(float)data_num,GraphData.x,GraphData.y,GraphData.z}))
    							{
    								logger.set_logging(false);
    								Toast.makeText(getApplicationContext(), "Could not wrote to log. Logging is now off.", Toast.LENGTH_LONG).show();
    							}
    							logger.set_busy(false);
    						}
    					}, 100);
    					data_num++;
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
    				{
    					GraphData.data_x.poll();
    				}
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

    /**
     * @brief Empty, needed for overriding
     */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	
    /**
     * @brief Called when user presses "Menu" key
     * @details Inflates the options menu
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
    /**
     * @brief Gets called when one of the options menu items is selected
     * @details Handles which item was pressed, and invokes actions based on that 
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.reset:
			GraphData.reset();
			return true;
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
			if (logger.is_logging())
			{
				/**
				* @todo make it stop post-delayed (if possible) and wait til it's done.
				*/
				logger.set_logging(false);
				item.setTitle(R.string.logging_off);
				data_num = 0;
		        Toast.makeText(getApplicationContext(), "Logging is now off.", Toast.LENGTH_LONG).show();
			}
			else
			{
				logger.set_logging(true);
				String log_message = logger.initialize();
		        Toast.makeText(getApplicationContext(), log_message, Toast.LENGTH_LONG).show();
	        	logger.log_header();
		        if (logger.is_logging())
		        {
		        	item.setTitle(R.string.logging_on);
		        	data_num = 0;
		        }
			}
			return true;
		case R.id.settings:
			startActivity(new Intent(LogAccelerationActivity.this, PreferencesActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}