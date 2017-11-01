package toolBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import data.RawModel;

public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();

	public RawModel loadToVAO(float[] positions, float[] textureCoordinates, int[] indices){
		//create empty VAO
		int vaoID = createVAO();
		
		//store the data
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 3, textureCoordinates);
		
		//unbind the VAO
		unbindVAO();
		
		return new RawModel(vaoID, indices.length);
	}
	
	public int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("assets/" + fileName + ".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.25f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		textures.add(texture.getTextureID());
		return texture.getTextureID();
	}
	
	private int createVAO(){
		//creates an empty VAO and return the id
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		
		//bind the VAO
		GL30.glBindVertexArray(vaoID);
		
		return vaoID;
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		//create empty VBO
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		
		//bind the new VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		//create buffer with data
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		
		//store the data in a static form
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
		//put the VBO into the VAO
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		
		//unbind the current VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void bindIndicesBuffer(int[] indices){
		//create new VBO
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		
		//bind VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		
		//create and bind buffer
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data){
		//create a new buffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		
		//put into the buffer
		buffer.put(data);
		
		//prepares buffer to be read from
		buffer.flip();
		
		return buffer;
	}
	
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		//create a new buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		
		//put into the buffer
		buffer.put(data);
		
		//prepares buffer to be read from
		buffer.flip();
		
		return buffer;
	}
	
	public void cleanUp(){
		//delete VAOs
		for(int vao: vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		
		//delete VBOs
		for(int vbo: vbos){
			GL15.glDeleteBuffers(vbo);
		}
		
		//delete textures
		for(int texture: textures){
			GL11.glDeleteTextures(texture);
		}
	}
}
