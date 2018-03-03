package bengine.input;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard {

	private static long window;
	
	public static void create(long window) {
		Keyboard.window = window;
	}
	
	public static boolean isKeyDown(int key) {
		int keyState = glfwGetKey(window, key);
		
		return keyState == GLFW_PRESS;
	}

}
