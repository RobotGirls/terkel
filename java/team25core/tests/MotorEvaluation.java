package team25core.tests;

//import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * A convenient method to test motors on any robot
 * without having to modify and redeploy an opmode.
 *
 * To use connect to ftc-dashboard from a laptop that is on the
 * robot contoller network and then connect to:
 *   http://192.168.43.1:8080/dash
 *
 * On the right hand side of the browser window is a Configuration
 * panel.  Open the panel, enter the name(s) of the motors as in
 * the configuration, then run the opmode.
 *
 * Ports that are left empty are not evaluated.  This approach
 * allows a user to test any combination of motors.
 *
 * Note that the port numbers are just for reference here.
 * e.g. If you enter a motor name that is actually on port 2
 * in port 1 it will still run when you run this opmode.
 *
 * You can however use this to determine if you have mapped your
 * motors correctly in the configuration.  Enter just a single
 * target motor, for example leftFront, and run the opmode to
 * observe which motor runs.  If it's not the leftFront motor
 * you know you have your configuration mapped incorrectly.
 */
//@Config
@Autonomous(group="Tests", name="MotorEvaluation")
public class MotorEvaluation extends LinearOpMode {

    /*
     * Do not set motor names here.
     * See the comment on using ftc-dashboard above.
     */
    public static String PORT_1 = "";
    public static String PORT_2 = "";
    public static String PORT_3 = "";
    public static String PORT_4 = "";

    public static double MOTOR_POWER = 0.5;

    private Map<Telemetry.Item, DcMotor> setupMotors()
    {
        Map<Telemetry.Item, DcMotor> motors = new HashMap();
        for (String s : Arrays.asList(PORT_1, PORT_2, PORT_3, PORT_4)) {
            if (!s.isEmpty()) {
                DcMotor motor = hardwareMap.get(DcMotorEx.class, s);
                if (motor != null) {
                    motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    motors.put(telemetry.addData(s + "on port " + motor.getPortNumber(), 0), motor);
                }
            }
        }
        return motors;
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        /*
         * Sends telemetry to both the driver station and FtcDashboard
         *
         * Unfortunately this is broken in ftc-dashboard wherein ftc-dashboard
         * returns a null item for addData resulting in an NPE when calling setValue.
         *
         * Leaving here in the event that the dashboard telemetry is fixed.
         *
         * telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
         */

        Map<Telemetry.Item, DcMotor> motors = setupMotors();

        waitForStart();

        if (isStopRequested()) return;

        while (!isStopRequested()) {
            for (Map.Entry<Telemetry.Item, DcMotor> entry : motors.entrySet()) {
                Telemetry.Item item = entry.getKey();
                DcMotor motor = entry.getValue();
                motor.setPower(MOTOR_POWER);
                if (item != null) {
                    item.setValue(motor.getCurrentPosition());
                }
            }
            telemetry.update();
        }
    }
}
