package kawaiiklash;

import java.awt.geom.Dimension2D;

/**
 *
 * @author Jeff Niu
 */
@SuppressWarnings("PublicField")
public class Dimensions extends Dimension2D {

    public double width;
    public double height;

    public Dimensions() {
        this(0, 0);
    }

    public Dimensions(Dimension2D d) {
        this(d.getWidth(), d.getHeight());
    }
    
    public Dimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Dimensions clone() {
        super.clone();
        return new Dimensions(width, height);
    }

}
