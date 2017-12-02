package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.Robot;
import team25core.RobotEvent;
import team25core.VuforiaLCRTask;

/**
 * Created by admin on 11/30/2017.
 */

@TeleOp(name="TEST ViewMarks")
public class VuforiaLCRTest extends Robot {

    VuforiaLCRTask vuforia;
    Telemetry.Item viewMark;
    boolean flashOn = false;

    @Override
    public void handleEvent(RobotEvent e)
    {
        RobotLog.i("View mark:adb " + e.toString());
        viewMark.setValue(e.toString());
    }

    @Override
    public void init()
    {
        telemetry.setAutoClear(false);
        viewMark = telemetry.addData("View mark: ", "No data");

        vuforia = new VuforiaLCRTask(this);
    }

    @Override
    public void start()
    {
        addTask(vuforia);
    }
}
