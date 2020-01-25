package team25core;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import team25core.SensorCriteria;

public class TouchSensorCriteria implements SensorCriteria {

    protected TouchSensor touchSensor;

    public TouchSensorCriteria(TouchSensor touchSensor) {
        this.touchSensor = touchSensor;
    }

    @Override
    public boolean satisfied()
    {
        if (touchSensor.isPressed() == true) {
            return true;        // touch sensor is pressed
        } else {
            return false;       // touch sensor is unpressed
        }
    }

}
