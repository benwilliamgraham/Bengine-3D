package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import data.RawModel;
import data.TexturedModel;
import entities.DynEntity;
import entities.Entity;
import shaders.StaticShader;
import toolBox.Calc;
import world.FaceMap;

public class Renderer {
	
	private static final float FOV = 120;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 128f;
	
	private Matrix4f projectionMatrix;
	
	public Renderer(StaticShader shader){
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
	}

	public void prepare(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClearColor(0.8f, 0.85f, 0.75f, 1);
	}
	
	public void render(FaceMap faceMap, StaticShader shader){
		GL30.glBindVertexArray(faceMap.model.model.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		Matrix4f transformationMatrix = Calc.createTransformationMatrix(faceMap.position, faceMap.rotation, faceMap.scale);
		shader.loadTransformationMatrix(transformationMatrix);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, faceMap.model.texture.textureID);
		GL11.glDrawElements(GL11.GL_TRIANGLES, faceMap.model.model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	public void render(Map<String, DynEntity> entities, StaticShader shader){
		for (Map.Entry<String, DynEntity> entity : entities.entrySet()) {
			GL30.glBindVertexArray(entity.getValue().model.model.vaoID);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getValue().model.texture.textureID);
			
			Matrix4f transformationMatrix = Calc.createTransformationMatrix(entity.getValue().position, entity.getValue().rotation, entity.getValue().scale);
			shader.loadTransformationMatrix(transformationMatrix);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getValue().model.model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}
	
	private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
