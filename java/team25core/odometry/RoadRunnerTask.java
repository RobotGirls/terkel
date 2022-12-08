package team25core.odometry;

import com.acmerobotics.roadrunner.drive.SampleMecanumDrive;
import com.acmerobotics.roadrunner.trajectorysequence.TrajectorySequence;

import team25core.DeadmanMotorTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RobotTask;

public class RoadRunnerTask extends RobotTask {

    SampleMecanumDrive drive;
    TrajectorySequence trajSeq;

    public enum EventKind {
        TRAJECTORY_DONE,
    }

    public class RoadRunnerEvent extends RobotEvent {
        public EventKind kind;

        public RoadRunnerEvent(RobotTask task, RoadRunnerTask.EventKind k){
            super(task);
            kind = k;
        }
    }

    public RoadRunnerTask(Robot robot, SampleMecanumDrive drive, TrajectorySequence trajSeq)
    {
        super(robot);

        this.drive = drive;
        this.trajSeq = trajSeq;
    }

    @Override
    public void start()
    {
        drive.followTrajectorySequenceAsync(trajSeq);
    }

    @Override
    public boolean timeslice()
    {
        drive.update();
        if (drive.isBusy()) {
            return false;
        } else {
            robot.queueEvent(new RoadRunnerTask.RoadRunnerEvent(this, EventKind.TRAJECTORY_DONE));
            return true;
        }
    }
}
