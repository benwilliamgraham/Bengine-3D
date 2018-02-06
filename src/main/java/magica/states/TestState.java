package magica.states;

import java.util.Map;
import java.util.HashMap;

import bengine.State;
import bengine.entities.Entity;
import bengine.rendering.Renderer;

public class TestState implements State {

	protected Map<String, Entity> entities;
	
	public TestState() {
		this.entities = new HashMap<String, Entity>();
		
		
	}
	
	@Override
	public void onCreated() {
		
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
		
	}
	
}
