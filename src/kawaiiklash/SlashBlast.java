package kawaiiklash;

import static kawaiiklash.Utility.randInt;

/**
 * 
 * @author Jeff Niu
 */
public class SlashBlast extends AttackMelee {

    public SlashBlast(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public SlashBlast(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, "effect", new int[]{5}, Monster.class);
        setDamage(new Bound(100, 110));
        setKnockback(new Bound(120, 130));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT, 0);
    }

    @Override
    public int getHitType() {
        return HIT[randInt(0, HIT.length - 1)];
    }

}
