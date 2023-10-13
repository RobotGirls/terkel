
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
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensorTask extends RobotTask {


    private DistanceSensor rightSensor;
    private DistanceSensor leftSensor;

    double rightDistance;
    double leftDistance;
    double minDistance;
    double maxDistance;
    int numLoops;

    boolean continuousFlag;
    int pollingThresh;

    private Telemetry.Item rightDistanceTlm;
    private Telemetry.Item leftDistanceTlm;
    private Telemetry.Item rightCountTlm;
    private Telemetry.Item leftCountTlm;
    private Telemetry.Item notFoundCountTlm;


    // Constructor.
    public DistanceSensorTask(Robot robot, DistanceSensor myRightSensor,
                              DistanceSensor myLeftSensor, Telemetry telemetry,
                              double myMinDistance, double myMaxDistance, int numLoops,
                              int pollingThresh, boolean continuousFlag)
    {
        super(robot);

        this.rightSensor = myRightSensor;
        this.leftSensor = myLeftSensor;
        this.minDistance = myMinDistance;
        this.maxDistance = myMaxDistance;

        this.continuousFlag = continuousFlag;

        this.numLoops = numLoops;
        this.pollingThresh = pollingThresh;

        this.leftDistanceTlm = telemetry.addData("leftDistance", "none");
        this.rightDistanceTlm = telemetry.addData("rightDistance", "none");
        this.rightCountTlm = telemetry.addData("rightCount", "none");
        this.leftCountTlm = telemetry.addData("leftCount", "none");
        this.notFoundCountTlm = telemetry.addData("notFoundCount", "none");

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

    public void setMinMax(double minDistance, double maxDistance)
    {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    // this function will poll the data from the sensors and will determine whether the object is
    // on the left or right
    private boolean pollingAndVoting()
    {
        int leftCount = 0;
        int rightCount = 0;
        int notFound = 0;

        for(int i=0; i< numLoops ; i++){
            leftDistance = leftSensor.getDistance(DistanceUnit.INCH);
            rightDistance = rightSensor.getDistance(DistanceUnit.INCH);
            rightDistanceTlm.setValue(rightDistance);
            leftDistanceTlm.setValue(leftDistance);


            if (leftDistance < maxDistance && leftDistance > minDistance){
                leftCount++;
            }
            else if (rightDistance < maxDistance && rightDistance > minDistance){
                rightCount++;
            }
            else {
                notFound++;
            }
        }
        leftCountTlm.setValue(leftCount);
        rightCountTlm.setValue(rightCount);
        notFoundCountTlm.setValue(notFound);
        // FIXME later bound the distance so you return a distance relevant to where the prop is
        if (leftCount > rightCount && leftCount > pollingThresh){
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.LEFT_DISTANCE,
                    leftDistance));
        }
        else if (rightCount > leftCount && rightCount > pollingThresh){
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.RIGHT_DISTANCE,
                    rightDistance));
        }
        else {
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.UNKNOWN,
                    leftDistance));
        }
        return true;
    }

    private boolean continuousRead()
    {
        leftDistance = leftSensor.getDistance(DistanceUnit.INCH);
        rightDistance = rightSensor.getDistance(DistanceUnit.INCH);
        rightDistanceTlm.setValue(rightDistance);
        leftDistanceTlm.setValue(leftDistance);

        // FIXME later bound the distance so you return a distance relevant to where the prop is
        if (leftDistance < maxDistance && leftDistance > minDistance){
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.LEFT_DISTANCE,
                    leftDistance));
        }
        if (rightDistance < maxDistance && rightDistance > minDistance){
            robot.queueEvent(new DistanceSensorEvent(this, EventKind.RIGHT_DISTANCE,
                    rightDistance));
        }

        // This task doesn't stop.
        return false;
    }

    @Override
    public boolean timeslice() {
        if (continuousFlag)
        {
            return continuousRead();
        }
        else
        {
            return pollingAndVoting();
        }
        // This task doesn't stop.
        //return false;
    }
}
