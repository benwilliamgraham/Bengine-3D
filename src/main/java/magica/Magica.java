package magica;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import bengine.Game;
import bengine.networking.PermissionManager;
import bengine.networking.messages.DebugMessage;
import bengine.networking.messages.HandshakeMessage;
import bengine.networking.messages.NetworkMessage;
import bengine.networking.messages.ObjectMessage;
import bengine.networking.messages.RPCMessage;
import bengine.networking.serialization.ObjectParser;
import bengine.networking.serialization.serializers.CollectionSerializer;
import bengine.networking.serialization.serializers.DoubleSerializer;
import bengine.networking.serialization.serializers.FloatSerializer;
import bengine.networking.serialization.serializers.IntSerializer;
import bengine.networking.serialization.serializers.LongSerializer;
import bengine.networking.serialization.serializers.PermissionSerializer;
import bengine.networking.serialization.serializers.StringSerializer;
import bengine.networking.serialization.serializers.Vector3fSerializer;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Shader;

public class Magica extends Game {
	
	private DisplayMode displayMode;
	private boolean isFullscreen;
	
	private Mesh m;
	
	private Material testMaterial;
	
	public Magica(DisplayMode displayMode, boolean isFullscreen) {
		super();
		this.displayMode = displayMode;
		this.isFullscreen = isFullscreen;
	}
	
	@Override
	public void onConfigure() {
		createDisplay(displayMode, isFullscreen, "Magica: The game about sand.");
	}
	
	@Override
	protected void onCreated() {
	Shader testShader = createShader("./assets/shader/default.json");
		
		testMaterial = new Material(testShader);
		
		testMaterial.texture = createTexture("./assets/misc/hatch.png");
		
		//Assets.squareMesh.create(renderer);
		
		// switchState(new TestState(testMaterial));
		
		Vector3f[] vertices = new Vector3f[] {
				new Vector3f(-0.5f,  0.5f,  0.0f),
				new Vector3f(-0.5f, -0.5f,  0.0f),
				new Vector3f( 0.5f, -0.5f,  0.0f),
				
				new Vector3f( 0.5f, -0.5f,  0.0f),
				new Vector3f( 0.5f,  0.5f,  0.0f),
				new Vector3f(-0.5f,  0.5f,  0.0f),
			};
			
			Vector3f[] normals = new Vector3f[] {
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(0.0f, 0.0f, 0.0f),
				
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(0.0f, 0.0f, 0.0f),
			};
			
			Vector3f[] texCoords = new Vector3f[] {
				new Vector3f( 0.0f,  1.0f,  0.0f),
				new Vector3f( 0.0f,  0.0f,  0.0f),
				new Vector3f( 1.0f,  0.0f,  0.0f),
				
				new Vector3f( 1.0f,  0.0f,  0.0f),
				new Vector3f( 1.0f,  1.0f,  0.0f),
				new Vector3f( 0.0f,  1.0f,  0.0f)
			};
			
			int[] indices = new int[] {0, 1, 2, 3, 4, 5};
			
			m = new Mesh(vertices, normals, texCoords, indices);
			
			m.create(renderer);
	}

	@Override
	protected void onUpdate(float delta) {
		
		Vector3f eulerRotation = new Vector3f();
		
		camera.rotation.getEulerAnglesXYZ(eulerRotation);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			camera.position.x -= Math.sin(eulerRotation.y) * 2.0f * delta;
			camera.position.z += Math.cos(eulerRotation.y) * 2.0f * delta;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			camera.position.x += Math.sin(eulerRotation.y) * 2.0f * delta;
			camera.position.z -= Math.cos(eulerRotation.y) * 2.0f * delta;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			camera.position.x += Math.cos(eulerRotation.y) * 2.0f * delta;
			camera.position.z += Math.sin(eulerRotation.y) * 2.0f * delta;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			camera.position.x -= Math.cos(eulerRotation.y) * 2.0f * delta;
			camera.position.z -= Math.sin(eulerRotation.y) * 2.0f * delta;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			camera.rotation.rotateAxis((float) Math.PI / 4 * delta, new Vector3f(0.0f, 1.0f, 0.0f));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			camera.rotation.rotateAxis((float) -Math.PI / 4 * delta, new Vector3f(0.0f, 1.0f, 0.0f));
		}
		
		renderer.clear(); 
		
		renderer.useShader(testMaterial.shader);
		
		renderer.getShader().pushTexture(testMaterial.texture);
		
		m.render(new Matrix4f().identity());
	}
	
	@Override
	protected void onDestroyed() {
		
		
	}
	
	public static void main(String[] args) {
		
		//Register everything for networking.
		NetworkMessage.registerMessage(HandshakeMessage.class);
		NetworkMessage.registerMessage(ObjectMessage.class);
		NetworkMessage.registerMessage(DebugMessage.class);
		NetworkMessage.registerMessage(RPCMessage.class);
				
		ObjectParser.registerType(Integer.class, new IntSerializer());
		ObjectParser.registerType(Float.class, new FloatSerializer());
		ObjectParser.registerType(String.class, new StringSerializer());
		ObjectParser.registerType(Double.class, new DoubleSerializer());
		ObjectParser.registerType(Long.class, new LongSerializer());
		ObjectParser.registerType(ArrayList.class, new CollectionSerializer());
		ObjectParser.registerType(PermissionManager.class, new PermissionSerializer());
		ObjectParser.registerType(Vector3f.class, new Vector3fSerializer());
		
		MagicaLauncher launcher = new MagicaLauncher((DisplayMode mode, boolean isFullscreen, String serverAddress, String name) -> {
			Game game = new Magica(mode, isFullscreen);
			
			game.create();
		});
		
		launcher.setVisible(true);
	}
}
