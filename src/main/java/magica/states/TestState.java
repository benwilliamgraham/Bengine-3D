package magica.states;

import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;

import bengine.Game;
import bengine.ModelLoader;
import bengine.State;
import bengine.animation.Animation;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;
import magica.entities.TestEntity;
import magica.voxel.VoxelMesh;

public class TestState implements State {

	protected Map<Long, Entity> entities;
	
	private Material testMaterial;
	
	private Mesh m;
	private Animation a;
	
	private float time = 0.0f;
	
	public TestState(Material testMaterial, Mesh m, Animation a) {
		this.entities = new HashMap<Long, Entity>();
		this.testMaterial = testMaterial;
		
		this.m = m;
		this.a = a;
		
	}
	
	@Override
	public void onCreated() {
		
	}

	@Override
	public void onUpdate(float delta) {
		
		for (Entity e : this.entities.values()) {
			e.onUpdate(delta);
		}
		
		time += delta;
	}

	@Override
	public void onDraw(Renderer renderer) {
		
		renderer.useShader(testMaterial.shader);
		
		renderer.getShader().push("jointTransforms", a.GetBoneDataAtTime(0.0f));
		
		m.render(new Matrix4f().identity());
		
		
		/*for (Entity e : this.entities.values()) {
			e.onDraw(renderer);
		}*/
	}

	@Override
	public void onDestroyed() {
		for (Entity e : this.entities.values()) {
			e.onDestroyed();
		}
	}
	
}
