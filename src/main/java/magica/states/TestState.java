package magica.states;

import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.HashMap;

import bengine.Game;
import bengine.State;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;
import magica.entities.TestEntity;
import magica.voxel.VoxelMesh;

public class TestState implements State {

	protected Map<Long, Entity> entities;
	
	private Material testMaterial;
	
	private Mesh voxelMesh;
	
	public TestState(Material testMaterial) {
		this.entities = new HashMap<Long, Entity>();
		this.testMaterial = testMaterial;
		
	}
	
	@Override
	public void onCreated() {
		
		Entity testEntity = new TestEntity();
		testEntity.material = testMaterial;
		this.entities.put(testEntity.getInstanceID(), testEntity);
	}

	@Override
	public void onUpdate(float delta) {
		
		for (Entity e : this.entities.values()) {
			e.onUpdate(delta);
		}
		
		
	}

	@Override
	public void onDraw(Renderer renderer) {
		
		renderer.useShader(testMaterial.shader);
		
		//voxelMesh.render(new Matrix4f().identity());
		
		for (Entity e : this.entities.values()) {
			e.onDraw(renderer);
		}
	}

	@Override
	public void onDestroyed() {
		for (Entity e : this.entities.values()) {
			e.onDestroyed();
		}
	}
	
}
