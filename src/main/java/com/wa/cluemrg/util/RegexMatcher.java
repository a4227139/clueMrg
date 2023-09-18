package com.wa.cluemrg.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexMatcher {
    static String[] regexArray = {
            "我叫：?([^，,：:。.}】、；;|\\])）\\s+]+)",
            "([^，,：:。.}】、；;|\\]）)\\s+]+)）?报警?称",
            "[，。,.|\\n](.*?)[，。,.]?[（(](?:男|女|身份证)[,，：:]?",
            "(?:报警人|报案人|事主|被诈骗人)[：|\\s+|叫|是|名为|姓名]?([^，,：:。.{}【】、；;|\\[\\]()（）\\s]+)",
            ".+(?==，|。|\\.|,|\\s+|，身份证)"
    };


    static Pattern[] patterns = new Pattern[regexArray.length];
    static {
        for (int i=0;i<regexArray.length;i++) {
            Pattern pattern = Pattern.compile(regexArray[i]);
            patterns[i]=pattern;
        }
    }

    public static String matchNameRegex(String text) {
        if (StringUtils.isEmpty(text)){
            return "";
        }
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                //System.out.println(regex);
                //System.out.println(matcher.group(0));
                try {
                    return matcher.group(1);
                }catch (IndexOutOfBoundsException e){
                    //e.printStackTrace();
                    return "";
                }
            }
        }
        return "";
    }

    static String regexId = "(\\b|是|为|证|号)(\\d{17}(\\d|X|x))\\b";
    static Pattern patternId = Pattern.compile(regexId);
    public static String matchIdRegex(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        Matcher matcher = patternId.matcher(text);
        if (matcher.find()) {
            //System.out.println(regex);
            //System.out.println(matcher.group(0));
            try {
                return matcher.group(2);
            }catch (IndexOutOfBoundsException e){
                //e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public static void main(String[] args) {
        //民警电话联系报警人到所了解，谭正（男，身份证号
        //已指令进德派出所处置。兰容，（身份证号：
        String text = "我叫来娜，身份证号：211381198806115220，地址：兴柳路碧桂园22栋501号，前天有人电话联系我说自己是上海启赟数科有限责任公司的工作人员，让我添加QQ进入群聊做兼职可以得到佣金返现，今天我做兼职的时候，对方还一直派发任务，让我按照提示进行操作，刚开始还能得到佣金返现，到了第三次之对方就说我操作失误，无法提现，让我转钱过去才能得到佣金，接着我在兴柳路碧桂园22栋501号通过手机银行分七次共计转了差不多五万到对方的账户，但是一直提现失败，对方还让我继续转钱，我才意识到自己被骗了，现在对方还在与我联系，我无法看到完整的银行账号，账号太多了，我一下搞不清楚，我手机内下有银行APP，但是我不知道怎么操作。（通话中已告知报警人尽快到派出所报案）\n" +
                "20:32分（15224663260）报：我一共转了42800元（分别是7000元、3800元、1万元、1万元、2000元和1万元）到后面的账号。嫌疑人邓俊 江苏银行 6228760255000457987 嫌疑人彭洪涛 江苏银行 622152001006343747 嫌疑人乔安 6217921475378077 嫌疑人苏可 河南省农信联合社 623059103602706146 嫌疑人潘芷欣 中国邮政银行 6217994910227389758 嫌疑人张斌 中国银行 6217806100080650952玩游戏，今天晚上我老公就发现他支付宝账户里的3600元钱被转出去，问了我小孩她说是对方打电话然后对着手机操作，钱就被转走了。23:08分13307728963再报：现在我看不到账户，不能确定我到底是不是被骗了，明天我到银行打印流水后再到派出所报。了一个亚马逊的链接让我帮她付款，我从22时17分开始分四笔通过支付宝转账共24899元，现在发现被骗了。信用社。";
        String result = matchIdRegex(text);
        System.out.println(result);  // Output: sample
    }
}
