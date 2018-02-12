package bengine.rendering;

import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Matrix3f;
import org.joml.Vector4f;
import org.joml.Vector3f;
import org.joml.Vector2f;

public class Shader {
	
	public int shader;
	
	protected int viewUniform, textureUniform;
	
	public Shader(int shaderHandle) {
		this.shader = shaderHandle;
		
		this.viewUniform = glGetUniformLocation(shaderHandle, "viewmodelMatrix");
		this.textureUniform = glGetUniformLocation(shaderHandle, "modelTexture");
	}
	
	public void pushView(Matrix4f viewMatrix) {
		
		FloatBuffer matrixData = ByteBuffer.allocateDirect(16 * Float.BYTES)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		
		viewMatrix.get(matrixData);
		
		glUniformMatrix4(this.viewUniform, false, matrixData);
	}
	
	public void pushTexture(Texture texture) {
		texture.bind();
		glUniform1i(textureUniform, 0); //Bind the main texture uniform to texture0.
	}
	
	public void push(String uniform, float value) {
		int uniformId = glGetUniformLocation(shader, uniform);
		
		if (uniformId != -1) {
			glUniform1f(uniformId, value);
		}
	}
	
	public void push(String uniform, Vector2f value) {
		int uniformId = glGetUniformLocation(shader, uniform);
		
		if (uniformId != -1) {
			glUniform2f(uniformId, value.x, value.y);
		}
	}
	
	public void push(String uniform, Vector3f value) {
		int uniformId = glGetUniformLocation(shader, uniform);
		
		if (uniformId != -1) {
			glUniform3f(uniformId, value.x, value.y, value.z);
		}
	}
	
	public void push(String uniform, Vector4f value) {
		int uniformId = glGetUniformLocation(shader, uniform);
		
		if (uniformId != -1) {
			glUniform4f(uniformId, value.x, value.y, value.z, value.w);
		}
	}
	
	public void push(String uniform, Matrix3f value) {
		int uniformId = glGetUniformLocation(shader, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 9)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			value.get(data);
			glUniformMatrix3(uniformId, false, data);
		}
	}
	
	public void push(String uniform, Matrix4f value) {
		int uniformId = glGetUniformLocation(shader, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 16)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			value.get(data);
			glUniformMatrix4(uniformId, false, data);
		}
	}
}
