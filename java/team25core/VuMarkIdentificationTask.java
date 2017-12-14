package team25core;
/*
 * FTC Team 25: Created by Elizabeth Wu on December 09, 2017
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

public class VuMarkIdentificationTask extends RobotTask {


    public enum EventKind {
        LEFT,
        RIGHT,
        CENTER,
        UNKNOWN,
    }

    public class VuMarkIdentificationEvent extends RobotEvent {

        public EventKind kind;

        public VuMarkIdentificationEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }

        public String toString()
        {
            return kind.toString();
        }
    }

    protected VuforiaLocalizerCustom vuforia;
    protected VuforiaTrackable relicTemplate;
    protected Telemetry.Item vuMarkTelemetry;

    public VuMarkIdentificationTask(Robot robot) {
        super(robot);
        this.vuMarkTelemetry = robot.telemetry.addData("Vumark: ", "Not Visible");
    }

    public VuMarkIdentificationTask(Robot robot, VuforiaLocalizerCustom vuforia)
    {
        super(robot);
        this.vuforia = vuforia;
        this.vuMarkTelemetry = robot.telemetry.addData("Vumark: ", "Not Visible");
    }

    @Override
    public void start()
    {
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);

        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean timeslice()
    {
        /**
         * See if any of the instances of {@link relicTemplate} are currently visible.
         * {@link RelicRecoveryVuMark} is an enum which can have the following values:
         * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
         * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
         */
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
           // vuMarkTelemetry.addData("VuMark", "%s visible", vuMark);

            vuMarkTelemetry.setValue("VuMark: %s visible", vuMark.toString());
            return false;

        }

        else {
            vuMarkTelemetry.setValue("VuMark: not visible");

        }



        return false;

    }
}
