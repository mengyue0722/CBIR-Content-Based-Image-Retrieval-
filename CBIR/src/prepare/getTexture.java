package prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class getTexture {
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
	public static double[] texture(String path) {
		Mat mat = Imgcodecs.imread(path);
	    Mat gray = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);//灰度图象
	    Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);//RGB->GRAY
	    int height = gray.height();
	    int width = gray.width();
	    int [][] graylow = new int[height][width];//降低等级后的灰度图像
	    double[][][] glcm = new double[4][8][8];//四个灰度共生矩阵
	    int gi,gj;//像素对
	    int sum0 = 0,sum45 = 0,sum90 = 0,sum135 = 0;//不同角度灰度共生矩阵中像素点个数
	    double[] asm=new double[4];//纹理一致性
		double[] contrast=new double[4];//纹理对比度
		double[] entropy=new double[4];//纹理熵
		double[] correlation=new double[4];//纹理相关性
		double[] ux=new double[4];//相关性的μ
		double[] uy=new double[4];
		double[] ax=new double[4];//相关性的σ
		double[] ay=new double[4];
		
	    //降低灰度等级，分成8个区间
	    for(int i=0;i<height;i++) {
	    	for(int j=0;j<width;j++) {
	    		graylow[i][j] = (int) (gray.get(i,j)[0]/32);
	    	}
	    }
	    //按四个方向遍历图片，记录灰度值对出现的次数
        for(int i=0;i<height;i++){
		    for(int j=0;j<width;j++){
				//0度
				if(i+2>=0&&i+2<height){
					gi=graylow[i][j];
					gj=graylow[i+2][j];
					glcm[0][gi][gj]+=1;
					sum0++;
				}
				//45度
				if(i+2>=0&&i+2<height&&j+2>=0&&j+2<width){
					gi=graylow[i][j];
					gj=graylow[i+2][j+2];
					glcm[1][gi][gj]+=1;
					sum45++;
				}
				//90度
				if(j+2>=0&&j+2<width){
					gi=graylow[i][j];
					gj=graylow[i][j+2];
					glcm[2][gi][gj]+=1;
					sum90++;
				}
				//135度
				if(i-2>=0&&i-2<height&&j+2>=0&&j+2<width){
					gi=graylow[i][j];
					gj=graylow[i-2][j+2];
					glcm[3][gi][gj]+=1;
					sum135++;
				}
			}
		}
        //求灰度共生矩阵，每个单元为像素对出现的概率
      	for(int i=0;i<8;i++){
      		for(int j=0;j<8;j++){
      			glcm[0][i][j]=glcm[0][i][j]/sum0;
      			glcm[1][i][j]=glcm[1][i][j]/sum45;
      			glcm[2][i][j]=glcm[2][i][j]/sum90;
      			glcm[3][i][j]=glcm[3][i][j]/sum135;
      		}
      	}
      	//计算纹理特征
      	for(int index=0;index<4;index++){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					//纹理一致性
					asm[index]+=Math.pow(glcm[index][i][j], 2);
					//纹理对比度
					contrast[index]+=Math.pow(i-j,2)*glcm[index][i][j];
					//纹理熵
					if(glcm[index][i][j]!=0){
						entropy[index]-=glcm[index][i][j]*Math.log(glcm[index][i][j]);
					}
					//相关性的u
					ux[index]+=i*glcm[index][i][j];
					uy[index]+=j*glcm[index][i][j];
				}
			}
		}
        //相关性的σ
      	for(int index=0;index<4;index++){
      	    for(int i=0;i<8;i++){
      		    for(int j=0;j<8;j++){
      		        ax[index]+=Math.pow(i-ux[index], 2)*glcm[index][i][j];
      		        ay[index]+=Math.pow(j-uy[index], 2)*glcm[index][i][j];
      		        correlation[index]+=i*j*glcm[index][i][j];
      		    }
      		}
      	    //纹理相关性
      	    if(ax[index] != 0 && ay[index] != 0) {
      	    	correlation[index]=(correlation[index]-ux[index]*uy[index])/ax[index]/ay[index];
      	    }
      	    else {
      	    	correlation[index] = 8;
      	    }
      	}
      	double [] y = new double[8];
        //期望
	    y[0] = (asm[0]+asm[1]+asm[2]+asm[3])/4;
	    y[1] = (contrast[0]+contrast[1]+contrast[2]+contrast[3])/4;
	    y[2] = (correlation[0]+correlation[1]+correlation[2]+correlation[3])/4;
	    y[3] = (entropy[0]+entropy[1]+entropy[2]+entropy[3])/4;
	    //标准差
	    y[4] = Math.sqrt(Math.pow(asm[0]-y[0], 2)+Math.pow(asm[1]-y[0], 2)+Math.pow(asm[2]-y[0], 2)+Math.pow(asm[3]-y[0], 2));
	    y[5] = Math.sqrt(Math.pow(contrast[0]-y[0], 2)+Math.pow(contrast[1]-y[0], 2)+Math.pow(contrast[2]-y[0], 2)+Math.pow(contrast[3]-y[0], 2));;
	    y[6] = Math.sqrt(Math.pow(correlation[0]-y[0], 2)+Math.pow(correlation[1]-y[0], 2)+Math.pow(correlation[2]-y[0], 2)+Math.pow(correlation[3]-y[0], 2));;
	    y[7] = Math.sqrt(Math.pow(entropy[0]-y[0], 2)+Math.pow(entropy[1]-y[0], 2)+Math.pow(entropy[2]-y[0], 2)+Math.pow(entropy[3]-y[0], 2));;	   
        return y;
    }
	//保存纹理特征
	public void save_feature(double[] data,String path) throws IOException {
		
	    FileWriter writer = new FileWriter(path, true);  
	    String datas = "" + data[0];
		for(int i=1;i<8;i++) {
			datas = datas + ";" + data[i];   
		}
		writer.write(datas+System.getProperty("line.separator"));   
		writer.close(); 
	}

	

}
