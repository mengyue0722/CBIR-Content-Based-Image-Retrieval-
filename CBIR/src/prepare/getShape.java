package prepare;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class getShape {

	//读取图片路径
		public ArrayList<String> getPath(String path_file){
			ArrayList<String> paths = new ArrayList<>();
			try {
				FileReader fr = new FileReader(path_file);	
				BufferedReader bf = new BufferedReader(fr);		
				String str;			// 按行读取字符串	
				while ((str = bf.readLine()) != null) {		
					paths.add(str);		
				}		
				bf.close();		
				fr.close();		
				} catch (IOException e) {	
					e.printStackTrace();
				}
			return paths;
		}
		
		private int Width,Height;
		private int width,height;
		private int scale=350;
		private double x0,y0;
		private double M00,M10,M01;
		private double[][] 	M=new double[4][4];
		private double[][] 	u=new double[4][4];
		private double[]   	e=new double[8];
		
		/**获取图像形状特征值 形状不变矩**/
		public  double[] shape(String src_path) throws IOException
		{
			BufferedImage bufImg=null;
			File file  = new File(src_path);
	          if (!file.exists()) {
	               System.out.println("file doesn't find.");
	          }
	        bufImg = ImageIO.read(file);
	        Height = bufImg.getHeight();
	        Width = bufImg.getWidth();
	        
	        //图像处理
	        //图像灰度化
	        BufferedImage bufImg_gray=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
	    	for (int i = 0; i < Width; i++) {		/**注意行列顺序**/
	            for (int j = 0; j < Height; j++) {
	            	//System.out.println("getRGB: "+bufImg.getRGB(i, j)+" "+i+" "+j);
	            	int ARGB=bufImg.getRGB(i, j);
	            	int A=(ARGB>>24)&0Xff;
	            	int R=30*(((ARGB>>16)&0xff));
	            	/**灰度化公式y=0.30*R+0.59*G+0.11*B**/
	            	int G=59*(((ARGB>>8)&0xff));
	            	int B=11*((ARGB &0xff));
	            	int gray=(R+G+B)/100;
	            	int g=(A<<24)|(gray<<16)|(gray<<8)|gray;
	            	bufImg_gray.setRGB(i, j, g);
	            	
	            }
	       }
	    	bufImg = bufImg_gray;
	    	
	    	//图象平滑-中值滤波
	    	BufferedImage filter=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
	    	int [] pixel = new int[9];
			int t;
			int alpha=(bufImg.getRGB(0, 0)>>24)&0xff;
			for(int i=1;i<Width-1;i++)
				for(int j=1;j<Height-1;j++)
				{
					t=0;
					//找中心及周围的八个点
					int p0=bufImg.getRGB(i-1, j-1)&0xff; 	pixel[t++]=p0;
					int p1=bufImg.getRGB(i-1, j)&0xff;		pixel[t++]=p1;
					int p2=bufImg.getRGB(i-1, j+1)&0xff;	pixel[t++]=p2;
					int p3=bufImg.getRGB(i, j-1)&0xff;		pixel[t++]=p3;
					int p4=bufImg.getRGB(i, j)&0xff;		pixel[t++]=p4;
					int p5=bufImg.getRGB(i, j+1)&0xff;		pixel[t++]=p5;
					int p6=bufImg.getRGB(i+1, j-1)&0xff;	pixel[t++]=p6;
					int p7=bufImg.getRGB(i+1, j)&0xff;		pixel[t++]=p7;
					int p8=bufImg.getRGB(i+1, j+1)&0xff;	pixel[t++]=p8;
					int mid=bubble_sort(pixel);
					int rgb=(alpha<<24)|(mid<<16)|(mid<<8)|mid;
					filter.setRGB(i, j, rgb);
				}
	       bufImg = filter;	
	       //图像锐化_Sobel
	       Sharpen sharpen = new Sharpen();
	       BufferedImage bufImg_sharpen=sharpen.get_Sharpen_Sobel(bufImg, Width, Height);
	       //二值化：迭代阈值法(iteration_threshold)
	       Iteration_Threshold thres = new Iteration_Threshold();
	       bufImg=thres.get_Iteration_Threshold(bufImg_sharpen, Width, Height);
	        
	        
			this.get_Centroid(bufImg);//计算矩心
			this.get_Central_Moment(bufImg);//计算中心矩
			this.get_Normalization();//中心矩归一化
			this.get_Momet_Invariants();//计算不变矩和离心率
			return e;
		}
		public int bubble_sort(int []a)
		{
			for(int i=0;i<a.length-1;i++)
			{
				for(int j=0;j<a.length-i-1;j++)
				{
					if(a[j]>a[j+1])
					{
						int temp=a[j];a[j]=a[j+1];a[j+1]=temp;
					}
				}
			}
			return a[a.length/2];
		}
		//计算矩心
		public void get_Centroid(BufferedImage bufImg) 
		{
			M00=M10=M01=0.0;
			for(int i=0;i<Width;i++)
				for(int j=0;j<Height;j++)
				{
					int pixel=(bufImg.getRGB(i, j)&0xff)&1;	/**二值图0和255转化为0和1**/
					if(pixel==1)							/**目标物体**/	
					{
						M10+=i;
						M01+=j;
						M00+=1;
					}
				}
			x0=M10/M00;
			y0=M01/M00;
			return;
		}
		
		//计算中心矩
		public void get_Central_Moment(BufferedImage bufImg)
		{
			for(int p=0;p<4;p++)
				for(int k=0;k<4;k++)
				{
					M[p][k]=0.0;
					for(int i=0;i<Width;i++)
						for(int j=0;j<Height;j++)
						{
							int pixel=(bufImg.getRGB(i, j)&0xff)&1;	/**二值图0和255转化为0和1**/
							if(pixel==1)
								M[p][k]+=Math.pow(i-x0,p*1.0)*Math.pow(j-y0,k*1.0);
						}
				}
		}	
		//中心矩归一化
		public void get_Normalization()
		{
			for(int p=0;p<4;p++)
				for(int k=0;k<4;k++)
					u[p][k]=M[p][k]/(Math.pow(M[0][0],(((double)(p+k))/2.0+1)));
		}
		//计算不变矩和离心率
		public void get_Momet_Invariants()
		{
			//离心率
			e[0]=(Math.pow(u[2][0]-u[0][2],2.0)+4*Math.pow(u[1][1],2.0))/Math.pow(u[2][0]+u[0][2],2.0);

			//不变矩
			e[1]=u[2][0]+u[0][2];
			e[2]=Math.pow(u[2][0]-u[0][2],2.0)+4*Math.pow(u[1][1],2.0);
			e[3]=Math.pow(u[3][0]-3*u[1][2],2.0)+Math.pow(u[0][3]-3*u[2][1],2.0);
			e[4]=Math.pow(u[3][0]+u[1][2],2.0)+Math.pow(u[0][3]+u[2][1],2.0);
			e[5]=(u[3][0]-3*u[1][2])*(u[3][0]+u[1][2])*(Math.pow(u[3][0]+u[1][2],2.0)
					-3*Math.pow(u[2][1]+u[0][3],2.0))+(u[0][3]-3*u[2][1])*(u[0][3]+u[2][1])
					*(Math.pow(u[0][3]+u[2][1],2.0)-3*Math.pow(u[1][2]+u[3][0],2.0));
			e[6]=(u[2][0]-u[0][2])*(Math.pow(u[3][0]+u[1][2],2.0)-Math.pow(u[2][1]+u[0][3],2.0))
					+4*u[1][1]*(u[3][0]+u[1][2])*(u[0][3]+u[2][1]);
			e[7]=(3*u[2][1]-u[0][3])*(u[3][0]+u[1][2])*(Math.pow(u[3][0]+u[1][2],2.0)-
					3*Math.pow(u[0][3]+u[2][1],2.0))+(3*u[1][2]-u[3][0])*(u[2][1]+u[0][3])
					*(3*Math.pow(u[3][0]+u[1][2],2.0)-Math.pow(u[0][3]+u[2][1],2.0));
		}
		
		
		
		//保存形状特征
		public  void save_feature(double[] data,String path) throws IOException {
		    FileWriter writer = new FileWriter(path, true);  
		    String datas = "" + data[0];
			for(int i=1;i<8;i++) {
				datas = datas + ";" + data[i];   
			}
			writer.write(datas+System.getProperty("line.separator"));   
			writer.close(); 
		}

}
