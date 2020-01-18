package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorPackage {

    public enum MotorLocation {
        FRONT_RIGHT,
        FRONT_LEFT,
        BACK_RIGHT,
        BACK_LEFT
    };

    /*
     * The offset coefficient can be used to compensate for an andymark
     * motor that is losing encoder pulses.  To use, determine which speed
     * polarity causes the motor to lose pulses (and hence speed up) and then
     * define an offset polarity of some percentage of the target speed in
     * an attempt to get it to slow down and match the other wheels.
     */
    public enum OffsetPolarity {
        POLARITY_POSITIVE,
        POLARITY_NEGATIVE,
    };

    public DcMotor motor;
    public double offsetCoefficient;
    public OffsetPolarity offsetPolarity;

    public MotorPackage(DcMotor motor, double offsetCoefficient, OffsetPolarity polarity)
    {
        this.motor = motor;
        this.offsetCoefficient = offsetCoefficient;
        this.offsetPolarity = polarity;
    }

    public MotorPackage(DcMotor motor)
    {
        this.motor = motor;
        this.offsetCoefficient = 0.0;
        this.offsetPolarity = OffsetPolarity.POLARITY_POSITIVE;
    }
}
