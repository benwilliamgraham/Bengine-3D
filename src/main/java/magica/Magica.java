package magica;

import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import bengine.Game;
import bengine.State;
import bengine.assets.AssetLoader;
import bengine.assets.AssetManager;
import bengine.assets.Model;
import bengine.assets.Shader;
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
import magica.states.TestState;

public class Magica extends Game {
	
	public static final String WINDOW_TITLE = "Magica: The game about sand.";
	
	private int width, height;
	private boolean isFullscreen;
	
	public Magica(int width, int height, boolean isFullscreen) {
		super();
		this.width = width;
		this.height = height;
		this.isFullscreen = isFullscreen;
	}
	
	@Override
	public void onConfigure() {
		createDisplay(width, height, isFullscreen, WINDOW_TITLE);
	}
	
	@Override
	protected void onCreated() {
		
		//System.out.println(new Matrix4f().identity().translate(new Vector3f(1.0f, 0.0f, 0.0f)));
		
		AssetLoader loader = new AssetLoader(this) {

			@Override
			protected void onLoaded(AssetManager assets) {
				State newState = new TestState(assets);
				
				
				switchState(newState);
			}
			
		};
		
		loader.addAsset("defaultShader", new Shader(new File("./assets/shader/default.json")));
		loader.addAsset("robotModel", new Model(new File("./assets/misc/rigged_test.fbx")));
		loader.addAsset("cubeModel", new Model(new File("./assets/misc/Cube.fbx")));
		loader.addAsset("simpleShader", new Shader(new File("./assets/shader/simple/simple.json")));
		loader.addAsset("wireframeShader", new Shader(new File("./assets/shader/red_wireframe/rwf.json")));
		
		switchState(loader.load()); //Switch to the loading state while we load the assets.
	}

	@Override
	protected void onUpdate(float delta) {
		glfwSetWindowTitle(getWindow(), String.format("(%d FPS) : %s", (int)Math.floor(1.0 / delta), WINDOW_TITLE));
		
	}
	
	@Override
	protected void onDestroyed() {
		if (currentState != null) {
			//TODO: Once i've finished reverting all this stuff from debugging, cuz this is probably a mem leak and i don't know cuz my gpu has like 12 gb of vram, and it's not significant enough to do anything to my computer.
			//currentState.onDestroyed();
		}
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
