//package team25core;
//
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.vuforia.PIXEL_FORMAT;
//import com.vuforia.Vuforia;
//
//import org.firstinspires.ftc.robotcore.external.ClassFactory;
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
//import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
//import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import team25core.vision.vuforia.VuforiaConstants;
//
//
//public class ObjectDetectionTask extends RobotTask {
//
//    public enum EventKind {
//        OBJECTS_DETECTED,
//    }
//
//    protected ElapsedTime timer;
//
//    public class ObjectDetectionEvent extends RobotEvent {
//
//        public EventKind kind;
//        public List<Recognition> objects;
//
//        //this is constructor for object detection event
//        public ObjectDetectionEvent(RobotTask task, EventKind kind, List<Recognition> m)
//        {
//            super(task);
//            this.kind = kind;
//            this.objects = new ArrayList<>(m.size());
//            this.objects.addAll(m);
//        }
//
//        public String toString()
//        {
//            return kind.toString();
//        }
//    }
//
//    private VuforiaLocalizer vuforia;
//    private Telemetry telemetry;
//    private TFObjectDetector tfod;
//
//    public static final String LABEL_OBJECT1 = "Element";
//    private static final String TFOD_MODEL_ASSET = "FreightFrenzyReg.tflite";
//    private int rateLimitMs;
//    private DetectionKind detectionKind;
//    private String cameraName;
//
//    public enum DetectionKind {
//        EVERYTHING, //this may go away
//        OBJECT1_DETECTED,
//        OBJECT2_DETECTED,
//        LARGEST_SKY_STONE_DETECTED, //this may go away
//        UNKNOWN_DETECTED,
//    }
//    public enum ObjectKind {
//        OBJECT2_KIND,
//        OBJECT1_KIND,
//        UNKNOWN_KIND,
//    };
//
//
//    //for phone camera constructor
//    public ObjectDetectionTask(Robot robot)
//    {
//        super(robot);
//
//        rateLimitMs = 0;
//        detectionKind = DetectionKind.EVERYTHING;
//    }
//    //for webcamera construtor
//    public ObjectDetectionTask(Robot robot, String cameraName)
//    {
//        super(robot);
//        rateLimitMs = 0;
//        detectionKind = DetectionKind.EVERYTHING;
//        this.cameraName = cameraName;
//    }
//
//    private void initVuforia(HardwareMap hardwareMap) {
//        /*
//         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
//         */
//        //new=your own copy of vuforia parameters
//        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
//        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
//        //if webcam
//        if (cameraName != null) {
//            parameters.vuforiaLicenseKey = VuforiaConstants.WEBCAM_VUFORIA_KEY;
//            parameters.cameraName = hardwareMap.get(WebcamName.class, cameraName);
//
//        } else {   // if phonecam
//            parameters.vuforiaLicenseKey = VuforiaConstants.VUFORIA_KEY;
//        }
//
//        //  Instantiate the Vuforia engine
//        vuforia = ClassFactory.getInstance().createVuforia(parameters);
//        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565,true);
//        vuforia.setFrameQueueCapacity(1);
//
//        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
//    }
//
//    private void initTfod(HardwareMap hardwareMap)
//    {
//        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
//                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
//        tfodParameters.minResultConfidence = 0.8f; //the example in the Ultimate Goal Tensor flow example defaults to a MinimumConfidence of 0.8f
//        tfodParameters.isModelTensorFlow2 = true;
//        tfodParameters.inputSize = 320;
//        tfodParameters.useObjectTracker = false;
//
//        //concept tensor flow object detection had minimum confidence
//        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
//        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_OBJECT1);
//    }
//
//    public void init(Telemetry telemetry, HardwareMap hardwareMap)
//    {
//        initVuforia(hardwareMap);
//
//        // if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
//        initTfod(hardwareMap);
////        } else {
////            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
////        }
//    }
//
//    public void rateLimit(int ms)
//    {
//        this.rateLimitMs = ms;
//    }
//
//    public void setDetectionKind(DetectionKind detectionKind)
//    {
//        this.detectionKind = detectionKind;
//    }
//    //this will start tfod activation and start detecting
//    @Override
//    public void start()
//    {
//        tfod.activate();
//
//        if (rateLimitMs != 0) {
//            timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
//        }
//    }
//
//    @Override
//    public void stop()
//    {
//        tfod.deactivate();
//        robot.removeTask(this);
//    }
//
//    public static ObjectKind isObject(Recognition object)
//    {
//        if (object.getLabel().equals(LABEL_OBJECT1)) {
//            return ObjectKind.OBJECT1_KIND;
//        } else {
//            return ObjectKind.UNKNOWN_KIND;
//        }
//    }
//    //if recognize anything will add to que
//    protected void processEverything(List<Recognition> objects)
//    {
//        if (objects.size() > 0) {
//            robot.queueEvent(new ObjectDetectionEvent(this, EventKind.OBJECTS_DETECTED, objects));
//        }
//    }
//    //only adds objects which will make event and add to que
//    protected void processObject1(List<Recognition> objects)
//    {
//        List<Recognition> objectList = new ArrayList<>();
//        for (Recognition object : objects) {
//            if (isObject(object) == ObjectKind.OBJECT1_KIND){
//                objectList.add(object);
//            }
//        }
//
//        if (!objectList.isEmpty()) {
//            robot.queueEvent(new ObjectDetectionEvent(this, EventKind.OBJECTS_DETECTED, objectList));
//        }
//    }
//
////    protected void processObject2(List<Recognition> objects)
////    {
////        List<Recognition> objectList = new ArrayList<>();
////        for (Recognition object : objects) {
////            if (isObject(object) == ObjectKind.OBJECT2_KIND) {
////                objectList.add(object);
////            }
////        }
////
////        if (!objectList.isEmpty()) {
////            robot.queueEvent(new ObjectDetectionEvent(this, EventKind.OBJECTS_DETECTED, objectList));
////        }
////    }
//
//    //timeslice calls to get information from recognition
//    protected void processDetectedObjects(List<Recognition> objects)
//    {
//        if (objects == null || objects.isEmpty()) {
//            return;
//        }
//
//        switch (detectionKind) {
//            case EVERYTHING:
//                processEverything(objects);
//                break;
////            case OBJECT2_DETECTED:
////                processObject2(objects);
////                break;
//            case OBJECT1_DETECTED:
//                processObject1(objects);
//                break;
//        }
//    }
//
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
//        processDetectedObjects(tfod.getUpdatedRecognitions());
//
//        if (rateLimitMs != 0) {
//            timer.reset();
//        }
//        return false;
//    }
//}