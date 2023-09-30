//package team25core;
//
//import android.content.Context;
//import android.view.MotionEvent;
//
//import org.firstinspires.ftc.robotcore.internal.opengl.AutoConfigGLSurfaceView;
//
//import team25core.vision.vuforia.VuforiaLocalizerCustom;
//
//public class TouchEnabledGLSurfaceView extends AutoConfigGLSurfaceView {
//
//    VuforiaLocalizerCustom localizer;
//
//    public TouchEnabledGLSurfaceView(Context context, VuforiaLocalizerCustom localizer)
//    {
//        super(context);
//
//        this.localizer = localizer;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event)
//    {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            localizer.setTouchPosition((int)event.getX(), (int)event.getY());
//        }
//        return true;
//    }
//}
