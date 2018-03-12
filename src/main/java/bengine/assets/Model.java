package bengine.assets;

import static org.lwjgl.assimp.Assimp.*;

import org.joml.Vector3f;
import org.lwjgl.assimp.*;

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
	
	private Map<Integer, Material> materials = new HashMap<Integer, Material>();
	
	private AIScene scene;
	
	public Model(File file) {
		super(file);
	}

	@Override
	public void onLoad(File file) throws AssetCreationException {
		scene = aiImportFile(file.toString(), aiProcessPreset_TargetRealtime_MaxQuality | aiProcess_Triangulate);
		
		AINode rootNode = scene.mRootNode();
		
		//Load meshes from the object.
		meshes = new Mesh[scene.mNumMeshes()];
		
		for (int i = 0; i < scene.mNumMeshes(); i++) {
			AIMesh aMesh = AIMesh.create(scene.mMeshes().get(i));
			
			int vertexCount = aMesh.mNumVertices();
			
			Vertex[] vertices = new Vertex[vertexCount];
			
			AIVector3D.Buffer positionData = aMesh.mVertices();
			AIVector3D.Buffer normalData = aMesh.mVertices();
			AIVector3D.Buffer texCoordData = aMesh.mTextureCoords(0);
			
			for (int v = 0; v < vertexCount; v++) {
				vertices[v] = new Vertex();
				
				vertices[v].position = new Vector3f(positionData.get(v).x(), positionData.get(v).y(), positionData.get(v).z());
				
				vertices[v].normal = new Vector3f(normalData.get(v).x(), normalData.get(v).y(), normalData.get(v).z());
				
				if (aMesh.mNumUVComponents().get() > 0) {
					vertices[v].texCoord = new Vector3f(texCoordData.get(v).x(), texCoordData.get(v).y(), texCoordData.get(v).z());
				}
			}
			
			Skeleton skeleton = new Skeleton();
			
			for (int b = 0; b < aMesh.mNumBones(); b++) {
				AIBone bone = AIBone.create(aMesh.mBones().get(b));
				
				Bone jBone = new Bone(bone.mName().dataString(), bone.mOffsetMatrix());
				
				skeleton.AddBone(jBone);
			
				for (int w = 0; w < bone.mNumWeights(); w++) {
					AIVertexWeight weight = bone.mWeights().get(w);
					
					Vertex.SkinData skinData = (vertices[weight.mVertexId()].skinData == null)? new Vertex.SkinData() : vertices[weight.mVertexId()].skinData;
					
					skinData.AddWeight(skeleton.ResolveName(jBone.name), weight.mWeight());
					
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
				meshes[i].skeleton = skeleton;
			}
			
			meshes[i].create();
		}
	}
	
	@Override
	public void destroy() {
		
		aiReleaseImport(scene);
		
		synchronized (getGame().renderLock) {
			getGame().grab();
		
			for (Mesh m : meshes) {
				m.destroy();
			}
			
			getGame().release();
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
}
