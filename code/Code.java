package code;

import java.nio.*;
import javax.swing.*;
import java.lang.Math;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

import org.joml.*;

public class Code extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[8];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float pyrLocX, pyrLocY, pyrLocZ;
	private float tetraLocX, tetraLocY, tetraLocZ;

	// cow variables
	private float cowLocX, cowLocY, cowLocZ;
	private int numObjVertices;
	private ImportedModel myModel;
	private int cowTexture;

	// tree variables
	private float treeLocX, treeLocY, treeLocZ;
	private int numTreeVertices;
	private ImportedModel myTreeModel;
	private int treeTexture;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, pLoc;
	private float aspect;

	// allocate variables for animation
	private double tf;
	private double startTime;
	private double elapsedTime;

	// general texture
	private int genTexture;

	public Code() {
		setTitle("Chapter 4 - program 3");
		setSize(600, 600);
		GLProfile glp = GLProfile.getMaxProgrammableCore(true);
		GLCapabilities caps = new GLCapabilities(glp);
		myCanvas = new GLCanvas(caps);
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);

		// // create animator
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_COLOR_BUFFER_BIT);

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		vMat.translation(-cameraX, -cameraY, -cameraZ);

		// draw the cube using buffer #0

		mMat.translation(cubeLocX, cubeLocY, cubeLocZ);

		// use system time to genrtate slowly increasing values for animation
		elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
		tf = elapsedTime % 10.0;

		// scales the cube to be smaller
		mMat.scale(0.6f, 0.6f, 0.6f);

		// make square that a circular path around the origin
		mMat.translate((float) Math.sin(tf * 1.2 * Math.PI) * 10.0f, 0.0f,
				(float) Math.cos(tf * 1.2 * Math.PI) * 15.0f);
		// rock the square back and forth
		mMat.rotateY((float) Math.toRadians(45.0f * tf));

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, 36);

		// draw the tetrahedron using buffer #1
		mMat.translation(tetraLocX, tetraLocY, tetraLocZ);

		// scales the traingle to be bigger
		mMat.scale(2.5f, 2.5f, 2.5f);

		// make the tetrahedron do a spinning motion
		mMat.rotateY((float) Math.toRadians(45.0f * tf));

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, 12);

		// draw the cow using buffer
		mMat.translation(cowLocX, cowLocY, cowLocZ);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		// set the texture
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, cowTexture);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "texture0"), 0);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, myModel.getNumVertices());

		// draw the tree using buffer
		mMat.translation(treeLocX, treeLocY, treeLocZ);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		// assign the texture to the tree
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, treeTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, myTreeModel.getNumVertices());

		// print out the elapsed time
		System.out.println(elapsedTime);

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// import the model
		myModel = new ImportedModel("moo.obj");
		myTreeModel = new ImportedModel("tree.obj");

		renderingProgram = Utils.createShaderProgram("code/vertShader.glsl", "code/fragShader.glsl");

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupVertices();
		cameraX = 0.0f;
		cameraY = 3.0f;
		cameraZ = 15.0f;

		cubeLocX = 0.0f;
		cubeLocY = 2.3f;
		cubeLocZ = 0.0f;
		// tetrahedron vertices
		tetraLocX = 0.0f;
		tetraLocY = 6.0f;
		tetraLocZ = 0.0f;
		// cow vertices
		cowLocX = 0.0f;
		cowLocY = -2.0f;
		cowLocZ = 0.0f;

		// tree vertices
		treeLocX = 3.0f;
		treeLocY = 0.0f;
		treeLocZ = -6.0f;

		// texture for tree
		cowTexture = Utils.loadTexture("furCow.jpg");
		genTexture = Utils.loadTexture("green.jpg");
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		float[] cubePositions = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
				-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
		};

		// vertice postions for triangular pyramid
		float[] tetrahedronPositions = { 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // front
				0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, // right
				0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, // left
				-1.0f, 0.0f, 1.0f, 1, .0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f // bottom
		};

		// float[] tetrahedronPositions = { 0.0f, 1.0f, 0.0f, -1.0f, -1.0f, 1.0f, 1.0f,
		// -1.0f, 1.0f, // front
		// 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, // right
		// 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, // back
		// 0.0f, 1.0f, 0.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f // left
		// };

		// set up cow vertices
		//
		numObjVertices = myModel.getNumVertices();

		Vector3f[] vertices = myModel.getVertices();
		Vector2f[] texCoords = myModel.getTexCoords();
		Vector3f[] normals = myModel.getNormals();

		float[] pvalues = new float[numObjVertices * 3];
		float[] tvalues = new float[numObjVertices * 2];
		float[] nvalues = new float[numObjVertices * 3];

		for (int i = 0; i < numObjVertices; i++) {
			pvalues[i * 3] = (float) (vertices[i].x());
			pvalues[i * 3 + 1] = (float) (vertices[i].y());
			pvalues[i * 3 + 2] = (float) (vertices[i].z());
			tvalues[i * 2] = (float) (texCoords[i].x());
			tvalues[i * 2 + 1] = (float) (texCoords[i].y());
			nvalues[i * 3] = (float) (normals[i].x());
			nvalues[i * 3 + 1] = (float) (normals[i].y());
			nvalues[i * 3 + 2] = (float) (normals[i].z());

		}

		// set up tree vertices
		//
		numTreeVertices = myTreeModel.getNumVertices();
		Vector3f[] treeVertices = myTreeModel.getVertices();
		Vector2f[] treeTexCoords = myTreeModel.getTexCoords();
		Vector3f[] treeNormals = myTreeModel.getNormals();

		float[] treePvalues = new float[numTreeVertices * 3];
		float[] treeTvalues = new float[numTreeVertices * 2];
		float[] treeNvalues = new float[numTreeVertices * 3];

		for (int i = 0; i < numTreeVertices; i++) {
			treePvalues[i * 3] = (float) (treeVertices[i].x());
			treePvalues[i * 3 + 1] = (float) (treeVertices[i].y());
			treePvalues[i * 3 + 2] = (float) (treeVertices[i].z());
			treeTvalues[i * 2] = (float) (treeTexCoords[i].x());
			treeTvalues[i * 2 + 1] = (float) (treeTexCoords[i].y());
			treeNvalues[i * 3] = (float) (treeNormals[i].x());
			treeNvalues[i * 3 + 1] = (float) (treeNormals[i].y());
			treeNvalues[i * 3 + 2] = (float) (treeNormals[i].z());

		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(
				cubePositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit() * 4, cubeBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer tetBuf = Buffers.newDirectFloatBuffer(
				tetrahedronPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, tetBuf.limit() * 4, tetBuf, GL_STATIC_DRAW);

		// set up cow vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer vertBuf = Buffers
				.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer texBuf = Buffers
				.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer normBuf = Buffers
				.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, normBuf.limit() * 4, normBuf, GL_STATIC_DRAW);

		// set up tree vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer treeVertBuf = Buffers.newDirectFloatBuffer(
				treePvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, treeVertBuf.limit() * 4, treeVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer treeTexBuf = Buffers.newDirectFloatBuffer(
				treeTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, treeTexBuf.limit() * 4, treeTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer treeNormBuf = Buffers.newDirectFloatBuffer(
				treeNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, treeNormBuf.limit() * 4, treeNormBuf, GL_STATIC_DRAW);

	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

	}

	public void dispose(GLAutoDrawable drawable) {
	}
}