package team25core.sensors.color;

import com.qualcomm.robotcore.hardware.ColorSensor;

import com.qualcomm.robotcore.hardware.DcMotor;

import team25core.Robot;
import team25core.RobotEvent;
import team25core.RobotEventListener;
import team25core.RobotTask;

import team25core.sensors.color.RGBColorSensorTask;

public class RGBColorSensorMotorTask extends RGBColorSensorTask {

    protected int targetBlueEncoderValue = 0;
    protected int targetRedEncoderValue = 0;
    protected int targetGreenEncoderValue = 0;
    protected ColorSensor colorSensor;
    protected double MotorSpeed = 1.0;
    protected boolean reverseMotor = false;
    protected boolean blueBool = false;
    protected boolean redBool = false;
    protected boolean greenBool = false;
    protected boolean blueDetected = false;
    protected boolean redDetected = false;
    protected boolean greenDetected = false;

    protected int hardStopTopValue = 0;

    protected RGBColorSensorTask colorSensorTask;

    private DcMotor RGBMotor;
    protected Robot robot;

    public enum EventKind {
        RED_DETECTED,
        BLUE_DETECTED,
        GREEN_DETECTED,
        NO_COLOR_DETECTED,
        GOT_TO_RED,
        GOT_TO_BLUE,
        GOT_TO_GREEN,
    }

    public class ColorSensorMotorEvent extends RobotEvent
    {
        public RGBColorSensorMotorTask.EventKind kind;

        public ColorSensorMotorEvent(RobotTask task, RGBColorSensorMotorTask.EventKind kind)
        {
            super(task);
            this.kind = kind;
        }
    }

    public RGBColorSensorMotorTask(Robot robot, ColorSensor colorSensor, DcMotor RGBMotor) {
        super(robot, colorSensor);
        this.colorSensor = colorSensor;
        this.RGBMotor = RGBMotor;
        this.robot = robot;
    }

    public void setMotorSpeed(double MotorSpeed) {
        this.MotorSpeed = MotorSpeed;
    }

    public void setReverseMotor(boolean reverseMotor) {
        this.reverseMotor = reverseMotor;
    }

    public void setTargetEncoderValues(int targetBlueEncoderValue, int targetRedEncoderValue, int targetGreenEncoderValue) {
        this.targetBlueEncoderValue = targetBlueEncoderValue;
        this.targetRedEncoderValue = targetRedEncoderValue;
        this.targetGreenEncoderValue = targetGreenEncoderValue;
    }

    public void setHardStopTopValue (int hardStopTopValue) {
        this.hardStopTopValue = hardStopTopValue;
    }

