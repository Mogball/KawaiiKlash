package kawaiiklash;

import static kawaiiklash.Utility.randInt;
import org.newdawn.slick.Input;

/**
 * A mage is a player character that uses spells, or really just a
 * combination of ranged and melee attacks. The melee-based magic attacks
 * have a very large range and procuration speed but low damage. Thunder
 * bolt, for example, hits a large area with, well, thunder. Holy arrow
 * hits a long, thin area with medium damage and small knockback. Flame orb
 * shoots a projectile that is fired from wherever the attack was procured
 * so it is easy to aim with it. It pierces enemies.
 *
 * @author Jeff Niu
 */
public class Mage extends Player {

    static {
        SpriteLoader sprite = SpriteLoader.get();
        SoundLoader sound = SoundLoader.get();
        sprite.preloadSprite(Bank.getSpriteRef("FlameOrb"));
        sprite.preloadSprite(Bank.getSpriteRef("ThunderBolt"));
        sprite.preloadSprite(Bank.getSpriteRef("HolyArrow"));
        sound.preloadSound(Bank.getSoundRef("FlameOrb"));
        sound.preloadSound(Bank.getSoundRef("ThunderBolt"));
        sound.preloadSound(Bank.getSoundRef("HolyArrow"));
    }

    private final int[] STATE_SWING = getStateNumberSet("swingO");
    private final int[] DELAY_SWING = getStateDelay(STATE_SWING);

    private int attackSwingType;

    public Mage(Player player) {
        super(player);
    }

    @Override
    public void checkInput() {
        super.checkInput();
        if (!isAttacking()) {
            if (getGame().keyDown()[Input.KEY_LSHIFT]) {
                setAttacking(true);
                attackHolyArrow();
            } else if (getGame().keyDown()[Input.KEY_LCONTROL]) {
                setAttacking(true);
                attackFlameOrb();
            } else if (getGame().keyDown()[Input.KEY_Z]) {
                setAttacking(true);
                attackThunderBolt();
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
        }
        super.updateState(dt);
    }

    private void attackFlameOrb() {
        setCount(0);
        attackSwingType = randInt(0, STATE_SWING.length - 1);
        cycleFrames(STATE_SWING, false);
        changeToState(STATE_SWING[attackSwingType]);

        getGame().add(new FlameOrb(this).create());
    }

    private void attackThunderBolt() {
        setCount(0);
        attackSwingType = randInt(0, STATE_SWING.length - 1);
        cycleFrames(STATE_SWING, false);
        changeToState(STATE_SWING[attackSwingType]);

        getGame().add(new ThunderBolt(this).create());
    }

    private void attackHolyArrow() {
        setCount(0);
        attackSwingType = randInt(0, STATE_SWING.length - 1);
        cycleFrames(STATE_SWING, false);
        changeToState(STATE_SWING[attackSwingType]);

        getGame().add(new HolyArrow(this).create());
    }

}
