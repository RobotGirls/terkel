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

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.HashSet;
import java.util.Set;

public class RunToEncoderValueTask extends RobotTask {

    public enum EventKind {
        THRESHOLD_25,
        THRESHOLD_50,
        THRESHOLD_70,
        THRESHOLD_80,
        THRESHOLD_90,
        THRESHOLD_95,
        THRESHOLD_98,
        DONE,
    }

    public class RunToEncoderValueEvent extends RobotEvent {

        public EventKind kind;

        public RunToEncoderValueEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "RunToEncoderValue Event " + kind);
        }

    }


    protected DcMotor master;
    protected int encoderValue;
    protected Set<DcMotor> slaves;
    protected double power;

    /*
     * A cheap and easy way to do one shot events.
     *
     * If the event is non-null it's been sent, don't send again.
     * One instance for each THRESHOLD event in the enumeration above.
     * The construct is not necessary for the DONE event because this
     * task will quit when that's been sent.
     *
     * Of course the event thresholds could be modified to suit any
     * particular need.  This is just an example.
     */
    protected RunToEncoderValueEvent t_25 = null;
    protected RunToEncoderValueEvent t_50 = null;
    protected RunToEncoderValueEvent t_70 = null;
    protected RunToEncoderValueEvent t_80 = null;
    protected RunToEncoderValueEvent t_90 = null;
    protected RunToEncoderValueEvent t_95 = null;
    protected RunToEncoderValueEvent t_98 = null;

    /*
     *
     */
    public RunToEncoderValueTask(Robot robot, DcMotor master, Set<DcMotor> slaves, int encoderValue)
    {
        super(robot);
        this.master = master;
        this.master = master;
        this.slaves = slaves;
        this.encoderValue = encoderValue;
    }

    public RunToEncoderValueTask(Robot robot, DcMotor master, int encoderValue, double power)
    {
        super(robot);
        this.master = master;
        this.master = master;
        this.slaves = new HashSet<DcMotor>();
        this.encoderValue = encoderValue;
        this.power = power;
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
        master.setPower(0.0);
        master.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        master.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        for (DcMotor slave : slaves) {
            slave.setPower(0.0);
        }

        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        master.setPower(power);

        int pos = Math.abs(master.getCurrentPosition());

        if (pos >= (encoderValue * 0.25) && (t_25 == null)) {
            t_25 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_25);
            robot.queueEvent(t_25);
            return false;
        } else if (pos >= (encoderValue * 0.50) && (t_50 == null)) {
            t_50 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_50);
            robot.queueEvent(t_50);
            return false;
        } else if (pos >= (encoderValue * 0.70) && (t_70 == null)) {
            t_70 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_70);
            robot.queueEvent(t_70);
            return false;
        } else if (pos >= (encoderValue * 0.80) && (t_80 == null)) {
            t_80 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_80);
            robot.queueEvent(t_80);
            return false;
        } else if (pos >= (encoderValue * 0.90) && (t_90 == null)) {
            t_90 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_90);
            robot.queueEvent(t_90);
            return false;
        } else if (pos >= (encoderValue * 0.95) && (t_95 == null)) {
            t_95 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_95);
            robot.queueEvent(t_95);
            return false;
        } else if (pos >= (encoderValue * 0.98) && (t_98 == null)) {
            t_98 = new RunToEncoderValueEvent(this, EventKind.THRESHOLD_98);
            robot.queueEvent(t_98);
            return false;
        } else if (pos >= encoderValue) {
            RobotLog.i("EVENT: RunToEncoder master done " + master.getConnectionInfo() + " Position: " + encoderValue);
            robot.queueEvent(new RunToEncoderValueEvent(this, EventKind.DONE));
            return true;
        } else {
            return false;
        }
    }
}
