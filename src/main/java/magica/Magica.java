package magica;

import static org.lwjgl.glfw.GLFW.*;
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

import bengine.Game;
import bengine.input.Keyboard;
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
	
	private int width, height;
	private boolean isFullscreen;
	
	private Mesh m;
	
	private Material testMaterial;
	
	public Magica(int width, int height, boolean isFullscreen) {
		super();
		this.width = width;
		this.height = height;
		this.isFullscreen = isFullscreen;
	}
	
	@Override
	public void onConfigure() {
		createDisplay(width, height, isFullscreen, "Magica: The game about sand.");
	}
	
	@Override
	protected void onCreated() {
	Shader testShader = createShader("./assets/shader/default.json");
		
		testMaterial = new Material(testShader);
		
		testMaterial.texture = createTexture("./assets/misc/hatch.png");
		
		Assets.create(renderer);
		
		switchState(new TestState(testMaterial));
	}

	@Override
	protected void onUpdate(float delta) {
		
		System.out.println(Math.floor(1.0 / delta) + " FPS.");
		
		/*if (Keyboard.isKeyDown(GLFW_KEY_W)) {
			camera.transform.move(camera.transform.forwards().mul(2.0f * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_S)) {
			camera.transform.move(camera.transform.forwards().mul(-2.0f * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_A)) {
			camera.transform.move(camera.transform.right().mul(2.0f * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_D)) {
			camera.transform.move(camera.transform.right().mul(-2.0f * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
			camera.transform.rotation.rotateAxis((float) Math.PI / 4 * delta, new Vector3f(0.0f, 1.0f, 0.0f));
		} else if (Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
			camera.transform.rotation.rotateAxis((float) -Math.PI / 4 * delta, new Vector3f(0.0f, 1.0f, 0.0f));
		}*/
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
		
		MagicaLauncher launcher = new MagicaLauncher((int width, int height, boolean isFullscreen, String serverAddress, String name) -> {
			Game game = new Magica(width, height, isFullscreen);
			
			game.create();
		});
		
		launcher.setVisible(true);
	}
}
