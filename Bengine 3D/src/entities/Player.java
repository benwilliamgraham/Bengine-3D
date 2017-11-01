package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;

public class Player{

	public Vector3f position, rotation;
	public Camera camera;
	
	public Player(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
		camera = new Camera();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(true);
	}

	public void update(){
		int mouseXChange = Display.getWidth() / 2 - Mouse.getX();
		int mouseYChange = Display.getHeight() / 2 - Mouse.getY();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(true);
		
		rotation.y += mouseXChange * 1 / DisplayManager.FPS;
		rotation.x += mouseYChange * 1 / DisplayManager.FPS;
		rotation.x = (float) Math.min(Math.max(rotation.x, -Math.PI / 2.5), Math.PI / 2.5);
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		
		float forwardInput = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			forwardInput = 1;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			forwardInput = -1;
		}
		
		float sidewaysInput = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			sidewaysInput = 1;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			sidewaysInput = -1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			position.y += 20 / DisplayManager.FPS;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			position.y -= 20 / DisplayManager.FPS;
		}
		
		position.x += (20 * forwardInput * Math.sin(rotation.y) + 10 * sidewaysInput * Math.sin(rotation.y + Math.PI / 2f)) / DisplayManager.FPS;
		position.z += (20 * forwardInput * Math.cos(rotation.y) + 10 * sidewaysInput * Math.cos(rotation.y + Math.PI / 2f)) / DisplayManager.FPS;
		
		camera.position = position;
		camera.yaw = (float) (Math.PI - rotation.y);
		camera.pitch = (float) (rotation.x);
	}
}
