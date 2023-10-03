//package team25core;
//
//
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ObjectDetectionNewTask extends RobotTask {
//    // create the TensorFlowProcessorBuilder (used for configuration)
//    TfodProcessor.Builder myTfodProcessorBuilder;
//    //create the TensorFlow Object Detection processor
//    TfodProcessor myTfodProcessor;
//    // create the AprilTag processor builder (used for config)
//    AprilTagProcessor.Builder myAprilTagProcessorBuilder;
//    // create the AprilTag processor
//    AprilTagProcessor myAprilTagProcessor;
//    // create the VisionPortal
//    VisionPortal myVisionPortal;
//
//    public static final String LABEL_OBJECT1 = "Prop";
//    public static final String LABEL_OBJECT2 = "Pixel";
//
//    // not sure if this is needed with husky lens
//    //private static final String TFOD_MODEL_ASSET = "FreightFrenzyReg.tflite";
//
//    private String cameraName;
//
//    protected ElapsedTime timer;
//
//
//    private int rateLimitMs;
//
//    public enum DetectionKind {
//        EVERYTHING, //this may go away
//        OBJECT1_DETECTED,
//        OBJECT2_DETECTED,
//        UNKNOWN_DETECTED,
//    }
//
//    public enum ObjectKind {
//        OBJECT2_KIND,
//        OBJECT1_KIND,
//        UNKNOWN_KIND,
//    }
//
//    ;
//
//    private ObjectDetectionTask.DetectionKind detectionKind;
//
//    public enum EventKind {
//        OBJECTS_DETECTED,
//    }
//
//    public class ObjectDetectionEvent extends RobotEvent {
//
//        public ObjectDetectionNewTask.EventKind kind;
//        public List<Recognition> objects;
//
//        //this is constructor for object detection event
//        public ObjectDetectionEvent(RobotTask task, ObjectDetectionTask.EventKind kind, List<Recognition> m) {
//            super(task);
//            this.kind = kind;
//            this.objects = new ArrayList<>(m.size());
//            this.objects.addAll(m);
//        }
//
//        public String toString() {
//            return kind.toString();
//        }
//    }
//
//    //---------------------------------------------------------
//    //constructor
//    public ObjectDetectionNewTask(Robot robot) {
//        super(robot);
//        rateLimitMs = 0;
//        //FIXME figure out what kind of detection we want it to be
//        detectionKind = ObjectDetectionNewTask.DetectionKind.EVERYTHING;
//
//    }
//
//    //for webcamera construtor
//    public ObjectDetectionNewTask(Robot robot, String cameraName) {
//        super(robot);
//        rateLimitMs = 0;
//        detectionKind = ObjectDetectionNewTask.DetectionKind.EVERYTHING;
//        this.cameraName = cameraName;
//    }
//
//    public class ObjectDetectionEvent extends RobotEvent {
//
//        public ObjectDetectionTask.EventKind kind;
//        public List<Recognition> objects;
//
//        //this is constructor for object detection event
//        public ObjectDetectionEvent(RobotTask task, ObjectDetectionTask.EventKind kind, List<Recognition> m) {
//            super(task);
//            this.kind = kind;
//            this.objects = new ArrayList<>(m.size());
//            this.objects.addAll(m);
//        }
//
//        public String toString() {
//            return kind.toString();
//        }
//    }
//
//
//    public TfodProcessor initTfod() {
//        TfodProcessor
//        TfodProcessor tfodProcessor;
//        //Create a TFOD processor by calling build
//        myTfodProcessorBuilder = new Tfodprocessor.Builder();
//
//        // Optional: set other custom features of the TFOD Processor.
//        myTfodProcessorBuilder.setMaxNumRecognitions(10);
//        myTfodProcessorBuilder.setUseObjectTracker(true);
//        myTfodProcessorBuilder.setTrackerMaxOverlap((float) 0.2);
//        myTfodProcessorBuilder.setTrackerMinSize(16);
//
//        // Create a TFOD Processor by calling build()
//        tfodProcessor = myTfodProcessorBuilder.build();
//
//        return tfodProcessor;
//
//
//    }
//
//    public AprilTagProcessor initAprilTag() {
//        AprilTagProcessor aprilTagProcessor;
//        // Create a new AprilTag Processor Builder object.
//        myAprilTagProcessorBuilder = new AprilTagProcessor.Builder();
//        myAprilTagProcessorBuilder.setTagLibrary(myAprilTagLibrary);
//
//        // set other cutsom features of the AprilTag Processor
//        myAprilTagProcessorBuilder.setTagLibrary(myAprilTagLibrary); // Optional
//        myAprilTagProcessorBuilder.setDrawTagID(true); // Default: true
//        myAprilTagProcessorBuilder.setDrawTagOutline(true); // Default: true
//        myAprilTagProcessorBuilder.setDrawAxes(true); // Default: false
//        myAprilTagProcessorBuilder.setDrawCubeProjection(true); // Default: false
//
//        // Create an AprilTagProcessor by calling build()
//        aprilTagProcessor = myAprilTagProcessorBuilder.build();
//
//        return aprilTagProcessor;
//
//
//    }
//
//    public void initVisionPortal(HardwareMap hardwareMap,
//                                 AprilTagProcessor myAprilTagProcessor,
//                                 VisionPortal myVisionPortal
//    ) {
//
//
//        // Create a new VisionPortal Builder object.
//        myVisionPortalBuilder = new VisionPortal.Builder();
//        VisionPortal myVisionPortal;
//
//        // Set the camera and Processors. (confirm the camera name on the phone)
//        myVisionPortalBuilder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
//        myVisionPortalBuilder.addProcessor(myAprilTagProcessor);
//        myVisionPortalBuilder.addProcessor(myTfodProcessor);
//
//        // Optional: set other custom features of the VisionPortal.
//        myVisionPortalBuilder.setCameraResolution(new Size(640, 480));
//        myVisionPortalBuilder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
//        myVisionPortalBuilder.enableLiveView(true);
//        myVisionPortalBuilder.setAutoStopLiveView(true);
//
//        // Create the VisionPortal by calling build()
//        myVisionPortal = myVisionPortalBuilder.build();
//
//    }
//
//    public void init(Telemetry telemetry, HardwareMap hardwareMap) {
//        TfodProcessor tp;
//        AprilTagProcessor ap;
//
//        // initialize TFOD Processor
//        tp = initTfod();
//        // initialize April Tag Processor
//        ap = initAprilTag();
//        // initalize Vision Portal Processor
//        initVisionPortal(hardwareMap, ap, tp);
//
//    }
//
//    public void rateLimit(int ms) {
//        this.rateLimitMs = ms;
//    }
//
//    public void setDetectionKind(ObjectDetectionTask.DetectionKind detectionKind) {
//        this.detectionKind = detectionKind;
//    }
//
//    @Override
//    public void start() {
//
//        if (rateLimitMs != 0) {
//            timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
//        }
//    }
//
//    @Override
//    public void stop() {
//        robot.removeTask(this);
//    }
//
//    public static ObjectDetectionTask.ObjectKind isObject(Recognition object) {
//        if (object.getLabel().equals(LABEL_OBJECT1)) {
//            return ObjectDetectionTask.ObjectKind.OBJECT1_KIND;
//        } else if (object.getLabel().equals(LABEL_OBJECT2)) {
//            return ObjectDetectionTask.ObjectKind.OBJECT2_KIND;
//        } else {
//            return ObjectDetectionTask.ObjectKind.UNKNOWN_KIND;
//        }
//    }
//
//    protected void processEverything(List<Recognition> objects) {
//        if (objects.size() > 0) {
//            robot.queueEvent(new ObjectDetectionNewTask.ObjectDetectionEvent(this, ObjectDetectionTask.EventKind.OBJECTS_DETECTED, objects));
//        }
//    }
//
//    protected void processObject1(List<Recognition> objects) {
//        List<Recognition> objectList = new ArrayList<>();
//        for (Recognition object : objects) {
//            if (isObject(object) == ObjectDetectionTask.ObjectKind.OBJECT1_KIND) {
//                objectList.add(object);
//            }
//        }
//
//        if (!objectList.isEmpty()) {
//            robot.queueEvent(new ObjectDetectionNewTask.ObjectDetectionEvent(this, ObjectDetectionTask.EventKind.OBJECTS_DETECTED, objectList));
//        }
//    }
//
//    protected void processDetectedObjects(List<Recognition> objects) {
//        if (objects == null || objects.isEmpty()) {
//            return;
//        }
//        switch (detectionKind) {
//            case EVERYTHING:
//                processEverything(objects);
//                break;
////           case OBJECT2_DETECTED:
////               processObject2(objects);
////               break;
//            case OBJECT1_DETECTED:
//                processObject1(objects);
//                break;
//        }
//    }
//    @Override
//    public boolean timeslice()
//    {
//        //timeslice set to 0 do when it gets called
//        if (rateLimitMs != 0) {
//            if (timer.time() < rateLimitMs) {
//                return false;
//            }
//        }
//        //shows location of object
//        //FIXME how to get the recognitions
//        processDetectedObjects(tfod.getUpdatedRecognitions());
//
//        if (rateLimitMs != 0) {
//            timer.reset();
//        }
//        return false;
//    }
//
//
//}
