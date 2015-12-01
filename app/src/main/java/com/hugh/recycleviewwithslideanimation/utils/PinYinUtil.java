package com.hugh.recycleviewwithslideanimation.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	/**
	 * 获取汉字对应的拼音, 其内部实现是查找和匹配xml，那么会消耗一定内存，所以这个方法不能被频繁调用
	 * @param chinese
	 * @return
	 */
	public static String getPinYin(String chinese){
		if(TextUtils.isEmpty(chinese))return null;
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();//用来设置转化的拼音的格式
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//设置转化的拼音是大写字母
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//设置转化的拼音不需要声调
		
		//由于只能对单个汉字转化，那么需要将字符串转为字符数组,对每个字符进行转化
		char[] charArr = chinese.toCharArray();
		String pinyin = "";
		//a传 c#*，。智  
		for (int i = 0; i < charArr.length; i++) {
			//忽略掉空格
			if(Character.isWhitespace(charArr[i]))continue;
			
			//判断当前的字符是否是汉字，汉字占2个字节，所以汉字肯定大于128(一个字节)
			if(charArr[i]>128){
				//说明有可能是汉字
				try {
					//由于多音字的存在，返回的数组, 单 :dan shan
					String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(charArr[i],format);
					if(pinyinArr!=null){
						pinyin += pinyinArr[0];
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					//说明不是汉字，比如O(∩_∩)O~我是xxx
				}
			}else {
				//说明当前是字符,可以直接拼接
				pinyin += charArr[i];
			}
		}
		return pinyin;
	}
}
