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

import com.qualcomm.robotcore.util.RobotLog;

public abstract class RobotTask {

    private final static String TAG = "RobotTask";

    protected Robot robot;
    protected RobotEventListener listener;
    protected boolean isSuspended;

    public RobotTask(Robot robot)
    {
        this.robot = robot;
        this.listener = null;
        this.isSuspended = false;
    }

    public void start()
    {
        RobotLog.i(TAG, "Start task %s", this.toString());
    }

    public void stop()
    {
        RobotLog.i(TAG, "Stop task %s", this.toString());
    }

    public String toString()
    {
        return this.getClass().getName();
    }

    public void setEventListener(RobotEventListener listener)
    {
        this.listener = listener;
    }

    public void handleEvent(RobotEvent e)
    {
        if (listener != null) {
            listener.handleEvent(e);
        } else {
            robot.handleEvent(e);
        }
    }

    public void suspend()
    {
        isSuspended = true;
    }

    public void resume()
    {
        isSuspended = false;
    }

    public boolean isSuspended()
    {
        return isSuspended;
    }

    /*
     * Perform work for this task.
     *
     * The task should return false if there is more work to
     * do, true otherwise.
     */
    public abstract boolean timeslice();


}
