package bengine.input;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
	private static long window;
	
	private static double mouseX, mouseY, lastX, lastY;
	
	public static void create(long window) {
		Mouse.window = window;
		
		mouseX = 0;
		mouseY = 0;
		
		glfwSetCursorPosCallback(window, (long _w, double x, double y) -> {
			lastX = mouseX;
			lastY = mouseY;
			
			mouseX = x;
			mouseY = y;
		});
	}
	
	public static int getDX() {
		return (int) (mouseX - lastX);
	}
	
	public static int getDY() {
		return (int) (mouseY - lastY);
	}
	
	public static int getX() {
		return (int) (mouseX);
	}
	
	public static int getY() {
		return (int) (mouseY);
	}
	
	public boolean isButtonDown(int mouseButton) {
		int state = glfwGetMouseButton(window, mouseButton);
		
		return state == GLFW_PRESS;
	}
	
	public static void lockCursor() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	public static void unlockCursor() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
}
