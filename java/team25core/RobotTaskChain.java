package team25core;

import com.qualcomm.robotcore.util.RobotLog;

import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * A special kind of task that exists only to run other tasks sequentially.
 *
 * Implements a sequenced order of tasks.  Each task is run, in order, to completion,
 * then the next task is run.  Callers should ensure that the tasks do not run for
 * infinite time.
 */
public class RobotTaskChain extends RobotTask {

    private final static String TAG = "RobotTaskChain";

    ConcurrentLinkedQueue<RobotTask> tasks = new ConcurrentLinkedQueue<>();
    RobotTask currentTask;

    public RobotTaskChain(Robot robot)
    {
        super(robot);
    }

    public void addTask(RobotTask task)
    {
        tasks.add(task);
    }

    public void start()
    {
        RobotLog.i(TAG, "Start RobotTaskChain");
        if (tasks.isEmpty()) {
            RobotLog.i(TAG, "No tasks for RobotTaskChain");
            return;
        }

        currentTask = tasks.poll();
        currentTask.start();
    }

    public void stop()
    {
        RobotLog.i(TAG, "Stop RobotTaskChain");
    }

    @Override
    public boolean timeslice()
    {
        if (currentTask.isSuspended()) {
            return false;
        }

        if (currentTask.timeslice()) {
            currentTask.stop();
            currentTask = tasks.poll();
            if (currentTask == null) {
                stop();
                robot.removeTask(this);
                return true;
            } else {
                currentTask.start();
            }
        }

        /*
         * Events will be processed by the caller (Robot).
         */
        return false;
    }
}
