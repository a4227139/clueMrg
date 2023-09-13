import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class test {
    public static void main(String[] args) throws UnsupportedEncodingException {

        //System.out.println("008618252580086".replaceFirst("0086",""));
        //JZ
        /*System.out.println(Integer.toHexString(46112));
        System.out.println(Integer.toHexString(50489010));
        System.out.println(Integer.toHexString(179564546));
        System.out.println(Integer.toHexString(189179702));
        //YD 涓嶅簲璇ヨ杞崲鐨?
        System.out.println("-----------YD-----------");
        System.out.println(Integer.toHexString(340262));
        System.out.println(Integer.toHexString(404083003));*/

        /*int a=112;int b=22;
        double c;
        c=(double) b/a*100;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String formattedResult = decimalFormat.format(c);
        System.out.println(formattedResult);*/

        String strGBK = "广西柳州市公安局成团派出所";
        byte[] bytes = strGBK.getBytes("GBK");
        String strUTF = new String(bytes,"UTF-8");
        System.out.println(strUTF);
    }
}
