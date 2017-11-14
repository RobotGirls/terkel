package team25core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.opengl.AutoConfigGLSurfaceView;

public class TouchEnabledGLSurfaceView extends AutoConfigGLSurfaceView {

    VuforiaLocalizerCustom localizer;

    public TouchEnabledGLSurfaceView(Context context, VuforiaLocalizerCustom localizer)
    {
        super(context);

        this.localizer = localizer;

        setDrawingCacheEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        localizer.setTouchPosition((int)event.getX(), (int)event.getY());
        localizer.setBitmap(getDrawingCache());

        return true;
    }
}
