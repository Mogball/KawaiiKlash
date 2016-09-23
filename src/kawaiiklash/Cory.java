package kawaiiklash;

import java.util.List;
import org.newdawn.slick.Input;

/**
 *
 * @author Jeff Niu
 */
public class Cory extends Player {

    /**
     *
     * @param player
     */
    public Cory(Player player) {
        super(player);
        setSpeed(new Bound(1000, 1200));
        setHealth(5000);
        setAccel(10000);
        setStance(1.00);
        setDefense(1000000);
        setJump(1000);
    }

    @Override
    public void checkInput() {
        super.checkInput();
        boolean[] key = getGame().keyDown();
        if (key[Input.KEY_LSHIFT]) {
            List<Object> objects = getGame().getObjects();
            for (Object o : objects) {
                if (o instanceof Monster) {
                    Entity e = (Entity) o;
                    if (e.canCollide()) {
                        e.died();
                    }
                }
            }
        }
        if (key[Input.KEY_Z]) {
            getGame().add(new ShadowSpark(this).create());
        }
        if (key[Input.KEY_LCONTROL]){ 
            final BlueMushroom bm = new BlueMushroom(getGame());
            bm.setX(getX());
            bm.setY(getY());
            bm.setDirX(getDirX());
            getGame().add(bm);
        }
    }

}
