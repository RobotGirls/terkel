package team25core;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.internal.opengl.AutoConfigGLSurfaceView;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.BUILDINGS;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.NONE;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.TEAPOT;

public class VuforiaLocalizerCustom extends VuforiaLocalizerImpl {

    protected static final String DETECTION_TAG = "Jewel Detection";

    protected GLSurfaceViewCustomRenderer glSurfaceViewCustomRenderer;
    protected Bitmap bitmap;
    protected Point touchPoint;
    protected boolean refreshBitmap;
    protected boolean attentionNeeded;

    public VuforiaLocalizerCustom(Parameters parameters)
    {
        super(parameters);

        touchPoint = new Point();
        bitmap = null;
        refreshBitmap = false;
        attentionNeeded = false;
    }

    protected class GLSurfaceViewCustomRenderer extends GLSurfaceViewRenderer {

        @Override
        public void onDrawFrame(GL10 gl)
        {
            if (rendererIsActive) {
                renderFrame();
            }
        }

    }

    @Override
    public void onRenderFrame()
    {
        int row;
        int midpoint = glSurface.getHeight() / 2;

        if (refreshBitmap == true) {
            if (touchPoint.y > midpoint) {
                row = midpoint - Math.abs(midpoint - touchPoint.y);
            } else {
                row = midpoint + Math.abs(midpoint - touchPoint.y);
            }

            bitmap = createBitmapFromGLSurface(touchPoint.x, row, 20, 20);
            refreshBitmap = false;
            attentionNeeded = true;
        }
    }

    private void reverseBuf(IntBuffer buf, int width, int height)
    {
        long ts = System.currentTimeMillis();
        int i = 0;
        int[] tmp = new int[width];
        while (i++ < height / 2)
        {
            buf.get(tmp);
            System.arraycopy(buf.array(), buf.limit() - buf.position(), buf.array(), buf.position() - width, width);
            System.arraycopy(tmp, 0, buf.array(), buf.limit() - buf.position(), width);
        }
        buf.rewind();
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h)
            throws OutOfMemoryError
    {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        int bitmapReversed[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        RobotLog.ii(DETECTION_TAG, "Copying from %d, %d, Size %d, %d", x, y, w, h);

        try {
            GLES20.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void setTouchPosition(int x, int y)
    {
        refreshBitmap = true;
        touchPoint.set(x, y);
        RobotLog.ii(DETECTION_TAG, "Touched at " + x + ", " + y);
    }

    public Point getTouchPosition()
    {
        return touchPoint;
    }

    public boolean attentionNeeded()
    {
        return attentionNeeded;
    }

    public void clearAttentionNeeded()
    {
        attentionNeeded = false;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    protected void makeGlSurface()
    {
        // Create OpenGL ES view:
        final int depthSize = 16;
        final int stencilSize = 0;
        final boolean translucent = Vuforia.requiresAlpha();
        final VuforiaLocalizerCustom localizer = this;

        if (glSurfaceViewCustomRenderer == null) {
            glSurfaceViewCustomRenderer = new GLSurfaceViewCustomRenderer();
        }

        if (glSurfaceParent != null) {
            appUtil.synchronousRunOnUiThread(new Runnable() { @Override public void run() {
                ViewGroup parent = glSurfaceParent;
                if (parent != null) {
                    TouchEnabledGLSurfaceView surface = new TouchEnabledGLSurfaceView(activity, localizer);
                    glSurface = surface;
                    surface.init(translucent, depthSize, stencilSize);

                    surface.setRenderer(glSurfaceViewCustomRenderer);

                    // Now add the GL surface view. It is important that the OpenGL ES surface view gets added
                    // BEFORE the camera is started and video background is configured.
                    surface.setVisibility(View.INVISIBLE);    // invisible until we know if we have to resize it or not
                    parent.addView(surface);
                }
            }});
        }
    }
}
