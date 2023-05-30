/*** Author :Vibhav Gogate, Terrence Klock
The University of Texas at Dallas
*****/

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;
 

public class KMeans {
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
		System.out.println("Run Complete!");
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	kmeans(rgb,k);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){
		//generate k random numbers within the range of the rgb array
		int[] clusternumbers = new Random().ints(0, rgb.length-1).distinct().limit(k).toArray();
		//array for storing the current rgb values of each cluster
		int[] clusters = new int[k];
		for(int i = 0; i < k; i++){
			clusters[i] = rgb[clusternumbers[i]];
		}
		//array for storing number of pixels in each cluster
		int[] clustercount = new int[k];
		
		//array for storing the current cluster each rbg pixel is part of, -1 showing it is not yet part of a cluster
		int[] currentcluster = new int[rgb.length];
		Arrays.fill(currentcluster, -1);

		boolean unfinished = true;
		//loop until no pixels are changing clusters
		while(unfinished){
			//set unfinished to false, if any changes take place it moves back to true
			unfinished = false;
			for(int i = 0; i < rgb.length; i ++){
				int pixel = rgb[i];
				//find closest cluster to pixel
				int closestcluster = -1;
				int minimum = Integer.MAX_VALUE;
				for(int j = 0; j < clusters.length; j++)
				{
					int distance = calcRGBDistance(pixel, clusters[j]);
					if(distance < minimum)
					{
						minimum = distance;
						closestcluster = j;
					}
				}

				//if cluster is different, move pixel to cluster
				int oldcluster = currentcluster[i];
				if(closestcluster != oldcluster)
				{
					currentcluster[i] = closestcluster;
					clustercount[closestcluster] +=1;
					if(oldcluster != -1)
					{
						clustercount[oldcluster] -=1;
					}

					//since change occured, need to perform another loop
					unfinished = true;
				}


			}
			
			//update color centers
			for(int n = 0; n < clusters.length; n++)
			{
				clusters[n] = 0;
			}

			int[] clusterred = new int[k];
			int[] clustergreen = new int[k];
			int[] clusterblue = new int[k];


			for(int m = 0; m < rgb.length; m++)
			{
				int cluster = currentcluster[m];
				int r = rgb[m]>>16&0x000000FF;
				int g = rgb[m]>>8&0x000000FF;
				int b = rgb[m]>>0&0x000000FF;

				clusterred[cluster] += r;
				clustergreen[cluster] += g;
				clusterblue[cluster] += b;
			}

			for(int o = 0; o < clusters.length; o++)
			{
				if(clustercount[o] != 0){
					int red = clusterred[o]/clustercount[o];
					int green = clustergreen[o]/clustercount[o];
					int blue = clusterblue[o]/clustercount[o];

					clusters[o] = 0xff000000|red<<16|green<<8|blue;
				}
				else
				{
					clusters[o] = 0xff000000;
				}
			}

		}

		//make new rbg array using cluster data
		for(int l = 0; l < rgb.length; l++)
		{
			int c = currentcluster[l];
			rgb[l] = clusters[c];
		}
    }

	private static int calcRGBDistance(int pixel, int colorcenter)
	{
		int pixelr = pixel>>16&0x000000FF;
		int pixelg = pixel>>8&0x000000FF;
		int pixelb = pixel>>0&0x000000FF;
		int ccr = colorcenter>>16&0x000000FF;
		int ccg = colorcenter>>8&0x000000FF;
		int ccb = colorcenter>>0&0x000000FF;
		int distance = (Math.abs(ccr-pixelr) + Math.abs(ccg-pixelg) + Math.abs(ccb-pixelb))/3;
		return distance;
	}

}
