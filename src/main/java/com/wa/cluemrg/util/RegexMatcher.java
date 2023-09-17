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
        String text = "我叫罗秀萍，身份证号452727197712081466，地址柳邕路新翔小区五区门面，我今天中午的时候接到一个电话，对方自称保对方就一步步让小孩和我们拿手机装作玩游戏，今天晚上我老公就发现他支付宝账户里的3600元钱被转出去，问了我小孩她说是对方打电话然后对着手机操作，钱就被转走了。23:08分13307728963再报：现在我看不到账户，不能确定我到底是不是被骗了，明天我到银行打印流水后再到派出所报。了一个亚马逊的链接让我帮她付款，我从22时17分开始分四笔通过支付宝转账共24899元，现在发现被骗了。信用社。";
        String result = matchIdRegex(text);
        System.out.println(result);  // Output: sample
    }
}
