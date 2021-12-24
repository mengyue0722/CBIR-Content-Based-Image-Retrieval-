package prepare;

import java.util.*;
import java.io.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;
//读取txt获取图片路径，计算每个图片的颜色特征保存在color.txt
public class getColor {
	//读取txt，获得图片路径
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
	static double mean(double [] data){        //一阶矩均值      
	    double sum=0;       
	    for(int i=0;i<data.length;i++){         
		    sum+=data[i];      
		    }      
	    double mean=sum/data.length;
	    //      System.out.println(mean);        
	    return mean;     
	}     
    static double std(double [] data,double mean){    //二阶矩方差 
    	double sum=0;      
    	for(int i=0;i<data.length;i++){     
    		sum+=Math.pow((data[i]-mean), 2);  
    	}//         System.out.println(sum);      
    	double std=Math.pow((sum/data.length),0.5);     
    	return std;  
    }     
    static double skew(double [] data,double mean){   //三阶矩斜度   
    	double sum=0;    
    	for(int i=0;i<data.length;i++){   
    		sum+=Math.pow((data[i]-mean), 3);   
    		}      
    	double skew=Math.cbrt(sum/data.length);     
    	return skew;   
    }
    //计算9个特征值y[]
    public static double[] color_HSV_msv(String path) {
    	Mat mat = Imgcodecs.imread(path);
    	//直方图均衡化 图片增强
    	Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> list1 = new ArrayList<>();
        Core.split(mat, list1);
        Imgproc.equalizeHist(list1.get(0), list1.get(0));
        Core.normalize(list1.get(0), list1.get(0), 0, 255, Core.NORM_MINMAX);
        Core.merge(list1, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_YCrCb2BGR);
        Mat imagemat = mat;
        //RGB到HSV
        //Mat imagemat = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);
	    //Imgproc.cvtColor(mat, imagemat, Imgproc.COLOR_RGB2HSV);
	    double [] y = new double[9];
	    double [] B=new double[imagemat.rows()*imagemat.cols()];         
	    double [] G=new double[imagemat.rows()*imagemat.cols()];         
	    double [] R=new double[imagemat.rows()*imagemat.cols()];         
	    for(int j=0;j<imagemat.rows();j++){                     
	        for(int k=0;k<imagemat.cols();k++){     
	            double [] data=imagemat.get(j, k);   
	    	    B[j*imagemat.cols()+k]=data[0];        
	    	    G[j*imagemat.cols()+k]=data[1];        
	            R[j*imagemat.cols()+k]=data[2];       
	        }     
	    }        
	   y[0]=mean(B);       
	   y[1]=std(B, mean(B));     
	   y[2]=skew(B, mean(B));      
	   y[3]=mean(G);      
	   y[4]=std(G, mean(G));   
	   y[5]=skew(G, mean(G));    
	   y[6]=mean(R);     
	   y[7]=std(R, mean(R));    
	   y[8]=skew(R, mean(R));
	   //归一化
	   double[] temp = new double[9];
	   temp[0] = y[0];
	   temp[1] = y[1];
	   temp[2] = y[2];
	   temp[3] = y[3];    
	   temp[4] = y[4];
	   temp[5] = y[5];
	   temp[6] = y[6];
	   temp[7] = y[7];
	   temp[8] = y[8];
	   Arrays.sort(temp);
	   double mm = temp[8]-temp[0];
	   double min = temp[0];
	   for(int i=0;i<9;i++) {
		   y[i] = (y[i]-min)/mm;
	   }   
       return y;
    }
    public void save_feature(double[] data,String path) throws IOException {
    
    	FileWriter writer = new FileWriter(path, true);  
    	String datas = "" + data[0];
		for(int i=1;i<9;i++) {
			datas = datas + ";" + data[i];   
		}
		writer.write(datas+System.getProperty("line.separator"));   
		writer.close(); 
    }
	
}
