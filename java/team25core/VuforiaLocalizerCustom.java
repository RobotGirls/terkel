package team25core;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import android.view.View;
import android.view.ViewGroup;

import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.internal.opengl.AutoConfigGLSurfaceView;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.BUILDINGS;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.NONE;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.Parameters.CameraMonitorFeedback.TEAPOT;

public class VuforiaLocalizerCustom extends VuforiaLocalizerImpl {

    protected GLSurfaceViewCustomRenderer glSurfaceViewCustomRenderer;
    protected Bitmap bitmap;
    protected Point touchPoint;

    public VuforiaLocalizerCustom(Parameters parameters)
    {
        super(parameters);

        touchPoint = new Point();
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

    }

    public void setTouchPosition(int x, int y)
    {
        touchPoint.set(x, y);
    }

    public void setBitmap(Bitmap b)
    {
        this.bitmap = b;
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
