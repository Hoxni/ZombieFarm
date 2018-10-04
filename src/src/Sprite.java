import javafx.scene.layout.Pane;

public abstract class Sprite extends Pane {

    Vector2D location;
    Vector2D velocity;
    Vector2D acceleration;

    double maxForce = Settings.SPRITE_MAX_FORCE;
    double maxSpeed = Settings.SPRITE_MAX_SPEED;

    // view dimensions

    double centerX;
    double centerY;

    //double angle;


    public Sprite(Vector2D location, Vector2D velocity, Vector2D acceleration) {

        this.location = location;
        this.velocity = velocity;
        this.acceleration = acceleration;

    }

    public void setCenter(){
        this.centerX = getWidth() / 2;
        this.centerY = getHeight() / 2 + Settings.HEIGHT_OFFSET;
    }

    public void applyForce(Vector2D force) {
        acceleration.add(force);
    }

    public void move() {

        // set velocity depending on acceleration
        velocity.add(acceleration);

        // limit velocity to max speed
        velocity.limit(maxSpeed);

        // change location depending on velocity
        location.add(velocity);

        // angle: towards velocity (ie target)
        //angle = velocity.heading2D();

        // clear acceleration
        acceleration.multiply(0);

    }

    /**
     * Move sprite towards target
     */
    public void seek(Vector2D target) {

        Vector2D desired = Vector2D.subtract(target, location);

        // The distance is the magnitude of the vector pointing from location to target.

        double d = desired.magnitude();
        desired.normalize();

        // If we are closer than 30 pixels...
        if (d < Settings.SPRITE_SLOW_DOWN_DISTANCE) {
            double m = Utils.map(d, 0, Settings.SPRITE_SLOW_DOWN_DISTANCE, 0, maxSpeed);
            desired.multiply(m);
        }
        // Otherwise, proceed at maximum speed.
        else {
            desired.multiply(maxSpeed);
        }

        // The usual steering = desired - velocity
        Vector2D steer = Vector2D.subtract(desired, velocity);
        steer.limit(maxForce);

        applyForce(steer);

    }

    Vector2D getNormalPoint(Vector2D p, Vector2D a, Vector2D b) {
        Vector2D ap = Vector2D.subtract(p, a);
        Vector2D ab = Vector2D.subtract(b, a);

        ab.normalize();
        ab.multiply(ap.dot(ab));
        Vector2D normalPoint = Vector2D.add(a, ab);

        return normalPoint;
    }

    /**
     * Update node position
     */
    public void display() {

        relocate(location.x - centerX, location.y - centerY);
        //setRotate(Math.toDegrees( angle));
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Vector2D getLocation() {
        return location;
    }

    public void setLocation( double x, double y) {
        location.x = x;
        location.y = y;
    }

    public void setLocationOffset( double x, double y) {
        location.x += x;
        location.y += y;
    }

    public void update(Vector2D v){
        seek(v);
        move();
        display();
    }

    abstract public void stop();
}
