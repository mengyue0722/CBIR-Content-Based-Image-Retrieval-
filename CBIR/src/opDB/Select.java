package opDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class Select {
	Connection con;
	String driver = "com.mysql.cj.jdbc.Driver";
    String user = "root";
    String password = "990722";
    String picture_url = "jdbc:mysql://localhost:3306/picture?serverTimezone=GMT";//图片库
    String feature_url = "jdbc:mysql://localhost:3306/feature?serverTimezone=GMT";//特征库
    
    //查询特征库，返回特征数据
	public ArrayList<double[]> selectfeature(String kind) throws Exception {
    	Class.forName(driver);
        con = DriverManager.getConnection(feature_url,user,password);
        Statement statement = con.createStatement();
        ResultSet rs = null;
        String sql;
        ArrayList<double[]> datas = new ArrayList();
        int num = 0;//特征值数目
        switch(kind) {
            case "color":
        	    num = 9;
        	    break;
            case "texture":
        	    num = 8;
        	    break;
            case "shape":
        	    num = 8;
        	    break;
        	default:
        		System.out.println("feature is error");
        	    break;
        }
        sql =  "select * from " + kind + ";"; 
        rs = statement.executeQuery(sql);
        ResultSetMetaData metadata = rs.getMetaData();  
        int col = metadata.getColumnCount()-1;
        double[] data = new double[num];
        int k=0;
        while(rs.next()){
        	for(int i=0;i<col;i++) {
        		data[i] = rs.getDouble(i+2);
        	}
       		datas.add(data.clone());
        }
        rs.close();
        con.close();
        return datas;
    }
    //查询数据库，返回图片路径
    public ArrayList<String> selectpath(ArrayList<String> sqls) throws Exception {
    	Class.forName(driver);
        con = DriverManager.getConnection(picture_url,user,password);
        Statement statement = con.createStatement();
        ResultSet rs = null;
        ArrayList<String> paths = new ArrayList<String>();
        int num = sqls.size();
        for(int i=0;i<num;i++) {
            rs = statement.executeQuery(sqls.get(i));
        	while(rs.next()){
            		paths.add(rs.getString("path"));
            }
        }
        rs.close();
        statement.close();
        con.close();
        return paths;
    }
    //生成查询路径的sql语句
    public ArrayList<String> pathSelectsql(int[] ids){
    	ArrayList<String> sqls = new ArrayList<String>();
    	String sql;
    	int num = ids.length;
    	for(int i=0;i<num;i++) {
    		sql =  "select * from paths where id=" + ids[i] + ";"; 
    		sqls.add(sql);
    	}
    	return sqls;
    }


}
