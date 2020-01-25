package team25core;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.HashMap;
import java.util.Map;

/*
 * StandardFourMotorRobot
 *
 * A standard class that opmodes can extend instead of Robot that
 * defines and implements motors and a motor map.
 *
 * The intent of this class is to establish a standard naming scheme
 * for motors, and to avoid copy/paste of motor initialization throughout
 * all opmodes.
 */
public class StandardFourMotorRobot extends Robot {

    /*
     * BEGIN: DO NOT CHANGE THESE STRINGS.
     *
     * Standard means standard.  If you are tempted to change these strings you
     * should either not use this class, or more preferably, change your robot
     * configuration to match the _standard_ naming scheme for four motor robots.
     */
    private final static String FRONT_LEFT = "frontLeft";
    private final static String FRONT_RIGHT = "frontRight";
    private final static String BACK_LEFT = "backLeft";
    private final static String BACK_RIGHT = "backRight";
    /*
     * END: DO NOT CHANGE THESE STRINGS.
     */

    protected DcMotorEx frontLeft;
    protected DcMotorEx frontRight;
    protected DcMotorEx backLeft;
    protected DcMotorEx backRight;

    protected Map<MotorPackage.MotorLocation, MotorPackage> motorMap;

    @Override
    public void init()
    {
        motorMap = new HashMap<>();

        frontLeft = hardwareMap.get(DcMotorEx.class, FRONT_LEFT);
        motorMap.put(MotorPackage.MotorLocation.FRONT_LEFT, new MotorPackage(frontLeft));

        frontRight = hardwareMap.get(DcMotorEx.class, FRONT_RIGHT);
        motorMap.put(MotorPackage.MotorLocation.FRONT_RIGHT, new MotorPackage(frontRight));

        backLeft = hardwareMap.get(DcMotorEx.class, BACK_LEFT);
        motorMap.put(MotorPackage.MotorLocation.BACK_LEFT, new MotorPackage(backLeft));

        backRight = hardwareMap.get(DcMotorEx.class, BACK_RIGHT);
        motorMap.put(MotorPackage.MotorLocation.BACK_RIGHT, new MotorPackage(backRight));
    }

    @Override
    public void handleEvent(RobotEvent e) { }
}
