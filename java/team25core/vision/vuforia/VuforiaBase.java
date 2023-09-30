//package team25core.vision.vuforia;
//
//import com.qualcomm.robotcore.util.RobotLog;
//import com.vuforia.PIXEL_FORMAT;
//import com.vuforia.Vuforia;
//
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
//
//import team25core.Robot;
//
//public class VuforiaBase
//{
//    protected static VuforiaLocalizerCustom vuforia;
//    protected VuforiaLocalizer.CameraDirection cameraDirection;
//
//    public VuforiaBase()
//    {
//        this.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
//    }
//
//    public VuforiaLocalizerCustom getVuforia()
//    {
//        return vuforia;
//    }
//
//    public void setCameraDirection(VuforiaLocalizer.CameraDirection cameraDirection)
//    {
//        this.cameraDirection = cameraDirection;
//    }
//
//    public void init(Robot robot)
//    {
//        int cameraMonitorViewId = robot.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", robot.hardwareMap.appContext.getPackageName());
//        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
//
//        parameters.vuforiaLicenseKey = "AdLmvUj/////AAAAGe/kAsI/H0WukR1Af5Og5w2Ey6b+wOXQ0h30RtwQyvYckcYCH8CBcrs0EGIqrGt0wbi7/icc/5DO3kqFkMdUh41bqjMCXWLU4d3Bz35AwPn89qCf/zp+ggEwgIUry20vwpU4uACQEqOJox8PHwzBmax9PquM/Jiq+/6wTx+8Bnd3Io4ymylg2uTVOsumVcphYhjkSyzaT+sUYtXGEdVEMWdyny8WuK4RE1SsaVLOvYap++/pA9b/7LLOFqW3yAwkaDMrPeqkCIN7RnDwH0ZxTbHsRRC/xKl43igL1T02tg0eUmeeyHdUxjP8T9BQlCdDmZvA5wGg6AAqe2ORWauhS49UvjW5xLGxglnsXXm0N4ce";
//
//        /*
//         * We also indicate which camera on the RC that we wish to use.
//         * Here we chose the back (HiRes) camera (for greater range), but
//         * for a competition robot, the front camera might be more convenient.
//         */
//        parameters.cameraDirection = this.cameraDirection;
//        this.vuforia = new VuforiaLocalizerCustom(parameters);
//
//        vuforia.setFrameQueueCapacity(1);
//        if (Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true)) {
//            RobotLog.i("Frame format set");
//        } else {
//        }
//    }
//
//}
