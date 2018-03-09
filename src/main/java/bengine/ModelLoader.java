package bengine;

import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import bengine.animation.Animation;
import bengine.animation.Bone;
import bengine.animation.KeyFrame;
import bengine.animation.Skeleton;
import bengine.rendering.Mesh;
import bengine.rendering.Vertex;

public class ModelLoader {
	
	AIScene scene;
	
	public ModelLoader(String file) {
		scene = aiImportFile(file, aiProcessPreset_TargetRealtime_MaxQuality | aiProcess_Triangulate);
	}
	
	public Map<String, Animation> generateAnimations() {
		
		HashMap<String, Animation> animations = new HashMap<String, Animation>();
		
		int numAnimations = scene.mNumAnimations();
		
		PointerBuffer animPointer = scene.mAnimations();
			
		for (int n = 0; n < numAnimations; n++) {
			AIAnimation anim = AIAnimation.create(animPointer.get(n));
			
			Animation a = new Animation(anim.mName().dataString());
			
			a.duration = (float) anim.mDuration();
			
			PointerBuffer animChannels = anim.mChannels();
			
			for (int l = 0; l < anim.mNumChannels(); l++) { // Each channel contains all of the position rotation and scaling data for one node.
				AINodeAnim nodeAnim = AINodeAnim.create(animChannels.get(l));
				
				String nodeName = nodeAnim.mNodeName().dataString();
				
				nodeAnim.mPositionKeys().forEach((AIVectorKey key) -> {
					KeyFrame posKey = new KeyFrame();
					
					posKey.time = (float) key.mTime();
					
					posKey.position = new Vector3f(key.mValue().x(), key.mValue().y(), key.mValue().z());
					
					a.AddPositionKeyframe(nodeName, posKey);
				});
				
				nodeAnim.mRotationKeys().forEach((AIQuatKey key) -> {
					KeyFrame rotKey = new KeyFrame();
					
					rotKey.time = (float) key.mTime();
					
					rotKey.rotation = new Quaternionf(key.mValue().x(), key.mValue().y(), key.mValue().z(), key.mValue().w());
					
					a.AddRotationKeyframe(nodeName, rotKey);
				});
				
				nodeAnim.mScalingKeys().forEach((AIVectorKey key) -> {
					KeyFrame scaleKey = new KeyFrame();
				
					scaleKey.time = (float) key.mTime();
					
					scaleKey.scale = new Vector3f(key.mValue().x(), key.mValue().y(), key.mValue().z());
					
					a.AddScalingKeyframe(nodeName, scaleKey);
				});
			}
			
			animations.put(a.getName(), a);
		}
		
		return animations;
	}
	
	public Skeleton[] generateSkeletons() {
		int numMeshes = scene.mNumMeshes();
		
		PointerBuffer meshPointer = scene.mMeshes();
		
		Skeleton[] skeletons = new Skeleton[numMeshes];
		
		for (int n = 0; n < numMeshes; n++) {
			AIMesh a = AIMesh.create(meshPointer.get(n));
			
			int numBones = a.mNumBones();
			
			if (numBones >  0) {
				skeletons[n] = new Skeleton();
			}
			
			PointerBuffer bonePointer = a.mBones();
			
			for (int b = 0; b < numBones; b++) {
				AIBone aBone = AIBone.create(bonePointer.get(b));
				
				Bone bone = new Bone(aBone.mName().dataString(), aBone.mOffsetMatrix());
				
				skeletons[n].AddBone(bone);
				
			}
			
		}
		
		return skeletons;
	}
	
	public Mesh[] generateMeshes() {
		
		int numMeshes = scene.mNumMeshes();
		
		PointerBuffer meshPointer = scene.mMeshes();
		
		Mesh[] meshes = new Mesh[numMeshes];
		
		AIMatrix4x4 modelTransform = scene.mRootNode().mTransformation();
		
		for (int n = 0; n < numMeshes; n++) {
			AIMesh a = AIMesh.create(meshPointer.get(n));
			//Load vertices, normals & tex-coords
			
			int numVertices = a.mNumVertices();
			
			AIVector3D.Buffer aVertices = a.mVertices();
			AIVector3D.Buffer aNormals = a.mNormals();
			AIVector3D.Buffer aTexCoords = a.mTextureCoords(0);
			
			Vertex[] vertices = new Vertex[numVertices];
			
			for (int x = 0; x < numVertices; x++) {
				AIVector3D vec = aVertices.get(x);
				
				aiTransformVecByMatrix4(vec, modelTransform);
				
				Vector3f vertex = new Vector3f(vec.x(), vec.y(), vec.z());
				
				vec = aNormals.get(x);
				
				aiTransformVecByMatrix4(vec, modelTransform);
				
				Vector3f normal = new Vector3f(vec.x(), vec.y(), vec.z());
				
				Vector3f texCoord = new Vector3f(0, 0, 0);
				
				if (a.mNumUVComponents().get(0) > 0) {
					vec = aTexCoords.get(x);
					
					texCoord.x = vec.x();
					texCoord.y = vec.y();
					texCoord.z = vec.z();
				}
				
				Vertex v = new Vertex();
				
				v.position = vertex;
				v.normal = normal;
				v.texCoord = texCoord;
				
				vertices[x] = v;
			}
			
			//Load skinning data
			
			for (int b = 0; b < a.mNumBones(); b++) {
				AIBone bone = AIBone.create(a.mBones().get(b));
				
				for (int w = 0; w < bone.mNumWeights(); w++) {
					AIVertexWeight weight = bone.mWeights().get(w);
					
					int vertexIndex = weight.mVertexId();
					
					if (vertices[vertexIndex].skinData == null) {
						vertices[vertexIndex].skinData = new Vertex.SkinData();
					}
					
					Vertex.SkinData skinData = vertices[vertexIndex].skinData;
					
					skinData.AddWeight(b, weight.mWeight());
				}
				
			}
			
			//Load indices
			
			ArrayList<Integer> indices = new ArrayList<Integer>();
			
			int numFaces = a.mNumFaces();
			
			AIFace.Buffer faces = a.mFaces();
			
			for (int x = 0; x < numFaces; x++) {
				AIFace face = faces.get(x);
				
				int numIndices = face.mNumIndices();
				
				IntBuffer aIndices = face.mIndices();
				
				for (int i = 0; i < numIndices; i++) {
					int index = aIndices.get(i);
					
					indices.add(index);
				}
			}
			
			int[] indices_int = new int[indices.size()];
			
			for (int i = 0; i < indices.size(); i++) {
				indices_int[i] = indices.get(i).intValue();
			}
			
			Mesh m = new Mesh(vertices, indices_int);
			
			meshes[n] = m;
		}
		
		return meshes;
	}
	
	public void destroy() {
		aiReleaseImport(scene);
		scene = null;
	}
}
