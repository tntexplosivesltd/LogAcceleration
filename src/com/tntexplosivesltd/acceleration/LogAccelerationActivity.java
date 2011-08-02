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
	
	float offset = 0f;
	Float x, y, z; 
	Float min_x = 100.f; 
	Float min_y = 100.f; 
	Float min_z = 100.f; 
	Float max_x = -100.f; 
	Float max_y = -100.f; 
	Float max_z = -100.f;
	
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

    			x = event.values[0];
    			y = event.values[1];
    			z = event.values[2];
    			
    			if (x > max_x)
    				max_x = x;
    			else if (x < min_x)
    				min_x = x;

    			if (y > max_y)
    				max_y = y;
    			else if (y < min_y)
    				min_y = y;
    			
    			if (z > max_z)
    				max_z = z;
    			else if (z < min_z)
    				min_z = z;
    			
    			output_x.setText("x:" + String.format("%.2f%n", x));
    			output_y.setText("y:" + String.format("%.2f%n", y));
    			output_z.setText("z:" + String.format("%.2f%n", z - offset));
    			
    			
    			minimum_x.setText("min_x:" + String.format("%.2f%n", min_x));
    			minimum_y.setText("min_y:" + String.format("%.2f%n", min_y));
    			minimum_z.setText("min_z:" + String.format("%.2f%n", min_z - offset));
    			
    			maximum_x.setText("max_x:" + String.format("%.2f%n", max_x));
    			maximum_y.setText("max_y:" + String.format("%.2f%n", max_y));
    			maximum_z.setText("max_z:" + String.format("%.2f%n", max_z - offset));
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
			max_x = -100.f;
			max_y = -100.f;
			max_z = -100.f;
			min_x = 100.f;
			min_y = 100.f;
			min_z = 100.f;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}