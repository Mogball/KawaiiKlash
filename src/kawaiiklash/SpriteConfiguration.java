/*
 * A class to store one entry in a the data.xml file.
 */
package kawaiiklash;

import java.util.Scanner;

/**
 * A class which stores information read from the data.xml file that
 * concerns a single Sprite. It will store the y-offset, x-offset,
 * animation delay, and reference for that Sprite. On the other hand, the
 * entire data.xml file will contain a set of SpriteConfiguration items for
 * the set of SpriteSheets. Everything is stored as a String but will be
 * converted to other types as needed.
 *
 * @author Jeff Niu
 * @version 28 February 2015
 */
public class SpriteConfiguration {

    // The coordinate offsets of the Sprite
    // The y-offset comes before the x-offset because the file is created as such
    private int offsetY;
    private int offsetX;

    // The animation delay 
    private int delay;

    // The reference for the Image representing the Sprite
    private String image;

    /**
     * Get the offset in the y-direction.
     *
     * @return the offset
     */
    public int getOffsetY() {
        return this.offsetY;
    }

    /**
     * Set the y-direction offset.
     *
     * @param offsetY the offset
     */
    public void setOffsetY(String offsetY) {
        this.offsetY = Integer.parseInt(offsetY);
    }

    /**
     * Get the offset in the x-direction.
     *
     * @return the offset
     */
    public int getOffsetX() {
        return this.offsetX;
    }

    /**
     * Set the x-direction offset.
     *
     * @param offsetX the offset
     */
    public void setOffsetX(String offsetX) {
        this.offsetX = Integer.parseInt(offsetX);
    }

    /**
     * Get the animation delay.
     *
     * @return the delay in milliseconds
     */
    public int getDelay() {
        return this.delay;
    }

    /**
     * Set the animation delay.
     *
     * @param delay the delay in milliseconds
     */
    public void setDelay(String delay) {
        this.delay = Integer.parseInt(delay);
    }

    /**
     * Get the reference for the Image.
     *
     * @return the reference
     */
    public String getRef() {
        return image;
    }

    /**
     * Set the reference for the Image.
     *
     * @param image the reference
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Returns the sheet name of this SpriteConfiguration. The reference
     * has the form [sheetName]_[frameNumber].png
     *
     * @return the sheet name
     */
    public String getSheetName() {
        String sheetName;
        try (Scanner referenceName = new Scanner(this.image)) {
            referenceName.useDelimiter("_");
            sheetName = referenceName.next();
        }
        return sheetName;
    }

    /**
     * Get the frame number of this Sprite Configuration. For example,
     * suppose we have a Sprite whose image is sheet2_5.png. This method
     * would return the frame number, which is 5.
     *
     * @return
     */
    public int getFrameNumber() {
        int frameNum;
        try (Scanner referenceName = new Scanner(this.image)) {
            referenceName.useDelimiter("_");
            referenceName.next();
            referenceName.useDelimiter(".");
            frameNum = -1;
            try {
                frameNum = Integer.parseInt(referenceName.next());
            } catch (NumberFormatException e) {
            }
        }
        return frameNum;
    }

    /**
     * Return a formatted String of the data fields.
     *
     * @return the String
     */
    @Override
    public String toString() {
        return String.format("SpriteConfig {offsetY = %s, offsetX = %s, delay = %s, image = %s}", offsetY, offsetX, delay, image);
    }

    /**
     * Dynamically-called method to set the x-offset.
     *
     * @param offsetX
     */
    public void x(String offsetX) {
        setOffsetX(offsetX);
    }

    /**
     * Dynamically-called method to set the y-offset.
     *
     * @param offsetY
     */
    public void y(String offsetY) {
        setOffsetY(offsetY);
    }

    /**
     * Dynamically-called method to set the delay.
     *
     * @param delay
     */
    public void delay(String delay) {
        setDelay(delay);
    }

    /**
     * Dynamically-called method to set the image name.
     *
     * @param image
     */
    public void image(String image) {
        setImage(image);
    }
}
