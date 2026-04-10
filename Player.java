import java.awt.Color;
import java.util.Random;

public class Player
{
    public enum PlayerType
    {
        MELEE,
        DAGGER,
        SPEEDSTER
    }
    
    public PlayerType type;

    // POSITIONAL
    public double x;
    public double y;
    public double initDir;

    // SPEED
    public double vx;
    public double vy;
    public double timeScale;
    public double playingTimeScale;

    // TYPE PROPERTIES
    public int maxHealth;
    public int health;
    public int contactDamage;
    public double radius;
    public double speed;
    public Color baseColor;
    public Color color;
    public Color borderColor;
    public Color fontColor;

    public boolean alive;

    // ATTACK COOLDOWN
    public double cooldown;

    private static Random random = new Random();

    public Weapon weapon;

    public Player(PlayerType type, double x, double y, double initDir)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.initDir = initDir;
    }

    public static Player createPlayer(PlayerType type, double x, double y, double initDir)
    {
        Player player = new Player(type, x, y, initDir);

        player.weapon = null;

        player.timeScale = 1;
        player.playingTimeScale = 1;

        switch (type)
        {
            case MELEE:
                player.maxHealth = 100;
                player.contactDamage = 2;
                player.radius = 60;
                player.speed = 10;
                player.baseColor = new Color(180, 180, 180);
                player.borderColor = new Color(100, 100, 100);
                player.fontColor = new Color(20, 20, 20);

                break;
            case DAGGER:
                player.maxHealth = 80;
                player.contactDamage = 0;
                player.radius = 50;
                player.speed = 12;
                player.baseColor = new Color(115, 85, 245);
                player.borderColor = new Color(35, 5, 165);
                player.fontColor = new Color(20, 5, 90);

                player.weapon = Weapon.createWeapon(Weapon.WeaponType.DAGGER, player.x + player.radius, player.y + player.radius, random.nextDouble() * Math.PI * 2);

                break;
            case SPEEDSTER:
                player.maxHealth = 90;
                player.contactDamage = 3;
                player.radius = 50;
                player.speed = 15;
                player.baseColor = new Color(255, 255, 0);
                player.borderColor = new Color(115, 115, 0);
                player.fontColor = new Color(65, 65, 0);

                break;
            default:
                System.out.println("Not a valid player!");
        }

        player.vx = Math.cos(initDir) * player.speed;
        player.vy = Math.sin(initDir) * player.speed;

        player.health = player.maxHealth;

        player.color = player.baseColor;

        player.alive = true;

        return player;
    }

    public void upgrade(Player player, PlayerType type)
    {
        switch (type)
        {
            case MELEE:
                break;
            case DAGGER:
                player.weapon.turnSpeed += Math.signum(player.weapon.turnSpeed) * 0.02;

                break;
            case SPEEDSTER:
                player.speed += 0.03;

                break;
            default:
                System.out.println("Not a valid player to upgrade!");
        }
    }
}