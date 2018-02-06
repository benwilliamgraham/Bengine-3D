package bengine.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import org.joml.*;

import bengine.entities.Camera;

import java.nio.*;

public class Renderer {
	
	public static final int VERTEX_INDEX = 0;
	public static final int NORMAL_INDEX = 1;
	public static final int TEX_COORD_INDEX = 2;
	
	Shader currentShader;
	Camera activeCamera = new Camera(new Vector3f(), 120.0f, 150.0f);
	
	public Renderer() {}
	
	public void initialize() {
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
	}
	
	public void clear() {
		glClearColor(activeCamera.clearColor.x, activeCamera.clearColor.y, activeCamera.clearColor.z, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void useShader(Shader shader) {
		
		if (shader != null) {
			this.currentShader = shader;
		}
		
		glUseProgram(this.currentShader.shader);
	}
	
	public Shader getShader() {
		return currentShader;
	}
	
	public void useCamera(Camera camera) {
		this.activeCamera = camera;
	}
	
	public Camera getCamera() { 
		return activeCamera;
	}
	
	public int createTexture(IntBuffer imageData, int width, int height, boolean doSubsample) {
		int textureHandle = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		if (doSubsample) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		}
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		glTexImage2D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, width, height, 0, GL_RGBA_INTEGER, GL_INT, imageData);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return textureHandle;
	}
	
	public int createTexture(IntBuffer imageData, int width, int height) {
		return createTexture(imageData, width, height, true);
	}
	
	public int[] createVAO(boolean isStatic, FloatBuffer verticies, FloatBuffer normals, FloatBuffer texCoords) {
		
		//Create the opengl objects.
		int vao = glGenVertexArrays();
		
		glBindVertexArray(vao);
		
		int vertBuffer = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vertBuffer);
		glBufferData(GL_ARRAY_BUFFER, verticies, (isStatic)? GL_STATIC_DRAW : GL_DYNAMIC_DRAW);
		glVertexAttribPointer(VERTEX_INDEX, 3, GL_FLOAT, false, 0, 0L);
		
		int normalBuffer = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, normalBuffer);
		glBufferData(GL_ARRAY_BUFFER, normals, (isStatic)? GL_STATIC_DRAW : GL_DYNAMIC_DRAW);
		glVertexAttribPointer(NORMAL_INDEX, 3, GL_FLOAT, false, 0, 0L);
		
		int texCoordBuffer = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffer);
		glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);
		glVertexAttribPointer(TEX_COORD_INDEX, 3, GL_FLOAT, false, 0, 0L);
		
		glBindVertexArray(0);
		
		return new int[] {vao, vertBuffer, normalBuffer, texCoordBuffer};
	}
	
	public void updateBuffer(int buffer, FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public int compileShader(String shaderSource, boolean isVertex) throws Exception {
		int shader = glCreateShader((isVertex)? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
		
		glShaderSource(shader, shaderSource);
		glCompileShader(shader);
		
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			int infoLogLength = glGetShaderi(shader, GL_INFO_LOG_LENGTH);
			String shaderStackTrace = glGetShaderInfoLog(shader, infoLogLength);
			
			glDeleteShader(shader);
			
			throw new Exception(shaderStackTrace);
		}
		
		
		return shader;
	}
	
	public int createShaderProgram(int vertexShader, int fragmentShader) throws Exception {
		int program = glCreateProgram();
		
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);
		
		glLinkProgram(program);
		
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			int maxLength = glGetProgrami(program, GL_INFO_LOG_LENGTH);
			
			String infoLog = glGetProgramInfoLog(program, maxLength);
			
			glDeleteProgram(program);
			
			glDeleteShader(vertexShader);
			glDeleteShader(fragmentShader);
			
			throw new Exception(infoLog);
		}
		
		glDetachShader(program, vertexShader);
		glDetachShader(program, fragmentShader);
		
		return program;
	}
}
