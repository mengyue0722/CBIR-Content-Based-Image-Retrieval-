package prepare;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//读取文件下的所有图片路径 保存在print.txt
public class getPicture {
	
	
	public void find(FileWriter writer,String path,String reg) throws IOException{

		Pattern pat=Pattern.compile(reg);

		File file=new File(path);
		File[] arr=file.listFiles();
		
		for(int i=0;i<arr.length;i++){

			//判断是否是文件夹，如果是的话，再调用一下find方法
			if(arr[i].isDirectory()){
				find(writer, arr[i].getAbsolutePath(),reg);
			}
			Matcher mat=pat.matcher(arr[i].getAbsolutePath());
			//根据正则表达式，寻找匹配的文件
			String str = arr[i].getAbsolutePath();
			if(mat.matches()){
				//这个getAbsolutePath()方法返回一个String的文件绝对路径
				System.out.println(str);
				writer.write(str+System.getProperty("line.separator"));   
			}
		}
	}

	public static void main(String[] args) throws IOException{
		String filename = "E:\\print.txt";
		File file = new File(filename);
	    if (file.exists()) {
	      	file.delete();
        }
	    FileWriter writer = new FileWriter("E:\\print.txt", true);  
		new getPicture().find(writer,"E:\\data", "\\S+\\.jpg");
		writer.close(); 
	}
}
