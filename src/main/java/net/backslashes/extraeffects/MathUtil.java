package net.backslashes.extraeffects;

public class MathUtil {
    public static float lerpf(float a, float b, float x) {
        return a + x * (b - a);
    }

    public static int parseHexCode(String str){
        String colorString = str.toUpperCase();
        if (colorString.startsWith("0X")) {
            colorString = colorString.substring(2);
        }
        return ((int) Long.parseLong(colorString, 16)) | 0xFF000000;
    }

    public record RgbColor(float r, float g, float b){
        public static RgbColor fromArgbHex(int argb){
            return new RgbColor(
                    (float)((argb >> 16) & 0xFF) / 255.0f,
                    (float)((argb >> 8) & 0xFF) / 255.0f,
                    (float)(argb & 0xFF) / 255.0f
            );
        }

        public static RgbColor lerp(RgbColor a, RgbColor b, float value){
            return new RgbColor(
                    lerpf(a.r, b.r, value),
                    lerpf(a.g, b.g, value),
                    lerpf(a.b, b.b, value)
            );
        }

        public int toHexArgb(){
            return ((int)(r * 255.0f) << 16) | ((int)(g * 255.0f) << 8) | (int)(b * 255.0f);
        }
    }

}