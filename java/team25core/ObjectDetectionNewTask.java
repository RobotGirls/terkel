package team25core;

import static java.lang.Thread.sleep;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import team25core.vision.apriltags.AprilTagDetectionTask;

public class ObjectDetectionNewTask extends RobotTask {
    // create the TensorFlowProcessorBuilder (used for configuration)
    TfodProcessor.Builder myTfodProcessorBuilder;
    //create the TensorFlow Object Detection processor
    TfodProcessor myTfodProcessor;
    // create the AprilTag processor builder (used for config)
    AprilTagProcessor.Builder myAprilTagProcessorBuilder;
    // create the AprilTag processor
    //AprilTagProcessor myAprilTagProcessor;
    AprilTagProcessor aprilTag = null;


    // create the VisionPortal
    VisionPortal myVisionPortal;
    VisionPortal.Builder myVisionPortalBuilder;

    public static final String LABEL_OBJECT1 = "Prop";
    public static final String LABEL_OBJECT2 = "Pixel";

    private Telemetry.Item numAprilTagsDetectedTlm;
    private Telemetry.Item aprilTagIdTlm;
    private Telemetry.Item aprilTagNameTlm;
    private Telemetry.Item aprilTagPoseXTlm;
    private Telemetry.Item aprilTagPoseYTlm;
    private Telemetry.Item aprilTagPoseZTlm;
    private Telemetry.Item aprilTagYawTlm;
    private Telemetry.Item aprilTagPitchTlm;
    private Telemetry.Item aprilTagRollTlm;
    private Telemetry.Item aprilTagPoseRangeTlm;
    private Telemetry.Item aprilTagPoseBearingTlm;
    private Telemetry.Item aprilTagPoseElevationTlm;
    private Telemetry.Item aprilTagCenterXTlm;
    private Telemetry.Item aprilTagCenterYTlm;


    // not sure if this is needed with husky lens
    //private static final String TFOD_MODEL_ASSET = "FreightFrenzyReg.tflite";

    private String cameraName;

    protected ElapsedTime timer;


    private int rateLimitMs;

    Telemetry myTelemetry;

    boolean doStreaming = false;

    float myDecimation = 2;

    public enum DetectionKind {
        EVERYTHING, //do both object detection and AprilTags
        OBJECT1_DETECTED,
        OBJECT2_DETECTED,
        APRILTAG_DETECTED,
        UNKNOWN_DETECTED,
    }

    public enum ObjectKind {
        OBJECT2_KIND,
        OBJECT1_KIND,
        UNKNOWN_KIND,
    }

    private ObjectDetectionNewTask.DetectionKind detectionKind;

    public enum EventKind {
        OBJECTS_DETECTED,
        APRIL_TAG_DETECTED,
    }

    private final float DEFAULT_DECIMATION = 2;

    private boolean myDoManualExposure = false;

    private int myExposureMS;
    private int myGain;

    private AprilTagDetection desiredTag = null;
    private boolean targetFound = false;

    private int desiredTagID;

    public class ObjectDetectionEvent extends RobotEvent {

        public ObjectDetectionNewTask.EventKind kind;
        public List<Recognition> objects;

        //this is constructor for object detection event
        public ObjectDetectionEvent(RobotTask task, ObjectDetectionNewTask.EventKind kind, List<Recognition> m) {
            super(task);
            this.kind = kind;
            this.objects = new ArrayList<>(m.size());
            this.objects.addAll(m);
        }

        public String toString() {
            return kind.toString();
        }
    }

