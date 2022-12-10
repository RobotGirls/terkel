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
 * A convenient method to test a through bore encoder on any robot
 * without having to modify and redeploy an opmode.
 *
 * To use connect to ftc-dashboard from a laptop that is on the
 * robot contoller network and then connect to:
 *   http://192.168.43.1:8080/dash
 *
 * On the right hand side of the browser window is a Configuration
 * panel.  Open the panel, enter the name(s) of the encoders as in
 * the configuration, then run the opmode.
 *
 * Ports that are left empty are not evaluated.  This approach
 * allows a user to test any combination of motors.
 *
 * Note that the port numbers are just for reference here.
 * e.g. If you enter a motor name that is actually on port 2
 * in port 1 it will still run when you run this opmode.
 *
 */
//@Config
@Disabled
@Autonomous(group="Tests", name="EncoderEvaluation")
public class EncoderEvaluation extends LinearOpMode {

    /*
     * Do not set encoder names here.
     * See the comment on using ftc-dashboard above.
     */
    public static String PORT_1 = "";
    public static String PORT_2 = "";
    public static String PORT_3 = "";
    public static String PORT_4 = "";

    private Map<Telemetry.Item, DcMotor> setupEncoders()
    {
        Map<Telemetry.Item, DcMotor> encoders = new HashMap();
        for (String s : Arrays.asList(PORT_1, PORT_2, PORT_3, PORT_4)) {
            if (!s.isEmpty()) {
                DcMotor encoder = hardwareMap.get(DcMotorEx.class, s);
                if (encoder != null) {
                    encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    encoders.put(telemetry.addData(s, 0), encoder);
                }
            }
        }
        return encoders;
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
         * telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
         */

        Map<Telemetry.Item, DcMotor> encoders = setupEncoders();

        waitForStart();

        if (isStopRequested()) return;

        while (!isStopRequested()) {
            for (Map.Entry<Telemetry.Item, DcMotor> entry : encoders.entrySet()) {
                Telemetry.Item item = entry.getKey();
                DcMotor encoder = entry.getValue();
                if (item != null) {
                    item.setValue(encoder.getCurrentPosition());
                }
            }
            telemetry.update();
        }
    }
}
