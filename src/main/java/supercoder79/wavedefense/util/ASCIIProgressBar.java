package supercoder79.wavedefense.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public class ASCIIProgressBar {
    public static MutableComponent get(double progress, int length) {
        // █
        MutableComponent bar = Component.empty();

        for (int i = 1; i <= length; i++) {
            double currentProgress = (double) i / length;

            String color = "dark_gray";

            if (currentProgress <= progress)
                color = "white";
            else if (currentProgress - progress < 1d / length)
                color = "gray";

            String finalColor = color;
            bar.append(Component.literal("█").withStyle(style -> style.withColor(TextColor.parseColor(finalColor).result().get())));
        }

        return bar;
    }
}
