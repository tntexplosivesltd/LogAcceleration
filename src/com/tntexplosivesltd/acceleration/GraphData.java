package com.tntexplosivesltd.acceleration;
import java.util.LinkedList;


public class GraphData {
	
	public static float x, y, z; 
	public float min_x = 100.f; 
	public float min_y = 100.f; 
	public float min_z = 100.f; 
	public float max_x = -100.f; 
	public float max_y = -100.f; 
	public float max_z = -100.f;
	public LinkedList<Float> data = new LinkedList<Float>();
	
	public GraphData() {}
	
	public void reset()
	{
		min_x = 100.f; 
		min_y = 100.f; 
		min_z = 100.f; 
		max_x = -100.f; 
		max_y = -100.f; 
		max_z = -100.f;
	}
}
