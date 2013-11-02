#pragma config(Hubs,  S1, HTMotor,  HTMotor,  HTServo,  HTMotor)
#pragma config(Sensor, S1,     ,               sensorI2CMuxController)
#pragma config(Motor,  mtr_S1_C1_1,     driveLeft,     tmotorTetrix, openLoop, reversed)
#pragma config(Motor,  mtr_S1_C1_2,     leftElevator,  tmotorTetrix, PIDControl, encoder)
#pragma config(Motor,  mtr_S1_C2_1,     conveyor,      tmotorTetrix, openLoop, reversed)
#pragma config(Motor,  mtr_S1_C2_2,     motorG,        tmotorTetrix, openLoop)
#pragma config(Motor,  mtr_S1_C4_1,     rightElevator, tmotorTetrix, PIDControl, encoder)
#pragma config(Motor,  mtr_S1_C4_2,     driveRight,    tmotorTetrix, openLoop)
#pragma config(Servo,  srvo_S1_C3_1,    right,                tServoNone)
#pragma config(Servo,  srvo_S1_C3_2,    left,                 tServoNone)
#pragma config(Servo,  srvo_S1_C3_3,    servo3,               tServoNone)
#pragma config(Servo,  srvo_S1_C3_4,    servo4,               tServoNone)
#pragma config(Servo,  srvo_S1_C3_5,    servo5,               tServoNone)
#pragma config(Servo,  srvo_S1_C3_6,    servo6,               tServoNone)
//*!!Code automatically generated by 'ROBOTC' configuration wizard               !!*//

/////////////////////////////////////////////////////////////////////////////////////////////////////
//
//                           Tele-Operation Mode Code Template
//
// This file contains a template for simplified creation of an tele-op program for an FTC
// competition.
//
// You need to customize two functions with code unique to your specific robot.
//
/////////////////////////////////////////////////////////////////////////////////////////////////////

#include "JoystickDriver.c"  //Include file to "handle" the Bluetooth messages.

#define CONVEYOR_SPEED 100
#define ELEVATOR_SPEED 20
#define OFF             0

#define SERVO_FORWARD 0
#define SERVO_REVERSE 256
#define SERVO_STOPPED 127

typedef enum {
    UP,
    DOWN,
    STOPPED,
} linear_state_t;

linear_state_t elevator_state;
linear_state_t conveyor_state;

typedef enum {
    RIGHT_TRIGGER_UP = 6,
    RIGHT_TRIGGER_DOWN = 8,
    LEFT_TRIGGER_UP = 5,
    LEFT_TRIGGER_DOWN = 7,
} joystick_event_t;

joystick_event_t joystick_event;

bool debounce;

/////////////////////////////////////////////////////////////////////////////////////////////////////
//
//                                    initializeRobot
//
// Prior to the start of tele-op mode, you may want to perform some initialization on your robot
// and the variables within your program.
//
// In most cases, you may not have to add any code to this function and it will remain "empty".
//
/////////////////////////////////////////////////////////////////////////////////////////////////////

task debounceTask()
{
    debounce = true;
    wait1Msec(500);
    debounce = false;
}

void all_stop()
{
    motor[driveLeft] = 0;
    motor[driveRight] = 0;
    motor[conveyor] = 0;
}

void initializeRobot()
{
  // Place code here to sinitialize servos to starting positions.
  // Sensors are automatically configured and setup by ROBOTC. They may need a brief time to stabilize.

    elevator_state = STOPPED;
    conveyor_state = STOPPED;
    debounce = false;

    servo[left] = SERVO_STOPPED;
    servo[right] = SERVO_STOPPED;

    all_stop();

  return;
}

void elev_enter_state(linear_state_t state)
{
    elevator_state = state;

    switch (elevator_state) {
    case UP:
	    motor[leftElevator] = ELEVATOR_SPEED;
	    motor[rightElevator] = -ELEVATOR_SPEED;
        break;
    case DOWN:
	    motor[leftElevator] = -ELEVATOR_SPEED;
	    motor[rightElevator] = ELEVATOR_SPEED;
        break;
    case STOPPED:
	    motor[leftElevator] = OFF;
	    motor[rightElevator] = OFF;
        break;
    }
}

