//package examples;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.util.RobotLog;
//
//import team25core.MineralDetectionTask;
//import team25core.Robot;
//import team25core.RobotEvent;
//
//@Autonomous(name = "Mineral Detection Test", group = "Team 25")
//public class MineralDetectionTest extends Robot {
//
//    private final static String TAG = "MineralDetectionTest";
//
//    MineralDetectionTask mdTask;
//
//    @Override
//    public void handleEvent(RobotEvent e)
//    {
//    }
//
//    @Override
//        public void init()
//        {
//            mdTask = new MineralDetectionTask(this) {
//                @Override
//                public void handleEvent(RobotEvent e) {
//                    MineralDetectionEvent event = (MineralDetectionEvent)e;
//                    RobotLog.ii(TAG, "Saw: " + event.kind + " Confidence: " + event.minerals.get(0).getConfidence());
//                }
//            };
//
//            mdTask.init(telemetry, hardwareMap);
//            mdTask.setDetectionKind(MineralDetectionTask.DetectionKind.EVERYTHING);
//
//        }
//    @Override
//    public void start()
//    {
//        addTask(mdTask);
//    }
//}
