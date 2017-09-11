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

import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

public class UltrasonicDualSensorCriteria implements SensorCriteria {

    UltrasonicAveragingTask left;
    UltrasonicAveragingTask right;
    Team25UltrasonicSensor leftSensor;
    Team25UltrasonicSensor rightSensor;
    UltrasonicSensorArbitratorTask arbitrator;
    int margin;
    ElapsedTime et = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    public UltrasonicDualSensorCriteria(UltrasonicAveragingTask left, UltrasonicAveragingTask right, int margin)
    {
        this.left = left;
        this.right = right;
        this.margin = margin;
        this.arbitrator = null;
    }

    public UltrasonicDualSensorCriteria(UltrasonicAveragingTask left, UltrasonicAveragingTask right)
    {
        this.left = left;
        this.right = right;
        this.margin = 0;
        this.arbitrator = null;
    }

    public UltrasonicDualSensorCriteria(UltrasonicSensorArbitratorTask arbitrator, Team25UltrasonicSensor left, Team25UltrasonicSensor right,
                                        int margin)
    {
        this.leftSensor = left;
        this.rightSensor = right;
        this.margin = margin;
        this.arbitrator = arbitrator;
    }

    @Override
    public boolean satisfied()
    {
        double leftVal;
        double rightVal;

        if (arbitrator != null) {
            leftVal = arbitrator.getUltrasonicLevel(leftSensor);
            rightVal = arbitrator.getUltrasonicLevel(rightSensor);
        } else {
            leftVal = left.getAverage();
            rightVal = right.getAverage();
        }

        if (et.time() >= 200) {
            RobotLog.i("251 Left %3.1f, right %3.1f", leftVal, rightVal);
            et.reset();
        }


        if (Math.abs(leftVal - rightVal) <= margin) {
            RobotLog.i("251 Ultrasonic satisfied: Left %3.1f, right %3.1f", left.getAverage(), right.getAverage());
            return true;
        } else {
            return false;
        }
    }
}
