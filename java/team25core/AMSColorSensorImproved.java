///*
//Copyright (c) 2016 Robert Atkinson & Steve Geffner
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
//Neither the names of Robert Atkinson nor Steve Geffner nor the names of their contributors
//may be used to endorse or promote products derived from this software without specific
//prior written permission.
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
//package team25core;
//
//import android.graphics.Color;
//
//import com.qualcomm.hardware.ams.AMSColorSensor;
//import com.qualcomm.hardware.ams.AMSColorSensorImpl;
//import com.qualcomm.robotcore.hardware.I2cAddr;
//import com.qualcomm.robotcore.hardware.I2cDevice;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
//import com.qualcomm.robotcore.hardware.I2cWaitControl;
//import com.qualcomm.robotcore.hardware.NormalizedRGBA;
//import com.qualcomm.robotcore.util.RobotLog;
//import com.qualcomm.robotcore.util.TypeConversion;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//
///**
// * AMSColorSensorImpl is used to support the AdaFruit color sensor:
// * http://adafru.it/1334
// * https://www.adafruit.com/products/1334?&main_page=product_info&products_id=1334
// * https://github.com/adafruit/Adafruit_TCS34725
// * <p>
// * AMSColorSensorImpl also supports the Lynx color sensor.
// * <p>
// * More generally, there is a family of color sensors from AMS which this could support
// * http://ams.com/eng/Support/Demoboards/Light-Sensors/(show)/145298
// * <p>
// * Importantly, this implementation sits on top of I2cDeviceSyncSimple instead of I2cDevice
// */
//public class AMSColorSensorImproved extends I2cDeviceSynchDevice<I2cDeviceSynchSimple> implements AMSColorSensor {
//    @Override
//    public NormalizedRGBA getNormalizedColors()
//    {
//        return null;
//    }
//    //----------------------------------------------------------------------------------------------
//    // State
//    //----------------------------------------------------------------------------------------------
//
//    enum IntegrationTime
//    {
//        MS_2_4(0xFF),
//        MS_24(0xF6),
//        MS_50(0xEB),
//        MS_101(0xD5),
//        MS_154(0xC0),
//        MS_700(0x00);
//
//        public final byte byteVal;
//        IntegrationTime(int i) { this.byteVal = (byte) i; }
//    }
//
//    int AMS_COLOR_ENABLE = 0x00;
//    int AMS_COLOR_ENABLE_PIEN = 0x20;        /* Proximity interrupt enable */
//    int AMS_COLOR_ENABLE_AIEN = 0x10;        /* RGBC Interrupt Enable */
//    int AMS_COLOR_ENABLE_WEN = 0x08;         /* Wait enable - Writing 1 activates the wait timer */
//    int AMS_COLOR_ENABLE_PEN = 0x04;         /* Proximity enable */
//    int AMS_COLOR_ENABLE_AEN = 0x02;         /* RGBC Enable - Writing 1 actives the ADC, 0 disables it */
//    int AMS_COLOR_ENABLE_PON = 0x01;         /* Power on - Writing 1 activates the internal oscillator, 0 disables it */
//    int AMS_COLOR_ATIME = 0x01;              /* Integration time */
//    int AMS_COLOR_WTIME = 0x03;              /* Wait time = if AMS_COLOR_ENABLE_WEN is asserted; */
//    int AMS_COLOR_WTIME_2_4MS = 0xFF;        /* WLONG0 = 2.4ms   WLONG1 = 0.029s */
//    int AMS_COLOR_WTIME_204MS = 0xAB;        /* WLONG0 = 204ms   WLONG1 = 2.45s  */
//    int AMS_COLOR_WTIME_614MS = 0x00;        /* WLONG0 = 614ms   WLONG1 = 7.4s   */
//    int AMS_COLOR_AILTL = 0x04;              /* Clear channel lower interrupt threshold */
//    int AMS_COLOR_AILTH = 0x05;
//    int AMS_COLOR_AIHTL = 0x06;              /* Clear channel upper interrupt threshold */
//    int AMS_COLOR_AIHTH = 0x07;
//    int AMS_COLOR_PERS = 0x0C;               /* Persistence register - basic SW filtering mechanism for interrupts */
//    int AMS_COLOR_PERS_NONE = 0b0000;        /* Every RGBC cycle generates an interrupt                                */
//    int AMS_COLOR_PERS_1_CYCLE = 0b0001;     /* 1 clean channel value outside threshold range generates an interrupt   */
//    int AMS_COLOR_PERS_2_CYCLE = 0b0010;     /* 2 clean channel values outside threshold range generates an interrupt  */
//    int AMS_COLOR_PERS_3_CYCLE = 0b0011;     /* 3 clean channel values outside threshold range generates an interrupt  */
//    int AMS_COLOR_PERS_5_CYCLE = 0b0100;     /* 5 clean channel values outside threshold range generates an interrupt  */
//    int AMS_COLOR_PERS_10_CYCLE = 0b0101;    /* 10 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_15_CYCLE = 0b0110;    /* 15 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_20_CYCLE = 0b0111;    /* 20 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_25_CYCLE = 0b1000;    /* 25 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_30_CYCLE = 0b1001;    /* 30 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_35_CYCLE = 0b1010;    /* 35 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_40_CYCLE = 0b1011;    /* 40 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_45_CYCLE = 0b1100;    /* 45 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_50_CYCLE = 0b1101;    /* 50 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_55_CYCLE = 0b1110;    /* 55 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_PERS_60_CYCLE = 0b1111;    /* 60 clean channel values outside threshold range generates an interrupt */
//    int AMS_COLOR_CONFIG = 0x0D;
//    int AMS_COLOR_CONFIG_NORMAL = 0x00;      /* normal wait times */
//    int AMS_COLOR_CONFIG_WLONG = 0x02;       /* Extended wait time = 12x normal wait times via AMS_COLOR_WTIME */
//    int AMS_COLOR_CONTROL = 0x0F;            /* Set the gain level for the sensor */
//    int AMS_COLOR_GAIN_1 = 0x00;             /* normal gain */
//    int AMS_COLOR_GAIN_4 = 0x01;             /* 4x gain */
//    int AMS_COLOR_GAIN_16 = 0x02;            /* 16x gain */
//    int AMS_COLOR_GAIN_60 = 0x03;            /* 60x gain */
//    int AMS_COLOR_ID = 0x12;                 /* 0x44 = TCS34721/AMS_COLOR, 0x4D = TCS34723/TCS34727 */
//    int AMS_COLOR_STATUS = 0x13;
//    int AMS_COLOR_STATUS_AINT = 0x10;        /* RGBC Clean channel interrupt */
//    int AMS_COLOR_STATUS_AVALID = 0x01;      /* Indicates that the RGBC channels have completed an integration cycle */
//    int AMS_COLOR_CDATAL = 0x14;             /* Clear channel data */
//    int AMS_COLOR_CDATAH = 0x15;
//    int AMS_COLOR_RDATAL = 0x16;             /* Red channel data */
//    int AMS_COLOR_RDATAH = 0x17;
//    int AMS_COLOR_GDATAL = 0x18;             /* Green channel data */
//    int AMS_COLOR_GDATAH = 0x19;
//    int AMS_COLOR_BDATAL = 0x1A;             /* Blue channel data */
//    int AMS_COLOR_BDATAH = 0x1B;
//
//    protected AMSColorSensor.Parameters parameters;
//
//    //----------------------------------------------------------------------------------------------
//    // Construction
//    //----------------------------------------------------------------------------------------------
//
//    protected AMSColorSensorImproved(AMSColorSensor.Parameters params, I2cDeviceSynchSimple deviceClient, boolean isOwned)
//    {
//        super(deviceClient, isOwned);
//        this.parameters = params;
//        this.deviceClient.setLogging(this.parameters.loggingEnabled);
//        this.deviceClient.setLoggingTag(this.parameters.loggingTag);
//        super.registerArmingStateCallback(false);
//        this.engage();
//    }
//
//    public static AMSColorSensorImproved create(AMSColorSensor.Parameters parameters, I2cDevice i2cDevice)
//    {
//        I2cDeviceSynchSimple i2cDeviceSynchSimple = new I2cDeviceSynchImpl(i2cDevice, parameters.i2cAddr, false);
//        return create(parameters, i2cDeviceSynchSimple, true);
//    }
//
//    public static AMSColorSensorImproved create(AMSColorSensor.Parameters parameters, I2cDeviceSynchSimple i2cDevice, boolean isOwned)
//    {
//        AMSColorSensorImproved result = new AMSColorSensorImproved(parameters, i2cDevice, isOwned);
//        result.initialize(parameters);
//        return result;
//    }
//
//    //----------------------------------------------------------------------------------------------
//    // Initialization
//    //----------------------------------------------------------------------------------------------
//
//    public Parameters getParameters()
//    {
//        return this.parameters;
//    }
//
//    @Override
//    protected synchronized boolean doInitialize()
//    {
//        return initialize(this.parameters);
//    }
//
//    public synchronized boolean initialize(Parameters parameters)
//    {
//        if (initializeOnce(parameters)) {
//            this.isInitialized = true;
//            return true;
//        }
//        return false;
//    }
//
//    private synchronized boolean initializeOnce(Parameters parameters)
//    {
//        if (this.parameters != null && this.parameters.deviceId != parameters.deviceId) {
//            throw new IllegalArgumentException(String.format("can't change device types (modify existing params instead): old=%d new=%d", this.parameters.deviceId, parameters.deviceId));
//        }
//
//        // Remember the parameters for future use
//        this.parameters = parameters;
//
//        // Can't do anything if we're not really talking to the hardware
//        if (!this.deviceClient.isArmed()) {
//            return false;
//        }
//
//        // Make sure we're connected
//        this.engage();
//
//        // Make sure we're talking to the correct I2c device
//        this.setI2cAddress(parameters.i2cAddr);
//
//        // Verify that that's a color sensor!
//        byte id = this.getDeviceID();
//        if ((id != parameters.deviceId)) {
//            RobotLog.e("unexpected AMS color sensor chipid: found=%d expected=%d", id, parameters.deviceId);
//            return false;
//        }
//
//        reportEnable();
//        reportIntegrationTime();
//
//        // Set the gain and integration time
//        setIntegrationTime(IntegrationTime.MS_700);
//        setGain(parameters.gain);
//
//        reportEnable();
//        reportIntegrationTime();
//
//        // Enable the device
//        enable();
//        return true;
//    }
//
//    private synchronized void enable()
//    {
//        write8(Register.ENABLE, AMS_COLOR_ENABLE_PON);
//        delayLore(6); // Adafruit's sample implementation uses 3ms
//        write8(Register.ENABLE, AMS_COLOR_ENABLE_PON | AMS_COLOR_ENABLE_AEN);
//    }
//
//    private synchronized void disable()
//    {
//        /* Turn the device off to save power */
//        byte reg = read8(Register.ENABLE);
//        write8(Register.ENABLE, reg & ~(AMS_COLOR_ENABLE_PON | AMS_COLOR_ENABLE_AEN));
//    }
//
//    private void setIntegrationTime(IntegrationTime time)
//    {
//        write8(Register.ATIME, time.byteVal);
//    }
//
//    private boolean is3782()
//    {
//        return this.parameters.deviceId == AMS_TMD37821_ID || this.parameters.deviceId == AMS_TMD37823_ID;
//    }
//
//    private void setGain(Gain gain)
//    {
//        // 3782 must set bit 5 as 1. On 3472, that must be written as zero. Ugh.
//        write8(Register.CONTROL, gain.bVal | (is3782() ? 0x20 : 0));
//    }
//
//    public void reportEnable()
//    {
//        int b = TypeConversion.unsignedByteToInt(read8(Register.ENABLE));
//        RobotLog.i("enable=0x%08x", b);
//    }
//
//    public void reportIntegrationTime()
//    {
//        int b = TypeConversion.unsignedByteToInt(read8(Register.ATIME));
//        RobotLog.i("integration=0x%08x", b);
//    }
//
//    public void reportGain()
//    {
//        int b = TypeConversion.unsignedByteToInt(read8(Register.CONTROL));
//        RobotLog.i("gain=0x%08x", b);
//    }
//
//    public byte getDeviceID()
//    {
//        return this.read8(Register.DEVICE_ID);
//    }
//
//    @Override
//    public synchronized int red()
//    {
//        return this.readColorRegister(Register.RED);
//    }
//
//    @Override
//    public synchronized int green()
//    {
//        return this.readColorRegister(Register.GREEN);
//    }
//
//    @Override
//    public synchronized int blue()
//    {
//        return this.readColorRegister(Register.BLUE);
//    }
//
//    @Override
//    public synchronized int alpha()
//    {
//        return this.readColorRegister(Register.ALPHA);
//    }
//
//    private int readColorRegister(Register reg)
//    {
//        return readUnsignedShort(reg);
//    }
//
//    @Override
//    public synchronized int argb()
//    {
//        byte[] bytes = read(Register.ALPHA, 8);
//        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
//        //
//        int clear = TypeConversion.unsignedShortToInt(buffer.getShort());
//        int red = TypeConversion.unsignedShortToInt(buffer.getShort());
//        int green = TypeConversion.unsignedShortToInt(buffer.getShort());
//        int blue = TypeConversion.unsignedShortToInt(buffer.getShort());
//        //
//        return Color.argb(clear, red, green, blue);
//    }
//
//    @Override
//    public synchronized void enableLed(boolean enable)
//    // We can't directly control the LED with I2C; it's always on
//    {
//        throw new UnsupportedOperationException("controlling LED is not supported on this color sensor; use a digital channel for that.");
//    }
//
//    @Override
//    public synchronized I2cAddr getI2cAddress()
//    {
//        return this.deviceClient.getI2cAddr();
//    }
//
//    @Override
//    public synchronized void setI2cAddress(I2cAddr i2cAddr)
//    {
//        this.deviceClient.setI2cAddr(i2cAddr);
//    }
//
//    //----------------------------------------------------------------------------------------------
//    // HardwareDevice
//    //----------------------------------------------------------------------------------------------
//
//    @Override
//    public String getDeviceName()
//    {
//        return "AMS I2C Color Sensor";
//    }
//
//    @Override
//    public Manufacturer getManufacturer()
//    {
//        return Manufacturer.AMS;
//    }
//
//    //----------------------------------------------------------------------------------------------
//    // ColorSensor
//    //----------------------------------------------------------------------------------------------
//
//    @Override
//    public synchronized byte read8(final Register reg)
//    {
//        return deviceClient.read8(reg.bVal | AMS_COLOR_COMMAND_BIT);
//    }
//
//    @Override
//    public synchronized byte[] read(final Register reg, final int cb)
//    {
//        return deviceClient.read(reg.bVal | AMS_COLOR_COMMAND_BIT, cb);
//    }
//
//    @Override
//    public synchronized void write8(Register reg, int data)
//    {
//        this.deviceClient.write8(reg.bVal | AMS_COLOR_COMMAND_BIT, data);
//        this.deviceClient.waitForWriteCompletions(I2cWaitControl.ATOMIC);
//    }
//
//    @Override
//    public synchronized void write(Register reg, byte[] data)
//    {
//        this.deviceClient.write(reg.bVal | AMS_COLOR_COMMAND_BIT, data);
//        this.deviceClient.waitForWriteCompletions(I2cWaitControl.ATOMIC);
//    }
//
//    public int readUnsignedShort(Register reg)
//    {
//        byte[] bytes = this.read(reg, 2);
//        int result = 0;
//        if (bytes.length == 2) {
//            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
//            result = TypeConversion.unsignedShortToInt(buffer.getShort());
//        }
//        return result;
//    }
//
//    public void writeShort(Register ireg, int value)
//    {
//        byte[] bytes = TypeConversion.shortToByteArray((short) value, ByteOrder.LITTLE_ENDIAN);
//        this.write(ireg, bytes);
//    }
//
//    /**
//     * delay() implements a delay that is specified in the device datasheet and therefore should be correct
//     *
//     * @see #delayLore(int)
//     */
//    void delay(int ms)
//    {
//        try {
//            Thread.sleep(ms);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    /**
//     * delayLore() implements a delay that only known by lore and mythology to be necessary.
//     *
//     * @see #delay(int)
//     */
//    private void delayLore(int ms)
//    {
//        try {
//            Thread.sleep(ms);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//
//}
//
///*
//  some code borrowed from Adafruit's sample implementation at:
//  https://github.com/adafruit/Adafruit_TCS34725
// */
