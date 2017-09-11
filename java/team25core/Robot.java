/*
 * Copyright (c) September 2017 FTC Teams 25/5218
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted (subject to the limitations in the disclaimer below) provided that
 *  the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this list
 *  of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or
 *  other materials provided with the distribution.
 *
 *  Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
 *  endorse or promote products derived from this software without specific prior
 *  written permission.
 *
 *  NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 *  LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 *  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package team25core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Robot extends OpMode {

    ConcurrentLinkedQueue<RobotTask> tasks;
    ConcurrentLinkedQueue<RobotEvent> events;

    boolean started;

    public Robot()
    {
        msStuckDetectInit     = 100000;
        msStuckDetectInitLoop = 1500000;
        msStuckDetectStart    = 5000000;
        msStuckDetectLoop     = 500000;
        msStuckDetectStop     = 1000000;

        tasks = new ConcurrentLinkedQueue<RobotTask>();
        events = new ConcurrentLinkedQueue<RobotEvent>();
        started = false;
    }

    public abstract void handleEvent(RobotEvent e);

    public void addTask(RobotTask task)
    {
        tasks.add(task);
        task.start();
    }

    public void removeTask(RobotTask task)
    {
        tasks.remove(task);
    }

    public void queueEvent(RobotEvent event)
    {
        events.add(event);
    }

    public boolean taskRunning(RobotTask task)
    {
        return tasks.contains(task);
    }

    public void dumpTask()
    {
        for (RobotTask t:tasks) {
            RobotLog.i(t.toString());
        }
    }

    public void init()
    {
        // TODO: ??
    }

    public void init_loop() {
        if (gamepad1 == null) {
            return;
        }

        loop();
    }

    @Override
    public void loop()
    {
        RobotEvent e;

        /*
         * A list of tasks to give timeslices to.  A task remains in the list
         * until it tells the Robot that it is finished (true: I'm done, false: I have
         * more work to do), at which point it is stopped.
         */
        for (RobotTask t : tasks) {
            if (t.timeslice()) {
                t.stop();
            }
        }

        /*
         * This is a straight FIFO queue.  Pull an event off the queue, process it,
         * move on to the next one.
         */
        e = events.poll();
        while (e != null) {
            e.handleEvent();
            e = events.poll();
        }

    }
}
