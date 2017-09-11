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

// import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashSet;
import java.util.Set;

public class UltrasonicSensorHighAvailabilityTask extends UltrasonicSensorArbitratorTask
        implements IUltrasonicAveraging {

    Team25UltrasonicSensor active;
    Team25UltrasonicSensor primary;
    Team25UltrasonicSensor secondary;
    // DescriptiveStatistics failureDetection;
    HashSet<Team25UltrasonicSensor> primarySet;
    HashSet<Team25UltrasonicSensor> secondarySet;

    public double getAverage() {
        // Not an average yet.
        double val;
        val = getUltrasonicLevel();

        return val;
    }

    public UltrasonicSensorHighAvailabilityTask(Robot robot, Team25UltrasonicSensor p, Team25UltrasonicSensor s)
    {
        super(robot, null);
        this.primarySet = new HashSet<Team25UltrasonicSensor>();
        this.primarySet.add(p);
        this.secondarySet = new HashSet<Team25UltrasonicSensor>();
        this.secondarySet.add(s);
        super.setSensors(primarySet);

        this.primary = p;
        this.secondary = s;
        // this.failureDetection = new DescriptiveStatistics(50);
        this.active = primary;
    }

    public static UltrasonicSensorHighAvailabilityTask factory(Robot robot, Team25UltrasonicSensor primary,
                  Team25UltrasonicSensor secondary)
    {
        UltrasonicSensorHighAvailabilityTask task;

        task = new UltrasonicSensorHighAvailabilityTask(robot, primary, secondary);
        task.setFilterGarbage(false);
        return task;
    }

    public void forceSwitchover()
    {
        if (active == primary) {
            active = secondary;
            super.setSensors(secondarySet);
        } else {
            active = primary;
            super.setSensors(primarySet);
        }
        // failureDetection.clear();
        state = SensorState.PING;
    }

    public double getUltrasonicLevel()
    {
        double val;

        val = super.getUltrasonicLevel(active);
        // failureDetection.addValue(val);

        /*
        if (failureDetection.getN() >= 50) {
            if ((failureDetection.getMean() == 0) || (failureDetection.getMean() == 255)) {
                forceSwitchover();
            }
        }
        */

        return val;
    }

    @Override
    public boolean timeslice()
    {
        super.timeslice();

        if (active == primary) {
            this.robot.telemetry.addData("Active: ", "Right");
        } else {
            this.robot.telemetry.addData("Active: ", "Left");
        }
        return false;
    }

}
