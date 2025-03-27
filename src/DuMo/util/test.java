package DuMo.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++) {
                image.setRGB(i, j, 0x00_00_00); //bl
            }

        for (int i = 0; i < 16; i++) {
            image.setRGB(i, i, 0xff_ff_ff); //wh
        }

        File output = new File("myImage.png");

        try {
            boolean worked = ImageIO.write(image, "png", output);

            if (worked) {
                System.out.println(output.getAbsolutePath());
            } else {
                System.out.println("fuk");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
