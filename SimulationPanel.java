import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SimulationPanel extends JPanel
{
    ContainerPanel containerPanel;

    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;

    private final int BOUNDARY_WIDTH = 600;
    private final int BOUNDARY_HEIGHT = 600;
    private int boundaryLeft;
    private int boundaryRight;
    private int boundaryBottom;
    private int boundaryTop;

    private final Color BOUNDARY_COLOR = new Color(240, 240, 240);

    private final double GRAVITY = 0.4;

    private final int FPS = 60;

    private Timer timer;

    private int timeScaleTimer = 0;

    private Player[] players = new Player[3];

    private Random random = new Random();

    private Font roboto;
    private Font robotoBold;

    private Sound music = new Sound();
    private Sound sfx = new Sound();

    private final double BOUNDARY_RESTITUTION = 0.95;
    private final double COLLISION_RESTITUTION = 0.6;

    private int alive = 0;

    public boolean paused = false;

    public KeyHandler keyHandler = new KeyHandler(this);

    public SimulationPanel(ContainerPanel containerPanel)
    {
        this.containerPanel = containerPanel;
        
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(20, 20, 20));

        registerFonts();

        updateBoundaries();

        this.addKeyListener(keyHandler);

        this.setFocusable(true);
        this.requestFocusInWindow();

        this.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                updateBoundaries();
            }    
        });

        this.timer = new Timer(1000 / FPS, e -> {
            update();
            repaint();
        });
    }

    public void start()
    {
        this.requestFocusInWindow();

        for (int i = 0; i < players.length; i++)
        {
            players[i] = Player.createPlayer(Player.PlayerType.values()[random.nextInt(Player.PlayerType.values().length)], random.nextInt(BOUNDARY_WIDTH - 60 * 2) + boundaryLeft, random.nextInt(BOUNDARY_HEIGHT - 60 * 2) + boundaryTop, random.nextDouble() * Math.PI * 2);
        }

        timer.start();
    }

    private void stop()
    {
        timer.stop();
    }

    private void update()
    {
        if (!paused)
        {
            alive = 0;

            // LOOP THROUGH PLAYERS
            for (int i = 0; i < players.length; i++)
            {
                Player player = players[i];

                if (player.health <= 0)
                {
                    player.alive = false;
                }

                if (!player.alive) continue;

                // INCREMENT PLAYERS ALIVE
                alive++;

                // APPLY GRAVITY
                player.vy += GRAVITY * player.timeScale;

                // MOVE PLAYER
                player.x += player.vx * player.timeScale;
                player.y += player.vy * player.timeScale;

                // CHECK BOUNDARIES
                if (player.x <= boundaryLeft)
                {
                    player.x = boundaryLeft;
                    player.vx = Math.abs(player.vx) * BOUNDARY_RESTITUTION;
                    
                    playSFX(0, -15f, "game");
                }
                else if (player.x + player.radius * 2 >= boundaryRight)
                {
                    player.x = boundaryRight - player.radius * 2;
                    player.vx = -Math.abs(player.vx) * BOUNDARY_RESTITUTION;

                    playSFX(0, -15f, "game");
                }

                if (player.y <= boundaryTop)
                {
                    player.y = boundaryTop;
                    player.vy = Math.abs(player.vy) * BOUNDARY_RESTITUTION;

                    playSFX(0, -15f, "game");
                }
                else if (player.y + player.radius * 2 >= boundaryBottom)
                {
                    player.y = boundaryBottom - player.radius * 2;
                    player.vy = -Math.abs(player.vy) * BOUNDARY_RESTITUTION;

                    // CLAMP Y VELOCITY ON BOUNCE AT BOTTOM BOUNDARY
                    if (Math.abs(player.vy) < player.speed)
                    {
                        player.vy = Math.copySign(player.speed, player.vy);
                    }

                    playSFX(0, -15f, "game");
                }

                // CHECK COLLISION WITH OTHER PLAYERS
                for (int j = i + 1; j < players.length; j++)
                {
                    if (!players[j].alive)
                    {
                        continue;
                    }

                    playerCollision(player, players[j]);
                }

                // CLAMP X VELOCITY
                if (Math.abs(player.vx) < player.speed)
                {
                    player.vx = Math.copySign(player.speed, player.vx);
                }

                Weapon weapon = player.weapon;

                if (weapon != null) // UPDATE WEAPON POSITIONING AND DIRECTION
                {
                    weapon.x = player.x + player.radius + Math.cos(weapon.dir) * weapon.distance;
                    weapon.y = player.y + player.radius + Math.sin(weapon.dir) * weapon.distance;

                    weapon.dir += Math.toRadians(weapon.turnSpeed) * player.timeScale;

                    weapon.updateHitbox();
                }

                if (players[i].cooldown > 0)
                {
                    players[i].cooldown--;
                }

                if (!paused)
                {
                    // UPGRADE PLAYER PER FRAME ACCORDING TO PLAYER TYPE
                    player.upgrade(player, player.type);
                }
            }

            // CHECK FOR WEAPON-WEAPON COLLISIONS
            for (int i = 0; i < players.length; i++)
            {
                Weapon w1 = players[i].weapon;

                if (w1 == null || !players[i].alive) continue;

                for (int j = i + 1; j < players.length; j++)
                {
                    Weapon w2 = players[j].weapon;

                    if (w2 == null || !players[j].alive) continue;

                    // CLASH UPON COLLISION
                    if (weaponWeaponCollision(w1, w2))
                    {
                        if (players[i].cooldown <= 0 && players[j].cooldown <= 0)
                        {
                            players[i].cooldown = 10;
                            players[j].cooldown = 10;
                            players[i].weapon.turnSpeed *= -1;
                            players[j].weapon.turnSpeed *= -1;

                            playSFX(4, -12f, "game");
                        }
                    }
                }
            }

            // CHECK FOR WEAPON-PLAYER COLLISIONS
            for (int i = 0; i < players.length; i++)
            {
                Player player = players[i];

                Weapon weapon = player.weapon;

                if (weapon == null || !player.alive) continue;

                for (int j = 0; j < players.length; j++)
                {
                    if (i == j) continue;

                    Player target = players[j];

                    if (!target.alive) continue;

                    // CHECKS IF WEAPON HAS HIT A PLAYER
                    if (weaponPlayerCollision(weapon, target) && player.cooldown <= 0)
                    {
                        player.cooldown = 10;

                        target.health = Math.max(target.health - weapon.damage, 0);
                        target.color = Color.WHITE;

                        target.timeScale = 0.15;
                        timeScaleTimer = 3;

                        playSFX(weapon.soundIndex, -12f, "game");
                    }
                }
            }

            // RESET SLOW MOTION
            if (timeScaleTimer > 0 && !paused)
            {
                timeScaleTimer--;

                if (timeScaleTimer == 0)
                {
                    for (int i = 0; i < players.length; i++)
                    {
                        players[i].timeScale = 1;
                        players[i].color = players[i].baseColor;
                    }
                }
            }

            if (alive <= 1)
            {
                stop();
            }
        }
    }

    private void registerFonts()
    {
        try
        {
            roboto = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/Roboto.ttf")).deriveFont(Font.BOLD, 36f);
            robotoBold = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/RobotoBold.ttf")).deriveFont(Font.BOLD, 36f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(roboto);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(robotoBold);
        }
        catch (FontFormatException e)
        {
            System.out.println("Font Format Exception: " + e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    private void updateBoundaries()
    {
        boundaryLeft = this.getWidth() / 2 - BOUNDARY_WIDTH / 2;
        boundaryRight = this.getWidth() / 2 + BOUNDARY_WIDTH / 2;
        boundaryBottom = this.getHeight() / 2 + BOUNDARY_HEIGHT / 2;
        boundaryTop = this.getHeight() / 2 - BOUNDARY_HEIGHT / 2;
    }

    // PLAYER-PLAYER COLLISION
    private void playerCollision(Player playerA, Player playerB)
    {
        double ax = playerA.x;
        double ay = playerA.y;
        double bx = playerB.x;
        double by = playerB.y;

        double distX = bx - ax;
        double distY = by - ay;
        double dist = Math.hypot(distX, distY);
        double minDist = playerA.radius + playerB.radius;

        if (dist == 0 || dist >= minDist) return;

        double nx = distX / dist;
        double ny = distY / dist;

        double overlap = minDist - dist;
        double slop = 0.2;
        double percent = 0.8;
        double correction = Math.max(overlap - slop, 0) * percent / 2;

        playerA.x -= nx * correction;
        playerA.y -= ny * correction;
        playerB.x += nx * correction;
        playerB.y += ny * correction;

        double rx = playerB.vx - playerA.vx;
        double ry = playerB.vy - playerA.vy;

        double velAlongNormal  = rx * nx + ry * ny;

        if (Math.abs(velAlongNormal) < 0.8   || velAlongNormal > 0) return;

        double ix = -(1 + COLLISION_RESTITUTION) * velAlongNormal * nx / 2;
        double iy = -(1 + COLLISION_RESTITUTION) * velAlongNormal * ny / 2;

        playerA.vx -= ix;
        playerA.vy -= iy;
        playerB.vx += ix;
        playerB.vy += iy;

        if (playerB.contactDamage > 0)
        {
            playerA.health = Math.max(playerA.health - playerB.contactDamage, 0);
            playerA.color = Color.WHITE;
        }
        
        if (playerA.contactDamage > 0)
        {
            playerB.health = Math.max(playerB.health - playerA.contactDamage, 0);
            playerB.color = Color.WHITE;
        }

        // A PLAYER HAS TAKEN DAMAGE
        if ((playerA.contactDamage > 0 || playerB.contactDamage > 0) && !paused)
        {
            playerA.timeScale = 0.15;
            playerB.timeScale = 0.15;
            timeScaleTimer = 3;

            playSFX(1, -12f, "game");
        }
    }

    // WEAPON-PLAYER COLLISION
    private boolean weaponPlayerCollision(Weapon weapon, Player player)
    {
        // PLAYER CENTER
        double cx = player.x + player.radius;
        double cy = player.y + player.radius;

        // WEAPON BASE
        double ax = weapon.x1;
        double ay = weapon.y1;

        // WEAPON TIP
        double bx = weapon.x2;
        double by = weapon.y2;

        // DIRECTIONAL VECTOR
        double abx = bx - ax;
        double aby = by - ay;

        // DOT PRODUCT PROJECTION (HOW FAR ALONG THE CLOSEST POINT IS TO THE PLAYER)
        double t = ((cx - ax) * abx + (cy - ay) * aby) / (abx * abx + aby * aby);
        t = Math.max(0, Math.min(1, t)); // CLAMPS T BETWEEN 0 AND 1

        // CLOSEST POINT
        double closestX = ax + abx * t;
        double closestY = ay + aby * t;

        // DISTANCE
        double dx = cx - closestX;
        double dy = cy - closestY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        double padding = 5;

        return dist < player.radius + weapon.width + padding;
    }

    // WEAPON-WEAPON COLLISION (USED AI FOR BULK OF LOGIC)
    private boolean weaponWeaponCollision(Weapon a, Weapon b)
    {
        double padding = 30;

        double d1 = pointLineDistance(a.x1, a.y1, b.x1, b.y1, b.x2, b.y2);
        double d2 = pointLineDistance(a.x2, a.y2, b.x1, b.y1, b.x2, b.y2);
        double d3 = pointLineDistance(b.x1, b.y1, a.x1, a.y1, a.x2, a.y2);
        double d4 = pointLineDistance(b.x2, b.y2, a.x1, a.y1, a.x2, a.y2);

        return d1 <= padding || d2 <= padding || d3 <= padding || d4 <= padding;
    }

    private double pointLineDistance(double px, double py, double x1, double y1, double x2, double y2)
    {
        // DIRECTIONAL VECTORS
        double dx = x2 - x1;
        double dy = y2 - y1;

        double lengthSq = dx * dx + dy * dy;

        double t = ((px - x1) * dx + (py - y1) * dy) / lengthSq;
        t = Math.max(0, Math.min(1, t));

        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        double distX = px - closestX;
        double distY = py - closestY;

        return Math.hypot(distX, distY);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(robotoBold);

        g2.setColor(BOUNDARY_COLOR);
        g2.fillRect(this.getWidth() / 2 - BOUNDARY_WIDTH / 2, this.getHeight() / 2 - BOUNDARY_HEIGHT / 2, BOUNDARY_WIDTH, BOUNDARY_HEIGHT);

        for (int i = 0; i < players.length; i++)
        {
            Player player = players[i];
            Weapon weapon = player.weapon;

            if (!player.alive)
            {
                continue;
            }

            FontMetrics fm = g2.getFontMetrics();

            int textWidth = fm.stringWidth(String.valueOf(player.health));
            int textHeight = fm.getAscent();

            // WEAPON
            if (weapon != null)
            {
                AffineTransform old = g2.getTransform();

                double drawX = weapon.x;
                double drawY = weapon.y - weapon.width / 2;

                g2.rotate(weapon.dir, weapon.x, weapon.y);

                g2.drawImage(weapon.image, (int) drawX, (int) drawY, (int) weapon.length, (int) weapon.width, null);
                g2.setColor(Color.BLACK);

                g2.setTransform(old);
            }

            // PLAYER FILL
            g2.setColor(player.color);
            g2.fillArc((int) Math.round(player.x), (int) Math.round(player.y), (int) player.radius * 2, (int) player.radius * 2, 0, 360);

            // PLAYER OUTLINE
            g2.setColor(player.borderColor);
            g2.setStroke(new BasicStroke(4f));
            g2.drawArc((int) Math.round(player.x + 2), (int) Math.round(player.y + 2), (int) player.radius * 2 - 2, (int) player.radius * 2 - 2, 0, 360);

            // PLAYER HEALTH
            g2.setColor(player.fontColor);
            g2.drawString(String.valueOf(player.health), (int) (player.x + player.radius - (textWidth / 2)), (int) (player.y + player.radius + (textHeight / 2.5)));
        }
    }

    public void playMusic(int i, float gain)
    {
        music.setFile(i, gain);
        music.play();
        music.loop();
    }

    public void stopMusic()
    {
        music.stop();
    } 

    public void playSFX(int i, float gain, String type)
    {
        sfx.setFile(i, gain);

        if (sfx.clip == null) return;

        if (!paused || type.equals("universal"))
        {
            sfx.play();
        }
    }

    public void setTimeScale()
    {
        paused = !paused;

        for (int i = 0; i < players.length; i++)
        {
            if (!paused) // UNPAUSE
            {
                players[i].timeScale = players[i].playingTimeScale;
                playSFX(3, -16f, "universal");
            }
            else // PAUSE
            {
                players[i].playingTimeScale = players[i].timeScale;
                players[i].timeScale = 0;
                playSFX(2, -16f, "universal");
            }
        }
    }
}
