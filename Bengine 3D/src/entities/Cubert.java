package entities;

import org.lwjgl.util.vector.Vector3f;

import data.ModelTexture;
import data.TexturedModel;
import toolBox.Assets;
import toolBox.Loader;
import world.World;

public class Cubert extends DynEntity{
	
	public Cubert(Vector3f position) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(1, 2.5f, 1));
	}

	public boolean update(World world, String id) {
		return true;
	}
}
