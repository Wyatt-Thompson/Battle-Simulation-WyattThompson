import java.awt.Image;

import javax.swing.ImageIcon;

public class Weapon
{
    public enum WeaponType
    {
        DAGGER
    }

    public WeaponType type;

    // POSITIONAL
    public double x;
    public double y;
    public double dir;

    // HITBOX

    public double x1, y1; // BASE
    public double x2, y2; // TIP

    // TURN SPEED
    public double turnSpeed;

    // TYPE PROPERTIES
    public int damage;
    public double distance;
    public double length;
    public double width;
    public Image image;
    public int soundIndex;

    public Weapon(WeaponType type, double x, double y, double dir)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public static Weapon createWeapon(WeaponType type, double x, double y, double dir)
    {
        Weapon weapon = new Weapon(type, x, y, dir);

        switch (type)
        {
            case DAGGER:
                weapon.damage = 3;
                weapon.distance = 40;
                weapon.turnSpeed = 10;
                weapon.length = 91;
                weapon.width = 38;
                weapon.image = new ImageIcon("Images/Weapons/Dagger.png").getImage();
                weapon.soundIndex = 5;

                break;
            default:
                System.out.println("Not a valid weapon!");
        }

        weapon.x += Math.cos(dir) * weapon.distance;
        weapon.y += Math.sin(dir) * weapon.distance;

        return weapon;
    }

    public void updateHitbox()
    {
        x1 = x;
        y1 = y;

        x2 = x + Math.cos(dir) * length;
        y2 = y + Math.sin(dir) * length;
    }
}
