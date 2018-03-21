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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
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
	
	protected State currentState;
	
	protected int framerateCap = 60;
	
	protected int width, height;
	
	private float aspectRatio;
	private boolean isRunning = true;
	
	private long windowHandle = 0;
	
	private long lastTick, variableYieldTime, lastTime;
	
	private GLCapabilities capabilities;
	
	public Game() {
		initLogger();
		getLogger().setLevel(Level.ALL);
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
		
		if (currentState != null) currentState.onCreated(this);
	}
	
	public void create() {
		
		current = this;
		
		onConfigure();
		
		onCreated();
		
		lastTick = System.nanoTime();
		
		while (isRunning) {
			long currentTime = System.nanoTime();
			float delta = (float) ((currentTime - lastTick) / 1000000000.0d);
			lastTick = currentTime;
			
			onUpdate(delta);
			//TODO: maybe some synchronization stuff.
			
			if (currentState != null) {
				currentState.onUpdate(delta);
				currentState.onDraw();
			}
			
			glfwSwapBuffers(windowHandle);
			glfwPollEvents();
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
		
		glEnable(GL_DEPTH_TEST);
		
		glDepthFunc(GL_LEQUAL);
		
		glFrontFace(GL_CCW);
		
		glCullFace(GL_FRONT);
		
		glVertexAttrib4iv(4, new int[] {-1, -1, -1, -1});
		
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
				System.out.println(record.getMessage());
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

	/**
     * An accurate sync method that adapts automatically
     * to the system it runs on to provide reliable results.
     * 
     * @param fps The desired frame rate, in frames per second
     * @author kappa (On the LWJGL Forums)
     */
    private void sync(int fps) {
        if (fps <= 0) return;
          
        long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
        long overSleep = 0; // time the sync goes over by
          
        try {
            while (true) {
                long t = System.nanoTime() - lastTime;
                  
                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                }else if (t < sleepTime) {
                    // burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                }else {
                    overSleep = t - sleepTime;
                    break; // exit while loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
             
            // auto tune the time sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
            }
            else if (overSleep < variableYieldTime - 200*1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
            }
        }
    }
	
	public float getAspect() {
		return aspectRatio;
	}
	
	protected long getWindow() {
		return windowHandle;
	}
	
	private Logger getLogger() {
		return LOGGER;
	}
	
	public static Game getCurrent() {
		return current;
	}
}