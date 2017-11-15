package team25core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLException;
import android.view.MotionEvent;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.opengl.AutoConfigGLSurfaceView;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class TouchEnabledGLSurfaceView extends AutoConfigGLSurfaceView {

    VuforiaLocalizerCustom localizer;

    public TouchEnabledGLSurfaceView(Context context, VuforiaLocalizerCustom localizer)
    {
        super(context);

        this.localizer = localizer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            localizer.setTouchPosition((int)event.getX(), (int)event.getY());
        }
        return true;
    }
}
