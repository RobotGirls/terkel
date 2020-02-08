package examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import team25core.Robot;
import team25core.RobotEvent;

/**
 * Created by Lizzie on 9/26/2017.
 */

@Autonomous(name = "Monitor Motor Example")
//Disabled
public class MonitorMotorExample extends Robot {

    private DcMotor frontLeft;
    private DcMotor rearLeft;
    private DcMotor rearRight;
    private DcMotor frontRight;

    @Override
    public void handleEvent(RobotEvent e) {

    }

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontleft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        rearRight = hardwareMap.get(DcMotor.class, "backRight");
        rearLeft = hardwareMap.get(DcMotor.class, "backleft");

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void start() {
        frontLeft.setPower(.3);
        frontRight.setPower(.3);
        rearLeft.setPower(.3);
        rearRight.setPower(.3);
    }

}
