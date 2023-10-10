
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
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensorTask extends RobotTask {


    private DistanceSensor rightSensor;
    private DistanceSensor leftSensor;

    double rightDistance;
    double leftDistance;

    private Telemetry.Item rightDistanceTlm;
    private Telemetry.Item leftDistanceTlm;

    // Constructor.
    public DistanceSensorTask(Robot robot, DistanceSensor myRightSensor,
                              DistanceSensor myLeftSensor, Telemetry telemetry)
    {
        super(robot);

        this.rightSensor = myRightSensor;
        this.leftSensor = myLeftSensor;

        this.leftDistanceTlm = telemetry.addData("leftDistance", "none");
        this.rightDistanceTlm = telemetry.addData("rightDistance", "none");

    }


    // Class: events.
    public class DistanceSensorEvent extends RobotEvent {
        public EventKind kind;
        public double distance;


        public DistanceSensorEvent(RobotTask task, EventKind k, double myDistance)
        {
            super(task);
            kind = k;
            distance = myDistance;
        }
    }

    // Enumeration: events.
    public enum EventKind {
        LEFT_DISTANCE,
        RIGHT_DISTANCE,
        UNKNOWN,
    }

    @Override
    public void start()
    {
        /*
         * TODO: Implement with new hardware.
         */
    }

    @Override
    public void stop()
    {
    }

    @Override
    public boolean timeslice() {

        leftDistance = leftSensor.getDistance(DistanceUnit.CM);
        rightDistance = rightSensor.getDistance(DistanceUnit.CM);
        rightDistanceTlm.setValue(rightDistance);
        leftDistanceTlm.setValue(leftDistance);
        // FIXME later bound the distance so you return a distance relevant to where the prop is
        if (leftDistance > 0){
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.LEFT_DISTANCE, leftDistance));
        }
        if (rightDistance > 0){
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.RIGHT_DISTANCE, rightDistance));
        }

        // This task doesn't stop.
        return false;
    }
}
