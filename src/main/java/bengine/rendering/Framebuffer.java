package bengine.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;

import bengine.Game;
import bengine.assets.Texture;

public class Framebuffer {
	
	private int width, height;
	
	private int framebuffer, renderbuffer;
	
	private ArrayList<Texture> colorBuffers = new ArrayList<Texture>();
	
	public Framebuffer(int width, int height) {
		
		this.width = width;
		this.height = height;
		
		framebuffer = glGenFramebuffers();
		
		System.out.println("Framebuffer: " + framebuffer);
	}
	
	public Texture addColorBuffer(int attachment, int format, int dataType) {
		bind();
		
		int colorbuffer = glGenTextures();
	
		glBindTexture(GL_TEXTURE_2D, colorbuffer);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, width, height, 0, format, dataType, NULL);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		 glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		 glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, colorbuffer, 0);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		unbind();
		
		Texture tex = new Texture(colorbuffer, width, height);
		
		this.colorBuffers.add(tex);
		
		return tex;
	}
	
	public Framebuffer addRenderBuffer(int attachment) {
		bind();
		
		renderbuffer = glGenRenderbuffers();
		
		glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
		
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
		
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, renderbuffer);
		
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		
		unbind();
		
		return this;
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
		glViewport(0, 0, width, height);
	}
	
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Game.getCurrent().getWidth(), Game.getCurrent().getHeight());
	}
	
	public void resize(int width, int height) {
		//TODO:
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
