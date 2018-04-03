package bengine.assets;

import static org.lwjgl.stb.STBImage.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.nio.ByteBuffer;

public class Texture extends Asset {

	protected int textureHandle, width, height;

	protected ByteBuffer textureData;
	
	private int glWrapMode, glFilterMode;
	
	public Texture(File file) {
		this(file, GL_REPEAT, GL_LINEAR);
	}
	
	public Texture(File file, int glWrapMode, int glFilterMode) {
		super(file);
		
		this.glWrapMode = glWrapMode;
		this.glFilterMode = glFilterMode;
	}
	
	public Texture(int textureHandle, int width, int height) {
		super(null);
		
		this.textureHandle = textureHandle;
		this.width = width;
		this.height = height;
		
	}
	
	@Override
	public void create() {
		textureHandle = glGenTextures();
		
		update(glWrapMode, glFilterMode);
	}
	
	@Override
	public void onLoad(File file) throws AssetCreationException {
		int[] width, height, comp;
		
		width = new int[1];
		height = new int[1];
		comp = new int[1];
		
		textureData = stbi_load(file.toString(), width, height, comp, 4);
		
		if (textureData == null) {
			throw new AssetCreationException(this, stbi_failure_reason());
		}
		
		this.width = width[0];
		this.height = height[0];
	}
	
	public void update(int glWrapMode, int glFilterMode) {
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, glWrapMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, glWrapMode);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, glFilterMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, glFilterMode);
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureData);
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void bind() {
		bind(GL_TEXTURE0);
	}
	
	public void bind(int glTexture) {
		glActiveTexture(glTexture);
		glBindTexture(GL_TEXTURE_2D, textureHandle);
	}
	
	@Override
	public void destroy() {
		glDeleteTextures(this.textureHandle);	
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getTexture() {
		return textureHandle;
	}
	
	public ByteBuffer getTextureData() {
		return textureData;
	}
}
