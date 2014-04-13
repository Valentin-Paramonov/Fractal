package fractal.canvas;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static javax.media.opengl.GL.*;
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

    private FloatBuffer getFloatBuffer() {
        final FloatBuffer floatBuffer = ByteBuffer.allocateDirect(points.length).asFloatBuffer();
        floatBuffer.put(points);
        floatBuffer.rewind();

        return floatBuffer;
    }
}
