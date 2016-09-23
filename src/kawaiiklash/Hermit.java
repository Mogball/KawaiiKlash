package kawaiiklash;

import org.newdawn.slick.Input;

/**
 * The hermit is a ranged player class. It has three attacks: shadow spark
 * launches a large projectile in a straight line. This attack is knockback
 * attack that pierces enemies. Quad star is an attack that throws four
 * stars with knockback. It deals high damage but has a short charge up
 * time. Quintuple throw shoots five stars. It has the highest damage per
 * second rating and not charge up but has zero knockback.
 *
 * @author Jeff Niu
 */
public class Hermit extends Player {

    static {
        SpriteLoader sprite = SpriteLoader.get();
        SoundLoader sound = SoundLoader.get();
        sprite.preloadSprite(Bank.getSpriteRef("ShadowSpark"));
        sprite.preloadSprite(Bank.getSpriteRef("QuadStar"));
        sprite.preloadSprite(Bank.getSpriteRef("QuintupleThrow"));
        sound.preloadSound(Bank.getSoundRef("ShadowSpark"));
        sound.preloadSound(Bank.getSoundRef("QuadStar"));
        sound.preloadSound(Bank.getSoundRef("QuintupleThrow"));
    }

    private final int[] STATE_SWING = getStateNumberSet("swingO");
    private final int STATE_THROW = getStateNumber("throw");
    private final int STATE_SLASH = getStateNumber("swingTF");

    private final int[] DELAY_SWING = getStateDelay(STATE_SWING);
    private final int DELAY_THROW = getStateDelay(STATE_THROW);
    private final int DELAY_SLASH = getStateDelay(STATE_SLASH);

    private int attackSwingType;

    public Hermit(Player player) {
        super(player);
    }

    @Override
    public void checkInput() {
        super.checkInput();
        if (!isAttacking()) {
            if (getGame().keyDown()[Input.KEY_LSHIFT]) {
                setAttacking(true);
                attackShadowSpark();
            } else if (getGame().keyDown()[Input.KEY_Z]) {
                setAttacking(true);
                attackQuadStar();
            } else if (getGame().keyDown()[Input.KEY_LCONTROL]) {
                setAttacking(true);
                attackTripleThrow();
            }
        }
    }

    @Override
    public void updateState(int dt) {
        if (isAttacking()) {
            int state = getState();
            if (state == STATE_SWING[attackSwingType] && getCount() > DELAY_SWING[attackSwingType]) {
                setAttacking(false);
            }
            if (state == STATE_THROW && getCount() > DELAY_THROW) {
                setAttacking(false);
            }
            if (state == STATE_SLASH && getCount() > DELAY_SLASH) {
                setAttacking(false);
            }
        }
        super.updateState(dt);
    }

    private void attackShadowSpark() {
        setCount(0);
        attackSwingType = 2;
        cycleFrames(STATE_SWING, false);
        changeToState(STATE_SWING[attackSwingType]);

        getGame().add(new ShadowSpark(this).create());
    }

    private void attackQuadStar() {
        setCount(0);
        cycleFrames(STATE_THROW, false);
        changeToState(STATE_THROW);

        getGame().add(new QuadStar(this).create());
    }

    private void attackTripleThrow() {
        setCount(0);
        cycleFrames(STATE_SLASH, false);
        changeToState(STATE_SLASH);

        getGame().add(new QuintupleThrow(this).create());
    }

}
