package com.gocam.goscamdemopro.utils;


import java.util.regex.Pattern;

/**
 * Created by zhaolong on 2017/6/2.
 */

public final class RegexUtils {

    /**
     * 正则：手机号（简单）
     */
    public static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    /**
     * 正则：手机号（精确）
     * <p>移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188</p>
     * <p>联通：130、131、132、145、155、156、175、176、185、186</p>
     * <p>电信：133、153、173、177、180、181、189</p>
     * <p>全球星：1349</p>
     * <p>虚拟运营商：170</p>
     */
    //public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
    //前三位不受限制
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(1[0-9][0-9])|(147))\\d{8}$";

    /**
     * 匹配图象
     * <p/>
     * <p/>
     * 格式: /相对路径/文件名.后缀 (后缀为gif,dmp,png)
     * <p/>
     * 匹配 : /forum/head_icon/admini2005111_ff.gif 或 admini2005111.dmp
     * <p/>
     * <p/>
     * 不匹配: c:/admins4512.gif
     */
    public static final String REGEX_ICON = "^(/{0,1}//w){1,}//.(gif|dmp|png|jpg)$|^//w{1,}//.(gif|dmp|png|jpg)$";

    /**
     * 匹配email地址
     * <p/>
     * <p/>
     * 格式: XXX@XXX.XXX.XX
     * <p/>
     * 匹配 : foo@bar.com 或 foobar@foobar.com.au
     * <p/>
     * 不匹配: foo@bar 或 $$$@bar.com
     */
