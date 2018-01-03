package team25core;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

public class VuMarkIdentificationTask extends RobotTask
{

    public enum EventKind
    {
        LEFT,
        RIGHT,
        CENTER,
        UNKNOWN,
    }

    public enum PollingMode {
        ON,
        OFF,
    }

    public class VuMarkIdentificationEvent extends RobotEvent
    {
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
    protected ElapsedTime pollTimer;
    protected PollingMode pollingMode;
    protected VuforiaBase vuforiaBase;

    protected final static int POLL_RATE = 2;

    public VuMarkIdentificationTask(Robot robot, VuforiaBase vuforiaBase)
    {
        super(robot);
        this.vuMarkTelemetry = robot.telemetry.addData("Vumark: ", "Not Visible");
        this.pollingMode = PollingMode.OFF;
        this.vuforiaBase = vuforiaBase;
    }

    @Override
    public void start()
    {
        vuforia = vuforiaBase.getVuforia();

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);

        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();
    }

    @Override
    public void stop()
    {
        // Noop
    }

    public void setPollingMode(PollingMode pollingMode)
    {
        this.pollingMode = pollingMode;

        if (pollingMode == PollingMode.ON) {
            pollTimer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        }
    }

    @Override
    public boolean timeslice()
    {
        if ((pollingMode == PollingMode.ON) && (pollTimer.time() > POLL_RATE)) {
            pollTimer.reset();
            return false;
        }

        /**
         * See if any of the instances of {@link relicTemplate} are currently visible.
         * {@link RelicRecoveryVuMark} is an enum which can have the following values:
         * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
         * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
         */

        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        switch (vuMark) {
            case UNKNOWN:
                robot.queueEvent(new VuMarkIdentificationEvent(this, EventKind.UNKNOWN));
                break;
            case LEFT:
                robot.queueEvent(new VuMarkIdentificationEvent(this, EventKind.LEFT));
                break;
            case CENTER:
                robot.queueEvent(new VuMarkIdentificationEvent(this, EventKind.CENTER));
                break;
            case RIGHT:
                robot.queueEvent(new VuMarkIdentificationEvent(this, EventKind.RIGHT));
                break;
        }
        vuMarkTelemetry.setValue("VuMark: %s visible", vuMark.toString());

        return false;
    }
}
