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

    protected RGBColorSensorTask colorSensorTask;

    private DcMotor RGBMotor;
    protected Robot robot;

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
        colorSensorTask = new RGBColorSensorTask(this.robot, colorSensor) {
            public void handleEvent(RobotEvent e) {
                RGBColorSensorTask.ColorSensorEvent event = (RGBColorSensorTask.ColorSensorEvent) e;
                while(event.kind != event.kind.BLUE_DETECTED) {
                }
                RGBMotor.setPower(0);
            }
        };
        this.robot.addTask(colorSensorTask);
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
        colorSensorTask = new RGBColorSensorTask(this.robot, colorSensor) {
            public void handleEvent(RobotEvent e) {
                RGBColorSensorTask.ColorSensorEvent event = (RGBColorSensorTask.ColorSensorEvent) e;
                while(event.kind != event.kind.GREEN_DETECTED) {
                }
                RGBMotor.setPower(0);
            }
        };
        this.robot.addTask(colorSensorTask);
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
        colorSensorTask = new RGBColorSensorTask(this.robot, colorSensor) {
            public void handleEvent(RobotEvent e) {
                RGBColorSensorTask.ColorSensorEvent event = (RGBColorSensorTask.ColorSensorEvent) e;
                while(event.kind != event.kind.RED_DETECTED) {
                }
                RGBMotor.setPower(0);
            }
        };
        this.robot.addTask(colorSensorTask);
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
        return false;
    }
}