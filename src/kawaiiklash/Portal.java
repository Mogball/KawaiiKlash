package kawaiiklash;

import java.util.ArrayList;
import java.util.List;

/**
 * The portal entity is the level completion entity. Upon touching an
 * active portal, the level will complete. The portal will not be open
 * until all bosses are defeated, if any.
 *
 * @author Jeff Niu
 */
public class Portal extends Entity {

    private final int PORTAL = getStateNumber("portalContinue");
    private final int START = getStateNumber("portalStart");
    private final int EXIT = getStateNumber("portalExit");
    private final int HITBOX = getStateNumber("naked");

    private List<Boss> bosses;

    private int count;

    public Portal(Game game) {
        super(game);

        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(0);
        setMaxDx(UNLIMITED);
        setMaxDy(UNLIMITED);

        setDraw(false);
        setActive(false);
        setDead(false);

        setState(START);
        getSprites().get(START).cycleFrames(false);
        getSprites().get(EXIT).cycleFrames(false);

        count = 0;
    }

    @Override
    public Rect getHitbox() {
        SpriteSheet hit = getSprites().get(HITBOX);
        Rect b = new Rect();
        b.x = getX() + hit.getOffsetX();
        b.y = getY() + hit.getOffsetY();
        b.width = hit.getWidth();
        b.height = hit.getHeight();
        return b;
    }

    @Override
    public void isOutOfBounds(Direction dir) {
    }

    @Override
    public void isInBounds() {
    }

    @Override
    public void update(int dt) {
        if (bosses == null) {
            bosses = new ArrayList<>(3);
            List<Object> objects = getGame().getObjects();
            for (Object object : objects) {
                if (object instanceof Boss) {
                    bosses.add((Boss) object);
                }
            }
        }
        super.update(dt);
        if (isActive()) {
            count += dt;
        } else {
            boolean bossDead = true;
            for (Boss boss : bosses) {
                bossDead = bossDead && boss.isDead();
            }
            setActive(bossDead);
            setDraw(bossDead);
        }
    }

    @Override
    public void updateState(int dt) {
        int state = getState();
        if (state == START && count >= getStateDelay(START)) {
            count = 0;
            changeToState(PORTAL);
        }
        if (state == EXIT && count >= getStateDelay(EXIT)) {
            getGame().levelCompleted();
        }
    }

    @Override
    public void collidedWith(Collideable other) {
        if (getState() == PORTAL && other instanceof Player) {
            Game game = getGame();
            Player plr = (Player) other;
            plr.setDead(true);
            plr.setActive(false);
            Effects.fade(Effects.Fade.OUT, getGame(), plr.getSprites(), getStateDelay(EXIT));
            List<Sound> sounds = plr.getSounds();
            for (Sound sound : sounds) {
                if (sound.getName().equals("complete")) {
                    sound.playEffect(game.getPitch(), game.getGain(), false);
                }
            }
            count = 0;
            changeToState(EXIT);
        }
    }

    @Override
    public void died() {
    }

    @Override
    public int getZ() {
        return Drawable.OTHER;
    }

}
