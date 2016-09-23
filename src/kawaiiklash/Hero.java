package kawaiiklash;

import org.newdawn.slick.Input;

/**
 * The hero is a melee player class. To compensate for the fact that it is
 * a melee character in comparison with the high attack ranges and powerful
 * ranged attacks of the Mage and Hermit, respectively, the hero has
 * increased defense. It has three attacks. Slash blast is a melee attack
 * with a rather broad range. It attacks slightly forward from the player's
 * current position, has medium damage and medium knockback. Intrepid slash
 * deals incredible amounts of damage, hitting three times per attack. Each
 * strike deals smaller damage, but they build up on each other. So that
 * all the attacks actually hit, the attack has low overall knockback, but
 * it has some. Finally, radiant driver is an attack with a thin vertical
 * hitbox but a long horizontal hitbox. This attack deals low damage but
 * has incredible knockback.
 *
 * @author Jeff Niu
 */
public class Hero extends Player {

    static {
        SpriteLoader sprite = SpriteLoader.get();
        SoundLoader sound = SoundLoader.get();
        sprite.preloadSprite(Bank.getSpriteRef("SlashBlast"));
        sprite.preloadSprite(Bank.getSpriteRef("RadiantDriver"));
        sprite.preloadSprite(Bank.getSpriteRef("IntrepidSlash"));
        sound.preloadSound(Bank.getSoundRef("SlashBlast"));
        sound.preloadSound(Bank.getSoundRef("RadiantDriver"));
        sound.preloadSound(Bank.getSoundRef("IntrepidSlash"));
    }

    private final int STATE_SLASH = getStateNumber("slash");
    private final int[] STATE_SWING = getStateNumberSet("swingO");
    private final int[] STATE_STAB = getStateNumberSet("stabO");

    private final int DELAY_SLASH = getStateDelay(STATE_SLASH);
    private final int[] DELAY_SWING = getStateDelay(STATE_SWING);
    private final int[] DELAY_STAB = getStateDelay(STATE_STAB);

    private int attackSwingType;
    private int attackStabType;

    public Hero(Player player) {
        super(player);
        setDefense(30);
    }

    @Override
    public void checkInput() {
        super.checkInput();
        if (!isAttacking()) {
            if (getGame().keyDown()[Input.KEY_LSHIFT]) {
                setAttacking(true);
                attackSlashBlast();
            } else if (getGame().keyDown()[Input.KEY_LCONTROL]) {
                setAttacking(true);
                attackRadiantDriver();
            } else if (getGame().keyDown()[Input.KEY_Z]) {
                setAttacking(true);
                attackIntrepidSlash();
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
            if (state == STATE_STAB[attackStabType] && getCount() > DELAY_STAB[attackStabType]) {
                setAttacking(false);
            }
            if (state == STATE_SLASH && getCount() > DELAY_SLASH) {
                setAttacking(false);
            }
        }
        super.updateState(dt);
    }

    /**
     * Tell the Hero to do the SlashBlast attack.
     */
    private void attackSlashBlast() {
        setCount(0);
        attackSwingType = 3;
        cycleFrames(STATE_SWING, false);
        changeToState(STATE_SWING[attackSwingType]);

        getGame().add(new SlashBlast(this).create());
    }

    /**
     * Tell the Hero to do the RadiantDriver attack.
     */
    private void attackRadiantDriver() {
        setCount(0);
        attackStabType = 2;
        cycleFrames(STATE_STAB, false);
        changeToState(STATE_STAB[attackStabType]);

        getGame().add(new RadiantDriver(this).create());
    }

    /**
     * Tell the Hero to do the IntrepidSlash attack.
     */
    private void attackIntrepidSlash() {
        setCount(0);
        cycleFrames(STATE_SLASH, false);
        changeToState(STATE_SLASH);

        getGame().add(new IntrepidSlash(this).create());
    }
}
