package bengine.rendering.renderers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

import java.nio.IntBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import bengine.Game;
import bengine.animation.Animation;
import bengine.assets.CubeMap;
import bengine.assets.Shader;
import bengine.assets.Texture;
import bengine.entities.Skybox;
import bengine.rendering.Material;
import bengine.rendering.gl.VAO;

public class SkyboxRenderer extends Renderer<Skybox> {

	public SkyboxRenderer() {
		super(null);
	}

	@Override
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
	}

	@Override
	public void render(Skybox t) {
		
		glDisable(GL_CULL_FACE);
		
		Shader shader = t.getShader();
		
		CubeMap texture = t.getTexture();
		
		VAO renderObject = t.getMesh().getRenderable();
		IntBuffer indices = t.getMesh().getIndices();
		
		shader.bind();
		
		Matrix4f viewMatrix = t.getScene().getCamera().generateProjection();
		
		viewMatrix.rotate(t.getScene().getCamera().transform.rotation);
		
		shader.push("viewMatrix", viewMatrix);
		
		texture.bind();
		shader.push("skybox", 0);
		
		renderObject.bind();
		
		glDrawElements(GL_TRIANGLES, indices);
		
		renderObject.unbind();
		
		shader.unbind();
		
		glEnable(GL_CULL_FACE);
	}

}
