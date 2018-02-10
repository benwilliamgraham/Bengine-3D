package magica;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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
import magica.states.TestState;

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
		
		
		
		//Assets.squareMesh.create(renderer);
		
		//switchState(new TestState(testMaterial));
		
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
		renderer.clear(); 
		
		renderer.useShader(testMaterial.shader);
		
		m.render(new Matrix4f());
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
		ObjectParser.registerType(List.class, new CollectionSerializer());
		ObjectParser.registerType(PermissionManager.class, new PermissionSerializer());
		ObjectParser.registerType(Vector3f.class, new Vector3fSerializer());
		
		MagicaLauncher launcher = new MagicaLauncher((DisplayMode mode, boolean isFullscreen, String serverAddress, String name) -> {
			Game game = new Magica(mode, isFullscreen);
			
			game.create();
		});
		
		launcher.setVisible(true);
	}
}
