package bengine.assets;

import static org.lwjgl.assimp.Assimp.*;

import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import bengine.animation.Animation;
import bengine.animation.Bone;
import bengine.animation.Skeleton;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Vertex;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Model extends Asset {
	
	private Mesh[] meshes;
	private Skeleton[] skeletons;
	private Animation[] animations;
	
	private Map<String, Material> materials = new HashMap<String, Material>();
	private Map<Integer, String> materialIndices = new HashMap<Integer, String>();
	
	private AIScene scene;
	
	private boolean blenderFlip = false;
	
	public Model(File file) {
		super(file);
	}
	
	public Model(File file, boolean blenderFlip) {
		super(file);
		this.blenderFlip = blenderFlip;
	}
	
	@Override
	public void create() {
		for (Mesh m : meshes) {
			m.create();
		}
	}
	
	@Override
	public void onLoad(File file) throws AssetCreationException {
		scene = aiImportFile(file.toString(), aiProcessPreset_TargetRealtime_MaxQuality 
				| aiProcess_Triangulate 
				| aiProcess_GenSmoothNormals 
				| aiProcess_FixInfacingNormals 
				| aiProcess_LimitBoneWeights);
		
		AINode rootNode = scene.mRootNode();
		
		for (int m = 0; m < scene.mNumMaterials(); m++) {
			AIMaterial mat = AIMaterial.create(scene.mMaterials().get(m));
			
			PointerBuffer pbuff = PointerBuffer.allocateDirect(1);
			
			aiGetMaterialProperty(mat, AI_MATKEY_NAME, pbuff);
			
			AIMaterialProperty prop = AIMaterialProperty.create(pbuff.get());
			
			byte[] data = new byte[prop.mDataLength()];
			
			prop.mData().get(data);
			
			materialIndices.put(m, new String(data).trim());
			materials.put(new String(data).trim(), null);
		}
		
		//Load meshes from the object.
		meshes = new Mesh[scene.mNumMeshes()];
		
		ArrayList<Skeleton> skeletons = new ArrayList<Skeleton>();
		
		for (int i = 0; i < scene.mNumMeshes(); i++) {
			AIMesh aMesh = AIMesh.create(scene.mMeshes().get(i));
			
			int numChannels = 0;
			
			IntBuffer channels = aMesh.mNumUVComponents();
			while (channels.hasRemaining()) {
				if (channels.get() > 0) {
					numChannels++;
				}
			}
			
			int vertexCount = aMesh.mNumVertices();
			
			Vertex[] vertices = new Vertex[vertexCount];
			
			AIVector3D.Buffer positionData = aMesh.mVertices();
			AIVector3D.Buffer normalData = aMesh.mNormals();
			
			for (int v = 0; v < vertexCount; v++) {
				vertices[v] = new Vertex(numChannels);
				
				vertices[v].position = new Vector3f(positionData.get(v).x(), positionData.get(v).y(), positionData.get(v).z());
				
				vertices[v].normal = new Vector3f(normalData.get(v).x(), normalData.get(v).y(), normalData.get(v).z());
				
				if (numChannels > 0) {
					for (int c = 0; c < numChannels; c++) {
						AIVector3D.Buffer texCoordData = aMesh.mTextureCoords(c);
						
						vertices[v].uvData[c] = new Vector3f(texCoordData.get(v).x(), 1 - texCoordData.get(v).y(), texCoordData.get(v).z());
					}
				}
			}
			
			int skeletonIndex = -1;
			
			for (int b = 0; b < aMesh.mNumBones(); b++) {
				AIBone bone = AIBone.create(aMesh.mBones().get(b));
				
				if (skeletonIndex == -1) {
					for (int s = 0; s < skeletons.size(); s++) {
						if (skeletons.get(s).ResolveName(bone.mName().dataString()) != -1) { //If this bone exists in another skeleton. Use that skeleton.
							skeletonIndex = s;
							break;
						}
					}
					
					if (skeletonIndex == -1) {
						Skeleton sk = new Skeleton();
						skeletons.add(sk);
						
						skeletonIndex = skeletons.indexOf(sk);
					}
				}
				
				Skeleton skeleton = skeletons.get(skeletonIndex);
				
				if (skeleton.ResolveName(bone.mName().dataString()) == -1) { //If this bone doesn't exist in the skeleton, add it.
					Bone jBone = new Bone(bone.mName().dataString(), bone.mOffsetMatrix());
						
					skeleton.AddBone(b, jBone);
				}
				
				for (int w = 0; w < bone.mNumWeights(); w++) {
					AIVertexWeight weight = bone.mWeights().get(w);
					
					Vertex.SkinData skinData = vertices[weight.mVertexId()].skinData;
					
					skinData.AddWeight(skeleton.ResolveName(bone.mName().dataString()), weight.mWeight());
						
					vertices[weight.mVertexId()].skinData = skinData;
				}
			}
			
			int[] indices = new int[aMesh.mNumFaces() * aMesh.mFaces().get(0).mNumIndices()];
			
			for (int f = 0; f < aMesh.mNumFaces(); f++) {
				AIFace face = aMesh.mFaces().get(f);
				
				for (int in = 0; in < face.mNumIndices(); in++) {
					indices[f * 3 + in] = face.mIndices().get(in);
				}
			}
			
			meshes[i] = new Mesh(vertices, indices);
			
			meshes[i].materialIndex = aMesh.mMaterialIndex();
			
			if (aMesh.mNumBones() > 0) {
				meshes[i].skeleton = skeletons.get(skeletonIndex);
			}
		}
		
		this.skeletons = skeletons.toArray(new Skeleton[skeletons.size()]);
		
		
		animations = new Animation[scene.mNumAnimations()];
		
		for (int n = 0; n < scene.mNumAnimations(); n++) {
			AIAnimation anim = AIAnimation.create(scene.mAnimations().get(n));
			
			Animation animation = new Animation(anim, scene);
			
			animations[n] = animation;
		}
	}
	
	@Override
	public void destroy() {
		aiReleaseImport(scene);
		for (Mesh m : meshes) {
			m.destroy();
		}
	}
	
	public void bindMaterial(String material, Material mat) {
		materials.put(material, mat);
	}
	
	public void bindMaterial(Material mat) {
		for (String key : materials.keySet()) {
			materials.put(key, mat);
		}
	}
	
	public Material getMaterial(int index) {
		return materials.get(materialIndices.get(index));
	}
	
	public Mesh[] getMeshes() {
		return this.meshes;
	}
	
	public Animation[] getAnimations() {
		return animations;
	}
	
	public Skeleton[] getSkeletons() {
		return skeletons;
	}
	
	public AINode getRootNode() {
		return scene.mRootNode();
	}
}
