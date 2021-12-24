package user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import opDB.*;
import prepare.*;

public class main {
	private String filepic = null;//保存获取的图库路径
	private String filepath = null;//保存获取的文件路径
	private String pathtxt = "I:\\path.txt";//保存路径
	private String colortxt = "I:\\color.txt";//保存获取的颜色特征
	private String texturetxt = "I:\\texture.txt";//保存获取的纹理特征
	private String shapetxt = "I:\\shape.txt";//保存获取的形状特征
	JFrame frame = new JFrame("图像检索系统");
	private JLabel label_2;//选择的图片
	private JTextField texts;//图库路径
	private JTextField text;//图片路径
	private JTextField text1;//图片路径
	private JTextField text2;//图片路径
	private JTextField text3;//图片路径
	JLabel[] img = new JLabel[12];
	JLabel[] imgpath = new JLabel[12];
	
	
	double[] color = new double[9];//检索图像的颜色特征
	double[] texture = new double[9];//检索图像的纹理特征
	double[] hu = new double[8];//检索图像的形状特征
	
	private void picture_prepare() {
		
	}
	private void init() {
		frame.setBounds(100, 100, 1700, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label_db = new JLabel("载入图库:");
		label_db.setFont(new Font("宋体", Font.PLAIN, 15));
		label_db.setBounds(15, 15, 70, 25);
		frame.add(label_db);
		
		texts = new JTextField();
		texts.setBounds(90, 15, 170, 25);
		frame.add(texts);
		texts.setColumns(10);
		
		JButton brows = new JButton("浏览");
		brows.setFont(new Font("宋体", Font.PLAIN, 15));
		brows.setBounds(260, 15, 70, 25);
		frame.add(brows);
		//弹窗按钮监听
		brows.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				TanChuang1();
			}
		});
		
		JButton jz = new JButton("加载");
		jz.setFont(new Font("宋体", Font.PLAIN, 15));
		jz.setBounds(330, 15, 70, 25);
		frame.add(jz);
		//弹窗按钮监听
		jz.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					JiaZai();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JLabel label = new JLabel("选择文件:");
		label.setFont(new Font("宋体", Font.PLAIN, 15));
		label.setBounds(15, 50, 70, 25);
		frame.add(label);
		
		text = new JTextField();
		text.setBounds(90, 50, 170, 25);
		frame.add(text);
		text.setColumns(10);
		
		JButton brow = new JButton("浏览");
		brow.setFont(new Font("宋体", Font.PLAIN, 15));
		brow.setBounds(260, 50, 70, 25);
		frame.add(brow);
		//弹窗按钮监听
		brow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					TanChuang();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JLabel label_1 = new JLabel("当前图像:");
		label_1.setFont(new Font("宋体", Font.PLAIN, 15));
		label_1.setBounds(15, 85, 80, 25);
		frame.add(label_1);
		
		label_2 = new JLabel("");
		label_2.setBounds(30, 135, 300, 200);
		frame.add(label_2);
		
		for(int i=0;i<12;i++) {
			img[i] = new JLabel();
			imgpath[i] = new JLabel();
		}
		JLabel label1 = new JLabel("单特征检索:");
		label1.setFont(new Font("宋体", Font.PLAIN, 15));
		label1.setBounds(15, 385, 100, 25);
		frame.add(label1);
		
		JButton b1 = new JButton("基于颜色检索");
		b1.setFont(new Font("宋体", Font.PLAIN, 15));
		b1.setBounds(100, 435, 130, 30);
		frame.add(b1);
		//颜色检索按钮监听
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					color();//基于颜色检索
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton b2 = new JButton("基于纹理检索");
		b2.setFont(new Font("宋体", Font.PLAIN, 15));
		b2.setBounds(100, 485, 130, 30);
		frame.add(b2);
		//纹理检索按钮监听
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					texture();
				} catch (Exception e1) {
					e1.printStackTrace();
				}//基于纹理检索
			}
		});
		
		

		JButton b3 = new JButton("基于形状检索");
		b3.setFont(new Font("宋体", Font.PLAIN, 15));
		b3.setBounds(100, 535, 130, 30);
		frame.add(b3);
		//纹理检索按钮监听
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					shape();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//基于纹理检索
			}
		});
		
		JLabel zong = new JLabel("综合检索(请填入特征占权重):");
		zong.setFont(new Font("宋体", Font.PLAIN, 15));
		zong.setBounds(15, 610, 210, 25);
		frame.add(zong);
		
		JLabel zong1 = new JLabel("颜色权重(0.00-1.00):");
		zong1.setFont(new Font("宋体", Font.PLAIN, 15));
		zong1.setBounds(60, 650, 180, 25);
		frame.add(zong1);
		text1 = new JTextField();
		text1.setBounds(220, 650, 50, 25);
		frame.add(text1);
		text1.setColumns(10);
		
		JLabel zong2 = new JLabel("纹理权重(0.00-1.00):");
		zong2.setFont(new Font("宋体", Font.PLAIN, 15));
		zong2.setBounds(60, 690, 180, 25);
		frame.add(zong2);
		text2 = new JTextField();
		text2.setBounds(220, 690, 50, 25);
		frame.add(text2);
		text2.setColumns(10);
		
		JLabel zong3 = new JLabel("形状权重(0.00-1.00):");
		zong3.setFont(new Font("宋体", Font.PLAIN, 15));
		zong3.setBounds(60, 730, 180, 25);
		frame.add(zong3);
		text3 = new JTextField();
		text3.setBounds(220, 730, 50, 25);
		frame.add(text3);
		text3.setColumns(10);
		
		JButton b4 = new JButton("综合检索");
		b4.setFont(new Font("宋体", Font.PLAIN, 15));
		b4.setBounds(100, 780, 130, 30);
		frame.add(b4);
		//综合检索按钮监听
		b4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					zonghe();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//基于综合检索
			}
		});
		
		frame.setVisible(true);
	}
	public String TanChuang1(){
		JFileChooser chooser=new JFileChooser("E:\\");
		chooser.setDialogTitle("请选择图库");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//弹出选择框
		int returnVal=chooser.showOpenDialog(null);
		//如果选择了文件
		if(JFileChooser.APPROVE_OPTION==returnVal){
			filepic=chooser.getSelectedFile().toString();//获取所选文件路径
			texts.setText(filepic);//把路径值写到textField中
			return filepic;
		}
		else{
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		     JOptionPane.showMessageDialog(null, "未选中图库","提示",JOptionPane.PLAIN_MESSAGE);
			System.out.println("还未选择图库");
			return null;
		}
	}
	//加载图库
	public void JiaZai() throws Exception{
		//找到图库中所有jpg文件
		File file = new File(pathtxt);
	    if (file.exists()) {
	      	file.delete();
        }
	    File file1 = new File(colortxt);
	    if (file1.exists()) {
	      	file1.delete();
        } 
	    File file2 = new File(texturetxt);
	    if (file2.exists()) {
	      	file2.delete();
        } 
		File file3 = new File(shapetxt);
	    if (file3.exists()) {
	      	file3.delete();
        } 
	    FileWriter writer = new FileWriter(pathtxt, true);  
		new getPicture().find(writer,filepic, "\\S+\\.jpg");
		writer.close(); 
		//提取所有文件的特征到文件
		getColor get1 = new getColor();
		getTexture get2 = new getTexture();
		getShape get3 = new getShape();
		//颜色特征
		ArrayList<String> paths = new ArrayList<String>();//路径
		paths = get1.getPath(pathtxt);
		int length = paths.size();
		for(int i=0;i<length;i++) {
			get1.save_feature(get1.color_HSV_msv(paths.get(i)),colortxt);
		}
		//纹理特征
		
		for(int i=0;i<length;i++) {
			System.out.println(paths.get(i));
			get2.save_feature(get2.texture(paths.get(i)),texturetxt);
		}
		//形状特征
		
		for(int i=0;i<length;i++) {
			System.out.println(paths.get(i));
			get3.save_feature(get3.shape(paths.get(i)),shapetxt);
		}
		//将数据写入数据库
		Insert in = new Insert();
		ArrayList<String> SQL = in.readFile(pathtxt);
		SQL = in.pathInsertsql(SQL);
        in.insertdb(SQL,"path");
		SQL = in.readFile(colortxt);
		SQL = in.colorInsertsql(SQL);
        in.insertdb(SQL,"color");
        SQL = in.readFile(texturetxt);
        SQL = in.textureInsertsql(SQL);
        in.insertdb(SQL,"texture");
        SQL = in.readFile(shapetxt);
        SQL = in.shapeInsertsql(SQL);
        in.insertdb(SQL,"shape");
	}
	public String TanChuang() throws Exception{
		JFileChooser chooser=new JFileChooser("E:\\data");
		chooser.setDialogTitle("请选择图片文件");
		//设置为只能选择图片文件
		FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "jpg");
		chooser.setFileFilter(filter);
		//弹出选择框
		int returnVal=chooser.showOpenDialog(null);
		//如果选择了文件
		if(JFileChooser.APPROVE_OPTION==returnVal){
			filepath=chooser.getSelectedFile().toString();//获取所选文件路径
			text.setText(filepath);//把路径值写到textField中
			//在当前面板上显示所选图像
			ImageIcon sourceimg=new ImageIcon(filepath);
			int width=sourceimg.getIconWidth();
			int height=sourceimg.getIconHeight();
			label_2.setIcon(sourceimg);
			return filepath;
		}
		else{
			System.out.println("还未选择文件");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		     JOptionPane.showMessageDialog(null, "未选中文件","提示",JOptionPane.PLAIN_MESSAGE);
			return null;
		}
	}
	//基于颜色检索
	public void color() throws Exception {
		if(filepath == null) {
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		     JOptionPane.showMessageDialog(null, "还未选择文件","提示",JOptionPane.PLAIN_MESSAGE);
		     return ;
		}
		color = getColor.color_HSV_msv(filepath);
		Map<Integer,Double> map = new TreeMap<Integer,Double>();//相似度
		Select select = new Select();
		ArrayList<double[]> colors = new ArrayList();
		colors = select.selectfeature("color");
		int num = colors.size();
		double d = 0;
		for(int i=0;i<num;i++) {
			d = Compare_color(color,colors.get(i));
			map.put(i, d);
		}
		ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(map.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());//按value从小到大排序
            }
        });
        int[] best = new int[12];//最匹配的12张照片id
        for(int i=0;i<12;i++) {
        	Entry<Integer,Double> p = entryArrayList.get(i);
        	best[i] = p.getKey();
        }
        ArrayList<String> paths = new ArrayList();
        paths = select.selectpath(select.pathSelectsql(best));
        /*for(int i=0;i<12;i++) {
        	System.out.println(paths.get(i));
        }*/
       
        
        int i=0;
        for(int y=70;y<700;y+=240){
        	for(int x=400;x<1600;x+=320){
        		if(i<12){
        			ImageIcon sourceimg1=new ImageIcon(paths.get(i));
	        		img[i].setBounds(x, y, 300, 200);
	        		img[i].setIcon(sourceimg1);
	        		imgpath[i].setText(i+1+":"+paths.get(i));
	        		imgpath[i].setFont(new Font("宋体", Font.PLAIN, 12));
	        		imgpath[i].setBounds(x, y+205, 300, 20);
	        		
	        		frame.add(img[i]);
	        		frame.add(imgpath[i]);
	        		i++;
        		}
        	}
        }
	}
	//基于纹理检索
	public void texture() throws Exception {
		if(filepath == null) {
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		     JOptionPane.showMessageDialog(null, "还未选择文件","提示",JOptionPane.PLAIN_MESSAGE);
		     return ;
		}
		texture = getTexture.texture(filepath);
		Map<Integer,Double> map = new TreeMap<Integer,Double>();//相似度
		Select select = new Select();
		ArrayList<double[]> texs = new ArrayList();
		texs = select.selectfeature("texture");
		int num = texs.size();
		double d = 0;
		for(int i=0;i<num;i++) {
			d = cos_similar(texture,texs.get(i));
			map.put(i, d);
		}
		ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(map.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
            }
        });
        int[] best = new int[12];//最匹配的12张照片id
        for(int i=0;i<12;i++) {
        	Entry<Integer,Double> p = entryArrayList.get(i);
        	best[i] = p.getKey();
        }
        ArrayList<String> paths = new ArrayList();
        paths = select.selectpath(select.pathSelectsql(best));
        /*for(int i=0;i<12;i++) {
        	System.out.println(paths.get(i));
        }*/
        int i=0;
        for(int y=70;y<700;y+=240){
        	for(int x=400;x<1600;x+=320){
        		if(i<12){
        			ImageIcon sourceimg1=new ImageIcon(paths.get(i));
	        		img[i].setBounds(x, y, 300, 200);
	        		img[i].setIcon(sourceimg1);
	        		imgpath[i].setText(i+1+":"+paths.get(i));
	        		imgpath[i].setFont(new Font("宋体", Font.PLAIN, 12));
	        		imgpath[i].setBounds(x, y+205, 300, 20);
	        		
	        		frame.add(img[i]);
	        		frame.add(imgpath[i]);
	        		i++;
        		}
        	}
        }
        
	}
	//基于形状检索
		public void shape() throws Exception {
			if(filepath == null) {
				 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			     JOptionPane.showMessageDialog(null, "还未选择文件","提示",JOptionPane.PLAIN_MESSAGE);
			     return ;
			}
			hu = new getShape().shape(filepath);
			Map<Integer,Double> map = new TreeMap<Integer,Double>();//相似度
			Select select = new Select();
			ArrayList<double[]> shapes = new ArrayList();
			shapes = select.selectfeature("shape");
			int num = shapes.size();
			double d = 0;
			for(int i=0;i<num;i++) {
				d = cos_similar(hu,shapes.get(i));
				map.put(i, d);
			}
			ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(map.entrySet());
	        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
	            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
	                return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
	            }
	        });
	        int[] best = new int[12];//最匹配的12张照片id
	        for(int i=0;i<12;i++) {
	        	Entry<Integer,Double> p = entryArrayList.get(i);
	        	best[i] = p.getKey();
	        }
	        ArrayList<String> paths = new ArrayList();
	        paths = select.selectpath(select.pathSelectsql(best));
	        /*for(int i=0;i<12;i++) {
	        	System.out.println(paths.get(i));
	        }*/
	        int i=0;
	        for(int y=70;y<700;y+=240){
	        	for(int x=400;x<1600;x+=320){
	        		if(i<12){
	        			ImageIcon sourceimg1=new ImageIcon(paths.get(i));
		        		img[i].setBounds(x, y, 300, 200);
		        		img[i].setIcon(sourceimg1);
		        		imgpath[i].setText(i+1+":"+paths.get(i));
		        		imgpath[i].setFont(new Font("宋体", Font.PLAIN, 12));
		        		imgpath[i].setBounds(x, y+205, 300, 20);
		        		
		        		frame.add(img[i]);
		        		frame.add(imgpath[i]);
		        		i++;
	        		}
	        	}
	        }
	       
	}	
		//综合
		public void zonghe() throws Exception {
			if(filepath == null) {
				 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			     JOptionPane.showMessageDialog(null, "还未选择文件","提示",JOptionPane.PLAIN_MESSAGE);
			     return ;
			}
			color = getColor.color_HSV_msv(filepath);
			texture = getTexture.texture(filepath);
			hu = new getShape().shape(filepath);
			Map<Integer,Double> map = new TreeMap<Integer,Double>();//相似度
			Select select = new Select();
			ArrayList<double[]> colors = new ArrayList();
			colors = select.selectfeature("color");
			ArrayList<double[]> textures = new ArrayList();
			textures = select.selectfeature("texture");
			ArrayList<double[]> shapes = new ArrayList();
			shapes = select.selectfeature("shape");
			int num = shapes.size();
			double d1 = 0,d2 = 0,d3 = 0,d = 0;;
		    String s1 = text1.getText();
		    String s2 = text2.getText();
		    String s3 = text3.getText();
		    if(s1.isEmpty() || s2.isEmpty() || s3.isEmpty() ) {
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			     JOptionPane.showMessageDialog(null, "有未填入的特征占比","提示",JOptionPane.PLAIN_MESSAGE);
			     return ;
		    }
		    double b1 = new Double(s1);
		    double b2 = new Double(s2);
		    double b3 = new Double(s3);
			for(int i=0;i<num;i++) {
				d1 = cos_similar(color,colors.get(i));
				d2 = cos_similar(texture,textures.get(i));
				d3 = cos_similar(hu,shapes.get(i));
				d = d1*b1+d2*b2+d3*b3;
				map.put(i, d);
			}
			ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(map.entrySet());
	        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
	            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
	                return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
	            }
	        });
	        int[] best = new int[12];//最匹配的12张照片id
	        for(int i=0;i<12;i++) {
	        	Entry<Integer,Double> p = entryArrayList.get(i);
	        	best[i] = p.getKey();
	        }
	        ArrayList<String> paths = new ArrayList();
	        paths = select.selectpath(select.pathSelectsql(best));
	        /*for(int i=0;i<12;i++) {
	        	System.out.println(paths.get(i));
	        }*/
	        int i=0;
	        for(int y=70;y<700;y+=240){
	        	for(int x=400;x<1600;x+=320){
	        		if(i<12){
	        			ImageIcon sourceimg1=new ImageIcon(paths.get(i));
		        		img[i].setBounds(x, y, 300, 200);
		        		img[i].setIcon(sourceimg1);
		        		imgpath[i].setText(i+1+":"+paths.get(i));
		        		imgpath[i].setFont(new Font("宋体", Font.PLAIN, 12));
		        		imgpath[i].setBounds(x, y+205, 300, 20);
		        		
		        		frame.add(img[i]);
		        		frame.add(imgpath[i]);
		        		i++;
	        		}
	        	}
	        }
	       
	}
		
	//比较特征值 欧氏距离
    public double Compare_color(double[] a,double[] b) {
	    double D,sum=0;
	    for(int i=0;i<9;i++) {
		    sum = sum + Math.pow((a[i]-b[i]), 2);
	    }
	    D = Math.pow(sum, 0.5);
	    return D;
    }
   //用余弦定理求匹配图片与数据库中图片的相似度
  	public double cos_similar(double[] daicha, double[] kuzhi) {
  		double cosvalue=1,fenzi=0,fenmu1=0,fenmu2=0;
  		for (int i=0;i<kuzhi.length;i++) {
  			fenzi+=daicha[i]*kuzhi[i];
  			fenmu1+=daicha[i]*daicha[i];
  			fenmu2+=kuzhi[i]*kuzhi[i];
  		}
  		fenmu1=Math.sqrt(fenmu1);
  		fenmu2=Math.sqrt(fenmu2);
  		cosvalue=fenzi/(fenmu1*fenmu2);
  		return cosvalue;
  	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		new main().init();
	}

}