    public void gotoBlue() {
        // function to go to blue position
        if (RGBMotor.getCurrentPosition() < targetBlueEncoderValue && reverseMotor == false) {
            //set motor up if current encoder position is less than blue encoder position
            RGBMotor.setPower(MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() > targetBlueEncoderValue && reverseMotor == false) {
            //set motor down if current encoder position is greater than blue encoder position
            RGBMotor.setPower(-MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() < targetBlueEncoderValue && reverseMotor == true) {
            //set motor down
            RGBMotor.setPower(-MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() > targetBlueEncoderValue && reverseMotor == true) {
            //set motor up
            RGBMotor.setPower(+MotorSpeed);
        }
        else {
            // set motor off if motor is at blue position
            RGBMotor.setPower(0);
        }
        blueBool = true; // flag for timeslice
        //  RGBMotor.setPower(0);
        //  this.robot.removeTask(colorSensorTask);
    }

    public void gotoGreen() {
        if (RGBMotor.getCurrentPosition() < targetGreenEncoderValue && reverseMotor == false) {
            //set motor up
            RGBMotor.setPower(MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() > targetGreenEncoderValue && reverseMotor == false) {
            //set motor down
            RGBMotor.setPower(-MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() < targetGreenEncoderValue && reverseMotor == true) {
            //set motor down
            RGBMotor.setPower(-MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() > targetGreenEncoderValue && reverseMotor == true) {
            //set motor up
            RGBMotor.setPower(+MotorSpeed);
        }
        else {
            // set motor off
            RGBMotor.setPower(0);
        }
        greenBool = true; // flag for timeslice
        //  RGBMotor.setPower(0);
        //  this.robot.removeTask(colorSensorTask);
    }

    public void gotoRed() {
        if (RGBMotor.getCurrentPosition() < targetRedEncoderValue && reverseMotor == false) {
            //set motor up
            RGBMotor.setPower(MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() > targetRedEncoderValue && reverseMotor == false) {
            //set motor down
            RGBMotor.setPower(-MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() < targetRedEncoderValue && reverseMotor == true) {
            //set motor down
            RGBMotor.setPower(-MotorSpeed);
        }
        else if (RGBMotor.getCurrentPosition() > targetRedEncoderValue && reverseMotor == true) {
            //set motor up
            RGBMotor.setPower(+MotorSpeed);
        }
        else {
            // set motor off
            RGBMotor.setPower(0);
        }
        redBool = true; // flag for timeslice
        //  RGBMotor.setPower(0);
        //  this.robot.removeTask(colorSensorTask);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean timeslice() {
        ColorSensorMotorEvent event;
        // colorSensor.*** means it returns the value of a color as an integer
        // compares the values of red blue and green. The color with the highest value than the other
        // two colors and is less than it's color's threshold, then that color is returned

        if (colorSensor.red() > colorSensor.green() && colorSensor.red() > colorSensor.blue() && colorSensor.red() < redthreshold) {
            event = new ColorSensorMotorEvent(this, EventKind.RED_DETECTED);
            robot.queueEvent(event);
            // setting redDetected boolean as true and others as false
            redDetected = true;
            blueDetected = false;
            greenDetected = false;
        } else if (colorSensor.blue() > colorSensor.green() && colorSensor.blue() > colorSensor.red() && colorSensor.blue() < bluethreshold){
            event = new ColorSensorMotorEvent(this, EventKind.BLUE_DETECTED);
            robot.queueEvent(event);
            // setting blueDetected boolean as true and others as false
            redDetected = false;
            blueDetected = true;
            greenDetected = false;
        } else if (colorSensor.green() > colorSensor.red() && colorSensor.green() > colorSensor.blue() && colorSensor.green() < greenthreshold){
            event = new ColorSensorMotorEvent(this, EventKind.GREEN_DETECTED);
            robot.queueEvent(event);
            // setting greenDetected boolean as true and others as false
            redDetected = false;
            blueDetected = false;
            greenDetected = true;
        } else {
            event = new ColorSensorMotorEvent(this, EventKind.NO_COLOR_DETECTED);
            robot.queueEvent(event);
            // setting all color detected booleans as false if no color is detected
            redDetected = false;
            blueDetected = false;
            greenDetected = false;

        }
        // stopping motor if lift reaches color level
        if (blueBool == true) {
            // if gotoBlue is running and blue tape is detected, set motor power to 0
            if (blueDetected == true) {
                RGBMotor.setPower(0);
                blueBool = false;
                event = new ColorSensorMotorEvent(this, EventKind.GOT_TO_BLUE);
                robot.queueEvent(event);
            }
        }
        if (greenBool == true) {
            // if gotoGreen is running and green tape is detected, set motor power to 0
            if (greenDetected == true) {
                RGBMotor.setPower(0);
                greenBool = false;
                event = new ColorSensorMotorEvent(this, EventKind.GOT_TO_GREEN);
                robot.queueEvent(event);
            }
        }
        if (redBool == true) {
            // if gotoRed is running and red tape is detected, set motor power to 0
            if (redDetected == true) {
                RGBMotor.setPower(0);
                redBool = false;
                event = new ColorSensorMotorEvent(this, EventKind.GOT_TO_RED);
                robot.queueEvent(event);
            }
        }
        if (RGBMotor.getCurrentPosition() >= hardStopTopValue) {
            RGBMotor.setPower(0);
        }
        return false;
    }
}