    public AprilTagDetection getAprilTag(int tagID) {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        myTelemetry.addData("inside getAprilTag call in terkel ", "true");
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                myTelemetry.addData("inside getAprilTag tag detected id:", detection.id);
                printAprilTagTlm(detection);
                //  Check to see if we want to track towards this tag.
                if ((tagID < 0) || (detection.id == tagID)) {
                    return detection;
                }
            }
        }   // end for() loop
        return null;
    }

    public class TagDetectionEvent extends RobotEvent {

        //public org.openftc.apriltag.AprilTagDetection tagObject;
        public AprilTagDetection aprilTag;
        public ObjectDetectionNewTask.EventKind kind;

        //this is constructor for object detection event
        public TagDetectionEvent(RobotTask task, ObjectDetectionNewTask.EventKind kind, AprilTagDetection tag)
        {
            super(task);
            this.kind = kind;
            this.aprilTag = tag;

        }

        public String toString()
        {
            return kind.toString();
        }
    }


    public void initAprilTagTlm(Telemetry myTelemetry) {
        // add "key" information to telemetry
        myTelemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        myTelemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        myTelemetry.addLine("RBE = Range, Bearing & Elevation");
    }

    //---------------------------------------------------------
    //constructor
    // detection kind will be used to determine whether we're doing
    // both object detection and AprilTags (EVERYTHING)
    // OR just object detection (OBJECT1_DETECTED or OBJECT2_DETECTED)
    // OR just AprilTags (APRILTAGS_DETECTED)
    public ObjectDetectionNewTask(Robot robot, Telemetry telemetry, DetectionKind myDetectionKind) {
        super(robot);
        rateLimitMs = 0;
        //FIXME figure out what kind of detection we want it to be
        //detectionKind = ObjectDetectionNewTask.DetectionKind.EVERYTHING;
        detectionKind = myDetectionKind;
        myTelemetry = telemetry;
        myDecimation = DEFAULT_DECIMATION;
        initAprilTagTlm(telemetry);
    }
    //for webcamera construtor
    public ObjectDetectionNewTask(Robot robot, String cameraName) {
        super(robot);
        rateLimitMs = 0;
        detectionKind = ObjectDetectionNewTask.DetectionKind.EVERYTHING;
        this.cameraName = cameraName;
        myDecimation = DEFAULT_DECIMATION;
    }


    public TfodProcessor initTfod() {
        TfodProcessor tfodProcessor;
        //Create a TFOD processor by calling build
        myTfodProcessorBuilder = new TfodProcessor.Builder();

        // Optional: set other custom features of the TFOD Processor.
        myTfodProcessorBuilder.setMaxNumRecognitions(10);
        myTfodProcessorBuilder.setUseObjectTracker(true);
        myTfodProcessorBuilder.setTrackerMaxOverlap((float) 0.2);
        myTfodProcessorBuilder.setTrackerMinSize(16);

        // Create a TFOD Processor by calling build()
        tfodProcessor = myTfodProcessorBuilder.build();

        return tfodProcessor;


    }

    public AprilTagProcessor initAprilTag() {
        AprilTagProcessor aprilTagProcessor;
        // Create a new AprilTag Processor Builder object.
        myAprilTagProcessorBuilder = new AprilTagProcessor.Builder();

        // set other cutsom features of the AprilTag Processor
        //myAprilTagProcessorBuilder.setTagLibrary(myAprilTagLibrary); // Optional
        myAprilTagProcessorBuilder.setDrawTagID(true); // Default: true
        myAprilTagProcessorBuilder.setDrawTagOutline(true); // Default: true
        myAprilTagProcessorBuilder.setDrawAxes(true); // Default: false
        myAprilTagProcessorBuilder.setDrawCubeProjection(true); // Default: false


        // Create an AprilTagProcessor by calling build()
        aprilTagProcessor = myAprilTagProcessorBuilder.build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTagProcessor.setDecimation(myDecimation);

        return aprilTagProcessor;

    }

    public void setAprilTagDecimation(float decimation) {
        myDecimation = decimation;
    }

    public void initVisionPortal(HardwareMap hardwareMap,
                                 AprilTagProcessor myAprilTagProcessor,
                                 TfodProcessor myTfodProcessor
    ) {


        // Create a new VisionPortal Builder object.
        myVisionPortalBuilder = new VisionPortal.Builder();

        // Set the camera and Processors. (confirm the camera name on the phone)
        myVisionPortalBuilder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        if (doAprilTags()) {
            myVisionPortalBuilder.addProcessor(myAprilTagProcessor);
        }
        if (doObjDetection()) {
            myVisionPortalBuilder.addProcessor(myTfodProcessor);
        }

        // Optional: set other custom features of the VisionPortal.
        myVisionPortalBuilder.setCameraResolution(new Size(640, 480));
        myVisionPortalBuilder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
        myVisionPortalBuilder.enableLiveView(true);
        myVisionPortalBuilder.setAutoStopLiveView(true);

        // Create the VisionPortal by calling build()
        myVisionPortal = myVisionPortalBuilder.build();

    }

    // if we want to do AprilTags
    public boolean doAprilTags() {
        boolean runAprilTags = false;
        if ((detectionKind == ObjectDetectionNewTask.DetectionKind.EVERYTHING) ||
                (detectionKind == ObjectDetectionNewTask.DetectionKind.APRILTAG_DETECTED)) {
            runAprilTags = true;
        }
        return runAprilTags;
    }

    public boolean doObjDetection() {
        boolean runObjDetection = false;
        if ((detectionKind == ObjectDetectionNewTask.DetectionKind.EVERYTHING) ||
                (detectionKind == ObjectDetectionNewTask.DetectionKind.OBJECT1_DETECTED) ||
                (detectionKind == ObjectDetectionNewTask.DetectionKind.OBJECT2_DETECTED)) {
            runObjDetection = true;
        }
        return runObjDetection;
    }

    public void init(Telemetry telemetry, HardwareMap hardwareMap) {
        TfodProcessor tp = null;
        if (doAprilTags()) {
            // initialize April Tag Processor
            aprilTag = initAprilTag();
        }
        if (doObjDetection()) {
            // initialize TFOD Processor
            tp = initTfod();
        }

        // initalize Vision Portal Processor
        initVisionPortal(hardwareMap, aprilTag, tp);

    }

    public void rateLimit(int ms) {
        this.rateLimitMs = ms;
    }

    public void setDetectionKind(ObjectDetectionNewTask.DetectionKind detectionKind) {
        this.detectionKind = detectionKind;
    }

    @Override
    public void start() {
        if (rateLimitMs != 0) {
            timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        robot.removeTask(this);
        myVisionPortal.stopStreaming();
        myVisionPortal.close();
    }

    public static ObjectDetectionNewTask.ObjectKind isObject(Recognition object) {
        if (object.getLabel().equals(LABEL_OBJECT1)) {
            return ObjectDetectionNewTask.ObjectKind.OBJECT1_KIND;
        } else if (object.getLabel().equals(LABEL_OBJECT2)) {
            return ObjectDetectionNewTask.ObjectKind.OBJECT2_KIND;
        } else {
            return ObjectDetectionNewTask.ObjectKind.UNKNOWN_KIND;
        }
    }

    protected void processEverything(List<Recognition> objects) {
        if (objects.size() > 0) {
            robot.queueEvent(new ObjectDetectionNewTask.ObjectDetectionEvent(this, ObjectDetectionNewTask.EventKind.OBJECTS_DETECTED, objects));
        }
    }

    protected void processObject1(List<Recognition> objects) {
        List<Recognition> objectList = new ArrayList<>();
        for (Recognition object : objects) {
            if (isObject(object) == ObjectDetectionNewTask.ObjectKind.OBJECT1_KIND) {
                objectList.add(object);
            }
        }

        if (!objectList.isEmpty()) {
            robot.queueEvent(new ObjectDetectionNewTask.ObjectDetectionEvent(this, ObjectDetectionNewTask.EventKind.OBJECTS_DETECTED, objectList));
        }
    }

    protected void processDetectedObjects(List<Recognition> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        switch (detectionKind) {
            case EVERYTHING:
                processEverything(objects);
                break;
//           case OBJECT2_DETECTED:
//               processObject2(objects);
//               break;
            case OBJECT1_DETECTED:
                processObject1(objects);
                break;
        }
    }



    public void printAprilTagTlm(AprilTagDetection myDetection) {
        if (myDetection.metadata != null) {
            myTelemetry.addLine(String.format("\n==== (ID %d) %s", myDetection.id, myDetection.metadata.name));
            myTelemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", myDetection.ftcPose.x, myDetection.ftcPose.y, myDetection.ftcPose.z));
            myTelemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", myDetection.ftcPose.pitch, myDetection.ftcPose.roll, myDetection.ftcPose.yaw));
            myTelemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", myDetection.ftcPose.range, myDetection.ftcPose.bearing, myDetection.ftcPose.elevation));
        } else {
            myTelemetry.addLine(String.format("\n==== (ID %d) Unknown", myDetection.id));
            myTelemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", myDetection.center.x, myDetection.center.y));
        }

    }

    public void setDesiredTagID(int myDesiredTagID) {
        desiredTagID = myDesiredTagID;

    }

    protected void processAprilTags() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        //numAprilTagsDetectedTlm.setValue(currentDetections.size());
        myTelemetry.addData("# AprilTags Detected", currentDetections.size());
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                printAprilTagTlm(detection);
                //  Check to see if we want to track towards this tag.
                if ((desiredTagID < 0) || (detection.id == desiredTagID)) {
                    // Yes, we want to use this tag.
                    targetFound = true;
                    myTelemetry.addData("Desired tag detected: ", "true");
                    desiredTag = detection;
                    break;  // don't look any further.
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    myTelemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                    myTelemetry.addData("Desired tag detects: ", "false");
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                myTelemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }   // end for() loop

        if (targetFound) {
            robot.queueEvent(new ObjectDetectionNewTask.TagDetectionEvent(this, EventKind.APRIL_TAG_DETECTED, desiredTag));
        }

        // Add "key" information to telemetry
       // telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        //telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
       // telemetry.addLine("RBE = Range, Bearing & Elevation");

    }

    public void doManualExposure(int exposureMS, int gain) {
        myDoManualExposure = true;
        myExposureMS = exposureMS;
        myGain = gain;
    }

    /*
     * Manually set the camera gain and exposure.
     * This can only be called AFTER calling initAprilTag(), and only works for Webcams;
     */
    public void setManualExposure() {
        // Wait for the camera to be open, then use the controls

        if (myVisionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (myVisionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            myTelemetry.addData("Camera", "Waiting");
            myTelemetry.update();
            return;
        }

        myTelemetry.addData("Camera", "Ready");
        myTelemetry.update();

        // Set camera controls unless we are stopping.
        ExposureControl exposureControl = myVisionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
            //sleep(50);
        }
        exposureControl.setExposure((long)myExposureMS, TimeUnit.MILLISECONDS);
        //sleep(20);
        GainControl gainControl = myVisionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(myGain);
        //sleep(20);
    }

    public void resumeStreaming() {
        myVisionPortal.resumeStreaming();
    }
    public void stopStreaming() {
        myVisionPortal.stopStreaming();
    }

    @Override
    public boolean timeslice()
    {
        //timeslice set to 0 do when it gets called
        if (rateLimitMs != 0) {
            if (timer.time() < rateLimitMs) {
                return false;
            }
        }
        //shows location of object
        //FIXME how to get the recognitions
        if (doObjDetection()) {
            processDetectedObjects(myTfodProcessor.getRecognitions());
        }
        if (doAprilTags()) {
            processAprilTags();
        }

        if (myDoManualExposure) {
            setManualExposure();
        }

        if (rateLimitMs != 0) {
            timer.reset();
        }
        return false;
    }
}
