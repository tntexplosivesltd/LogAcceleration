package com.tntexplosivesltd.acceleration;
import java.util.LinkedList;


public class GraphData {
	
	public static float x, y, z; 
	public static float min_x = 20.f; 
	public static float min_y = 20.f; 
	public static float min_z = 20.f; 
	public static float max_x = -20.f; 
	public static float max_y = -20.f; 
	public static float max_z = -20.f;
	public static int max_data = 150;
	public static int logged_values; 
	
	/* MODE - might add more
	 * 0 = circle
	 * 1 = graphs */
	public static int mode = 0;
	
	/* ORIENTATION - might add more
	 * 0 = phone flat
	 * 1 = phone upright
	 */
	public static int orientation = 0;
	
	public static  LinkedList<Float> data_x = new LinkedList<Float>();
	public static  LinkedList<Float> data_y = new LinkedList<Float>();
	public static  LinkedList<Float> data_z = new LinkedList<Float>();
	
	
	public GraphData() {}
	
	public static void reset()
	{
		min_x = 20.f; 
		min_y = 20.f; 
		min_z = 20.f; 
		max_x = -20.f; 
		max_y = -20.f; 
		max_z = -20.f;
	}
}
