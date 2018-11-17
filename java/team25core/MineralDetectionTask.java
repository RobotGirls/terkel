package team25core;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

import static org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus.TFOD_MODEL_ASSET;

public class MineralDetectionTask extends RobotTask {

    public enum EventKind {
        GOLD_DETECTED,
        SILVER_DETECTED,
    }

    protected ElapsedTime timer;

    public class MineralDetectionEvent extends RobotEvent {

        public EventKind kind;
        protected float xCoordinate;

        public MineralDetectionEvent(RobotTask task, EventKind kind, float xCoordinate)
        {
            super(task);
            this.kind = kind;
            this.xCoordinate = xCoordinate;
        }

        public float getxCoordinate()
        {
            return xCoordinate;
        }

        public String toString()
        {
            return kind.toString();
        }
    }

    private VuforiaLocalizer vuforia;
    private Telemetry telemetry;
    private TFObjectDetector tfod;
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    public MineralDetectionTask(Robot robot)
    {
        super(robot);
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VuforiaConstants.VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    private void initTfod(HardwareMap hardwareMap)
    {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public void init(Telemetry telemetry, HardwareMap hardwareMap)
    {
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod(hardwareMap);
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    @Override
    public void start()
    {
        tfod.activate();
        timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
    }

    @Override
    public void stop()
    {
        tfod.deactivate();
    }

    @Override
    public boolean timeslice()
    {
        if (timer.time() < 3) {
            return false;
        }

        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions.size() > 0) {
            for (Recognition object : updatedRecognitions) {
                if (object.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    robot.queueEvent(new MineralDetectionEvent(this, EventKind.GOLD_DETECTED, object.getLeft()));
                } else if (object.getLabel().equals(LABEL_SILVER_MINERAL)) {
                    robot.queueEvent(new MineralDetectionEvent(this, EventKind.SILVER_DETECTED, object.getLeft()));
                }
            }
        }

        timer.reset();
        return false;
    }
}
