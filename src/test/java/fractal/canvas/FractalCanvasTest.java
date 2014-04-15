package fractal.canvas;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FractalCanvasTest {
    @Spy
    private FractalCanvas canvas;

    @Test
    public void testFloatSizeIs4Bytes() {
        final int floatSize = Float.SIZE / Byte.SIZE;

        assertThat(floatSize, equalTo(4));
    }

    @Test
    public void testReadResource_ReadDataMatchesContent() {
        final String contents = canvas.readResource("src/main/resources/test.txt");

        assertThat(contents, equalTo("Test!\n"));
    }

    @Ignore
    @Test
    public void testName() {
        System.out.println(canvas.readResource("src/main/resources/shaders/vertex.glsl"));
    }

    @Test
    public void testBuffer() {
        final float[] floats = {1, 2};
        final FloatBuffer floatBuffer = ByteBuffer.allocateDirect(4 * 2).asFloatBuffer();
        floatBuffer.put(floats);
        floatBuffer.rewind();
        final int remaining = floatBuffer.remaining();

        assertThat(floats.length, equalTo(remaining));
        assertThat(floatBuffer.get(), equalTo(floats[0]));
        assertThat(floatBuffer.get(), equalTo(floats[1]));
    }
}