//    public static final String REGEX_EMAIL = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
//      public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REGEX_EMAIL = "^[[a-z0-9A-Z]+[-._]?]+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
//    public static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * 匹配并提取url
     * <p/>
     * <p/>
     * 格式: XXXX://XXX.XXX.XXX.XX/XXX.XXX?XXX=XXX
     * <p/>
     * 匹配 : http://www.suncer.com 或news://www
     * <p/>
     * 不匹配: c:/window
     */
    public static final String REGEX_URL = "(//w+)://([^/:]+)(://d*)?([^#//s]*)";

    /**
     * 匹配并提取http
     * <p/>
     * 格式: http://XXX.XXX.XXX.XX/XXX.XXX?XXX=XXX 或 ftp://XXX.XXX.XXX 或
     * https://XXX
     * <p/>
     * 匹配 : http://www.suncer.com:8080/index.html?login=true
     * <p/>
     * 不匹配: news://www
     */
    public static final String REGEX_HTTP = "(http|https|ftp)://([^/:]+)(://d*)?([^#//s]*)";

    /**
     * 匹配日期
     * <p/>
     * <p/>
     * 格式(首位不为0): XXXX-XX-XX或 XXXX-X-X
     * <p/>
     * <p/>
     * 范围:1900--2099
     * <p/>
     * <p/>
     * 匹配 : 2005-04-04
     * <p/>
     * <p/>
     * 不匹配: 01-01-01
     */
    public static final String REGEX_DATE_BARS = "^((((19){1}|(20){1})\\d{2})|\\d{2})-[0,1]?\\d{1}-[0-3]?\\d{1}$";

    /**
     * 匹配日期
     * <p/>
     * <p/>
     * 格式: XXXX/XX/XX
     * <p/>
     * <p/>
     * 范围:
     * <p/>
     * <p/>
     * 匹配 : 2005/04/04
     * <p/>
     * <p/>
     * 不匹配: 01/01/01
     */
    public static final String REGEX_DATE_SLASH = "^[0-9]{4}/(((0[13578]|(10|12))/(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)/(0[1-9]|[1-2][0-9]|30)))$";


    /**
     * 匹配姓名,只能是中文或英文，不能为数字或其他字符，汉字和字母不能同时出现
     */
    public static final String REGEX_NAME = "(([\u4E00-\u9FA5]{2,7})|([a-zA-Z]{3,10}))";
    /**
     * 匹配身份证
     * <p/>
     * 格式为: XXXXXXXXXX(10位) 或 XXXXXXXXXXXXX(13位) 或 XXXXXXXXXXXXXXX(15位) 或
     * XXXXXXXXXXXXXXXXXX(18位)
     * <p/>
     * 匹配 : 0123456789123
     * <p/>
     * 不匹配: 0123456
     */
    public static final String REGEX_ID_CARD = "^//d{10}|//d{13}|//d{15}|//d{18}$";

    /**
     * 匹配邮编代码
     * <p/>
     * 格式为: XXXXXX(6位)
     * <p/>
     * 匹配 : 012345
     * <p/>
     * 不匹配: 0123456
     */
    public static final String REGEX_ZIP = "^[0-9]{6}$";// 匹配邮编代码

    /**
     * 不包括特殊字符的匹配 (字符串中不包括符号 数学次方号^ 单引号' 双引号" 分号; 逗号, 帽号: 数学减号- 右尖括号> 左尖括号< 反斜杠/
     * 即空格,制表符,回车符等 )
     * <p/>
     * 格式为: x 或 一个一上的字符
     * <p/>
     * 匹配 : 012345
     * <p/>
     * 不匹配: 0123456 // ;,:-<>//s].+$";//
     */
    public static final String REGEX_NON_SPECIAL_CHAR = "^[^'/";
    // 匹配邮编代码

    /**
     * 匹配非负整数（正整数 + 0)
     */
    public static final String REGEX_NON_NEGATIVE_INTEGERS = "^//d+$";

    /**
     * 匹配不包括零的非负整数（正整数 > 0)
     */
    public static final String REGEX_NON_ZERO_NEGATIVE_INTEGERS = "^[1-9]+//d*$";

    /**
     * 匹配正整数
     */
    public static final String REGEX_POSITIVE_INTEGER = "^[0-9]*[1-9][0-9]*$";

    /**
     * 匹配非正整数（负整数 + 0）
     */
    public static final String REGEX_NON_POSITIVE_INTEGERS = "^((-//d+)|(0+))$";

    /**
     * 匹配负整数
     */
    public static final String REGEX_NEGATIVE_INTEGERS = "^-[0-9]*[1-9][0-9]*$";

    /**
     * 匹配整数
     */
    public static final String REGEX_INTEGER = "^-?//d+$";

    /**
     * 匹配非负浮点数（正浮点数 + 0）
     */
    public static final String REGEX_NON_NEGATIVE_RATIONAL_NUMBERS = "^//d+(//.//d+)?$";

    /**
     * 匹配正浮点数
     */
    public static final String POSITIVE_RATIONAL_NUMBERS = "^(([0-9]+//.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*//.[0-9]+)|([0-9]*[1-9][0-9]*))$";

    /**
     * 匹配非正浮点数（负浮点数 + 0）
     */
    public static final String REGEX_NON_POSITIVE_RATIONAL_NUMBERS = "^((-//d+(//.//d+)?)|(0+(//.0+)?))$";

    /**
     * 匹配负浮点数
     */
    public static final String REGEX_NEGATIVE_RATIONAL_NUMBERS = "^(-(([0-9]+//.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*//.[0-9]+)|([0-9]*[1-9][0-9]*)))$";

    /**
     * 匹配浮点数
     */
    public static final String REGEX_RATIONAL_NUMBERS = "^(-?//d+)(//.//d+)?$";

    /**
     * 匹配由26个英文字母组成的字符串
     */
    public static final String REGEX_LETTER = "^[A-Za-z]+$";

    /**
     * 匹配由26个英文字母的大写组成的字符串
     */
    public static final String REGEX_UPWARD_LETTER = "^[A-Z]+$";

    /**
     * 匹配由26个英文字母的小写组成的字符串
     */
    public static final String REGEX_LOWER_LETTER = "^[a-z]+$";

    /**
     * 匹配由数字和26个英文字母组成的字符串
     */
    public static final String REGEX_LETTER_NUMBER = "^[A-Za-z0-9]+$";

    /**
     * 匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public static final String REGEX_LETTER_NUMBER_UNDERLINE = "^//w+$";

    /**
     * 正则表达式:单一字符(不包含特殊字符)
     */
    public static final String REGEX_SINGLE_TYPE_CHARACTER = "^([a-z]+|[A-Z]+|[0-9]+)$";

    /**
     * 匹配8个到16个由数字和26个英文字母组成的字符串
     */
    public static final String REGEX_8_16_LETTER_NUMBER = "^[A-Za-z0-9]{8,16}$";

    /**
     * 正则表达式:验证手机号
     */
    public static final String REGEX_MOBILE = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";


    /**
     * /**
     * 判断是否匹配正则
     *
     * @param REGEX 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(String REGEX, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(REGEX, input);
    }

    public static String hideUserName(String user){
        return isMatch(REGEX_MOBILE_EXACT,user) ?
                user.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") :
                isMatch(REGEX_EMAIL,user) ? user.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4"):user;
    }


}

