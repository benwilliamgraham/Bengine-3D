package bengine.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Texture {

	protected int textureID;

	public Texture(int textureID) {
		this.textureID = textureID;
	}
	
	public void bind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
	}
	
	int getTexture() {
		return textureID;
	}
}
