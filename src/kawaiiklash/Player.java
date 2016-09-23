package kawaiiklash;

import static java.lang.Math.round;
import java.util.Collections;
import java.util.List;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;
import static kawaiiklash.Utility.fail;
import kawaiiklash.exception.NoHitboxException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * A very specialized Entity class which will be the Entity controlled by
 * the player.
 *
 * @author Jeff Niu
 * @version 4 March 2015
 */
public class Player extends Entity implements Logicable, Attacker, AttackTarget {

    // Constant for whether or not a player is reliably jumping
    private static final double IS_JUMPING_MARGIN = 250.0;
    private static final double JUMP_THRESHOLD = 400.0;

    private static final int INVINCIBLE = 1_000;
    private static final int VULNERABLE = -1;
    private static final int FLASHES = 5;

    private static final double RESIST = 1_500.0;
    private static final double KNOCK_HEIGHT = 600.0;
    private static final int PRONE_HITBOX_REDUCTION = 47;

    // State reference for the hitbox Sprite
    protected final int HITBOX = getStateNumber("naked");

    // State constants for the player
    private final int WALK = getStateNumber("walk1");
    private final int STAND = getStateNumber("stand1");
    private final int PRONE = getStateNumber("prone");
    private final int JUMP = getStateNumber("jump");
    private final int GHOST = getStateNumber("ghost");

    // State flags
    private boolean jumping;
    private boolean prone;
    private boolean attacking;
    private boolean moving;
    private boolean hit;

    // Player stats
    private Bound speed;
    private double accel;
    private double health;
    private double defense;
    private double stance;
    private double attack;
    private double jump;
    private double maxHealth = -1;

    // Sounds specific to the player
    private List<Sound> sounds;
    private Sound jumpSound;
    private Sound hitSound;
    private Sound deathSound;

    // State counter for the player
    private Direction nextDir;
    private int count;
    private int invincibleCount;
    private int jumpCount;

    /**
     * The health bar.
     */
    private HealthBar healthbar;

    // Whether or not the Player should cause screen scrolling
    private boolean scroll = true;

    /**
     * Constructs an empty Player Object with no specified attributes other
     * than the Game to which it belongs. This constructor is called while
     * reading level files to record the Player location. The player will
     * then be casted through a subclass constructor into a specific Player
     * class.
     *
     * @param game
     */
    public Player(Game game) {
        super(game);
    }

    /**
     * Auxiliary method used to cast a Player into a class. Works in
     * conjunction with the above constructor.
     *
     * @param player
     */
    public Player(Player player) {
        this(player.getGame());

        // Get the position
        setX(player.getX());
        setY(player.getY());

        // Intiailize the stats
        speed = new Bound(300, 320);
        accel = 100.0;
        health = 1000.0;
        defense = 0.0;
        stance = 0.0;
        attack = 150.0;
        jump = 540.0;

        // Set the movement
        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(speed.upper());
        setMaxDy(GRAVITY);

        // The Player will face Direction.RIGHT
        setDirX(Direction.RIGHT);

        // State variables
        jumping = false;
        prone = false;
        attacking = false;
        moving = false;
        hit = false;

        // Intialize counters
        count = 0;
        jumpCount = 0;
        invincibleCount = -1;

        // Initialize to state stand
        setActive(true);
        setState(STAND);

        // Load the sounds
        sounds = SoundLoader.get().loadSounds(Bank.getSoundRef("Player"));
        for (Sound sound : sounds) {
            if (sound.getName().equals("jump")) {
                jumpSound = sound;
            }
            if (sound.getName().equals("hit")) {
                hitSound = sound;
            }
            if (sound.getName().equals("death")) {
                deathSound = sound;
            }
        }

        // Make sure the hitbox exists
        try {
            int hitbox = getStateNumber("naked");
            if (hitbox == -1) {
                throw new NoHitboxException("Could not find hitbox for: " + this);
            }
        } catch (NoHitboxException ex) {
            fail(ex);
        }

        // Initialize the UserInterface
        healthbar = null;
    }

