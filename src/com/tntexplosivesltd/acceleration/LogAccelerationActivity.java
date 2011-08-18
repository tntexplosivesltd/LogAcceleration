package com.tntexplosivesltd.acceleration;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
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

public class LogAccelerationActivity extends Activity implements SensorEventListener {
	
	// "Primitive" types
	String elements = new String();
	
	
	PowerManager pm = null;
	PowerManager.WakeLock wl = null;
	SensorManager sensor_manager = null;
	ArrayList<Float> to_log = new ArrayList<Float>();
	Logger logger = new Logger();
	int data_num;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "LogAcceleration");
        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.main);
    }
    
    /*
    @Override
    protected void onStart()
    {
        //Toast toast = Toast.makeText(getApplicationContext(), "I have started!", Toast.LENGTH_LONG);
        //toast.show();
    }
    */
    
    @Override
    protected void onResume()
    {
    	wl.acquire();
    	sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    	super.onResume();
    }
    
    /*
    protected void onPause()
    {
    	logger.set_logging(false);
    }
    */
    
    @Override
    protected void onStop()
    {
    	wl.release();
    	sensor_manager.unregisterListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    	super.onStop();
    }
    
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
    			
    			if (!logger.busy)
    			{
        			to_log.clear();
        			to_log.add((float)data_num);
        			to_log.add(GraphData.x);
        			to_log.add(GraphData.y);
        			to_log.add(GraphData.z);
        			logger.busy = true;
    				Handler handler = new Handler();
    				handler.postDelayed(new Runnable()
    				{
    					public void run()
    					{ logger.log(to_log);
    				  	logger.busy = false;}
    					}, 100);
    				data_num++;
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
    				elements="";
    				if (GraphData.data_x.size() > GraphData.max_data)
    				{
    					GraphData.data_x.poll();
    				}
    			}
    			
    			synchronized(GraphData.data_y)
    			{
    				GraphData.data_y.addLast(GraphData.y);
    				elements="";
    				if (GraphData.data_y.size() > GraphData.max_data)
    				{
    					GraphData.data_y.poll();
    				}
    			}
    			
    			synchronized(GraphData.data_z)
    			{
    				GraphData.data_z.addLast(GraphData.z);
    				elements="";
    				if (GraphData.data_z.size() > GraphData.max_data)
    				{
    					GraphData.data_z.poll();
    				}
    			}
    		}
    	}
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	
	// Menu/Options
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
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
				logger.set_logging(false);
				item.setTitle(R.string.logging_off);
				data_num = 0;
				GraphData.logged_values = 0;
			}
			else
			{
				logger.set_logging(true);
				String log_message = logger.initialize();
		        Toast toast = Toast.makeText(getApplicationContext(), log_message, Toast.LENGTH_LONG);
		        toast.show();
		        item.setTitle(R.string.logging_on);
		        data_num = 0;
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}