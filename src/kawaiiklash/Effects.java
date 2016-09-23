package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;

/**
 * A singleton controller that handles graphical effects like fading in and
 * out, flashing certain colors, etc. These effects are dependent on the
 * game's updating cycle which means that they must somehow be updated
 * according to the passed time. That is what the effects controller does.
 * Whenever a new effect is created on a sprite sheet, that effect object
 * is added to the controller's list. The controller, as a singleton, is
 * added once to the game objects list and updated with it. It also makes
 * sure that there is exactly one copy of itself in the game list at all
 * times.
 *
 * @author Jeff Niu
 */
public class Effects implements Updateable {

    /**
     * The single instance of this class.
     */
    private static Effects controller;

    /**
     * Add a new effect to the controller. Instantiates the controller if
     * it has not already been.
     *
     * @param device
     * @param game
     */
    private static void addDevice(Device device, Game game) {
        if (controller == null) {
            controller = new Effects();
        }
        controller.addDevice(device);
        if (!game.getObjects().contains(controller) && !game.objectAddQueued(controller)) {
            game.add(controller);
        }
    }

    /**
     * Fade in or out a sprite sheet over some time.
     *
     * @param fade {@link Fade#IN} or {@link Fade#OUT}.
     * @param game
     * @param sheet
     * @param delay the time to fade in or out
     */
    public static void fade(Fade fade, Game game, SpriteSheet sheet, int delay) {
        addDevice(new FadeTransition(sheet, fade, delay), game);
    }

    /**
     * @see Effects#fade(kawaiiklash.Effects.Fade, kawaiiklash.Game,
     * kawaiiklash.SpriteSheet, int)
     * @param fade
     * @param game
     * @param sheets
     * @param delay
     */
    public static void fade(Fade fade, Game game, List<SpriteSheet> sheets, int delay) {
        for (SpriteSheet sheet : sheets) {
            fade(fade, game, sheet, delay);
        }
    }

    /**
     * Flash a sprite sheet a certain color.
     *
     * @param color the color to flash
     * @param game
     * @param sheet
     * @param flashes the number of flashes
     * @param duration the duration of each flash
     * @param delay the delay between each flash
     */
    public static void flash(Color color, Game game, SpriteSheet sheet, int flashes, int duration, int delay) {
        addDevice(new ColorFlash(sheet, color, flashes, duration, delay), game);
    }

    /**
     * @see Effects#flash(org.newdawn.slick.Color, kawaiiklash.Game,
     * kawaiiklash.SpriteSheet, int, int, int)
     * @param color
     * @param game
     * @param sheets
     * @param flashes
     * @param duration
     * @param delay
     */
    public static void flash(Color color, Game game, List<SpriteSheet> sheets, int flashes, int duration, int delay) {
        for (SpriteSheet sheet : sheets) {
            flash(color, game, sheet, flashes, duration, delay);
        }
    }

    /**
     * The list of all the effect devices.
     */
    private final List<Device> devices;
    private final List<Device> add;
    private final List<Device> remove;

    /**
     * Create the controller.
     */
    private Effects() {
        devices = new ArrayList<>(0);
        add = new ArrayList<>(0);
        remove = new ArrayList<>(0);
    }

    /**
     * Add an effect device.
     *
     * @param device
     */
    private void addDevice(Device device) {
        add.add(device);
    }

    /**
     * Update all the devices and remove them when they are completed.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        devices.addAll(add);
        add.clear();
        for (Device device : devices) {
            device.update(dt);
            if (device.isComplete()) {
                remove.add(device);
            }
        }
        devices.removeAll(remove);
        remove.clear();
    }

    /**
     * An enumeration used in the fade in or out effects.
     */
    @SuppressWarnings("PublicInnerClass")
    public static enum Fade {

        IN(0.0f, 1),
        OUT(1.0f, -1);

        public final float alpha;
        public final int crease;

        private Fade(float alpha, int crease) {
            this.alpha = alpha;
            this.crease = crease;
        }

    }

    /**
     * A general interface for any effect device.
     */
    private static interface Device extends Updateable {

        @Override
        void update(int dt);

        boolean isComplete();

    }

    /**
     * Fade in or out with this effect device.
     */
    private static class FadeTransition implements Device {

        private final SpriteSheet sheet;
        private final Fade fade;
        private final int delay;

        private float alpha;

        private boolean complete;

        private FadeTransition(SpriteSheet sheet, Fade fade, int delay) {
            this.sheet = sheet;
            this.fade = fade;
            this.delay = delay;
            alpha = fade.alpha;
            complete = false;
        }

        @Override
        public void update(int dt) {
            float da = 1.0f / delay * dt;
            alpha += da * fade.crease;
            if (alpha < Fade.IN.alpha || alpha > Fade.OUT.alpha) {
                complete = true;
                alpha = alpha < 0 ? Fade.IN.alpha : Fade.OUT.alpha;
            }
            sheet.setFilter(new Color(1.0f, 1.0f, 1.0f, alpha));
        }

        @Override
        public boolean isComplete() {
            return complete;
        }

    }

    /**
     * Flash a color with this effect device.
     */
    private static class ColorFlash implements Device {

        private static final Color clean = new Color(1.0f, 1.0f, 1.0f, 1.0f);

        private final SpriteSheet sheet;
        private final Color color;
        private final int flashes;
        private final int duration;
        private final int delay;

        private int count;
        private int flashed;
        private boolean applied;
        private boolean complete;

        private ColorFlash(SpriteSheet sheet, Color color, int flashes, int duration, int delay) {
            this.sheet = sheet;
            this.color = color;
            this.flashes = flashes;
            this.duration = duration;
            this.delay = delay;
            count = 0;
            applied = true;
            complete = false;
            sheet.setFilter(color);
        }

        @Override
        public void update(int dt) {
            count += dt;
            if (!complete) {
                if (applied) {
                    if (count >= duration) {
                        count %= duration;
                        applied = false;
                        sheet.setFilter(clean);
                        flashed++;
                        if (flashed >= flashes) {
                            complete = true;
                        }
                    }
                }
                if (!applied) {
                    if (count >= delay) {
                        count %= delay;
                        applied = true;
                        sheet.setFilter(color);
                    }
                }
            }
        }

        @Override
        public boolean isComplete() {
            return complete;
        }

    }

}
