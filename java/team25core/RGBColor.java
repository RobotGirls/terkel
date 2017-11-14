package team25core;

import com.qualcomm.robotcore.util.RobotLog;

public class RGBColor {

    public int red;
    public int blue;
    public int green;

    private final static int RED565 = 0xF800;
    private final static int GREEN565 = 0x07E0;
    private final static int BLUE565 = 0x001F;

    public RGBColor(int red, int blue, int green)
    {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public static RGBColor from565(int rgb565)
    {
        int red = ((rgb565 & RED565) >> 11);
        int green = ((rgb565 & GREEN565) >> 5);
        int blue = (rgb565 & BLUE565);
        red = 255/31 * red;
        blue = 255/31 * blue;
        green = 255/63 * green;
        return new RGBColor(red, green, blue);
    }

    public int to888()
    {
        return ((red << 16) | (green << 8) | (blue));
    }

    public boolean isBlack()
    {
        if ((red == 0) && (green == 0) && (blue == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public String toString()
    {
        return String.format("Red 0x%X, Green 0x%X, Blue 0x%X", red, green, blue);
    }
}
