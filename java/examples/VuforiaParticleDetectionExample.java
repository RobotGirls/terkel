package examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import team25core.ColorThiefTask;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "VuforiaParticleDetection")
public class VuforiaParticleDetectionExample extends Robot {
    @Override
    public void handleEvent(RobotEvent e)
    {
        telemetry.addData("Particle: ", e.toString());
        telemetry.update();
    }

    @Override
    public void init()
    {
        this.addTask(new ColorThiefTask(this));
    }

    @Override
    public void start()
    {
    }
}
