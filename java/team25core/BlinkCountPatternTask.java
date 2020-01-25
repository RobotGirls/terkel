package team25core;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Event;
import org.firstinspires.ftc.robotcore.external.State;
import org.firstinspires.ftc.robotcore.external.StateMachine;
import org.firstinspires.ftc.robotcore.external.StateTransition;

public class BlinkCountPatternTask extends RobotTask {

    private final static int INTERVAL_SECONDS = 1;
    private final static int BLINK_SECONDS = 1;
    private final static int END_MARKER_SECONDS = 2;

    private RevBlinkinLedDriver.BlinkinPattern blinkColor = RevBlinkinLedDriver.BlinkinPattern.GREEN;
    private RevBlinkinLedDriver.BlinkinPattern intervalColor = RevBlinkinLedDriver.BlinkinPattern.BLACK;
    private RevBlinkinLedDriver.BlinkinPattern endMarkerColor = RevBlinkinLedDriver.BlinkinPattern.BLUE;

    private BlinkStateMachine stateMachine;
    private RevBlinkinLedDriver blinkin;
    private int blinkCount;

    private enum BlinkEvent implements Event {
        RESET,
        START,
        FINISH,
        TIMEOUT,
        ;
        @Override
        public String getName()
        {
            return this.toString();
        }
    }

    private class BlinkStateMachine extends StateMachine {

        BlinkState currState;
        BlinkReset blinkReset = new BlinkReset();
        DoBlink doBlink = new DoBlink();
        DoInterval doInterval = new DoInterval();
        EndMarker endMarker = new EndMarker();

        private abstract class BlinkState implements State {
            @Override
            public void onEnter(Event event)
            {
                 currState = this;
            }

            @Override
            public void onExit(Event event) { }

            abstract public void timeslice();
        }

        private class BlinkReset extends BlinkState {

            private ElapsedTime time;

            @Override
            public void onEnter(Event event)
            {
               super.onEnter(event);

               blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
               doInterval.reset();
               ElapsedTime time = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
            }

            @Override
            public void timeslice()
            {
                if (time.seconds() > INTERVAL_SECONDS) {
                    consumeEvent(BlinkEvent.START);
                }
            }
        }

        private class DoBlink extends BlinkState {

            private ElapsedTime time;

            @Override
            public void onEnter(Event event)
            {
                super.onEnter(event);

                blinkin.setPattern(blinkColor);
                ElapsedTime time = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
            }

            @Override
            public void timeslice()
            {
                if (time.seconds() >= BLINK_SECONDS) {
                    consumeEvent(BlinkEvent.TIMEOUT);
                }
            }
        }

        private class DoInterval extends BlinkState {

            private ElapsedTime time;
            private int blinkPos;

            public void reset()
            {
                blinkPos = 0;
            }

            @Override
            public void onEnter(Event event)
            {
                super.onEnter(event);

                blinkin.setPattern(intervalColor);
            }

            @Override
            public void timeslice()
            {
                if (time.seconds() >= BLINK_SECONDS) {
                    blinkPos++;
                    if (blinkPos >= blinkCount) {
                        consumeEvent(BlinkEvent.FINISH);
                    } else {
                        consumeEvent(BlinkEvent.TIMEOUT);
                    }
                }

            }
        }

        private class EndMarker extends BlinkState {

            ElapsedTime time;

            @Override
            public void onEnter(Event event)
            {
                super.onEnter(event);

                time = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
                blinkin.setPattern(endMarkerColor);
            }

            @Override
            public void timeslice()
            {
                if (time.seconds() > END_MARKER_SECONDS) {
                    consumeEvent(BlinkEvent.TIMEOUT);
                }
            }
        }

        public void timeslice()
        {
            currState.timeslice();
        }

        public void start()
        {
            super.start(blinkReset);
        }

        public void stop() { }

        public void initialize()
        {
            StateTransition transition;

            transition = new StateTransition(
                    blinkReset,
                    BlinkEvent.RESET,
                    blinkReset);
            addTransition(transition);

            transition = new StateTransition(
                    blinkReset,
                    BlinkEvent.START,
                    doBlink);
            addTransition(transition);

            transition = new StateTransition(
                    doBlink,
                    BlinkEvent.RESET,
                    blinkReset);
            addTransition(transition);

            transition = new StateTransition(
                    doBlink,
                    BlinkEvent.TIMEOUT,
                    doInterval);
            addTransition(transition);

            transition = new StateTransition(
                    doInterval,
                    BlinkEvent.RESET,
                    blinkReset);
            addTransition(transition);

            transition = new StateTransition(
                    doInterval,
                    BlinkEvent.TIMEOUT,
                    doBlink);
            addTransition(transition);

            transition = new StateTransition(
                    doInterval,
                    BlinkEvent.FINISH,
                    endMarker);
            addTransition(transition);

            transition = new StateTransition(
                    endMarker,
                    BlinkEvent.TIMEOUT,
                    doBlink);
            addTransition(transition);

            transition = new StateTransition(
                    endMarker,
                    BlinkEvent.RESET,
                    blinkReset);
            addTransition(transition);
        }
    }


    BlinkCountPatternTask(Robot robot, RevBlinkinLedDriver blinkin)
    {
        super(robot);
        this.blinkin = blinkin;
        this.stateMachine = new BlinkStateMachine();
        this.stateMachine.initialize();
    }

    public void reset()
    {
        stateMachine.consumeEvent(BlinkEvent.RESET);
    }

    public void setBlinkCount(int count)
    {
        blinkCount = count;
        reset();
    }

    @Override
    public void start()
    {
        stateMachine.start();
    }

    @Override
    public void stop() { }

    @Override
    public boolean timeslice()
    {
        stateMachine.timeslice();
        return false;
    }
}
