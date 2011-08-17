package com.tntexplosivesltd.acceleration;


import android.app.Activity;
import android.content.Context;
// Hardware/accelerometer imports
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
// OS stuff including power manager
import android.os.Bundle;
import android.os.PowerManager;
// View-related stuff
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class LogAccelerationActivity extends Activity implements SensorEventListener {
	
	// "Primitive" types
	String elements = new String();
	
	
	PowerManager pm = null;
	PowerManager.WakeLock wl = null;
	SensorManager sensor_manager = null;
	
	TextView output_x;
	TextView output_y;
	TextView output_z;
	TextView maximum_x;
	TextView maximum_y;
	TextView maximum_z;
	TextView minimum_x;
	TextView minimum_y;
	TextView minimum_z;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My_Tag");
        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.main);
        
        output_x = (TextView) findViewById(R.id.x);
        output_y = (TextView) findViewById(R.id.y);
        output_z = (TextView) findViewById(R.id.z);
        maximum_x = (TextView) findViewById(R.id.x_max);
        maximum_y = (TextView) findViewById(R.id.y_max);
        maximum_z = (TextView) findViewById(R.id.z_max);
        minimum_x = (TextView) findViewById(R.id.x_min);
        minimum_y = (TextView) findViewById(R.id.y_min);
        minimum_z = (TextView) findViewById(R.id.z_min);
        

        
    }
    
    @Override
    protected void onResume()
    {
    	wl.acquire();
    	sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    	super.onResume();
    }
    
    @Override
    protected void onStop()
    {
    	wl.release();
    	sensor_manager.unregisterListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    	moveTaskToBack(true);
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
    			
    			output_x.setText("x:" + String.format("%.2f%n", GraphData.x));
    			output_y.setText("y:" + String.format("%.2f%n", GraphData.y));
    			output_z.setText("z:" + String.format("%.2f%n", GraphData.z - GraphData.offset));
    			
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
    			
    			minimum_x.setText("min_x:" + String.format("%.2f%n", GraphData.min_x));
    			minimum_y.setText("min_y:" + String.format("%.2f%n", GraphData.min_y));
    			minimum_z.setText("min_z:" + String.format("%.2f%n", GraphData.min_z - GraphData.offset));
    			
    			maximum_x.setText("max_x:" + String.format("%.2f%n", GraphData.max_x));
    			maximum_y.setText("max_y:" + String.format("%.2f%n", GraphData.max_y));
    			maximum_z.setText("max_z:" + String.format("%.2f%n", GraphData.max_z - GraphData.offset));
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
		case R.id.z_offset:
			if (GraphData.offset == 0)
			{
				GraphData.offset = 9.80665f;
				item.setTitle(R.string.offset_on);
			}
			else
			{
				GraphData.offset = 0.f;
				item.setTitle(R.string.offset_off);
			}
			return true;
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
		case R.id.graphs:
			GraphData.show = !GraphData.show;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}