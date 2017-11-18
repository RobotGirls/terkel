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

public class DrivetrainBaseImpl implements DrivetrainBase {

    /**
     * Defaults that seem to work well for 4 inch wheels.
     */
    protected static final int TICKS_PER_DEGREE = 18;
    protected static final int TICKS_PER_INCH = 79;

    protected int encoderTicksPerInch;
    protected int encoderTicksPerDegree;
    protected int encoderTarget;
    protected DcMotor master;

    public DrivetrainBaseImpl()
    {
        setEncoderTicksPerInch(TICKS_PER_INCH);
        setEncoderTicksPerDegree(TICKS_PER_DEGREE);
    }

    public void setEncoderTicksPerInch(int encoderTicksPerInch)
    {
        this.encoderTicksPerInch = encoderTicksPerInch;
    }

    public void setEncoderTicksPerDegree(int encoderTicksPerDegree)
    {
        this.encoderTicksPerDegree = encoderTicksPerDegree;
    }

    /**
     *  DrivetrainBase implementation.
     */

    @Override
    public void setMasterMotor(DcMotor motor)
    {
        master = motor;
    }

    @Override
    public DcMotor getMasterMotor()
    {
        return master;
    }

    @Override
    public int getCurrentPosition()
    {
        return master.getCurrentPosition();
    }

    @Override
    public void setTargetInches(double inches)
    {
        encoderTarget = (int)(inches * encoderTicksPerInch);
    }

    @Override
    public void setTargetRotation(double degrees)
    {
        encoderTarget = (int)(degrees * encoderTicksPerDegree);
    }

    @Override
    public double percentComplete()
    {
        if (encoderTarget != 0) {
            return (Math.abs(getCurrentPosition()) / encoderTarget);
        } else {
            return 1;
        }
    }

    @Override
    public boolean isBusy()
    {
        if (Math.abs(getCurrentPosition()) <= encoderTarget) {
            return true;
        } else {
            return false;
        }
    }
}
