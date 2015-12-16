/*
 * Copyright (C) 2015 Krystian
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package utils;

import file.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Krystian
 */
public class ImageUtils {
    
    public static Graphics Normalize(Graphics g)
    {
        
        
        return g;
    }
    public int[] resizePixels(int[] pixels,int w1,int h1,int w2,int h2) {
    int[] temp = new int[w2*h2] ;
    // EDIT: added +1 to account for an early rounding problem
    int x_ratio = (int)((w1<<16)/w2) +1;
    int y_ratio = (int)((h1<<16)/h2) +1;
    //int x_ratio = (int)((w1<<16)/w2) ;
    //int y_ratio = (int)((h1<<16)/h2) ;
    int x2, y2 ;
    for (int i=0;i<h2;i++) {
        for (int j=0;j<w2;j++) {
            x2 = ((j*x_ratio)>>16) ;
            y2 = ((i*y_ratio)>>16) ;
            temp[(i*w2)+j] = pixels[(y2*w1)+x2] ;
        }                
    }                
    return temp ;
}
    
    

    public static BufferedImage getScaledImage(BufferedImage src, int w, int h){
    int finalw = w;
    int finalh = h;
    double factor = 1.0d;
    if(src.getWidth() > src.getHeight()){
        factor = ((double)src.getHeight()/(double)src.getWidth());
        finalh = (int)(finalw * factor);                
    }else{
        factor = ((double)src.getWidth()/(double)src.getHeight());
        finalw = (int)(finalh * factor);
    }   

    BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
    Graphics2D g2 = resizedImg.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(src, 0, 0, finalw, finalh, null);
    g2.dispose();
    return resizedImg;
}
    
    
    public static ArrayList<int[]> imageHistogram(BufferedImage input) {
 
    int[] rhistogram = new int[256];
    int[] ghistogram = new int[256];
    int[] bhistogram = new int[256];
 
    for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
    for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
    for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
    for(int i=0; i<input.getWidth(); i++) {
        for(int j=0; j<input.getHeight(); j++) {
 
            int red = new Color(input.getRGB (i, j)).getRed();
            int green = new Color(input.getRGB (i, j)).getGreen();
            int blue = new Color(input.getRGB (i, j)).getBlue();
 
            // Increase the values of colors
            rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;
 
        }
    }
 
    ArrayList<int[]> hist = new ArrayList<int[]>();
    hist.add(rhistogram);
    hist.add(ghistogram);
    hist.add(bhistogram);
 
    return hist;
 
}
    
    private static ArrayList<int[]> histogramEqualizationLUT(BufferedImage input) {
 
    // Get an image histogram - calculated values by R, G, B channels
    ArrayList<int[]> imageHist = imageHistogram(input);
 
    // Create the lookup table
    ArrayList<int[]> imageLUT = new ArrayList<int[]>();
 
    // Fill the lookup table
    int[] rhistogram = new int[256];
    int[] ghistogram = new int[256];
    int[] bhistogram = new int[256];
 
    for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
    for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
    for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
    long sumr = 0;
    long sumg = 0;
    long sumb = 0;
 
    // Calculate the scale factor
    float scale_factor = (float) (255.0 / (input.getWidth() * input.getHeight()));
 
    for(int i=0; i<rhistogram.length; i++) {
        sumr += imageHist.get(0)[i];
        int valr = (int) (sumr * scale_factor);
        if(valr > 255) {
            rhistogram[i] = 255;
        }
        else rhistogram[i] = valr;
 
        sumg += imageHist.get(1)[i];
        int valg = (int) (sumg * scale_factor);
        if(valg > 255) {
            ghistogram[i] = 255;
        }
        else ghistogram[i] = valg;
 
        sumb += imageHist.get(2)[i];
        int valb = (int) (sumb * scale_factor);
        if(valb > 255) {
            bhistogram[i] = 255;
        }
        else bhistogram[i] = valb;
    }
 
    imageLUT.add(rhistogram);
    imageLUT.add(ghistogram);
    imageLUT.add(bhistogram);
 
    return imageLUT;
 
}
    
    
    public static BufferedImage histogramEqualization(BufferedImage original) {
 
    int red;
    int green;
    int blue;
    int alpha;
    int newPixel = 0;
 
    // Get the Lookup table for histogram equalization
    ArrayList<int[]> histLUT = histogramEqualizationLUT(original);
 
    BufferedImage histogramEQ = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
 
    for(int i=0; i<original.getWidth(); i++) {
        for(int j=0; j<original.getHeight(); j++) {
 
            // Get pixels by R, G, B
            alpha = new Color(original.getRGB (i, j)).getAlpha();
            red = new Color(original.getRGB (i, j)).getRed();
            green = new Color(original.getRGB (i, j)).getGreen();
            blue = new Color(original.getRGB (i, j)).getBlue();
 
            // Set new pixel values using the histogram lookup table
            red = histLUT.get(0)[red];
            green = histLUT.get(1)[green];
            blue = histLUT.get(2)[blue];
 
            // Return back to original format
            newPixel = colorToRGB(alpha, red, green, blue);
 
            // Write pixels into image
            histogramEQ.setRGB(i, j, newPixel);
 
        }
    }
 
    return histogramEQ;
 
}
    
    
    
    
        private static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha; newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
    
    
}
