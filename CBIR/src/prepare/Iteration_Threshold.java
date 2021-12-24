package prepare;
//迭代阈值法进行二值化
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//二值化：迭代阈值法
public class Iteration_Threshold {

	private String Image_Format="jpg";
	private BufferedImage binaryzation=null;
	private int G1,G2,N1,N2;
	private int T0=1,T1=0,T2=0;
	//获取阈值
	public BufferedImage get_Iteration_Threshold(BufferedImage bufImg,int Width,int Height) throws IOException
	{
		do
		{
			T1=T2;
			G1=G2=N1=N2=0;
			for(int i=0;i<Width;i++)
				for(int j=0;j<Height;j++)
				{
					int pixel=(bufImg.getRGB(i, j)&0xff);
					if(pixel<=T1){G1+=pixel;N1++;}//分类
					else {G2+=pixel;N2++;}
				}
			T2=(G1/N1+G2/N2)/2;//新的阈值
		}while(Math.abs(T2-T1)>T0);//当迭代前后两次相差不多的时候停止迭代
		//System.out.println("T2 "+T2);
		this.get_Binaryzation(bufImg, Width, Height, T2);
	    return binaryzation;
	}
	//二值化
	public void get_Binaryzation(BufferedImage bufImg,int Width,int Height,int T) throws IOException
	{
		binaryzation=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
		int alpha=(bufImg.getRGB(0, 0)>>24)&0xff;
		int rgb;
		for(int i=0;i<Width;i++)
			for(int j=0;j<Height;j++)
			{
				int pixel=(bufImg.getRGB(i, j)&0xff);
				if(pixel<=T)
					rgb=(alpha<<24);
				else
					rgb=(alpha<<24)|(255<<16)|(255<<8)|255;
				
				binaryzation.setRGB(i, j, rgb);
			}
    }
}
