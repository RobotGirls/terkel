package team25core;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;

import java.util.concurrent.BlockingQueue;

public class ColorThiefTask extends RobotTask {

    public enum PollingMode {
        ON,
        OFF,
    }

    public enum EventKind {
        RED,
        BLUE,
        BLACK,
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

    /**
     * Examine the logs and adjust these values to suit lighting conditions.
     */
    private final static int RED_DOMINANT_RED_LOWER_THRESHOLD   = 0x60;
    private final static int RED_DOMINANT_BLUE_UPPER_THRESHOLD  = 0x30;
    private final static int BLUE_DOMINANT_BLUE_LOWER_THRESHOLD = 0x60;
    private final static int BLUE_DOMINANT_RED_UPPER_THRESHOLD  = 0x30;

    private final static int POLL_RATE = 2;

    protected VuforiaLocalizerCustom vuforia;
    protected BlockingQueue<VuforiaLocalizer.CloseableFrame> frameQueue;
    protected VuforiaLocalizer.CloseableFrame frame;
    protected Image image;
    protected PollingMode pollingMode;
    protected ElapsedTime pollTimer;
    protected Telemetry.Item dominantTelemetry;
    protected Telemetry.Item pollingTelemetry;

    public ColorThiefTask(Robot robot)
    {
        super(robot);

        this.pollingMode = PollingMode.OFF;
        this.dominantTelemetry = robot.telemetry.addData("Dominant color: ", "0x000000");
        this.pollingTelemetry = robot.telemetry.addData("Polling: ", "OFF");
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
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        // this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        this.vuforia = new VuforiaLocalizerCustom(parameters);

        vuforia.setFrameQueueCapacity(1);
        if (Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true)) {
            RobotLog.i("Frame format set");
        } else {
            RobotLog.e("Could not set frame format");
        }
    }

    @Override
    public void stop()
    {
    }

    public void setPollingMode(PollingMode pollingMode)
    {
        this.pollingMode = pollingMode;

        if (pollingMode == PollingMode.ON) {
            pollTimer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        }
    }

    private boolean thresholdSatisfied(int dominant, int subordinate, int dominantLower, int subordinateUpper)
    {
        if ((dominant > dominantLower) && (subordinate < subordinateUpper)) {
            return true;
        } else {
            return false;
        }
    }

    private void determineColor(RGBColor dominant)
    {
        if (!dominant.isBlack()) {
            if (dominant.red > dominant.blue) {
                if (thresholdSatisfied(dominant.red, dominant.blue, RED_DOMINANT_RED_LOWER_THRESHOLD, RED_DOMINANT_BLUE_UPPER_THRESHOLD)) {
                    robot.queueEvent(new ColorThiefEvent(this, EventKind.RED));
                } else {
                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLACK));
                }
            } else {
                if (thresholdSatisfied(dominant.blue, dominant.red, BLUE_DOMINANT_BLUE_LOWER_THRESHOLD, BLUE_DOMINANT_RED_UPPER_THRESHOLD)) {
                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLUE));
                } else {
                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLACK));
                }
            }
            dominantTelemetry.setValue("0x" + Integer.toHexString(dominant.to888()));
        } else {
            robot.queueEvent(new ColorThiefEvent(this, EventKind.BLACK));
        }

    }

    @Override
    public boolean timeslice()
    {
        RGBColor dominant;

        if ((pollingMode == PollingMode.ON) && (pollTimer.time() > POLL_RATE)) {
            vuforia.forceRefreshBitmap();
            pollTimer.reset();
            return false;
        }

        if (vuforia.attentionNeeded()) {
            Bitmap bitmap = vuforia.getBitmap();
            if (bitmap == null) {
                return false;
            }

            VuforiaImageHelper helper = new VuforiaImageHelper(bitmap);
            dominant = helper.getDominant(0, 0, bitmap.getWidth(), bitmap.getHeight());
            determineColor(dominant);

            vuforia.clearAttentionNeeded();
        }

        pollingTelemetry.setValue(pollingMode.toString());

        return false;
    }
}
