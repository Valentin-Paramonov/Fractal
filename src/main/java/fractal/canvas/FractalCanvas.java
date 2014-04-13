package fractal.canvas;

import fractal.exception.glsl.GLSLException;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2ES2.GL_COMPILE_STATUS;
import static javax.media.opengl.GL2ES2.GL_VERTEX_SHADER;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

public class FractalCanvas extends GLCanvas implements GLEventListener {
    private float[] points;

    public FractalCanvas() throws GLException {
        initCanvas();
    }

    private void initCanvas() {
        addGLEventListener(this);
        points = new float[0];
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl2 = glAutoDrawable.getGL().getGL2();

        gl2.glDisable(GL_DEPTH_TEST);
        gl2.glEnable(GL_TEXTURE_2D);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        final int bufferId = genVertexBufferObject(gl);
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        final FloatBuffer floatBuffer = getFloatBuffer();
        gl.glBufferData(GL_ARRAY_BUFFER, points.length, floatBuffer, GL_STATIC_DRAW);

        createShader(gl);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glTranslated(1 / 2, 1 / 2, 0);

//        gl.glBegin(GL_POINTS);
        gl.glBegin(GL_LINES);


        gl.glEnd();
    }


    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i2, int width, int height) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glViewport(0, 0, width, height);
    }


    public void setPoints(Point[] points) {
        final int pointsLength = points.length;
        this.points = new float[pointsLength * 2];
        for(int i = 0; i < pointsLength; i++) {
            final Point point = points[i];
            this.points[i] = (float) point.getX();
            this.points[i + 1] = (float) point.getY();
        }
    }

    public void drawFractal(int times) {
        repaint();
    }

    private int genVertexBufferObject(GL2 gl) {
        int[] bufferindex = new int[1];

        gl.glGenBuffers(1, bufferindex, 0);

        return bufferindex[0];
    }

    private int createShader(GL2 gl) {
        final int shaderIndex = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(shaderIndex, 1, null, null);
        gl.glCompileShader(shaderIndex);

        final int[] compileStatus = new int[1];
        gl.glGetShaderiv(shaderIndex, GL_COMPILE_STATUS, compileStatus, 0);
        if(compileStatus[0] != GL_TRUE) {
            final int msgSize = 512;
            final ByteBuffer msgBuffer = ByteBuffer.wrap(new byte[msgSize]);
            gl.glGetShaderInfoLog(shaderIndex, msgSize, null, msgBuffer);

            throw new GLSLException(msgBuffer.toString());
        }

        return shaderIndex;
    }

    private FloatBuffer getFloatBuffer() {
        final FloatBuffer floatBuffer = ByteBuffer.allocateDirect(points.length * Float.SIZE).asFloatBuffer();
        floatBuffer.put(points);
        floatBuffer.rewind();

        return floatBuffer;
    }
}
