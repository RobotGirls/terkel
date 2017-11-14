package team25core;

import android.graphics.Point;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;

import java.util.concurrent.BlockingQueue;

public class ColorThiefTask extends RobotTask {

    public enum EventKind {
        RED,
        BLUE,
        UNKNOWN,
    }

    public static class ImageBox {

        public Point lowerLeft;
        public Point extent;

        public ImageBox()
        {
            lowerLeft = new Point();
            extent = new Point();
        }
    }

    public class ColorThiefEvent extends RobotEvent {

        public EventKind kind;

        public ColorThiefEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }

        public String toString()
        {
            return kind.toString();
        }
    }

    VuforiaLocalizer vuforia;
    BlockingQueue<VuforiaLocalizer.CloseableFrame> frameQueue;
    VuforiaLocalizer.CloseableFrame frame;
    Image image;
    ImageBox box;

    public ColorThiefTask(Robot robot)
    {
        super(robot);
    }

    @Override
    public void start()
    {
        int cameraMonitorViewId = robot.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", robot.hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AdLmvUj/////AAAAGe/kAsI/H0WukR1Af5Og5w2Ey6b+wOXQ0h30RtwQyvYckcYCH8CBcrs0EGIqrGt0wbi7/icc/5DO3kqFkMdUh41bqjMCXWLU4d3Bz35AwPn89qCf/zp+ggEwgIUry20vwpU4uACQEqOJox8PHwzBmax9PquM/Jiq+/6wTx+8Bnd3Io4ymylg2uTVOsumVcphYhjkSyzaT+sUYtXGEdVEMWdyny8WuK4RE1SsaVLOvYap++/pA9b/7LLOFqW3yAwkaDMrPeqkCIN7RnDwH0ZxTbHsRRC/xKl43igL1T02tg0eUmeeyHdUxjP8T9BQlCdDmZvA5wGg6AAqe2ORWauhS49UvjW5xLGxglnsXXm0N4ce";

        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        // this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        this.vuforia = new VuforiaLocalizerCustom(parameters);

        vuforia.setFrameQueueCapacity(1);
        if (Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true)) {
            RobotLog.i("Frame format set");
        } else {
            RobotLog.e("Could not set frame format");
        }


        /*
         * Get an image to set the default size for.
         */
        frameQueue = vuforia.getFrameQueue();
        try {
            frame = frameQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        image = frame.getImage(0);
        setImageTargetLocation(0, image.getWidth()/2, image.getWidth(), image.getHeight()/2);

    }

    @Override
    public void stop()
    {
    }

    public void setImageTargetLocation(int x, int y, int width, int height)
    {
        if (box == null) {
            box = new ImageBox();
        }

        box.lowerLeft.set(x, y);
        box.extent.set(width, height);
    }

    @Override
    public boolean timeslice()
    {
        RGBColor dominant;
        try {
            frame = frameQueue.take();
            image = frame.getImage(0);
            VuforiaImageHelper helper = new VuforiaImageHelper(image);
            dominant = helper.getDominant(box.lowerLeft.x, box.lowerLeft.y, box.extent.x, box.extent.y);
            if (!dominant.isBlack()) {
                if (dominant.red > dominant.blue) {
                    robot.queueEvent(new ColorThiefEvent(this, EventKind.RED));
                } else {
                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLUE));
                }
                robot.telemetry.addData("Dominant color ", "0x" + Integer.toHexString(dominant.to888()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
