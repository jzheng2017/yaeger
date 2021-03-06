package com.github.hanyaeger.api.engine.entities.entity.motion;

import com.github.hanyaeger.api.engine.entities.entity.Coordinate2D;
import javafx.geometry.Point2D;

import java.util.Optional;

/**
 * A {@link MotionApplier} is an implementation of {@link MotionApplier} that does not abide
 * the laws of Physics and only provides basis behaviour regarding speed and direction.
 */
public class MotionApplier implements MotionModifier, LocationUpdater {

    private static final Point2D ZERO_ANGLE_IDENTITY_MOTION = new Point2D(0, 1);
    private Optional<Double> direction = Optional.empty();
    private Coordinate2D motion;
    private Optional<Coordinate2D> previousLocation = Optional.empty();
    private boolean halted = false;

    public static final double DEFAULT_GRAVITATIONAL_CONSTANT = 0.2d;
    public static final double DEFAULT_GRAVITATIONAL_DIRECTION = Direction.DOWN.getValue();
    private double gravityConstant = DEFAULT_GRAVITATIONAL_CONSTANT;
    private double gravityDirection = DEFAULT_GRAVITATIONAL_DIRECTION;

    private boolean gravitationalPull = true;

    /**
     * Create a new instance of {@link MotionApplier}.
     */
    public MotionApplier() {
        motion = new Coordinate2D();
    }

    @Override
    public void setMotion(final double speed, final double direction) {
        setSpeed(speed);
        setDirection(direction);
    }

    @Override
    public void setMotion(final double speed, final Direction direction) {
        setMotion(speed, direction.getValue());
    }


    // TODO unittest
    @Override
    public void addToMotion(final double speed, final Direction direction) {
        addToMotion(speed, direction.getValue());
    }

    // TODO unittest
    @Override
    public void addToMotion(final double speed, final double direction) {
        motion = motion.add(createVector(speed, direction));
    }

    @Override
    public void setSpeed(final double newSpeed) {
        hasBeenHalted(newSpeed);

        if (Double.compare(newSpeed, 0d) == 0) {
            this.direction = Optional.of(motion.angle(new Point2D(0, 1)));
        }

        if (motion.equals(new Coordinate2D(0, 0))) {
            motion = new Coordinate2D(0, newSpeed);
        } else {
            motion = new Coordinate2D((motion.normalize().multiply(newSpeed)));
        }

        direction.ifPresent(this::setDirection);
    }

    @Override
    public void setDirection(final Direction direction) {
        setDirection(direction.getValue());
    }

    @Override
    public void setDirection(final double direction) {
        if (Double.compare(0, motion.magnitude()) == 0) {
            this.direction = Optional.of(direction);
        } else {
            motion = createVector(motion.magnitude(), direction);
            this.direction = Optional.empty();
        }
    }

    /**
     * TODO unittest
     * Return the gravitational contant used by this {@link NewtonianMotionApplier}.
     *
     * @return the gravitational constant as a {@code double}
     */
    public double getGravityConstant() {
        return gravityConstant;
    }

    /**
     * TODO unittest
     * Set the gravitational contant used by this {@link NewtonianMotionApplier}.
     *
     * @param gravityConstant the gravitational constant as a {@code double}
     */
    public void setGravityConstant(double gravityConstant) {
        this.gravityConstant = gravityConstant;
    }

    /**
     * TODO unittest
     * Return the gravitational direction used by this {@link NewtonianMotionApplier}.
     *
     * @return the gravitational direction as a {@code double}
     */
    public double getGravityDirection() {
        return gravityDirection;
    }

    /**
     * TODO unittest
     * Set the gravitational direction used by this {@link NewtonianMotionApplier}.
     *
     * @param gravityDirection the gravitational constant as a {@code double}
     */
    public void setGravityDirection(double gravityDirection) {
        this.gravityDirection = gravityDirection;
    }

    public void setGravitationalPull(final boolean pull) {
        this.gravitationalPull = pull;
    }

    public boolean isGravitationalPull() {
        return gravitationalPull;
    }

    @Override
    public double getSpeed() {
        return motion.magnitude();
    }

    @Override
    public void incrementSpeed(final double increment) {
        motion = motion.add(motion.normalize().multiply(increment));
    }

    @Override
    public void multiplySpeed(final double multiplication) {
        motion = new Coordinate2D(motion.multiply(multiplication));
    }

    @Override
    public void changeDirection(final double rotation) {
        double currentAngle = getDirection();

        setDirection(rotation + currentAngle);
    }

    @Override
    public double getDirection() {
        if (direction.isPresent()) {
            return direction.get();
        } else {
            double currentAngle = motion.angle(ZERO_ANGLE_IDENTITY_MOTION);

            if (motion.getX() < 0) {
                currentAngle = 360 - currentAngle;
            }

            return currentAngle;
        }
    }

    /**
     * Return the current transformation.
     *
     * @return a {@link Coordinate2D} representing the transformation applied on {@link LocationUpdater#updateLocation(Point2D)}
     */
    public Coordinate2D get() {
        return motion;
    }

    @Override
    public Coordinate2D updateLocation(final Point2D currentLocation) {
        previousLocation = Optional.of(new Coordinate2D(currentLocation.getX(), currentLocation.getY()));
        return new Coordinate2D(currentLocation.add(motion));
    }

    /**
     * Return the previous location. This Object is exposed to resolve an issue with the
     * fact that collision detection occurs after all Entities are updated. If an
     * Entity sets its speed to 0 on collision detection, it still received its last motion.
     * <p>
     * Because of that it is impossible that halt an Entities movement if the user continues
     * to press the movement buttons or another system is continuing to set the speed to a positive
     * value.
     *
     * @return an {@link Optional} containing the {@link Coordinate2D} representing the previous location of the
     * Entity
     */
    public Optional<Coordinate2D> getPreviousLocation() {
        return previousLocation;
    }

    /**
     * Return whether this {@link MotionApplier} has been halted.
     */
    public boolean isHalted() {
        return halted;
    }

    /**
     * Set whether this {@link MotionApplier} has been halted.
     *
     * @param halted whether this {@link MotionApplier} has been halted
     */
    public void setHalted(final boolean halted) {
        this.halted = halted;
    }

    private void hasBeenHalted(final double newSpeed) {
        halted = (newSpeed == 0 && motion.magnitude() != 0);
    }

    private Coordinate2D createVector(final double speed, final double direction) {
        final var angleInRadians = Math.toRadians(direction);
        final var x = Math.sin(angleInRadians);
        final var y = Math.cos(angleInRadians);

        return new Coordinate2D(new Coordinate2D(x, y).multiply(speed));
    }
}
