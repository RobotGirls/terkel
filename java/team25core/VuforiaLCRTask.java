package team25core;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by admin on 10/21/2017.
 */

public class VuforiaLCRTask extends RobotTask {

    public enum EventKind {
        LEFT,
        CENTER,
        RIGHT,
        UNKNOWN,
    }

    VuforiaLocalizer vuforia;

    public VuforiaLCRTask(Robot robot)
    {
        super(robot);

    }

    public class VuMarkEvent extends RobotEvent
    {
        public EventKind kind;

        public VuMarkEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }
    }

    @Override
    public boolean timeslice()
    {
        int cameraMonitorViewId = robot.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", robot.hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AdLmvUj/////AAAAGe/kAsI/H0WukR1Af5Og5w2Ey6b+wOXQ0h30RtwQyvYckcYCH8CBcrs0EGIqrGt0wbi7/icc/5DO3kqFkMdUh41bqjMCXWLU4d3Bz35AwPn89qCf/zp+ggEwgIUry20vwpU4uACQEqOJox8PHwzBmax9PquM/Jiq+/6wTx+8Bnd3Io4ymylg2uTVOsumVcphYhjkSyzaT+sUYtXGEdVEMWdyny8WuK4RE1SsaVLOvYap++/pA9b/7LLOFqW3yAwkaDMrPeqkCIN7RnDwH0ZxTbHsRRC/xKl43igL1T02tg0eUmeeyHdUxjP8T9BQlCdDmZvA5wGg6AAqe2ORWauhS49UvjW5xLGxglnsXXm0N4ce";

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        relicTrackables.activate();

        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

        if (vuMark == RelicRecoveryVuMark.LEFT) {
            robot.queueEvent(new VuMarkEvent(this, EventKind.LEFT));
        } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
            robot.queueEvent(new VuMarkEvent(this, EventKind.RIGHT));
        } else if (vuMark == RelicRecoveryVuMark.CENTER) {
            robot.queueEvent(new VuMarkEvent(this, EventKind.CENTER));
        }

        return true;
    }

    @Override
    public void stop()
    {

    }

    @Override
    public void start()
    {

    }

}
