package supercoder79.wavedefense.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class ASCIIProgressBar {
    public static MutableText get(double progress, int length) {
        // █
        MutableText bar = Text.empty();

        for (int i = 1; i <= length; i++) {
            double currentProgress = (double) i / length;

            String color = "dark_gray";

            if (currentProgress <= progress)
                color = "white";
            else if (currentProgress - progress < 1d / length)
                color = "gray";

            String finalColor = color;
            bar.append(Text.literal("█").styled(style -> style.withColor(TextColor.parse(finalColor).result().get())));
        }

        return bar;
    }
}
