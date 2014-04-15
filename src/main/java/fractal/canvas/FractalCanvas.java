package fractal.canvas;

import fractal.exception.glsl.GLSLException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLPointerFunc;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_STATIC_DRAW;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_TRUE;
import static javax.media.opengl.GL2ES2.*;

public class FractalCanvas extends GLCanvas implements GLEventListener {
    private static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;
    private float[] points;
    private int programId;
    private int fragmentShaderId;
    private int vertexShaderId;
    private int bufferId;
    private int vertexArrayId;

    public FractalCanvas() throws GLException {
        initCanvas();
    }

    private void initCanvas() {
        addGLEventListener(this);
//        points = new float[0];
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        //gl2.glDisable(GL_DEPTH_TEST);
        //gl2.glEnable(GL_TEXTURE_2D);
        vertexArrayId = genVertexArrayObject(gl);
        gl.glBindVertexArray(vertexArrayId);

        points = new float[]{0f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f};

        bufferId = genBuffer(gl);
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        final FloatBuffer floatBuffer = getFloatBuffer();
        gl.glBufferData(GL_ARRAY_BUFFER, floatBuffer.capacity(), floatBuffer, GL_STATIC_DRAW);
        //gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        vertexShaderId = createShader(gl, GL_VERTEX_SHADER, "src/main/resources/shaders/vertex.glsl");
        fragmentShaderId = createShader(gl, GL_FRAGMENT_SHADER, "src/main/resources/shaders/fragment.glsl");

        assembleProgram(gl);
        gl.glUseProgram(programId);

//        final int positionAttributeLocation = gl.glGetAttribLocation(programId, "gl_FragColor");
//        gl.glVertexAttribPointer(positionAttributeLocation, 2, GL_FLOAT, false, 0, 0);
//        gl.glEnableVertexAttribArray(positionAttributeLocation);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glDeleteProgram(programId);
        gl.glDeleteShader(fragmentShaderId);
        gl.glDeleteShader(vertexShaderId);

        gl.glDeleteBuffers(1, new int[]{bufferId}, 0);
        gl.glDeleteVertexArrays(1, new int[]{vertexArrayId}, 0);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glLoadIdentity();


        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClear(GL_COLOR_BUFFER_BIT);

        //gl.glRectd(-0.5, -0.5, 0.5, 0.5);

        gl.glVertexPointer(points.length /2, GL_FLOAT, 0 , 0l);
        gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);

        gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);

//        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        gl.glMatrixMode(GL_PROJECTION);
//        gl.glLoadIdentity();
//        gl.glTranslated(1 / 2, 1 / 2, 0);
//
////        gl.glBegin(GL_POINTS);
//        gl.glBegin(GL_LINES);
//
//
//        gl.glEnd();
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

    private int genBuffer(GL2 gl) {
        int[] bufferId = new int[1];

        gl.glGenBuffers(1, bufferId, 0);

        return bufferId[0];
    }

    private int createShader(GL2 gl, int shaderType, String resourcePath) {
        final String shader = readResource(resourcePath);

        final int shaderIndex = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderIndex, 1, new String[]{shader}, null, 0);
        gl.glCompileShader(shaderIndex);

        final String errorMessage = checkShaderError(gl, shaderIndex);
        if(errorMessage != null) {
            throw new GLSLException(errorMessage);
        }

        return shaderIndex;
    }

    private String checkShaderError(GL2 gl, int shaderIndex) {
        final int[] compileStatus = new int[1];
        gl.glGetShaderiv(shaderIndex, GL_COMPILE_STATUS, compileStatus, 0);
        if(compileStatus[0] == GL_TRUE) {
            return null;
        }

        final int msgSize = 512;
        final byte[] msg = new byte[msgSize];
        final ByteBuffer msgBuffer = ByteBuffer.wrap(msg);
        gl.glGetShaderInfoLog(shaderIndex, msgSize, null, msgBuffer);
        final String message = new String(msg).trim();

        return message;
    }

    private String checkProgramError(GL2 gl, int programIndex, int programStatus) {
        final int[] linkStatus = new int[1];
        gl.glGetProgramiv(programIndex, programStatus, linkStatus, 0);
        if(linkStatus[0] == GL_TRUE) {
            return null;
        }

        final int msgSize = 512;
        final byte[] msg = new byte[msgSize];
        final ByteBuffer msgBuffer = ByteBuffer.wrap(msg);
        gl.glGetProgramInfoLog(programIndex, msgSize, null, msgBuffer);
        final String message = new String(msg).trim();

        return message;
    }

    private void assembleProgram(GL2 gl) {
        programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vertexShaderId);
        gl.glAttachShader(programId, fragmentShaderId);
        gl.glBindFragDataLocation(programId, 0, "outColor");
        gl.glLinkProgram(programId);
        final String linkingErrorMessage = checkProgramError(gl, programId, GL_LINK_STATUS);
        if(linkingErrorMessage != null) {
            throw new GLSLException(linkingErrorMessage);
        }
        gl.glValidateProgram(programId);
        final String validationErrorMessage = checkProgramError(gl, programId, GL_VALIDATE_STATUS);
        if(validationErrorMessage != null) {
            throw new GLSLException(validationErrorMessage);
        }
    }

    private FloatBuffer getFloatBuffer() {
        final int bufferCapacity = points.length * FLOAT_SIZE;
        final FloatBuffer floatBuffer = ByteBuffer.allocateDirect(bufferCapacity).asFloatBuffer();
        floatBuffer.put(points);
        floatBuffer.flip();

        return floatBuffer;
    }

    String readResource(String resourcePath) {
        final File resource = new File(resourcePath);
        final StringBuilder stringBuilder = new StringBuilder();

        try(final FileReader fileReader = new FileReader(resource);
            final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while(true) {
                final String line = bufferedReader.readLine();
                if(line == null) break;
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private int genVertexArrayObject(GL2 gl) {
        final int[] vertexArrayId = new int[1];

        gl.glGenVertexArrays(1, vertexArrayId, 0);

        return vertexArrayId[0];
    }
}
