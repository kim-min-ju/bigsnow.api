package kr.co.bigsnow.core.util;
import java.util.Random;


/********************************************************************************
 * Class Name : RandomUtil
 *
 * @author KKJ (osc.com)
 * @version 1.0.1 May 10 2019
 * @since version 1.0.1
 *********************************************************************************/
public class RandomUtil {


    /********************************************************************************
     * Random 발생
     *
     * 처리 내용 : 사용자가 필요한 길이 만큼 영문 대/소문자,숫자를 조합 하여 Random값 생성
     * @param rndlenth:	Random 생성 길이
     * @return String 발생된 Random값
     * @author PIK (osc.com)
     * @since version 1.0.1
     *********************************************************************************/
    public static String getRandom(int rndlenth) {
        StringBuilder resultRandom = new StringBuilder();
        Random rnd = new Random(System.nanoTime());

        for (int rndstart = 0; rndstart < rndlenth; rndstart++) {
            int rIndex = rnd.nextInt(3);

            switch (rIndex) {
                case 0:
                    // a-z
                    resultRandom.append((char) (rnd.nextInt(26) + 97));
                    break;
                case 1:
                    // A-Z
                    resultRandom.append((char) (rnd.nextInt(26) + 65));
                    break;
                case 2:
                    // 0-9
                    resultRandom.append((rnd.nextInt(10)));
                    break;
            }
        }

        return resultRandom.toString();
    }

}
