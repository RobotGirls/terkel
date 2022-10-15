package team25core;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ObjectImageInfo {
    private double confidence;
    private double left;
    private String objectDetectionType;
    private String objectType;
    private ObjectDetectionTask.EventKind objectKind;
    private double type;
    private double imageMidpoint;
    private double objectMidpoint;
    private double delta;
    private double margin = 100;
    private double setColor;
    private double width;
    private int imageWidth;
    private boolean inCenter;
    private double realNumPixelsPerInch;
    private final int DISTANCE_FROM_WEBCAM_TO_GRABBER =1;
    private double distance;

    private int numObjectsSeen;
    private Telemetry.Item currLocationTlm;
    private Telemetry.Item objectPositionTlm;
    private Telemetry.Item objectTlm;
    private Telemetry.Item objectConfidTlm;
    private Telemetry.Item objectConfidTlm2;
    private Telemetry.Item objectTypeTlm;
    private Telemetry.Item objectTypeTlm2;
    private Telemetry.Item objectMidpointTlm;
    private Telemetry.Item imageMidpointTlm;
    private Telemetry.Item deltaTlm;
    private Telemetry.Item numObjectsSeenTlm;
    private Telemetry.Item pathTlm;
    private Telemetry.Item widthTlm;
    private Telemetry.Item marginTlm;
    private Telemetry.Item imageWidthTlm;
    private Telemetry.Item pixelsPerInchTlm;
    private Telemetry.Item distanceBtWWebcamAndGrabberTlm;

    //this is the default constructor
    public ObjectImageInfo() {}

    public double getConfidence(){
        return confidence;
    }

    public String getObjDetectionType() { return objectDetectionType; }

    public String getObjectType(){
        return objectType;
    }

    public void getImageInfo(ObjectDetectionTask.ObjectDetectionEvent event) {
        //confidence is the likelihood that the object we detect is an object in percentage
        //get(0) = gets the first item in the list of recognition objects pointed to by objects.
        // objects = a variable in the object detection event
        confidence = event.objects.get(0).getConfidence();
        objectConfidTlm.setValue(confidence);
        //left is the left coordinate of the object(s)
        left = event.objects.get(0).getLeft();

        objectType = event.objects.get(0).getLabel(); //LABEL_OBJECT1 LABEL_OBJECT2
        objectTypeTlm.setValue(objectType);

        objectKind = event.kind; //OBJECTS_DETECTED

        numObjectsSeen = event.objects.size();
        numObjectsSeenTlm.setValue(numObjectsSeen);
        currLocationTlm.setValue("in getImageInfo");
        if (numObjectsSeen > 1){
            confidence = event.objects.get(1).getConfidence();
            objectConfidTlm2.setValue(confidence);
            objectType = event.objects.get(1).getLabel(); //LABEL_OBJECT1 LABEL_OBJECT2
            objectTypeTlm2.setValue(objectType);
        }
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.setAutoClear(false);
        //caption: what appears on the phone
        objectPositionTlm = telemetry.addData("LeftOrigin", "unknown");
        objectConfidTlm = telemetry.addData("Confidence", "N/A");
        objectConfidTlm2 = telemetry.addData("Confidence2", "N/A");
        objectTypeTlm = telemetry.addData("ObjectType","unknown");
        objectTypeTlm2 = telemetry.addData("ObjectType2","unknown");
        imageMidpointTlm = telemetry.addData("Image_Mdpt", "unknown");
        objectMidpointTlm = telemetry.addData("Object Mdpt", "unknown");
        objectTlm = telemetry.addData("kind", "unknown");
        deltaTlm = telemetry.addData("delta", "unknown");
        numObjectsSeenTlm = telemetry.addData("numObjects",-1);
        pathTlm = telemetry.addData("AllianceClr", "unknown");
        widthTlm = telemetry.addData("ObjectWidth", "unknown");
        imageWidthTlm = telemetry.addData("imageWidth", -1);
        marginTlm = telemetry.addData("margin" , "unknown");
        pixelsPerInchTlm = telemetry.addData("pixelsPerInch", "unknown");
        distanceBtWWebcamAndGrabberTlm = telemetry.addData("distance BtW Webcam and Grabber","unknown");
        currLocationTlm = telemetry.addData("curr location", "in ObjectImageInfo constructor");
    }
    //CONTINUE HERE ********
    //ADD METHOD TO RETURN THE OBJECT TYPE

}
