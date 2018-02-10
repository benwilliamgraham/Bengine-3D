package bengine;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;

import javax.imageio.ImageIO;

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
	
	protected State currentState;
	
	protected Camera camera;
	
	protected int framerateCap = Integer.MAX_VALUE;
	
	protected Renderer renderer;
	
	
	private boolean isRunning = true;
	private long lastTick = 0;
	
	public Game() {
		
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
		onConfigure();
		
		this.renderer = new Renderer();
		
		//this.renderer.initialize();
		
		onCreated();
		
		lastTick = new Date().getTime();
		
		while (isRunning) {
			long currentTime = new Date().getTime();
			float delta = (currentTime - lastTick) / 1000.0f;
			
			if (delta >= 1.0f / framerateCap) {
				
				//TODO: maybe some synchronization stuff.
				//currentState.onUpdate(delta);
				
				onUpdate(delta);
				
				//Prepare the drawing state.
				//renderer.clear();
				
				//currentState.onDraw(renderer);
			}
			Display.update(); //if we update the display every tick, regardless of whether or not we draw anything new, then we don't get input lag when using vsync.
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
			
			int pixels[] = new int[image.getWidth() * image.getHeight()];
			
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
			
			IntBuffer imageData = IntBuffer.wrap(pixels);
			
			int texture = renderer.createTexture(imageData, image.getWidth(), image.getHeight());
			
			Texture tex = new Texture(texture);
			
			return tex;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String loadFileAsString(String filePath) {
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
}