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
	float offset = 0f;
	int max_data = 10;
	String elements = new String();
	
	GraphData graph_data = new GraphData();
	
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
    	super.onResume();
    	sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop()
    {
    	wl.release();
    	super.onStop();
    	sensor_manager.unregisterListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }
    
    @Override
    public void onSensorChanged(SensorEvent event)
    {
    	synchronized (this)
    	{
    		switch (event.sensor.getType())
    		{
    		case Sensor.TYPE_ACCELEROMETER:

    			graph_data.x = event.values[0];
    			graph_data.y = event.values[1];
    			graph_data.z = event.values[2];
    			
    			if (graph_data.x > graph_data.max_x)
    				graph_data.max_x = graph_data.x;
    			else if (graph_data.x < graph_data.min_x)
    				graph_data.min_x = graph_data.x;

    			if (graph_data.y > graph_data.max_y)
    				graph_data.max_y = graph_data.y;
    			else if (graph_data.y < graph_data.min_y)
    				graph_data.min_y = graph_data.y;
    			
    			if (graph_data.z > graph_data.max_z)
    				graph_data.max_z = graph_data.z;
    			else if (graph_data.z < graph_data.min_z)
    				graph_data.min_z = graph_data.z;
    			
    			output_x.setText("x:" + String.format("%.2f%n", graph_data.x));
    			output_y.setText("y:" + String.format("%.2f%n", graph_data.y));
    			output_z.setText("z:" + String.format("%.2f%n", graph_data.z - offset));
    			
    			graph_data.data.addLast(graph_data.x);
    			elements="";
    			if (graph_data.data.size() > max_data)
    			{
    				graph_data.data.poll();
    			}
    			for (Float point : graph_data.data)
    			{
    				elements += (point.toString() + ", ");
    			}
    			
    			minimum_x.setText("min_x:" + String.format("%.2f%n", graph_data.min_x));
    			minimum_y.setText("min_y:" + String.format("%.2f%n", graph_data.min_y));
    			minimum_z.setText("min_z:" + String.format("%.2f%n", graph_data.min_z - offset));
    			
    			maximum_x.setText("max_x:" + String.format("%.2f%n", graph_data.max_x));
    			maximum_y.setText("max_y:" + String.format("%.2f%n", graph_data.max_y));
    			maximum_z.setText("max_z:" + String.format("%.2f%n", graph_data.max_z - offset));
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
			if (offset == 0)
			{
				offset = 9.80665f;
			}
			else
			{
				offset = 0.f;
			}
			return true;
		case R.id.reset:
			graph_data.reset();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}