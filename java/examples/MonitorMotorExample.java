package examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import team25core.MonitorMotorTask;
import team25core.Robot;
import team25core.RobotEvent;

/**
 * Created by Lizzie on 9/26/2017.
 */

@Autonomous(name = "Monitor Motor Example")
@Disabled
public class MonitorMotorExample extends Robot {
    private DcMotor left;

    @Override
    public void handleEvent(RobotEvent e) {

    }

    @Override
    public void init() {
        left = hardwareMap.get(DcMotor.class, "right");
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void start() {
        left.setPower(.5);
        this.addTask(new MonitorMotorTask(this, left, MonitorMotorTask.MotorKind.ANDYMARK_40, (char)((char)MonitorMotorTask.LOG_POSITION | MonitorMotorTask.DISPLAY_POSITION)));
    }

}