    /**
     * Player hitbox will be simply the dimensions and position of the
     * standard state. This method will use the STATE_STAND as the standard
     * hitbox and use layer zero.
     *
     * @return the hitbox of the player
     */
    @Override
    public Rect getHitbox() {
        final SpriteSheet sheet = getSprites().get(HITBOX);
        final double hx = getX() + sheet.getOffsetX();
        final double hw = sheet.getWidth();
        double hy = getY() + sheet.getOffsetY();
        double hh = sheet.getHeight();
        if (getState() == PRONE) {
            hy += PRONE_HITBOX_REDUCTION;
            hh -= PRONE_HITBOX_REDUCTION;
        }
        return new Rect(hx, hy, hw, hh);
    }

    /**
     * Have the player act accordingly to colliding with an Entity. If the
     * player collides with a Tile, have it will hit it as a static object.
     *
     * @param other
     */
    @Override
    public void collidedWith(Collideable other) {
        if (other instanceof Platform) {
            Platform p = (Platform) other;
            Rect tile = p.getHitbox();
            SpriteSheet hitbox = getSprites().get(HITBOX);
            Direction dir = getCollideDir(tile);
            Direction dirY = getDirY();
            double x = getX();
            double y = getY();
            if (dir == LEFT) {
                setDx(0);
                x = tile.getX() - hitbox.getWidth() - hitbox.getOffsetX();
            }
            if (dir == RIGHT) {
                setDx(0);
                x = tile.getX() + tile.getWidth() - hitbox.getOffsetX();
            }
            if (dir == UP && dirY == DOWN) {
                setDy(0);
                if (hit) {
                    hit = false;
                    setDdx(0);
                    setDirX(nextDir);
                }
                jumping = false;
                jumpCount = 0;
                y = tile.getY() - hitbox.getHeight() - hitbox.getOffsetY();
            }
            if (dir == DOWN && dirY == UP) {
                setDy(0);
                y = tile.getY() + tile.getHeight() - hitbox.getOffsetY();
            }
            setX(x);
            setY(y);
        }
        if (other instanceof Monster) {
            if (invincibleCount == VULNERABLE) {
                Monster m = (Monster) other;
                takeDamage(m, new MonsterAttack(m));
            }
        }
    }

    @Override
    public void takeDamage(Attacker m, Attack attack) {
        invincibleCount = 0;
        int flashDelay = INVINCIBLE / FLASHES / 2;
        Effects.flash(Color.red, getGame(), getSprites(), FLASHES, flashDelay, flashDelay);
        hitSound.playEffect(getGame().getPitch(), getGame().getGain(), false);
        hit = true;
        prone = false;
        attacking = false;
        changeToState(JUMP);
        Direction dir = getHorCollideDir(m.getHitbox());
        nextDir = dir.inverse();
        double dmg = attack.getDamage().rand();
        double def = defense * (1 - attack.getBreach());
        double gradient = m.getAttack() - def;
        if (gradient > 0) {
            dmg *= Monster.damageFunction().apply(gradient, m.getAttack());
            setHealth(health - dmg);
        }
        double kbx = attack.getKnockback().rand() * (1 - stance);
        double kby = KNOCK_HEIGHT * (1 - stance);
        if (kbx > 0) {
            setDx(kbx * dir.unit());
            setDdx(RESIST * -dir.unit());
            setDirX(nextDir);
        }
        if (kby > 0) {
            setDy(-kby);
        }
        if (health <= 0 && !isDead()) {
            died();
        }
    }

