package magica;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.opengl.DisplayMode;

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
import bengine.rendering.Shader;
import magica.states.TestState;

public class Magica extends Game {
	
	private DisplayMode displayMode;
	private boolean isFullscreen;
	
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
		
		Material testMaterial = new Material(testShader); 
		
		Assets.testMaterial = testMaterial;
		
		currentState = new TestState();
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
