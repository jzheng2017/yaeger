package nl.han.ica.waterworld.entities.game.spawners;

import nl.han.ica.waterworld.entities.game.Air;
import nl.han.ica.waterworld.entities.game.Poison;
import nl.han.ica.waterworld.scenes.Level;
import nl.han.ica.yaeger.engine.entities.spawners.EntitySpawner;

import java.util.Random;

public class BubbleSpawner extends EntitySpawner {

    private final int worldWidth;
    private final int worldHeight;
    private Level waterworld;

    public BubbleSpawner(int width, int height, Level waterworld) {
        super(10);

        this.worldWidth = width;
        this.worldHeight = height;
        this.waterworld = waterworld;
    }

    private void createAir() {
        var air = new Air(generateRandomSpeed(), waterworld);
        air.setLocation(generateRandomXLocation(), worldHeight - 30);

        spawn(air);
    }

    private void createPoison() {
        var poison = new Poison(generateRandomSpeed(), waterworld);
        poison.setLocation(generateRandomXLocation(), worldHeight - 30);

        spawn(poison);
    }

    @Override
    public void tick() {
        if (new Random().nextInt(10) < 2) {
            createPoison();
        } else {
            createAir();
        }
    }

    private int generateRandomXLocation() {
        return new Random().nextInt(worldWidth);
    }

    private int generateRandomSpeed() {
        return new Random().nextInt(5);
    }
}