import jxl.Sheet;
import jxl.Workbook;

import javax.xml.crypto.dsig.keyinfo.PGPData;
import java.io.File;
import java.util.Arrays;


public class Road_embedding {
    private static final int INF = Integer.MAX_VALUE;   // 最大值
    private char[] mVexs;       // 顶点集合
    public static void main(String[] args) {
        int[][] vexs_coordinate=new int[54][2];
        int[] vexs=new int[54];
        int[][] matrix=new int[54][54];
        for (int i = 0; i < 54; i++)  vexs[i]=i;//初始化vexs
        try{
            File file=new File("allgraph.xls");
            Workbook workbook = Workbook.getWorkbook(file);// 获得工作簿对象
            Sheet[] sheets = workbook.getSheets();// 获得所有工作表
            int rows = sheets[0].getRows();// 获得行数
            int cols = sheets[0].getColumns();// 获得列数
//            System.out.println("行数："+rows+" 列数："+cols);// 读取数据
            for(int row=1;row<55;row++){
                vexs_coordinate[row-1][0]=Integer.parseInt(sheets[0].getCell(0,row).getContents());
                vexs_coordinate[row-1][1]=Integer.parseInt(sheets[0].getCell(1,row).getContents());
//                System.out.println(vexs_coordinate[row-1][0]+"   "+vexs_coordinate[row-1][1]);//get it!
            }
            for (int row = 1; row < 55; row++)
            {
                for (int col = 3; col < row+3; col++)
                {
                    String A=sheets[0].getCell(col,row).getContents();
//                    System.out.printf("%6s",A);
                    String N="N";
                    if(A.equals(N)==true){
                        matrix[row-1][col-3]=INF;
                        matrix[col-3][row-1]=INF;
//                        System.out.print("yes");
                    }else if(A.equals("0")){
                        matrix[row-1][col-3]=0;
                        matrix[col-3][row-1]=0;
//                        System.out.print("zero");
                    }else {
                        matrix[row-1][col-3]=Integer.parseInt(A);
                        matrix[col-3][row-1]=Integer.parseInt(A);
//                        System.out.print("ohhh  ");
                    }
//                    System.out.print("    ");
                }
//                System.out.println();
            }
            // 遍历工作表
//            if (sheets != null)
//            {
//                System.out.println("dawd");
//                for (Sheet sheet : sheets)
//                {
//                    // 获得行数
//                    rows = sheet.getRows();
//                    // 获得列数
//                    cols = sheet.getColumns();
//                    // 读取数据
//                    System.out.println("行数："+rows+" 列数："+cols);
//                    for (int row = 0; row < rows; row++)
//                    {
//                        for (int col = 0; col < cols; col++)
//                        {
//                            System.out.printf("%6s",sheet.getCell(col,row).getContents());
//                            System.out.print("    ");
//                        }
//                        System.out.println();
//                    }
//                }
//            }
            workbook.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        //以上为都vexcel
        //输出excel所读数据
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
//                if(matrix[i][j]!=2147483647)
//                System.out.print(matrix[i][j]+"  ");
            }
//            System.out.println();
        }
        MatrixUDG pG= new MatrixUDG(vexs, matrix);//MatrixUDG_一个图类,里面封装了图的最短路径以及路网嵌入的计算等
        int[][] path = new int[pG.mVexs.length][pG.mVexs.length];//这是最短路径上所经点的记录
        int[][] floyd = new int[pG.mVexs.length][pG.mVexs.length];//这是floyd所计算出来的点间距离矩阵
        // floyd算法获取各个顶点之间的最短距离
        pG.floyd(path, floyd);//调用floyd算法
//        pG.floyd_path(path);//可视化打印floyd计算的最短路径
        int[][] Omega=pG.road_embedding(floyd);//路网嵌入计算！
//        int[] Ride=pG.calc_point(0,1,0);//计算乘客的路网嵌入
//        int[] driver1=pG.calc_point(2,4,1657);//next_a是邻接的第一个点,next_b是邻接的第二个点,point2a_len是点到a的距离
//        int[] driver2=pG.calc_point(6,0,5000);
        int[] Ride=pG.calc_point(26,27,0);//计算乘客的路网嵌入
        int[] driver1=pG.calc_point(26,24,100);//next_a是邻接的第一个点,next_b是邻接的第二个点,point2a_len是点到a的距离
        int[] driver2=pG.calc_point(29,33,0);
        Road_map_compare(pG,Ride,driver1,driver2);//路网比较，这是直接比较

        //以下为加入了混淆电路的比较
        int[] mu1=new int[54];//μ1
        int[] mu2=new int[54];//μ2
        Arrays.fill(mu1, 0);//初始化
        Arrays.fill(mu2, 0);
        int[] Driver1=Ride_Driver_mu(Ride,driver1,mu1);
        int[] Driver2=Ride_Driver_mu(Ride,driver2,mu2);
        two_point_gc_test(Driver1,Driver2,mu1,mu2);
    }
    public static void Road_map_compare(MatrixUDG pG,int[] Ride,int[] driver1,int[] driver2){
        int Min_len1=two_point_len(Ride,driver1);//乘客与司机1的距离
        int Min_len2=two_point_len(Ride,driver2);//乘客与司机2的距离
        if(Min_len1<Min_len2){
            System.out.println("乘客离司机1最近");
        }else{
            System.out.println("乘客离司机2最近");
        }
    }
    //计算两点之间最短距离
    public static int two_point_len(int[] pointA,int[] pointB){
        int Min_len=0;
        Min_len=Math.abs(pointA[0]-pointB[0]);
        for (int i = 1; i < pointA.length; i++) {
            Min_len=Math.max(Math.abs(pointA[i]-pointB[i]),Min_len);
        }
        System.out.print("ride与driver之间距离是：");
        System.out.println(Min_len);
        return Min_len;
    }
    //这里跳过rider和driver的初始化，直接计算p【d】,而且没有同态加密，已经假设CP有了同态加密
    public static int[] Ride_Driver_mu(int[] ride,int[] driver,int[] mu){
        int[] result=new int[54];//如果 加点 要改大小
        for (int i = 1; i < ride.length; i++) {
            result[i]=ride[i]-driver[i]+mu[i];
        }
        return result;
    }
    //模拟cp与服务器之前的操作，暂时先放在一起
    public static void two_point_gc_test(int[] driver1,int[] driver2,int[] mu1,int[] mu2){
        garbled_circuit gc=new garbled_circuit();
        int driver1_min=driver1[0];
        int mu1_min=mu1[0];
        for (int i = 1; i < driver1.length; i++) {
            boolean result=gc.gc_compare_2(driver1_min,mu1_min,driver1[i],mu1[i]);
            //result为true代表：driver2_min比driver2[i]小
            if(!result){
                driver1_min=driver1[i];
                mu1_min=mu1[i];
            }
        }
        int driver2_min=driver1[0];
        int mu2_min=mu1[0];
        for (int i = 1; i < driver2.length; i++) {
            boolean result=gc.gc_compare_2(driver2_min,mu2_min,driver2[i],mu2[i]);
            //result为true代表：driver2_min比driver2[i]小
            if(!result){
                driver2_min=driver2[i];
                mu2_min=mu2[i];
            }
        }
        if(driver1_min-mu1_min<driver2_min-mu2_min){
            System.out.println("乘客离司机1最近");
        }else{
            System.out.println("乘客离司机2最近");
        }
    }
}
