package kawaiiklash;

import static java.lang.Math.random;

/**
 * A health potion is an item drop that restores the player's health when
 * picked up.
 *
 * @author Jeff Niu
 */
public class HealthPotion extends Item {

    /**
     * There are three different kinds of health potions, each with their
     * unique sprite. They each health different amounts of health points.
     * Better potions are rarer.
     */
    private static final double[] HEALTH_RESTORATION = {
        0.125, 0.25, 0.5
    };

    /**
     * Calling this method will attempt to drop a certain health potion.
     * The drop rates vary according to the player's health. At full
     * health, there is a 70% chance of item drop, 10% of a rare potion,
     * and 30% of an uncommon potion. As the player's health approaches
     * zero, the drop rate is 100%, 25% for a rare potion, and 60% for an
     * uncommon potion.
     *
     * @param game
     * @param m
     * @param plr
     */
    public static void drop(Game game, Monster m, Player plr) {
        final double hp = plr.getHealth();
        final double maxHp = plr.getMaxHealth();
        final double drpHp = 0.8 * maxHp;
        final double topHp = 0.3 * maxHp;
        final double midHp = 0.5 * maxHp;
        final double chanceDrp = hp < drpHp ? 0.70 + (1 - hp / drpHp) * 0.30 : 0.70;
        final double chanceTop = hp < topHp ? 0.10 + (1 - hp / topHp) * 0.25 : 0.05;
        final double chanceMid = hp < midHp ? 0.30 + (1 - hp / midHp) * 0.60 : 0.10;
        double p = random();
        if (p <= chanceDrp) {
            double x = m.getX() + m.getOffsetX();
            double y = m.getY() + m.getOffsetY();
            p = random();
            if (p <= chanceTop) {
                drop(game, x, y, 2);
            } else if (p <= chanceMid) {
                drop(game, x, y, 1);
            } else {
                drop(game, x, y, 0);
            }
        }
    }

    /**
     * Create a new health potion based on a tier.
     *
     * @param game
     * @param x
     * @param y
     * @param tier
     */
    private static void drop(Game game, double x, double y, int tier) {
        HealthPotion pot = new HealthPotion(game, x, y, tier);
        game.add(pot);
    }

    /**
     * Constructor that can be used to add static health potions into the
     * level as power ups.
     *
     * @param game
     */
    public HealthPotion(Game game) {
        super(game);
    }

    /**
     * Private constructor for when a health potion is dropped.
     *
     * @param game
     * @param x
     * @param y
     * @param tier
     */
    private HealthPotion(Game game, double x, double y, int tier) {
        super(game, x, y);
        setState(tier);
    }

    /**
     * When the player touches this item, tell it that it has picked up the
     * health potion.
     *
     * @param player
     */
    @Override
    public void playerPickup(Player player) {
        player.itemPickup(this);
    }

    /**
     * Get the amount of HP healed by this potion. Corresponds the state.
     *
     * @return
     */
    public double getRestoration() {
        return HEALTH_RESTORATION[getState()];
    }

    @Parsable
    public void tier(String tier) {
        setState(Integer.parseInt(tier));
    }

}
