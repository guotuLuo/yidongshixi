package com.example.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FlipImage {
    public static void flip(String filename) {
        try {
            // 读取图片文件
            File inputFile = new File(filename);
            BufferedImage inputImage = ImageIO.read(inputFile);

            // 创建一个新的BufferedImage来存储翻转后的图片
            BufferedImage flippedImage = new BufferedImage(
                    inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());

            // 上下镜像翻转
            Graphics2D g = flippedImage.createGraphics();
            g.drawImage(inputImage, 0, 0, inputImage.getWidth(), inputImage.getHeight(),
                    0, inputImage.getHeight(), inputImage.getWidth(), 0, null);
            g.dispose();

            // 将翻转后的图片保存到文件
            File outputFile = new File(filename);
            ImageIO.write(flippedImage, "png", outputFile);

            System.out.println("图片已成功翻转并保存为: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("图片处理出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
