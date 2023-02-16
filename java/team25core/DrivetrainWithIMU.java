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

public interface DrivetrainWithIMU extends DrivetrainBase {

    enum PivotSide {
        LEFT_OVER_RIGHT,
        LEFT_OVER_LEFT,
        RIGHT_OVER_RIGHT,
        RIGHT_OVER_LEFT,
    }

    void straight(double speed);

    /**
     * Postitive is to the right, negative is to the left
     */
    void strafe(double speed);


    // setting the current yaw and yaw rate from the IMU to
    // use for calculating the correction for the yaw and yaw rate
    // the actual methods setCurrentYaw and setCurrentYawRate are implemented

    // in one of the children classes of DrivetrainWithIMU
    // For Example: FourWheelDirectIMUDrivetrain
    // because DeadReckonTaskWithIMU references DrivetrainWithIMU
    void setCurrentYaw(double currentYawFromIMU);
    void setCurrentYawRate(double currentYawRateFromIMU);
    void setTargetYaw(double targetYaw);
    /**
     * Move forward or back on a diagonal at 315 (forward) / 135 (backward) degrees
     *
     * Obviously only works for mechanum drivetrains
     */
    void leftDiagonal(double speed);

    /**
     * Move forward or back on a diagonal at 45 (forward) / 225 (backward) degrees
     *
     * Obviously only works for mechanum drivetrains
     */
    void rightDiagonal(double speed);

    /**
     * Postitive is to the right, negative is to the left
     */
    void turn(double speed);

    /**
     * Allows a turn around a pivot point that is not the dead center of the drivetrain.
     */
    void pivotTurn(PivotSide side, double speed);
    void setPivotMultiplier(double pivotMultiplier);

    void setPowerLeft(double speed);
    void setPowerRight(double speed);

    /**
     * Full stop, all motors off
     */
    void stop();

    /**
     * Move the robot according to axial, lateral, and yaw speeds.
     *
     * Note that this works best with Mecanum or Omni drivetrains.
     * YMMV when calling this on drivetrains that use regular wheels.
     */
    void move(double axial, double lateral, double yaw);

    void resetEncoders();
    void encodersOn();
    void logEncoderCounts();
}
