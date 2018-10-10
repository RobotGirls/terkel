package examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.ColorThiefTask;
import team25core.GamepadTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.VuforiaBase;

@Autonomous(name = "VuforiaParticleDetection")
@Disabled
public class VuforiaParticleDetectionExample extends Robot {

    Telemetry.Item particle;
    ColorThiefTask colorThiefTask;
    VuforiaBase vuforiaBase;

    boolean flashOn = false;

    @Override
    public void handleEvent(RobotEvent e)
    {
        RobotLog.i("Jewel: Detected " + e.toString());
        particle.setValue(e.toString());
    }

    @Override
    public void init()
    {
        vuforiaBase = new VuforiaBase();
        vuforiaBase.init(this);

        telemetry.setAutoClear(false);
        particle = telemetry.addData("Particle: ", "No data");
        colorThiefTask = new ColorThiefTask(this, vuforiaBase);
        this.addTask(colorThiefTask);

        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
            @Override
            public void handleEvent(RobotEvent e)
            {
                GamepadEvent gamepadEvent = (GamepadEvent)e;

                switch (gamepadEvent.kind) {
                    case BUTTON_A_DOWN:
                        colorThiefTask.setPollingMode(ColorThiefTask.PollingMode.ON);
                        break;
                    case BUTTON_B_DOWN:
                        CameraDevice.getInstance().setFlashTorchMode(!flashOn);
                        flashOn = !flashOn;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void start()
    {
    }
}
