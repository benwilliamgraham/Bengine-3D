package bengine;

import static org.lwjgl.opengl.GL11.*;

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
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import bengine.entities.Camera;
import bengine.rendering.Renderer;
import bengine.rendering.Shader;
import bengine.rendering.Texture;

public abstract class Game {
	
	private static Game current;
	
	protected State currentState;
	
	protected Camera camera;
	
	protected int framerateCap = Integer.MAX_VALUE;
	
	protected Renderer renderer;
	
	private float aspectRatio;
	private boolean isRunning = true;
	private long lastTick = 0;
	
	public Game() {}
	
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
		
		camera = new Camera(new Vector3f(), 120.0f, 150.0f); //Create a camera at the origin.
		camera.name = "DefaultCamera";
		
		onCreated();
		
		lastTick = new Date().getTime();
		
		while (isRunning) {
			long currentTime = new Date().getTime();
			float delta = (currentTime - lastTick) / 1000.0f;
			
			if (delta >= 1.0f / framerateCap) {
				lastTick = currentTime;
				
				this.renderer.useCamera(camera);
				
				//TODO: maybe some synchronization stuff.
				//currentState.onUpdate(delta);
				
				onUpdate(delta);
				
				//Prepare the drawing state.
				//renderer.clear();
				
				//currentState.onDraw(renderer);
			}
			Display.update(); //if we update the display every tick, regardless of whether or not we draw anything new, then we don't get input lag when using vsync.
			//checkGLError();
			if (Display.isCloseRequested()) {
				break;
			}
		}
		
		onDestroyed();
		Display.destroy();
		System.exit(0);
	}
	
	public void destroy() {
		isRunning = false;
	}
	
	public float getAspect() {
		return aspectRatio;
	}
	
	protected void createDisplay(DisplayMode mode, boolean fullscreen, String title) {
		
		try {
			PixelFormat pixelFormat = new PixelFormat();
            ContextAttribs contextAttributes = new ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true);
            
			Display.setDisplayMode(mode);
			Display.setFullscreen(fullscreen);
			Display.setTitle(title);
			Display.create(pixelFormat, contextAttributes);
			
			GL11.glViewport(0, 0, mode.getWidth(), mode.getHeight());
			
			aspectRatio = (float) mode.getWidth() / mode.getHeight();
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		
		
		
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
			} //TODO: not very efficient, but everything else was cancer to work with.
			
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
	
	public static Game getCurrent() {
		return current;
	}
}