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

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UltrasonicSensorArbitratorTask extends RobotTask {

    ElapsedTime rateLimit = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    HashSet<SensorCache> sensors;
    Iterator<SensorCache> iterator;
    SensorCache sensor;
    protected SensorState state;
    protected boolean filterGarbage;

    private class SensorCache {

        Team25UltrasonicSensor sensor;
        double cacheVal;

        public SensorCache(Team25UltrasonicSensor sensor)
        {
            this.sensor = sensor;
            this.cacheVal = 0;
        }
    }

    protected enum SensorState {
        PING,
        PONG,
    };

    public UltrasonicSensorArbitratorTask(Robot robot, Set<Team25UltrasonicSensor> set)
    {
        super(robot);

        this.setSensors(set);
        this.state = SensorState.PING;
        this.filterGarbage = true;
    }

    public double getUltrasonicLevel(Team25UltrasonicSensor sensor)
    {
        for (SensorCache s : sensors) {
            if (s.sensor == sensor) {
                return s.cacheVal;
            }
        }
        RobotLog.e("Could not find sensor " + sensor.getConnectionInfo() + " in set");
        return 255;
    }

    public void setSensors(Set<Team25UltrasonicSensor> sensors)
    {
        if (sensors == null) {
            return;
        }
        this.sensors = new HashSet<SensorCache>();

        for (Team25UltrasonicSensor s : sensors) {
            this.sensors.add(new SensorCache(s));
        }

        this.iterator = this.sensors.iterator();
        this.sensor = iterator.next();
    }

    public void setFilterGarbage(boolean filter)
    {
        filterGarbage = filter;
    }

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public boolean timeslice()
    {
        if (rateLimit.time() <= 20) {
            return false;
        }

        rateLimit.reset();

        if (state == SensorState.PING) {
            RobotLog.i("Arbitrator: " + sensor.sensor.getConnectionInfo() + " : Ping");
            sensor.sensor.doPing();
            state = SensorState.PONG;
        } else {
            RobotLog.i("Arbitrator: " + sensor.sensor.getConnectionInfo() + " : Pong");
            double val = sensor.sensor.getUltrasonicLevel();

            if ((filterGarbage == true) && ((val == 0) || (val == 255))) {
                return false;
            }

            // robot.telemetry.addData("Distance " + sensor.sensor.getConnectionInfo(), val);
            sensor.cacheVal = val;

            if (iterator.hasNext()) {
                sensor = iterator.next();
            } else {
                iterator = sensors.iterator();
                sensor = iterator.next();
            }
            state = SensorState.PING;
        }

        /*
         * Never stops
         */
        return false;
    }
}

