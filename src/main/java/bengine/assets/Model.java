package bengine.assets;

import static org.lwjgl.assimp.Assimp.*;

import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import bengine.animation.Animation;
import bengine.animation.Bone;
import bengine.animation.Skeleton;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Vertex;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Model extends Asset {
	
	private Mesh[] meshes;
	private Skeleton[] skeletons;
	private Animation[] animations;
	
	private Map<Integer, Material> materials = new HashMap<Integer, Material>();
	
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
				| aiProcess_JoinIdenticalVertices
				| aiProcess_LimitBoneWeights);
		
		AINode rootNode = scene.mRootNode();
		
		//Load meshes from the object.
		meshes = new Mesh[scene.mNumMeshes()];
		
		ArrayList<Skeleton> skeletons = new ArrayList<Skeleton>();
		
		for (int i = 0; i < scene.mNumMeshes(); i++) {
			AIMesh aMesh = AIMesh.create(scene.mMeshes().get(i));
			
			int vertexCount = aMesh.mNumVertices();
			
			Vertex[] vertices = new Vertex[vertexCount];
			
			AIVector3D.Buffer positionData = aMesh.mVertices();
			AIVector3D.Buffer normalData = aMesh.mVertices();
			AIVector3D.Buffer texCoordData = aMesh.mTextureCoords(0);
			
			for (int v = 0; v < vertexCount; v++) {
				vertices[v] = new Vertex();
				
				if (blenderFlip) {
					vertices[v].position = new Vector3f(positionData.get(v).x(), positionData.get(v).z(), positionData.get(v).y());
					
					vertices[v].normal = new Vector3f(normalData.get(v).x(), normalData.get(v).z(), normalData.get(v).y());
					
					if (aMesh.mNumUVComponents().get() > 0) {
						vertices[v].texCoord = new Vector3f(texCoordData.get(v).x(), texCoordData.get(v).z(), texCoordData.get(v).y());
					}
				} else {
					vertices[v].position = new Vector3f(positionData.get(v).x(), positionData.get(v).y(), positionData.get(v).z());
					
					vertices[v].normal = new Vector3f(normalData.get(v).x(), normalData.get(v).y(), normalData.get(v).z());
					
					if (aMesh.mNumUVComponents().get() > 0) {
						vertices[v].texCoord = new Vector3f(texCoordData.get(v).x(), texCoordData.get(v).y(), texCoordData.get(v).z());
					}
				}
			}
			
			Skeleton skeleton = new Skeleton(aMesh.mNumBones());
			
			for (int b = 0; b < aMesh.mNumBones(); b++) {
				AIBone bone = AIBone.create(aMesh.mBones().get(b));
				
				Bone jBone = new Bone(bone.mName().dataString(), bone.mOffsetMatrix());
				
				skeleton.AddBone(b, jBone);
				
				System.out.println("Adding bone: " + jBone.name);
				System.out.println(aMesh.mNumBones());
				
				for (int w = 0; w < bone.mNumWeights(); w++) {
					AIVertexWeight weight = bone.mWeights().get(w);
					
					Vertex.SkinData skinData = (vertices[weight.mVertexId()].skinData == null)? new Vertex.SkinData() : vertices[weight.mVertexId()].skinData;
					
					skinData.AddWeight(b, weight.mWeight());
					
					vertices[weight.mVertexId()].skinData = skinData;
				}
			}
			
			skeletons.add(skeleton);
			
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
				meshes[i].skeleton = skeleton;
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
	
	public void bindMaterial(int material, Material mat) {
		materials.put(material, mat);
	}
	
	public Material getMaterial(int index) {
		return materials.get(index);
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
