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
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorImpl;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Team25DcMotor extends DcMotorImpl
{
    public enum MotorLocation {
        UNKNOWN,
        FRONT_LEFT,
        FRONT_RIGHT,
        REAR_LEFT,
        REAR_RIGHT,
    }

    protected Robot robot;
    protected double power;
    protected int targetPosition;
    protected Set<Team25DcMotor> slaves = null;
    protected MotorLocation location;
    private final static double powerMax = 1.0;
    private final static double powerMin = -1.0;

    protected PeriodicTimerTask ptt = new PeriodicTimerTask(null, 200) {
        @Override
        public void handleEvent(RobotEvent e)
        {
            Team25DcMotor.super.setPower(Team25DcMotor.this.power);
        }
    };

    public Team25DcMotor(Robot robot, DcMotorController controller, int portNumber)
    {
        super(controller, portNumber);
        this.robot = robot;
        this.ptt.setRobot(robot);
        this.power = 0.0;
    }

    public Team25DcMotor(Robot robot, DcMotorController controller, int portNumber, MotorLocation loc)
    {
        super(controller, portNumber);
        this.robot = robot;
        this.ptt.setRobot(robot);
        this.power = 0.0;
        this.location = loc;
    }

    public void stopPeriodic()
    {
        this.robot.removeTask(ptt);
    }

    public void startPeriodic()
    {
        this.robot.addTask(ptt);
    }

    /**
     * Sets the power for this motor and any of its slaves.
     *
     * @param power The power to apply to the motor.
     */
    public void setPower(double power)
    {
        power = Range.clip(power, powerMin, powerMax);

        if (slaves != null) {
            if ((slaves.size() == 1) && (isEasyController() == true)) {
                Team25DcMotor m = (Team25DcMotor) slaves.toArray()[0];
                DcMotorController mc = m.getController();
                if (mc == this.getController()) {
                    mc.setMotorPower(m.getPortNumber(), power);
                    return;
                }
            }
            for (Team25DcMotor m : slaves) {
                m.setPower(power);
            }
        }

        this.power = power;
        super.setPower(power);
    }

    /**
     * Rotates two/four motors in opposite directions.  This motor must be
     * bound with one slave or three slaves.  If three, the user must
     * specify motor location in the constructor.
     *
     * @param power The motor power to apply to the master.  The slave is
     * applied a negated power value.
     */
    public void turn(double power)
    {
        if (slaves == null) {
            throw new UnsupportedOperationException("Turn must be called with at least one slave");
        }

        power = Range.clip(power, powerMin, powerMax);

        if (slaves.size() == 1) {
            this.power = power;
            super.setPower(power);
            ((Team25DcMotor)slaves.toArray()[0]).setPower(-power);
        } else if (slaves.size() == 3) {
            if (this.location == MotorLocation.UNKNOWN) {
                throw new UnsupportedOperationException("All motors must have location specified for the turn operation");
            }
            this.power = power;
            super.setPower(power);

            for (Team25DcMotor m : slaves) {
                if (this.location == MotorLocation.UNKNOWN) {
                    throw new UnsupportedOperationException("All motors must have location specified for the turn operation");
                } else if (this.location.toString().substring(this.location.toString().indexOf('_')) ==
                            m.location.toString().substring(m.location.toString().indexOf('_'))) {
                    m.setPower(power);
                } else {
                    m.setPower(-power);
                }
            }
        } else {
            throw new UnsupportedOperationException("Turn must be called with either one or three slaves");
        }
    }

    public void setTargetPosition(int position)
    {
        targetPosition = position;
    }

    public boolean isBusy()
    {
        int currentPosition = getCurrentPosition();

        return (Math.abs(currentPosition) < targetPosition);
    }

    /**
     * Bind a set of motors to this motor as slaves.  The setPower operation will
     * this apply to all of the slaves in the set.
     *
     * @param slaves A set of motors to bind to this master.
     */
    public void bind(Set<Team25DcMotor> slaves)
    {
        this.slaves = slaves;
    }

    /**
     * Bind three other motors to this one, making this one the master.
     * Assumes there's a second motor on the master's controller and two
     * motors on the controller passed in.
     *
     * Throws an exception if this is not running on an EasyModernMotorController
     *
     * @param mc The other two
     */
    public void bind(DcMotorController mc)
    {
        if ((isEasyController() == false) || (isEasyController(mc) == false)) {
            throw new UnsupportedOperationException("Binding only supported in conjunction with EasyModernMotorController");
        }

        this.slaves = new HashSet<Team25DcMotor>();

        /*
         * What port am I on?
         */
        int port = this.getPortNumber();
        if (port == 1) {
            // this.slaves.add((Team25DcMotor)(this.getController()).getMotor(2));
        } else {
            // this.slaves.add((Team25DcMotor)(this.getController()).getMotor(1));
        }

        /*
         * Add the other motor controller's motors.
         */
        // this.slaves.add((Team25DcMotor)(mc.getMotor(1));
        // this.slaves.add((Team25DcMotor)(mc.getMotor(2));
    }


    /**
     * A convenience function to bind a single motor to this master.  Will throw
     * away any previous binds/pairs.  If you want to bind multiple motors to
     * this motor use bind().
     *
     * @param slave A motor to bind to this master.
     */
    public void pair(Team25DcMotor slave)
    {
        this.slaves = new HashSet<Team25DcMotor>();
        this.slaves.add(slave);
    }

    public void unbind()
    {
        this.slaves = null;
    }

    private boolean isEasyController()
    {
        return isEasyController(this.getController());
    }

    private static boolean isEasyController(DcMotorController mc)
    {
        /*
        if (mc instanceof Team25DcMotorControllerWrapper) {
            return true;
        } else {
            return false;
        }
        */
        return false;
    }
}
