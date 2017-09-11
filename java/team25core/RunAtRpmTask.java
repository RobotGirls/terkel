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
