package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Graphics;

/**
 * The health bar is a heads up display element that shows the player's
 * current health as a number and a visual bar.
 *
 * @author Jeff Niu
 */
public class HealthBar extends HUDAbstractElement {

    private static final int GAUGE = 2;
    private static final int FILL = 0;
    private static final int OFFSET = 50;

    private final List<SpriteSheet> sprites;

    /**
     * The reference to the play that has health.
     */
    private final Player player;
    /**
     * The health bar indicator that is filled according to the player's
     * current health.
     */
    private final HealthBarIndicator indicator;

    /**
     * Used to keep track of the player's maximum health.
     */
    private final double maxHealth;

    private final int length;

    /**
     * The player's current health.
     */
    private double health;

    /**
     * Create the indicator and load the sprites.
     */
    {
        sprites = SpriteLoader.get().loadSprites(Bank.getSpriteRef(this));
        setX(OFFSET);
        setY(getGame().getScreen().getHeight() - OFFSET);
        indicator = new HealthBarIndicator();
    }

    /**
     * Create the health bar.
     *
     * @param game
     * @param player
     */
    public HealthBar(Game game, Player player) {
        super(game);
        this.player = player;
        maxHealth = player.getHealth();
        health = maxHealth;
        length = (int) (Math.log10(maxHealth) * 400.0 / 3.0);
    }

    /**
     * Update the health bar to reflect changes in the player's health.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        health = player.getHealth();
        if (health < 0) {
            health = 0;
        }
        indicator.update(dt);
    }

    /**
     * Draw the health bar. Fill it according to the player's health. Also
     * draw the health indicator.
     *
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        final SpriteSheet part = sprites.get(GAUGE);
        final Sprite pStart = part.get(0);
        final Sprite pMid = part.get(1);
        final Sprite pEnd = part.get(2);
        for (int i = 0; i < length; i++) {
            final float x = (float) getX() + i;
            final float y = (float) getY();
            if (i == 0) {
                pStart.draw(x + part.getOffsetX(0), y + part.getOffsetY(0));
            } else if (i == length - 1) {
                pEnd.draw(x + part.getOffsetX(2), y + part.getOffsetY(2));
            } else {
                pMid.draw(x + part.getOffsetX(1), y + part.getOffsetY(1));
            }
        }
        final SpriteSheet fill = sprites.get(FILL);
        final Sprite fStart = fill.get(0);
        final Sprite fMid = fill.get(1);
        final Sprite fEnd = fill.get(2);
        final double ratio = health / maxHealth;
        final int dLength = (int) (ratio * length);
        for (int i = 0; i < dLength; i++) {
            final float x = (float) getX() + fill.getOffsetX() + i;
            final float y = (float) getY() + fill.getOffsetY();
            if (i == 0) {
                fStart.draw(x, y);
            } else if (i == dLength - 1) {
                fEnd.draw(x, y);
            } else {
                fMid.draw(x, y);
            }
        }
        indicator.draw(g);
    }

    /**
     * The health bar indicator is class that represents the player's
     * health as a number. Rather than simply drawing the health with one
     * of the default fonts, custom sprites were used.
     */
    private class HealthBarIndicator extends HUDAbstractElement {

        private final SpriteSheet sprite;
        private final List<Sprite> number;

        {
            sprite = SpriteLoader.get().loadSprites(Bank.getSpriteRef(this)).get(0);
        }

        private HealthBarIndicator() {
            super(HealthBar.this.getGame());
            number = new ArrayList<>(0);
        }

        /**
         * Since the frame number of each number in the indicator sprite
         * sheet is the same as the number it represents, convert a number
         * into a integer array.
         *
         * @param number
         * @return
         */
        private int[] toIntArray(int number) {
            char[] chars = String.valueOf(number).toCharArray();
            int[] a = new int[chars.length];
            for (int i = 0; i < chars.length; i++) {
                a[i] = Integer.parseInt(String.valueOf(chars[i]));
            }
            return a;
        }

        /**
         * Add a sprite that represents each digit of the health.
         *
         * @param dt
         */
        @Override
        public void update(int dt) {
            int[] nums = toIntArray((int) health);
            for (int num : nums) {
                number.add(sprite.get(num));
            }
        }

        /**
         * Draw all the digits.
         *
         * @param g
         */
        @Override
        public void draw(Graphics g) {
            final HealthBar healthBar = HealthBar.this;
            final float y = (float) healthBar.getY() + 2;
            float x = (float) healthBar.getX() + 2;
            for (final Sprite s : number) {
                s.draw(x, y);
                x += s.getWidth();
            }
            number.clear();
        }

    }

}
