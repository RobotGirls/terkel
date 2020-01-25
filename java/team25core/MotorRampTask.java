package team25core;

public abstract class MotorRampTask extends RobotTask {

    public enum RampDirection {
        RAMP_UP,
        RAMP_DOWN,
    };

    private final static double RAMP_FACTOR = 0.03;
    private double currentSpeed;
    private double targetSpeed;
    private RampDirection direction;

    public MotorRampTask(Robot robot, RampDirection direction, double speed)
    {
        super(robot);
        this.direction = direction;
        if (direction == RampDirection.RAMP_UP) {
            currentSpeed = 0.0;
            targetSpeed = speed;
        } else {
            currentSpeed = speed;
            targetSpeed = 0;
        }
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    public abstract void run(double speed);

    @Override
    public boolean timeslice()
    {
        switch (direction) {
            case RAMP_UP:
                currentSpeed = currentSpeed + RAMP_FACTOR;
                if (currentSpeed >= targetSpeed) {
                    run(targetSpeed);
                    return true;
                }
                break;
            case RAMP_DOWN:
                currentSpeed = currentSpeed - RAMP_FACTOR;
                if (currentSpeed <= targetSpeed) {
                    run(targetSpeed);
                    return true;
                }
                break;
        }
        run (currentSpeed);
        return false;
    }
}
