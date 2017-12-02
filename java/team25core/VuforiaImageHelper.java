package team25core;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class VuforiaImageHelper {

    private enum ImageType {
        VUFORIA,
        BITMAP,
    }

    private final static String HELPER_TAG = "Jewel Helper";

    private Image image;
    private Bitmap bitmap;
    private ByteBuffer pixels;
    private int rows;
    private int columns;
    private int stride;
    private final static int BYTES_PER_PIXEL = 2;
    private final static int RED = 0xF800;
    private final static int GREEN = 0x07E0;
    private final static int BLUE = 0x001F;
    private ImageType imageType;

    public VuforiaImageHelper(Image image)
    {
        this.image = image;
        this.pixels = image.getPixels();
        this.rows = image.getHeight();
        this.columns = image.getWidth();
        this.stride = image.getStride();
        this.imageType = ImageType.VUFORIA;
    }

    public VuforiaImageHelper(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        this.imageType = ImageType.BITMAP;
    }

    public byte[] getRow(int row)
    {
        byte buf[] = new byte[stride];

        pixels.get(buf, row * stride, stride);
        return buf;
    }

    public int getPixelLocation(int row, int col)
    {
        return ((row * stride) + (col * 2));
    }

    public int getPixel(int row, int col)
    {
        int red;
        int green;
        int blue;
        byte[] pixel = new byte[3];
        int loc = getPixelLocation(row, col);
        int rgbVal = pixels.getShort(loc);
        int noAlpha = 0x00FFFF & rgbVal;
        int msb = 0x00FF00 & noAlpha;
        int lsb = 0x0000FF & noAlpha;
        noAlpha = (lsb << 8) | (msb >> 8);
        return (noAlpha);
    }

    public double averagePixels(int row, int col, int width, int height)
    {
        double total = 0;

        RobotLog.i("\nImage Format: " + image.getFormat() +
                        "\nImage Size:   " + image.getWidth() + "x" + image.getHeight() +
                        "\nBuffer Size:  " + image.getBufferWidth() + "x" + image.getBufferHeight() +
                        "\nImage Stride: " + image.getStride());
        if (image.getFormat() != PIXEL_FORMAT.RGB565) {
            return -1;
        }

        for (int i = row; i < height; i++) {
            for (int j = col; j < width; j++) {
                total += getPixel(i, j);
            }
        }
        return (total / (width * height));
    }

    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1;
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public RGBColor getDominant(int row, int col, int width, int height)
    {
        switch (imageType) {
            case VUFORIA:
                return getImageDominant(row, col, width, height);
            case BITMAP:
                return getBitmapDominant(row, col, width, height);
        }

        return null;
    }

    public RGBColor getBitmapDominant(int row, int col, int width, int height)
    {
        TreeMap<Integer, Counter> map = new TreeMap<>();
        Counter pixel;
        int rgbVal;

        for (int i = row; i < row + height; i++) {
            for (int j = col; j < col + width; j++) {
                rgbVal = Integer.valueOf(bitmap.getPixel(i, j));
                pixel = map.get(rgbVal);
                if (pixel != null) {
                    pixel.increment();
                } else {
                    map.put(rgbVal, new Counter());
                }
            }
        }

        SortedSet<Map.Entry<Integer, Counter>> set = entriesSortedByValues(map);
        Map.Entry<Integer, Counter> e = set.last();
        Counter c = e.getValue();
        Integer k = e.getKey();
        RGBColor rgbColor = RGBColor.from888(k);
        RobotLog.ii(HELPER_TAG, "Dominant = %s, Size = %d", rgbColor.toString(), set.size());
        return rgbColor;
    }

    public RGBColor getImageDominant(int row, int col, int width, int height)
    {
        TreeMap<Integer, Counter> map = new TreeMap<>();
        Counter pixel;
        int rgbVal;

        RobotLog.i("\nImage Format: " + image.getFormat() +
                "\nImage Size:   " + image.getWidth() + "x" + image.getHeight() +
                "\nBuffer Size:  " + image.getBufferWidth() + "x" + image.getBufferHeight() +
                "\nImage Stride: " + image.getStride());
        if (image.getFormat() != PIXEL_FORMAT.RGB565) {
            return new RGBColor(0, 0, 0);
        }

        for (int i = row; i < height; i++) {
            for (int j = col; j < width; j++) {
                rgbVal = Integer.valueOf(getPixel(i, j));
                pixel = map.get(rgbVal);
                if (pixel != null) {
                    pixel.increment();
                } else {
                    map.put(rgbVal, new Counter());
                }
            }
        }

        SortedSet<Map.Entry<Integer, Counter>> set = entriesSortedByValues(map);
        Map.Entry<Integer, Counter> e = set.last();
        Counter c = e.getValue();
        Integer k = e.getKey();
        RGBColor rgbColor = RGBColor.from565(k);
        RobotLog.i(rgbColor.toString());
        return rgbColor;
    }
}
