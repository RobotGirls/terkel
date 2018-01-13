package examples;
/*
 * FTC Team 25: Created by Elizabeth Wu on December 12, 2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import team25core.ColorThiefTask;
import team25core.GamepadTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.VuMarkIdentificationTask;
import team25core.VuforiaBase;
import team25core.VuforiaLocalizerCustom;

@Autonomous(name = "VuMark Detection", group = "Test")
public class VuMarkDetectionExample extends Robot {

    VuMarkIdentificationTask vmIdTask;
    VuforiaBase vuforiaBase;

    private ColorThiefTask colorThiefTask;

    boolean pollOn = false;

    @Override
    public void handleEvent(RobotEvent e)
    {
        RobotLog.i("VuMark: Detected" + e.toString());

    }

    @Override
    public void init()
    {
        RobotLog.i("506 Init Started");

        vuforiaBase = new VuforiaBase();
        vuforiaBase.init(this);

        vmIdTask = new VuMarkIdentificationTask(this, vuforiaBase);
        this.addTask(vmIdTask);
        RobotLog.i("506 added VuMark ID task");

        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
            @Override
            public void handleEvent(RobotEvent e)
            {
                GamepadEvent gamepadEvent = (GamepadEvent)e;
                switch (gamepadEvent.kind) {
                    case BUTTON_A_DOWN:
                        togglePolling();
                        break;
                    default:
                        break;
                }
            }
        });

        RobotLog.i("506 Init Finished");
    }

    private void togglePolling() {
        RobotLog.i("506 Polling toggled");
        if (pollOn == false) {
            vmIdTask.setPollingMode(VuMarkIdentificationTask.PollingMode.ON);
            RobotLog.i("506 Polling ON");
            pollOn = true;

        } else {
            vmIdTask.setPollingMode(VuMarkIdentificationTask.PollingMode.OFF);
            RobotLog.i("506 Polling OFF");
            pollOn = false;
        }
    }

    @Override
    public void start()
    {

    }

}

