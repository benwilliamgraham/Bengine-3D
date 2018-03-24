package bengine.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Map;
import java.util.HashMap;

import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard {

	private static long window;
	
	private static Map<Integer, Boolean> justPressed = new HashMap<Integer, Boolean>();
	
	public static void create(long window) {
		Keyboard.window = window;
		
		glfwSetKeyCallback(window, (long _window, int key, int scancode, int action, int mods) -> {
			if (action == GLFW_PRESS) {
				justPressed.put(key, true);
			}
		});
	}
	
	public static boolean isKeyDown(int key) {
		int keyState = glfwGetKey(window, key);
		
		return keyState == GLFW_PRESS;
	}
	
	public static boolean isKeyJustPressed(int key) {
		if (justPressed.containsKey(key)) {
			return justPressed.get(key);
		} else {
			return false;
		}
	}
	
	public static void update() {
		justPressed.clear();
	}
}
