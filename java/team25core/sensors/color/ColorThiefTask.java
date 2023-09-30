//package team25core.sensors.color;
//
//import android.graphics.Bitmap;
//import android.graphics.Point;
//
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.vuforia.Image;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
//
//import java.util.concurrent.BlockingQueue;
//
//import team25core.vision.vuforia.VuforiaBase;
//import team25core.vision.vuforia.VuforiaImageHelper;
//import team25core.vision.vuforia.VuforiaLocalizerCustom;
//
//import team25core.Robot;
//import team25core.RobotEvent;
//import team25core.RobotTask;
//
//public class ColorThiefTask extends RobotTask {
//
//    public enum PollingMode {
//        ON,
//        OFF,
//    }
//
//    public enum EventKind {
//        RED,
//        BLUE,
//        BLACK,
//    }
//
//    public static class ImageBox {
//
//        public Point lowerLeft;
//        public Point extent;
//
//        public ImageBox()
//        {
//            lowerLeft = new Point();
//            extent = new Point();
//        }
//    }
//
//    public class ColorThiefEvent extends RobotEvent {
//
//        public EventKind kind;
//
//        public ColorThiefEvent(RobotTask task, EventKind kind)
//        {
//            super(task);
//            this.kind = kind;
//        }
//
//        public String toString()
//        {
//            return kind.toString();
//        }
//    }
//
//    /**
//     * Examine the logs and adjust these values to suit lighting conditions.
//     */
//    private int redDominantRedLowerThreshold   = 0x60;
//    private int redDominantBlueUpperThreshold  = 0x30;
//    private int blueDominantBlueLowerThreshold = 0x60;
//    private int blueDominantRedUpperThreshold  = 0x30;
//
//    private final static int POLL_RATE = 2;
//
//    protected BlockingQueue<VuforiaLocalizer.CloseableFrame> frameQueue;
//    protected VuforiaLocalizer.CloseableFrame frame;
//    protected Image image;
//    protected PollingMode pollingMode;
//    protected ElapsedTime pollTimer;
//    protected Telemetry.Item dominantTelemetry;
//    protected Telemetry.Item pollingTelemetry;
//    protected VuforiaLocalizerCustom vuforia;
//    protected VuforiaBase vuforiaBase;
//
//    public ColorThiefTask(Robot robot, VuforiaBase vuforiaBase)
//    {
//        super(robot);
//
//        this.pollingMode = PollingMode.OFF;
//        this.dominantTelemetry = robot.telemetry.addData("Dominant color: ", "0x000000");
//        this.pollingTelemetry = robot.telemetry.addData("Polling: ", "OFF");
//        this.vuforia = vuforiaBase.getVuforia();
//    }
//
//    public ColorThiefTask(Robot robot, VuforiaBase vuforiaBase, VuforiaLocalizer.CameraDirection cameraDirection)
//    {
//        super(robot);
//
//        this.pollingMode = PollingMode.OFF;
//        this.dominantTelemetry = robot.telemetry.addData("Dominant color: ", "0x000000");
//        this.pollingTelemetry = robot.telemetry.addData("Polling: ", "OFF");
//        this.vuforia = vuforiaBase.getVuforia();
//        vuforiaBase.setCameraDirection(cameraDirection);
//    }
//
//    @Override
//    public void start()
//    {
//    }
//
//    @Override
//    public void stop()
//    {
//    }
//
//    public void setPollingMode(PollingMode pollingMode)
//    {
//        this.pollingMode = pollingMode;
//
//        if (pollingMode == PollingMode.ON) {
//            pollTimer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
//        }
//    }
//
//    public void setThresholds(int redRedLower, int redBlueUpper, int blueBlueLower, int blueRedUpper)
//    {
//        redDominantBlueUpperThreshold = redBlueUpper;
//        redDominantRedLowerThreshold = redRedLower;
//        blueDominantBlueLowerThreshold = blueBlueLower;
//        blueDominantRedUpperThreshold = blueRedUpper;
//    }
//
//    private boolean thresholdSatisfied(int dominant, int subordinate, int dominantLower, int subordinateUpper)
//    {
//        if ((dominant > dominantLower) && (subordinate < subordinateUpper)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private void determineColor(RGBColor dominant)
//    {
//        if (!dominant.isBlack()) {
//            if (dominant.red > dominant.blue) {
//                if (thresholdSatisfied(dominant.red, dominant.blue, redDominantRedLowerThreshold, redDominantBlueUpperThreshold)) {
//                    robot.queueEvent(new ColorThiefEvent(this, EventKind.RED));
//                } else {
//                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLACK));
//                }
//            } else {
//                if (thresholdSatisfied(dominant.blue, dominant.red, blueDominantBlueLowerThreshold, blueDominantRedUpperThreshold)) {
//                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLUE));
//                } else {
//                    robot.queueEvent(new ColorThiefEvent(this, EventKind.BLACK));
//                }
//            }
//            dominantTelemetry.setValue("0x" + Integer.toHexString(dominant.to888()));
//        } else {
//            robot.queueEvent(new ColorThiefEvent(this, EventKind.BLACK));
//        }
//
//    }
//
//    @Override
//    public boolean timeslice()
//    {
//        RGBColor dominant;
//
//        if ((pollingMode == PollingMode.ON) && (pollTimer.time() > POLL_RATE)) {
//            vuforia.forceRefreshBitmap();
//            pollTimer.reset();
//            return false;
//        }
//
//        if (vuforia.attentionNeeded()) {
//            Bitmap bitmap = vuforia.getBitmap();
//            if (bitmap == null) {
//                return false;
//            }
//
//            VuforiaImageHelper helper = new VuforiaImageHelper(bitmap);
//            dominant = helper.getDominant(0, 0, bitmap.getWidth(), bitmap.getHeight());
//            determineColor(dominant);
//
//            vuforia.clearAttentionNeeded();
//        }
//
//        pollingTelemetry.setValue(pollingMode.toString());
//
//        return false;
//    }
//}