    /**
     * Request that the Player Entity update. It will check the screen
     * scrolling and user input. It will also update counters.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        if (maxHealth == -1) {
            maxHealth = health;
        }
        if (healthbar == null) {
            Game game = getGame();
            HUD userInterface = game.getHUD();
            healthbar = new HealthBar(game, this);
            userInterface.addElement(healthbar);
        }
        setCount(getCount() + dt);
        if (jumpCount >= 0) {
            jumpCount += dt;
        }
        if (invincibleCount >= 0) {
            invincibleCount += dt;
        }
        if (invincibleCount > INVINCIBLE) {
            invincibleCount = VULNERABLE;
        }
        if (!hit && !isDead()) {
            checkInput();
        }
        super.update(dt);
        if (isDead() && isActive()) {
            move(dt);
        }
        if (scroll) {
            checkScreen();
        }
    }

    /**
     * Check the player input and act accordingly.
     * <p>
     * Consider reworking the Player logic since it is a bit messy
     * <p>
     */
    public void checkInput() {
        boolean[] keyDown = getGame().keyDown();
        double dx = getDx();
        double dy = getDy();
        if (!attacking) {
            if (!prone) {
                if (keyDown[Input.KEY_LEFT] && !keyDown[Input.KEY_RIGHT]) {
                    int vector = Direction.ofKey(Input.KEY_LEFT).unit();
                    if (dx == 0 && !attacking && !moving) {
                        dx = speed.lower() * vector;
                        setDdx(accel * vector);
                        prone = false;
                        moving = true;
                    } else if (dx > speed.lower() * vector) {
                        dx = speed.lower() * vector;
                    }
                }
                if (keyDown[Input.KEY_RIGHT] && !keyDown[Input.KEY_LEFT]) {
                    int vector = Direction.ofKey(Input.KEY_RIGHT).unit();
                    if (dx == 0 && !attacking && !moving) {
                        dx = speed.lower() * vector;
                        setDdx(accel * vector);
                        prone = false;
                        moving = true;
                    } else if (dx < speed.lower() * vector) {
                        dx = speed.lower() * vector;
                    }
                }
            }
            if ((!keyDown[Input.KEY_RIGHT] && !keyDown[Input.KEY_LEFT]) || (keyDown[Input.KEY_RIGHT] && keyDown[Input.KEY_LEFT])) {
                setDdx(0);
                dx = 0.0;
                moving = false;
            }
            if (keyDown[Input.KEY_LEFT] && !keyDown[Input.KEY_RIGHT]) {
                setDirX(LEFT);
            }
            if (keyDown[Input.KEY_RIGHT] && !keyDown[Input.KEY_LEFT]) {
                setDirX(RIGHT);
            }
            if (keyDown[Input.KEY_SPACE] && !jumping && round(dy / 100) == 0 && !attacking) {
                jumpSound.playEffect(getGame().getPitch(), getGame().getGain(), false);
                dy = -jump;
                prone = false;
                jumping = true;
                jumpCount = 0;
            }
            if (keyDown[Input.KEY_SPACE] && jumpCount >= 0 && jumpCount <= JUMP_THRESHOLD && jumping && !attacking && dy < 0) {
                dy = -jump;
                prone = false;
                jumping = true;
            }
            if (jumping && !keyDown[Input.KEY_SPACE]) {
                jumpCount = -1;
            }
            if (keyDown[Input.KEY_DOWN] && !jumping) {
                dx = 0.0;
                prone = true;
            }
            if (!keyDown[Input.KEY_DOWN]) {
                prone = false;
            }
        } else {
            if (!jumping) {
                dx = 0.0;
            }
        }
        setDx(dx);
        setDy(dy);
    }

    /**
     * Override the Entity updateDirection() method to update only the
     * vertical unit in this method. Updating horizontal unit is now the
     * responsibility of the checkInput() method, which updates the
     * horizontal unit based on Game.keyDown()[].
     */
    @Override
    public void updateDirection() {
        if (getDy() > 0) {
            setDirY(DOWN);
        }
        if (getDy() < 0) {
            setDirY(UP);
        }
        if (hit) {
            if (getDx() > 0) {
                setDirX(RIGHT);
            }
            if (getDx() < 0) {
                setDirX(LEFT);
            }
        }
    }

    @Override
    public void doLogic() {
        updateDirection();
    }

    /**
     * Request that the Entity update its state. All state-changing will be
     * controlled from this method.
     */
    @Override
    public void updateState(int dt) {
        if (!isDead()) {
            if (!attacking) {
                if (getDx() != 0 && !jumping) {
                    changeToState(WALK);
                } else if (jumping && getState() != PRONE) {
                    changeToState(STAND);
                }
                if (jumping) {
                    changeToState(JUMP);
                }
                if (prone) {
                    changeToState(PRONE);
                }
                if (!prone && !jumping && getDx() == 0) {
                    changeToState(STAND);
                }
                if (Math.abs(getDy()) > IS_JUMPING_MARGIN) {
                    jumping = true;
                    changeToState(JUMP);
                }
            }
            if (hit && (getDx() * Direction.dirOf(getDdx()).unit() > 0 || getDx() == 0)) {
                setDx(0);
                setDdx(0);
                setDirX(nextDir);
                hit = false;
            }
        }
    }

