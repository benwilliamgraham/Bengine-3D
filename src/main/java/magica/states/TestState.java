package magica.states;

import java.util.Map;
import java.util.HashMap;

import bengine.State;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.Renderer;
import magica.TestEntity;

public class TestState implements State {

	protected Map<Long, Entity> entities;
	
	private Material testMaterial;
	
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
