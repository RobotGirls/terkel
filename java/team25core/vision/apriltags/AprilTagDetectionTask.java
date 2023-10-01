package team25core.vision.apriltags;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import team25core.Robot;

import java.util.ArrayList;
import java.util.List;

import team25core.RobotEvent;
import team25core.RobotTask;

public class AprilTagDetectionTask extends RobotTask {

    public enum EventKind {
        OBJECTS_DETECTED,
    }

    public enum PollingMode {
        ON,
        OFF,
    }

    private String cameraName;
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 3.28084;

    //polling mode
    protected ElapsedTime pollTimer;
    protected PollingMode pollingMode;
    protected final static int POLL_RATE = 2;
    int apriltagID;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    int ID_TAG_OF_INTEREST = 0; // Tag ID 18 from the 36h11 family

    private Telemetry telemetry;
    private int rateLimitMs;
    private DetectionKind detectionKind;
    protected ElapsedTime timer;

    AprilTagDetection tagOfInterest = null;

    boolean tagFound = false;
    boolean firstTimeTagIsFound = true;

    public enum DetectionKind {
        TAG1_DETECTED,
        TAG2_DETECTED,
        TAG3_DETECTED,
        UNKNOWN_DETECTED,
    }
    public enum ObjectKind {
        TAG3_KIND,
        TAG2_KIND,
        TAG1_KIND,
        UNKNOWN_KIND,
    };

    public class TagDetectionEvent extends RobotEvent {

        public AprilTagDetection tagObject;
        public EventKind kind;

        //this is constructor for object detection event
        public TagDetectionEvent(RobotTask task, EventKind kind, AprilTagDetection tag)
        {
            super(task);
            this.kind = kind;
            this.tagObject = tag;

        }

        public String toString()
        {
            return kind.toString();
        }
    }

    public AprilTagDetectionTask(Robot robot, String cameraName)
    {
        super(robot);

        rateLimitMs = 0;
        detectionKind = DetectionKind.UNKNOWN_DETECTED;
        this.cameraName = cameraName;
        this.pollingMode = PollingMode.OFF;
    }

    public void initAprilTags(HardwareMap hardwareMap){

        // FIXME Commented this out to test IMU
       // telemetry.addLine("in initAprilTags");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId",
                "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class,
                this.cameraName), cameraMonitorViewId); 
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened() {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) { }
        });

        // Returns the string which is used to separate Telemetry.Items
        // contained within a line. The default separator is " | ".
        //   Returns:
        //     the string which is use to separate Telemetry.Items contained within a line.

        //telemetry.setMsTransmissionInterval(50);
    }

    public void init(Telemetry telemetry, HardwareMap hardwareMap)
    {
        this.telemetry = telemetry;
        // FIXME Commented this out to test IMU
       // telemetry.addLine("in AprilTagDetectionTask init");
        initAprilTags(hardwareMap);
    }

    @Override
    public void start()
    {
        if (rateLimitMs != 0) {
            timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        }
    }

    // This method prints out the detected april tag ID and
    // the X, Y, and Z translation and the yaw, pitch, and roll
    void tagToTelemetry(AprilTagDetection detection)
    {

        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
    }

    int getAprilTagID(AprilTagDetection detection)
    {
        return detection.id;
    }

    public void processAprilTags()
    {
        // Get an array of all the april tags detected
        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

        // FIXME Temporarily commenting out the telemetry for the tags (to test IMU)
        if (currentDetections.size() == 0) { // if no april tags were detected
//            telemetry.addLine("Don't see tag of interest :(");

            if (tagOfInterest == null) {
//                telemetry.addLine("(The tag has never been seen)");
            } else {
                telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
             //   tagToTelemetry(tagOfInterest);
            }
        } else {  // if april tags are detected
            // Get the first tag in the array of april tags
            tagOfInterest = currentDetections.get(0);
            tagFound = true;

            if (firstTimeTagIsFound) {
                robot.queueEvent(new TagDetectionEvent(this, EventKind.OBJECTS_DETECTED, tagOfInterest));
            }
            // FIXME Commenting out AprilTag tlm to test color sensor Lines 201 - 215
            if (tagFound) {
                telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                // tagToTelemtry prints out the detected april tag ID and
                // the X, Y, and Z translation and the yaw, pitch, and roll
              //  tagToTelemetry(tagOfInterest);
            } else { // if tag is not  found
                telemetry.addLine("Don't see tag of interest :(");

                if (tagOfInterest == null) {
                    telemetry.addLine("(The tag has never been seen)");
                } else {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                  //  tagToTelemetry(tagOfInterest);
                }
            }
        }
        telemetry.update();
    }

    public void setPollingMode(PollingMode pollingMode)
    {
        this.pollingMode = pollingMode;

        if (pollingMode == PollingMode.ON) {
            pollTimer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        }
    }

    @Override
    public boolean timeslice()
    {
        //shows location of object
        if ((pollingMode == PollingMode.ON) && (pollTimer.time() < POLL_RATE)) {
            return false;
        }
        processAprilTags();
//        pollTimer.reset();
        return false;
    }
}
