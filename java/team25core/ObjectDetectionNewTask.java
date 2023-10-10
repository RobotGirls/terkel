package team25core;


import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionNewTask extends RobotTask {
    // create the TensorFlowProcessorBuilder (used for configuration)
    TfodProcessor.Builder myTfodProcessorBuilder;
    //create the TensorFlow Object Detection processor
    TfodProcessor myTfodProcessor;
    // create the AprilTag processor builder (used for config)
    AprilTagProcessor.Builder myAprilTagProcessorBuilder;
    // create the AprilTag processor
    AprilTagProcessor myAprilTagProcessor;
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

    public enum DetectionKind {
        EVERYTHING, //this may go away
        OBJECT1_DETECTED,
        OBJECT2_DETECTED,
        UNKNOWN_DETECTED,
    }

    public enum ObjectKind {
        OBJECT2_KIND,
        OBJECT1_KIND,
        UNKNOWN_KIND,
    }

    ;

    private ObjectDetectionNewTask.DetectionKind detectionKind;

    public enum EventKind {
        OBJECTS_DETECTED,
    }

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


    public void initAprilTagTlm(Telemetry telemetry) {
        numAprilTagsDetectedTlm = myTelemetry.addData("numAprilTagsDetected: ","none");
        aprilTagIdTlm = myTelemetry.addData("aprilTagId: ","none");
        aprilTagNameTlm = myTelemetry.addData("aprilTagName: ","none");
        aprilTagPoseXTlm = myTelemetry.addData("aprilTagPoseX: ","none");
        aprilTagPoseYTlm = myTelemetry.addData("aprilTagPoseY: ","none");
        aprilTagPoseZTlm = myTelemetry.addData("aprilTagPoseZ: ","none");
        aprilTagYawTlm = myTelemetry.addData("aprilTagYaw: ","none");
        aprilTagPitchTlm = myTelemetry.addData("aprilTagPitch: ","none");
        aprilTagRollTlm = myTelemetry.addData("aprilTagRoll: ","none");
        aprilTagPoseRangeTlm = myTelemetry.addData("aprilTagPoseRange: ","none");
        aprilTagPoseBearingTlm = myTelemetry.addData("aprilTagPoseBearing: ","none");
        aprilTagPoseElevationTlm = myTelemetry.addData("aprilTagPoseElevation: ","none");
        aprilTagCenterXTlm = myTelemetry.addData("aprilTagCenterX: ","none");
        aprilTagCenterYTlm = myTelemetry.addData("aprilTagCenterY: ","none");
    }

    //---------------------------------------------------------
    //constructor
    public ObjectDetectionNewTask(Robot robot, Telemetry telemetry) {
        super(robot);
        rateLimitMs = 0;
        //FIXME figure out what kind of detection we want it to be
        detectionKind = ObjectDetectionNewTask.DetectionKind.EVERYTHING;

        initAprilTagTlm(telemetry);
    }
    //for webcamera construtor
    public ObjectDetectionNewTask(Robot robot, String cameraName) {
        super(robot);
        rateLimitMs = 0;
        detectionKind = ObjectDetectionNewTask.DetectionKind.EVERYTHING;
        this.cameraName = cameraName;
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

        return aprilTagProcessor;


    }

    public void initVisionPortal(HardwareMap hardwareMap,
                                 AprilTagProcessor myAprilTagProcessor,
                                 TfodProcessor myTfodProcessor
    ) {


        // Create a new VisionPortal Builder object.
        myVisionPortalBuilder = new VisionPortal.Builder();

        // Set the camera and Processors. (confirm the camera name on the phone)
        myVisionPortalBuilder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        myVisionPortalBuilder.addProcessor(myAprilTagProcessor);
        myVisionPortalBuilder.addProcessor(myTfodProcessor);

        // Optional: set other custom features of the VisionPortal.
        myVisionPortalBuilder.setCameraResolution(new Size(640, 480));
        myVisionPortalBuilder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
        myVisionPortalBuilder.enableLiveView(true);
        myVisionPortalBuilder.setAutoStopLiveView(true);

        // Create the VisionPortal by calling build()
        myVisionPortal = myVisionPortalBuilder.build();

    }

    public void init(Telemetry telemetry, HardwareMap hardwareMap) {
        TfodProcessor tp;
        AprilTagProcessor ap;

        // initialize TFOD Processor
        tp = initTfod();
        // initialize April Tag Processor
        ap = initAprilTag();
        // initalize Vision Portal Processor
        initVisionPortal(hardwareMap, ap, tp);

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

    protected void processAprilTags() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        numAprilTagsDetectedTlm.setValue(currentDetections.size());
        for (AprilTagDetection detection : currentDetections) {

            if (detection.metadata != null) {
                aprilTagIdTlm.setValue(detection.id);
                aprilTagNameTlm.setValue(detection.metadata.name);
                aprilTagPoseXTlm.setValue(detection.ftcPose.x);
                aprilTagPoseYTlm.setValue(detection.ftcPose.y);
                aprilTagPoseYTlm.setValue(detection.ftcPose.z);
                aprilTagYawTlm.setValue(detection.ftcPose.yaw);
                aprilTagPitchTlm.setValue(detection.ftcPose.pitch);
                aprilTagRollTlm.setValue(detection.ftcPose.roll);
                aprilTagPoseRangeTlm.setValue(detection.ftcPose.range);
                aprilTagPoseBearingTlm.setValue(detection.ftcPose.bearing);
                aprilTagPoseElevationTlm.setValue(detection.ftcPose.elevation);
                aprilTagCenterXTlm.setValue(detection.center.x);
                aprilTagCenterYTlm.setValue(detection.center.y);
            } else {
                //telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                //telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

        // Add "key" information to telemetry
       // telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        //telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
       // telemetry.addLine("RBE = Range, Bearing & Elevation");

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
        processDetectedObjects(myTfodProcessor.getRecognitions());
        processAprilTags();
        if (rateLimitMs != 0) {
            timer.reset();
        }
        return false;
    }


}