void conv_enter_state(linear_state_t state)
{
    conveyor_state = state;

    switch (conveyor_state) {
    case UP:
	    motor[conveyor] = CONVEYOR_SPEED;
        servo[left] = SERVO_REVERSE;
        servo[right] = SERVO_FORWARD;
        break;
    case DOWN:
	    motor[conveyor] = -CONVEYOR_SPEED;
        servo[left] = SERVO_STOPPED;
        servo[right] = SERVO_STOPPED;
        break;
    case STOPPED:
	    motor[conveyor] = OFF;
        servo[left] = SERVO_STOPPED;
        servo[right] = SERVO_STOPPED;
        break;
    }
}
void handle_event_rtu()
{
    switch (elevator_state) {
    case STOPPED:
        elev_enter_state(UP);
        break;
    case UP:
        elev_enter_state(STOPPED);
        break;
    case DOWN:
        elev_enter_state(UP);
        break;
    }
}

void handle_event_rtd()
{
    switch (elevator_state) {
    case STOPPED:
        elev_enter_state(DOWN);
        break;
    case UP:
        elev_enter_state(DOWN);
        break;
    case DOWN:
        elev_enter_state(STOPPED);
        break;
    }
}

void handle_event_ltu()
{
    switch (conveyor_state) {
    case STOPPED:
        conv_enter_state(UP);
        break;
    case UP:
        conv_enter_state(STOPPED);
        break;
    case DOWN:
        conv_enter_state(UP);
        break;
    }
}

void handle_event_ltd()
{
    switch (conveyor_state) {
    case STOPPED:
        conv_enter_state(DOWN);
        break;
    case UP:
        conv_enter_state(DOWN);
        break;
    case DOWN:
        conv_enter_state(STOPPED);
        break;
    }
}

void handle_event(joystick_event_t event)
{
    switch (event) {
    case RIGHT_TRIGGER_UP:
        handle_event_rtu();
        break;
    case RIGHT_TRIGGER_DOWN:
        handle_event_rtd();
        break;
    case LEFT_TRIGGER_UP:
        handle_event_ltu();
        break;
    case LEFT_TRIGGER_DOWN:
        handle_event_ltd();
        break;
    }

    StartTask(debounceTask);
}


/////////////////////////////////////////////////////////////////////////////////////////////////////
//
//                                         Main Task
//
// The following is the main code for the tele-op robot operation. Customize as appropriate for
// your specific robot.
//
// Game controller / joystick information is sent periodically (about every 50 milliseconds) from
// the FMS (Field Management System) to the robot. Most tele-op programs will follow the following
// logic:
//   1. Loop forever repeating the following actions:
//   2. Get the latest game controller / joystick settings that have been received from the PC.
//   3. Perform appropriate actions based on the joystick + buttons settings. This is usually a
//      simple action:
//      *  Joystick values are usually directly translated into power levels for a motor or
//         position of a servo.
//      *  Buttons are usually used to start/stop a motor or cause a servo to move to a specific
//         position.
//   4. Repeat the loop.
//
// Your program needs to continuously loop because you need to continuously respond to changes in
// the game controller settings.
//
// At the end of the tele-op period, the FMS will autonmatically abort (stop) execution of the program.
//
/////////////////////////////////////////////////////////////////////////////////////////////////////

task main()
{
    initializeRobot();

    waitForStart();   // wait for start of tele-op phase

    while (true)
    {
        getJoystickSettings(joystick);

        if (!debounce) {
	        if (joy1Btn(RIGHT_TRIGGER_UP)) {
	            handle_event(RIGHT_TRIGGER_UP);
	        } else if (joy1Btn(RIGHT_TRIGGER_DOWN)) {
	            handle_event(RIGHT_TRIGGER_DOWN);
	        } else if (joy1Btn(LEFT_TRIGGER_UP)) {
	            handle_event(LEFT_TRIGGER_UP);
	        } else if (joy1Btn(LEFT_TRIGGER_DOWN)) {
	            handle_event(LEFT_TRIGGER_DOWN);
	        }
        }

        if(abs(joystick.joy1_y2) > 20)
		{
	    	motor[driveRight] = joystick.joy1_y2;
		}
		else
		{
		    motor[driveRight] = 0;
		}

        if(abs(joystick.joy1_y1) > 20)
		{
		    motor[driveLeft] = joystick.joy1_y1;
		}
		else
		{
		    motor[driveLeft] = 0;
		}
    }
}
