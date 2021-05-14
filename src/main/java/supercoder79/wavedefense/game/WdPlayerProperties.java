package supercoder79.wavedefense.game;

public class WdPlayerProperties {
    public int sharpness;
    public int power;
    public int piercing;
    public int helmetProtection;
    public int chestplateProtection;
    public int leggingsProtection;
    public int bootsProtection;

    // 0 = chainmail, 1 = iron, 2 = diamond, 3 = netherite
    public int helmetLevel;
    public int chestplateLevel;
    public int leggingsLevel;
    public int bootsLevel;

    // 1 = iron, 2 = diamond, 3 = netherite
    public int swordLevel;

    public int quickChargeLevel;

    public WdPlayerProperties() {
        sharpness = 0;
        power = 0;
        piercing = 0;
        helmetProtection = 0;
        chestplateProtection = 0;
        leggingsProtection = 0;
        bootsProtection = 0;
        helmetLevel = 0;
        chestplateLevel = 0;
        leggingsLevel = 0;
        bootsLevel = 0;
        swordLevel = 1;
        quickChargeLevel = 0;
    }
}