    /**
     * Checks to see if the Player has moved into the border around the
     * Screen. If so, move the screen to push the player back into the
     * border.
     */
    public void checkScreen() {
        final Game game = getGame();
        final Rect border = game.getBorder();
        final Rect hitbox = getHitbox();
        final double x = getX();
        final double y = getY();
        if (x + hitbox.getWidth() > border.getX() + border.getWidth()) {
            final double moveX = x + hitbox.getWidth() - border.getX() - border.getWidth();
            game.xScroll(moveX);
        }
        if (x < border.getX()) {
            double moveX = x - border.getX();
            game.xScroll(moveX);
        }
        if (y + hitbox.getHeight() > border.getY() + border.getHeight()) {
            double moveY = y + hitbox.getHeight() - border.getY() - border.getHeight();
            game.yScroll(moveY);
        }
        if (y < border.getY()) {
            double moveY = y - border.getY();
            game.yScroll(moveY);
        }
    }

    @Override
    protected void drawEntity(Graphics g) {
        SpriteSheet box = getSprites().get(HITBOX);
        SpriteSheet ss = getSprites().get(getState());
        Sprite s = ss.getSprite();
        if (hit) {
            s = s.flipHorizontal();
        }
        double x;
        double y = getY() + ss.getOffsetY();
        if (getDirX() == DEFAULT_DIR) {
            x = getX() + ss.getOffsetX();
        } else {
            x = getX() + 2 * box.getOffsetX() + box.getWidth() - ss.getOffsetX() - ss.getWidth();
            s = s.flipHorizontal();
        }
        s.draw((float) x, (float) y);
    }

    /**
     * If the Player leaves the screen bounds, kill it.
     *
     * @param dir
     */
    @Override
    public void isOutOfBounds(Direction dir) {
        if (dir == DOWN && !isDead()) {
            died();
        }
    }

    @Override
    public void isInBounds() {
    }

    @Override
    public double getAttack() {
        return attack;
    }

    /**
     * Tell the Player that it has died.
     */
    @Override
    public void died() {
        health = 0;
        setDead(true);
        Game game = getGame();
        game.add(new Tombstone(game, getX()));
        deathSound.playEffect(game.getPitch(), game.getGain(), false);
        changeToState(GHOST);
        setDy(-50);
        setDdy(-800);
        setDdx(0);
        scroll = false;
    }

    public void scroll(boolean scroll) {
        this.scroll = scroll;
    }

    public void itemPickup(Item item) {
        if (item instanceof HealthPotion) {
            health += ((HealthPotion) item).getRestoration() * maxHealth;
            health = health > maxHealth ? maxHealth : health;
        }
    }

    /**
     * Overridden method for setting the x value of this Player.
     *
     * @param x
     */
    @Override
    public void x(String x) {
        setX(Integer.parseInt(x));
    }

    /**
     * Overridden method for setting the y value of this Player.
     *
     * @param y
     */
    @Override
    public void y(String y) {
        setY(Integer.parseInt(y));
    }

    /**
     * @return the attacking
     */
    public boolean isAttacking() {
        return attacking;
    }

    /**
     * @param attacking the attacking to set
     */
    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(Bound speed) {
        this.speed = speed;
    }

    /**
     * @param accel the accel to set
     */
    public void setAccel(double accel) {
        this.accel = accel;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(double health) {
        this.health = health;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * @param defense the defense to set
     */
    public void setDefense(double defense) {
        this.defense = defense;
    }

    /**
     * @param stance the stance to set
     */
    public void setStance(double stance) {
        this.stance = stance;
    }

    /**
     * @param attack the attack to set
     */
    public void setAttack(double attack) {
        this.attack = attack;
    }

    /**
     * @param jump the jump to set
     */
    public void setJump(double jump) {
        this.jump = jump;
    }

    @Override
    public SpriteSheet getHitboxSheet() {
        return getSprites().get(HITBOX);
    }

    @Override
    public boolean isDamageable() {
        return invincibleCount == VULNERABLE;
    }

    @Override
    public List<Sound> getSounds() {
        return Collections.unmodifiableList(sounds);
    }

    @Override
    public int getZ() {
        return Drawable.PLAYER;
    }

}
