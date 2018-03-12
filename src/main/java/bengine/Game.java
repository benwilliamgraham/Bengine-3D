package bengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.Date;
import java.util.logging.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import bengine.input.Keyboard;
import bengine.input.Mouse;

public abstract class Game {
	
	private static Logger LOGGER = Logger.getLogger(Game.class.getName());
	
	private static Game current;
	
	public Object renderLock = new Object();
	
	protected State currentState;
	
	protected int framerateCap = Integer.MAX_VALUE;
	
	protected int width, height;
	
	private float aspectRatio;
	private boolean isRunning = true;
	private long lastTick = 0;
	
	public long windowHandle = 0;
	
	private GLCapabilities capabilities;
	
	public Game() {
		initLogger();
		getLogger().setLevel(Level.ALL);
	}
	
	protected abstract void onConfigure();
	protected abstract void onCreated();
	protected abstract void onUpdate(float delta);
	protected abstract void onDestroyed();
	
	public void grab() {		
		if (glfwGetCurrentContext() != windowHandle) {
			
			getLogger().log(Level.FINE, String.format("Bringing context into thread: %s", Thread.currentThread().getName()));
			glfwMakeContextCurrent(windowHandle);
			
			GL.setCapabilities(capabilities);
		}
	}
	
	public void release() {
		if (glfwGetCurrentContext() == windowHandle) {
			getLogger().log(Level.FINE, String.format("Released context from thread: %s", Thread.currentThread().getName()));
			glfwMakeContextCurrent(NULL);
		}
	}
	
	public void switchState(State newState) {
		if (currentState != null) {
			currentState.onDestroyed();
		}
		
		currentState = newState;
		
		if (currentState != null) currentState.onCreated(this);
	}
	
	public void create() {
		
		current = this;
		
		onConfigure();
		
		onCreated();
		
		lastTick = new Date().getTime();
		
		while (isRunning) {
			long currentTime = new Date().getTime();
			float delta = (currentTime - lastTick) / 1000.0f;
			
			if (delta >= 1.0f / framerateCap) {
				lastTick = currentTime;
				
				onUpdate(delta);
				
				//TODO: maybe some synchronization stuff.
				
				if (currentState != null) {
					currentState.onUpdate(delta);
					
					synchronized (renderLock) {
						grab();
						
						currentState.onDraw();
						
						release();
					}
					
					
				}
			}
			
			synchronized(renderLock) {
				grab();
				
				glfwSwapBuffers(windowHandle);
				glfwPollEvents();
				
				release();
			}
			
			//if we update the display every tick, regardless of whether or not we draw anything new, then we don't get input lag when using vsync.
			//checkGLError();
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
		
		capabilities = GL.createCapabilities();
		
		glViewport(0, 0, width, height);
		
		Mouse.create(windowHandle);
		Keyboard.create(windowHandle);
	}
	
	private void initLogger() {
		Logger globalLogger = Logger.getGlobal();
		
		globalLogger.addHandler(new Handler() {
			
			@Override
			public void close() throws SecurityException {
				System.out.close();
			}

			@Override
			public void flush() {
				System.out.flush();
			}

			@Override
			public void publish(LogRecord record) {
				System.out.println(record.toString());
			}
			
		});
	}
	
	private void checkGLError() {
		int error = glGetError();
		
		if (error != GL_NO_ERROR) {
			
			System.err.println("OPENGL ERROR CODE: " + error);
			
			//if (Display.isCreated()) Display.destroy();
			
			//System.exit(-1);
		}
	}
	
	public float getAspect() {
		return aspectRatio;
	}
	
	private Logger getLogger() {
		return LOGGER;
	}
	
	public static Game getCurrent() {
		return current;
	}
}