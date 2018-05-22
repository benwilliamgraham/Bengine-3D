package bengine.assets;

import static org.lwjgl.stb.STBImage.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class CubeMap extends Texture {
	
	private ByteBuffer[] textureData = new ByteBuffer[6];
	private int[] widths, heights;
	
	public CubeMap(File file) {
		this(file, GL_CLAMP_TO_EDGE, GL_LINEAR);
	}
	
	public CubeMap(File file, int glWrapMode, int glFilterMode) {
		super(file);
		
		this.glWrapMode = glWrapMode;
		this.glFilterMode = glFilterMode;
	}
	
	@Override
	public void create() {
		this.textureHandle = glGenTextures();
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureHandle);
		
		for (int i = 0; i < textureData.length; i++) {
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, this.widths[i], this.heights[i], 0, GL_RGBA, GL_UNSIGNED_BYTE, textureData[i]);
		}
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, glFilterMode);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, glFilterMode);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, glWrapMode);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, glWrapMode);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, glWrapMode);  
	}
	
	@Override
	public void onLoad(File file) throws AssetCreationException {
		try {
			JsonValue cubeMapFile = Json.parse(new FileReader(file));
			
			if (cubeMapFile.isObject()) {
				JsonObject cubemap = cubeMapFile.asObject();
				
				String topPath = cubemap.get("top").asString();
				String bottomPath = cubemap.get("bottom").asString();
				String leftPath = cubemap.get("left").asString();
				String rightPath = cubemap.get("right").asString();
				String frontPath = cubemap.get("front").asString();
				String backPath = cubemap.get("back").asString();
				
				this.widths = new int[6];
				this.heights = new int[6];
				
				int[] width = new int[1];
				int[] height = new int[1];
				
				textureData[0] = stbi_load(rightPath, width, height, new int[1], 4);
				widths[0] = width[0];heights[0] = height[0];
				
				textureData[1] = stbi_load(leftPath, width, height, new int[1], 4);
				widths[1] = width[0];heights[1] = height[0];
				
				textureData[2] = stbi_load(topPath, width, height, new int[1], 4);
				widths[2] = width[0];heights[2] = height[0];
				
				textureData[3] = stbi_load(bottomPath, width, height, new int[1], 4);
				widths[3] = width[0];heights[3] = height[0];
				
				textureData[4] = stbi_load(backPath, width, height, new int[1], 4);
				widths[4] = width[0];heights[4] = height[0];
				
				textureData[5] = stbi_load(frontPath, width, height, new int[1], 4);
				widths[5] = width[0];heights[5] = height[0];
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void bind() {
		bind(GL_TEXTURE0);
	}
	
	@Override
	public void bind(int glTexture) {
		glActiveTexture(glTexture);
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureHandle);
	}
}
