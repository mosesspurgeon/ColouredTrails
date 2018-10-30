package data;

import org.lwjgl.Sys;

public class Clock {
	
	private static boolean paused = false;
	public static long lastFrame, totalTime;
	public static float d = 0;//delta time
	public static float multiplier = 1; //slow down enemies
	
	public static long getTime() {
		return Sys.getTime() *1000 / Sys.getTimerResolution();
	}
	
	/**
	 * The time between right now and the last update of the game
	 * @return
	 */
	public static float getDelta() {
		long currentTime = getTime();
		int delta = (int)(currentTime - lastFrame);
		lastFrame = currentTime;
		return delta*0.01f;
	}
	
	/**
	 * 
	 * @return
	 */
	public static float delta() {
		if (paused)
			return 0;
		else return d * multiplier;
	}
	
	public static float getTotalTime() {
		return totalTime;
	}
	
	public static float getMultiplier() {
		return multiplier;
	}
	
	public static void update() {
		d = getDelta();
		totalTime += d;
	}
	
	public static void changeMultiplier(int change) {
		if( multiplier+ change >= -1 && multiplier + change <= 7) 
			multiplier += change;
	}
	
	/**
	 * Pause button
	 */
	public static void pause() {
		if( paused)
			paused = false;
		else
			paused = true;
	}

}
