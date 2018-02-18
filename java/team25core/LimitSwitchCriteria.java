package team25core;

import com.qualcomm.robotcore.hardware.DigitalChannel;

/**
 * Created by Breanna Chan on 2/17/2018.
 */

public class LimitSwitchCriteria implements SensorCriteria {

    protected DigitalChannel limitSwitch;

    public LimitSwitchCriteria(DigitalChannel limitSwitch) {
        this.limitSwitch = limitSwitch;
    }

    @Override
    public boolean satisfied()
    {
        if (limitSwitch.getState() == false) {          // reed switch is closed (on magnet)
            return true;
        } else {                                        // reed switch is open (off magnet)
            return false;
        }
    }
}
