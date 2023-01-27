///*
//Copyright (c) September 2017 FTC Teams 25/5218
//
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted (subject to the limitations in the disclaimer below) provided that
//the following conditions are met:
//
//Redistributions of source code must retain the above copyright notice, this list
//of conditions and the following disclaimer.
//
//Redistributions in binary form must reproduce the above copyright notice, this
//list of conditions and the following disclaimer in the documentation and/or
//other materials provided with the distribution.
//
//Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
//endorse or promote products derived from this software without specific prior
//written permission.
//
//NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
//LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
//THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
//FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
//CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
//TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
//THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//*/
//
//package examples;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.util.RobotLog;
//
//import team25core.DeadReckonPath;
//import team25core.DeadReckonTask;
//import team25core.FourWheelDirectDrivetrain;
//import team25core.Robot;
//import team25core.RobotEvent;
//
//@Autonomous(name = "TEST Straight")
//@Disabled
//public class DeadReckonExampleRotation extends Robot {
//
//    private DcMotor frontLeft;
//    private DcMotor frontRight;
//    private DcMotor rearLeft;
//    private DcMotor rearRight;
//    private DeadReckonPath path;
//
//    private FourWheelDirectDrivetrain drivetrain;
//
//    /**
//     * The default event handler for the robot.
//     */
//    @Override
//    public void handleEvent(RobotEvent e)
//    {
//        /**
//         * Every time we complete a segment drop a note in the robot log.
//         */
//        if (e instanceof DeadReckonTask.DeadReckonEvent) {
//            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent)e).segment_num);
//        }
//    }
//
//    @Override
//    public void init()
//    {
//
//        frontLeft   = hardwareMap.dcMotor.get("frontLeft");
//        frontRight  = hardwareMap.dcMotor.get("frontRight");
//        rearLeft    = hardwareMap.dcMotor.get("rearLeft");
//        rearRight   = hardwareMap.dcMotor.get("rearRight");
//
//
//
//        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        drivetrain = new FourWheelDirectDrivetrain(frontRight, rearRight, frontLeft, rearLeft);
//    }
//
//    @Override
//    public void start()
//    {
//        path = new DeadReckonPath();
//
//        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 1.0);
//        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, -1.0);
//
//        /**
//         * Alternatively, this could be an anonymous class declaration that implements
//         * handleEvent() for task specific event handlers.
//         */
//
//        this.addTask(new DeadReckonTask(this, path, drivetrain));
//    }
//}
