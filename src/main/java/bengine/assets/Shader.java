package bengine.assets;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Matrix3f;
import org.joml.Vector4f;
import org.joml.Vector3f;
import org.joml.Vector2f;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

public class Shader extends Asset {
	
	protected int shaderProgram, vertexShader, fragmentShader;
	
	public Shader(File file) {
		super(file);
	}
	
	@Override
	public void onLoad(File file) {
		
		JsonObject config = Json.parse(loadFileAsString(file)).asObject();
		
		String vertShaderPath = config.getString("vertexShader", "");
		
		String vertexSource = loadFileAsString(vertShaderPath);
		
		String fragShaderPath = config.getString("fragmentShader", "");
		
		String fragmentSource = loadFileAsString(fragShaderPath);
		
		synchronized(getGame().renderLock) {
			getGame().grab(); //Bring the opengl context into the loader thread.
			
			//TODO: fix issue where shader creation fails randomly without an error message.
			//Likely related to a thread switching issue with opengl.
			
			vertexShader = glCreateShader(GL_VERTEX_SHADER);
			
			glShaderSource(vertexShader, vertexSource);
			glCompileShader(vertexShader);
			
			if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
				int infoLogLength = glGetShaderi(vertexShader, GL_INFO_LOG_LENGTH);
				String shaderStackTrace = glGetShaderInfoLog(vertexShader, infoLogLength);
				
				System.out.println(shaderStackTrace);
				
				destroy();
				
				throw new AssetCreationException(this, shaderStackTrace);
			}
			
			fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
			
			glShaderSource(fragmentShader, fragmentSource);
			glCompileShader(fragmentShader);
			
			if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
				int infoLogLength = glGetShaderi(fragmentShader, GL_INFO_LOG_LENGTH);
				String shaderStackTrace = glGetShaderInfoLog(fragmentShader, infoLogLength);
				
				destroy();
				
				throw new AssetCreationException(this, shaderStackTrace);
			}
			
			shaderProgram = glCreateProgram();
			
			glAttachShader(shaderProgram, vertexShader);
			glAttachShader(shaderProgram, fragmentShader);
			
			glLinkProgram(shaderProgram);
			
			if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
				int maxLength = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
				
				String infoLog = glGetProgramInfoLog(shaderProgram, maxLength);
				
				destroy();
				
				throw new AssetCreationException(this, infoLog);
			}
			
			glDetachShader(shaderProgram, vertexShader);
			glDetachShader(shaderProgram, fragmentShader);
			
			getGame().release(); //Release the opengl context back to the main thread.
		}
	}
	
	public void bind() {
		glUseProgram(shaderProgram);
	}
	
	public void unbind() {
		glUseProgram(0);
	}
	
	public void push(String uniform, int value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			glUniform1i(uniformId, value);
		}
	}
	
	public void push(String uniform, float value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			glUniform1f(uniformId, value);
		}
	}
	
	public void push(String uniform, float[] values) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			glUniform1fv(uniformId, values);
		}
	}
	
	public void push(String uniform, Vector2f value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			glUniform2f(uniformId, value.x, value.y);
		}
	}
	
	public void push(String uniform, Vector2f[] values) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 2 * values.length)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			
			for (Vector2f value : values) {
				data.put(value.x);;
				data.put(value.y);
			}
			
			data.flip();
			
			glUniform4fv(uniformId, data);
		}
	}
	
	public void push(String uniform, Vector3f value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			glUniform3f(uniformId, value.x, value.y, value.z);
		}
	}
	
	public void push(String uniform, Vector3f[] values) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 3 * values.length)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			
			for (Vector3f value : values) {
				data.put(value.x);
				data.put(value.y);
				data.put(value.z);
			}
			
			data.flip();
			
			glUniform4fv(uniformId, data);
		}
	}
	
	public void push(String uniform, Vector4f value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			glUniform4f(uniformId, value.x, value.y, value.z, value.w);
		}
	}
	
	public void push(String uniform, Vector4f[] values) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 3 * values.length)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			
			for (Vector4f value : values) {
				data.put(value.x);
				data.put(value.y);
				data.put(value.z);
				data.put(value.w);
			}
			
			data.flip();
			
			glUniform4fv(uniformId, data);
		}
	}
	
	public void push(String uniform, Matrix3f value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 9)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			value.get(data);
			glUniformMatrix3fv(uniformId, false, data);
		}
	}
	
	public void push(String uniform, Matrix3f[] values) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer fb = ByteBuffer.allocateDirect(Float.BYTES * 9 * values.length)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			
			for (Matrix3f mat : values) {
				fb.put(mat.m00());fb.put(mat.m01());fb.put(mat.m02());
				fb.put(mat.m10());fb.put(mat.m11());fb.put(mat.m12());
				fb.put(mat.m20());fb.put(mat.m21());fb.put(mat.m22());
			}
			
			fb.flip();
			
			glUniformMatrix3fv(uniformId, false, fb);
		}
	}
	
	public void push(String uniform, Matrix4f value) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer data = ByteBuffer.allocateDirect(Float.BYTES * 16)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			value.get(data);
			glUniformMatrix4fv(uniformId, false, data);
		}
	}
	
	public void push(String uniform, Matrix4f[] values) {
		int uniformId = glGetUniformLocation(shaderProgram, uniform);
		
		if (uniformId != -1) {
			FloatBuffer fb = ByteBuffer.allocateDirect(Float.BYTES * 16 * values.length)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			
			for (Matrix4f mat : values) {
				fb.put(mat.m00());fb.put(mat.m01());fb.put(mat.m02());fb.put(mat.m03());
				fb.put(mat.m10());fb.put(mat.m11());fb.put(mat.m12());fb.put(mat.m13());
				fb.put(mat.m20());fb.put(mat.m21());fb.put(mat.m22());fb.put(mat.m23());
				fb.put(mat.m30());fb.put(mat.m31());fb.put(mat.m32());fb.put(mat.m33());
			}
			
			fb.flip();
			
			glUniformMatrix4fv(uniformId, false, fb);
		}
	}
	
	public void destroy() {
		glDeleteProgram(shaderProgram);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}
}
