package team25core;

/*
 * FTC Team 25: cmacfarl, December 27, 2016
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * Takes a motor and ramps up it's speed according to the slewRate until it's at
 * or above a target rpm at which point it will maintain that speed.
 */
public class RunAtRpmTask extends MonitorMotorTask {

    private final static int DEFAULT_SLEW_RATE = 40;
    private final static double DEFAULT_SLEW_INCREMENTER = 0.05;

    protected ElapsedTime slewTime;
    protected double slewRate;
    protected double slewIncrementer;
    protected double currPower;

    public RunAtRpmTask(Robot robot, DcMotor motor, MotorKind motorKind, int targetRpm)
    {
        super(robot, motor, motorKind, DISPLAY_RPM);

        this.slewRate = DEFAULT_SLEW_RATE;
        this.slewIncrementer = DEFAULT_SLEW_INCREMENTER;
        this.currPower = 0.0;
        setTargetRpm(targetRpm);
    }

    public RunAtRpmTask(Robot robot, DcMotor motor, MotorKind motorKind, int targetRpm, double slewRate, double slewIncrementer)
    {
        super(robot, motor, motorKind, DISPLAY_RPM);

        this.slewRate = slewRate;
        this.slewIncrementer = slewIncrementer;
        this.currPower = 0.0;
        setTargetRpm(targetRpm);
    }

    @Override
    public void handleEvent(RobotEvent e)
    {
        MonitorMotorEvent me = (MonitorMotorEvent)e;

        if (me.kind == EventKind.TARGET_RPM) {
            /*
             * Stop increasing the speed by setting the slew rate to 0.
             */
            slewRate = 0.0;
        }
    }

    @Override
    public void start()
    {
        slewTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        slewTime.reset();
    }

    @Override
    public void stop()
    {
        motor.setPower(0.0);
    }

    @Override
    public boolean timeslice()
    {
        if (slewTime.time() >= slewIncrementer) {
            currPower += slewRate;
            motor.setPower(currPower);
        }

        return false;
    }
}
