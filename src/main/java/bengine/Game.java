package bengine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import bengine.entities.Camera;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import bengine.rendering.Renderer;
import bengine.rendering.Shader;
import bengine.rendering.Texture;

public abstract class Game {
	
	private static Game current;
	
	protected State currentState;
	
	protected Camera camera;
	
	protected int framerateCap = Integer.MAX_VALUE;
	
	protected int width, height;
	
	protected Renderer renderer;
	
	private float aspectRatio;
	private boolean isRunning = true;
	private long lastTick = 0;
	
	private long windowHandle = 0;
	
	public Game() {
		//SharedLibraryLoader.load();
	}
	
	protected abstract void onConfigure();
	protected abstract void onCreated();
	protected abstract void onUpdate(float delta);
	protected abstract void onDestroyed();
	
	public void switchState(State newState) {
		if (currentState != null) {
			currentState.onDestroyed();
		}
		
		currentState = newState;
		currentState.onCreated();
	}
	
	public void create() {
		
		current = this;
		
		onConfigure();
		
		this.renderer = new Renderer();
		
		this.renderer.initialize();
		
		camera = new Camera(new Vector3f(0, 0, -2.0f), 120.0f, 150.0f); //Create a camera at the origin.
		camera.name = "DefaultCamera";
		
		onCreated();
		
		lastTick = new Date().getTime();
		
		while (isRunning) {
			long currentTime = new Date().getTime();
			float delta = (currentTime - lastTick) / 1000.0f;
			
			if (delta >= 1.0f / framerateCap) {
				lastTick = currentTime;
				
				this.renderer.useCamera(camera);
				
				onUpdate(delta);
				
				//TODO: maybe some synchronization stuff.
				
				if (currentState != null) currentState.onUpdate(delta);
				
				
				//Prepare the drawing state.
				renderer.clear();
				
				if (currentState != null) currentState.onDraw(renderer);
			}
			
			glfwSwapBuffers(windowHandle);
			glfwPollEvents();
			//if we update the display every tick, regardless of whether or not we draw anything new, then we don't get input lag when using vsync.
			checkGLError();
			if (glfwWindowShouldClose(windowHandle)) {
				break;
			}
		}
		
		onDestroyed();
		glfwTerminate();
		System.exit(0);
	}
	
	public void destroy() {
		isRunning = false;
	}
	
	public float getAspect() {
		return aspectRatio;
	}
	
	protected void createDisplay(int width, int height, boolean fullscreen, String title) {
		
		if (!glfwInit()) {
			System.err.println("Failed to initialize GLFW.");
			System.exit(1);
		}
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		
		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			glfwGetWindowSize(windowHandle, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(
				windowHandle,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // center the window on the screen.
		
		glfwMakeContextCurrent(windowHandle);
		
		glfwShowWindow(windowHandle);
		
		aspectRatio = (float) width / height;
		
		this.width = width;
		this.height = height;
		
		GL.createCapabilities();
		
		glViewport(0, 0, width, height);
		
		Mouse.create(windowHandle);
		Keyboard.create(windowHandle);
	}
	
	protected Shader createShader(String shaderFile) {
		JsonObject config = Json.parse(loadFileAsString(shaderFile)).asObject();
		
		String vertShaderPath = config.getString("vertexShader", "");
		
		String vertShaderSource = loadFileAsString(vertShaderPath);
		
		String fragShaderPath = config.getString("fragmentShader", "");
		
		String fragShaderSource = loadFileAsString(fragShaderPath);
		
		try {
			int vertShader = renderer.compileShader(vertShaderSource, true);
			
			int fragShader = renderer.compileShader(fragShaderSource, false);
			
			int shaderProgram = renderer.createShaderProgram(vertShader, fragShader);
			
			Shader shader = new Shader(shaderProgram);
			
			return shader;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected Texture createTexture(String textureFile) {
		
		try {
			BufferedImage image = ImageIO.read(new File(textureFile));

			ByteBuffer imageBuffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4)
					.order(ByteOrder.nativeOrder());

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					Color c = new Color(image.getRGB(x, y));
					
					imageBuffer.put((byte) c.getRed());
					imageBuffer.put((byte) c.getGreen());
					imageBuffer.put((byte) c.getBlue());
					imageBuffer.put((byte) c.getAlpha());
				}
			} //TODO: not very efficient, but everything else was cancer to work with. switch to sdl later.
			
			imageBuffer.flip();
			
			int texture = renderer.createTexture(imageBuffer, image.getWidth(), image.getHeight());
			
			Texture tex = new Texture(texture);
			
			return tex;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected String loadFileAsString(String filePath) {
		try {
			File file = new File(filePath);
			
			FileInputStream is = new FileInputStream(file);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String line;
			
			String fileSource = "";
			
			while ((line = reader.readLine()) != null) {
				fileSource += line + "\n";
			}
			
			reader.close();
			
			return fileSource;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void checkGLError() {
		int error = glGetError();
		
		if (error != GL_NO_ERROR) {
			
			System.err.println("OPENGL ERROR CODE: " + error);
			
			//if (Display.isCreated()) Display.destroy();
			
			//System.exit(-1);
		}
	}
	
	public Renderer getRenderer() {
		return renderer;
	}
	
	public static Game getCurrent() {
		return current;
	}
}