package kr.co.bigsnow.core.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

 
public class DateUtil {
    /**
     * Constructor Summary. <br>
     * Constructor Description.
     * @since 1.00
     * @see
     */
    public DateUtil() { };

    /**
     * Throws : IOException <br>
     * Parameters : String StrDelimittoken : (ex) "/" , ".", "-" , String rtnFormmat <br>
     * Return Value : String <br>
     * 내용 : 오늘 날짜 값 가져오기 <br>
     */
    public static String getCurrentDate(String StrDelimittoken, String rtnFormmat) {
        String szReturn = null;

        try {
            Calendar currentWhat = Calendar.getInstance();

            int currentYear     = currentWhat.get(Calendar.YEAR);
            int currentMonth    = currentWhat.get(Calendar.MONTH) + 1;
            int currentDay      = currentWhat.get(Calendar.DAY_OF_MONTH);
            int currentHour     = currentWhat.get(Calendar.HOUR_OF_DAY);
            int currentMinute   = currentWhat.get(Calendar.MINUTE);
            int currentSecond   = currentWhat.get(Calendar.SECOND);

            String yearToday    = CommonUtil.padLeftwithZero(currentYear, 4);      // 4자리 스트링으로 변환
            String monthToday   = CommonUtil.padLeftwithZero(currentMonth, 2);     // 2자리 스트링으로 변환
            String dayToday     = CommonUtil.padLeftwithZero(currentDay, 2);       // 2자리 스트링으로 변환
            String hourToday    = CommonUtil.padLeftwithZero(currentHour, 2);      // 2자리 스트링으로 변환
            String minuteToday  = CommonUtil.padLeftwithZero(currentMinute, 2);    // 2자리 스트링으로 변환
            String secondToday  = CommonUtil.padLeftwithZero(currentSecond, 2);    // 2자리 스트링으로 변환

            if (rtnFormmat.equals("YYYY/MM/DD HH:MI:SS")) {
                szReturn = new String(yearToday + "/" + monthToday + "/" + dayToday
                                        + " " + hourToday + ":" + minuteToday + ":" + secondToday);
            }else if (rtnFormmat.equals("YYYY.MM.DD HH:MI:SS")) {
                szReturn = new String(yearToday + "." + monthToday + "." + dayToday
                        + " " + hourToday + ":" + minuteToday + ":" + secondToday);
            }else if (rtnFormmat.equals("YYYY-MM-DD HH:MI:SS")) {
                szReturn = new String(yearToday + "-" + monthToday + "-" + dayToday
                        + " " + hourToday + ":" + minuteToday + ":" + secondToday);
            } else if (rtnFormmat.equals("YYYY-MM-DD")) {
                szReturn = new String(yearToday + "-" + monthToday + "-" + dayToday);
            } else if (rtnFormmat.equals("YYYYMMDD-HHMISS")) {
                szReturn = new String(yearToday + monthToday + dayToday
                                        + "-" + hourToday + minuteToday + secondToday);
            } else if (rtnFormmat.equals("YYYYMMDDHHMISS")) {
                szReturn = new String(yearToday + monthToday + dayToday
                                        + hourToday + minuteToday + secondToday);
            } else if (rtnFormmat.equals("YYYYMMDD")) {
                szReturn = new String(yearToday + monthToday + dayToday);
            } else if (rtnFormmat.equals("YYYYMM")) {
                szReturn = new String(yearToday + monthToday);
            } else if (rtnFormmat.equals("HH:MI:SS")) {
                szReturn = new String(hourToday + ":" + minuteToday + ":" + secondToday);
            } else if (rtnFormmat.equals("HHMISS")) {
                szReturn = new String(hourToday + minuteToday + secondToday);
            } else if (rtnFormmat.equals("YYYY")) {
                szReturn = new String(yearToday);
            } else if (rtnFormmat.equals("MM")) {
                szReturn = new String(monthToday);
            } else if (rtnFormmat.equals("DD")) {
                szReturn = new String(dayToday);
            } else if (rtnFormmat.equals("HHMI")) {
                szReturn = new String(hourToday + minuteToday);
            } else if (rtnFormmat.equals("HH:MI")) {
                szReturn = new String(hourToday + ":" + minuteToday);
            } else if (rtnFormmat.equals("dafault")) {
                szReturn = new String(yearToday + StrDelimittoken + monthToday + StrDelimittoken + dayToday);
            } else {
                szReturn = new String(yearToday + StrDelimittoken + monthToday + StrDelimittoken + dayToday);
            }
        } catch (Exception e) {
            szReturn = "";
        }

        return szReturn;
    }

    public static String getCurrentDate() {
        return getCurrentDate("", "");
    }

    /**
     * Throws : IOException <br>
     * Parameters : String StrDelimittoken : (ex) "/" , ".", "-"
     * 			  , String rtnFormmat
     * 			  , dateItemFormat : 포맷할 데이트 형식 (ex) "year", "month" ..
     * 			  , dateItemFormatNumberValue : 포맷할 데이트 숫자값 (ex) 1, -1, 2 ..<br>
     * Return Value : String <br>
     * 내용 : 현재 날짜 기준으로 가공한 날짜값 가져오기 <br>
     */
    public static String getCurrentDateValueFormat(String StrDelimittoken, String rtnFormmat, String dateItemFormat, int dateItemFormatNumberValue) {
        String szReturn = null;

        try {
            Calendar currentWhat = Calendar.getInstance();

            //날짜 포맷 변경 start
            if("year".equals(CommonUtil.nvl(dateItemFormat, ""))){
                currentWhat.add(Calendar.YEAR , dateItemFormatNumberValue);
            }else if("month".equals(CommonUtil.nvl(dateItemFormat, ""))){
                currentWhat.add(Calendar.MONTH , dateItemFormatNumberValue);
            }else if("day".equals(CommonUtil.nvl(dateItemFormat, ""))){
                currentWhat.add(Calendar.DAY_OF_MONTH , dateItemFormatNumberValue);
            }else if("hour".equals(CommonUtil.nvl(dateItemFormat, ""))){
                currentWhat.add(Calendar.HOUR_OF_DAY , dateItemFormatNumberValue);
            }else if("minute".equals(CommonUtil.nvl(dateItemFormat, ""))){
                currentWhat.add(Calendar.MINUTE , dateItemFormatNumberValue);
            }else if("second".equals(CommonUtil.nvl(dateItemFormat, ""))){
                currentWhat.add(Calendar.SECOND , dateItemFormatNumberValue);
            }
            
            int currentYear     = currentWhat.get(currentWhat.YEAR);
            int currentMonth    = currentWhat.get(currentWhat.MONTH) + 1;
            int currentDay      = currentWhat.get(currentWhat.DAY_OF_MONTH);
            int currentHour     = currentWhat.get(currentWhat.HOUR);
            int currentMinute   = currentWhat.get(currentWhat.MINUTE);
            int currentSecond   = currentWhat.get(currentWhat.SECOND);
            //날짜 포맷 변경 End

            String yearToday    = CommonUtil.padLeftwithZero(currentYear, 4);      // 4자리 스트링으로 변환
            String monthToday   = CommonUtil.padLeftwithZero(currentMonth, 2);     // 2자리 스트링으로 변환
            String dayToday     = CommonUtil.padLeftwithZero(currentDay, 2);       // 2자리 스트링으로 변환
            String hourToday    = CommonUtil.padLeftwithZero(currentHour, 2);      // 2자리 스트링으로 변환
            String minuteToday  = CommonUtil.padLeftwithZero(currentMinute, 2);    // 2자리 스트링으로 변환
            String secondToday  = CommonUtil.padLeftwithZero(currentSecond, 2);    // 2자리 스트링으로 변환

            if (rtnFormmat.equals("YYYY/MM/DD HH:MI:SS")) {
                szReturn = new String(yearToday + "/" + monthToday + "/" + dayToday
                                        + " " + hourToday + ":" + minuteToday + ":" + secondToday);
            }else if (rtnFormmat.equals("YYYY.MM.DD HH:MI:SS")) {
                szReturn = new String(yearToday + "." + monthToday + "." + dayToday
                        + " " + hourToday + ":" + minuteToday + ":" + secondToday);
            } else if (rtnFormmat.equals("YYYY-MM-DD")) {
                szReturn = new String(yearToday + "-" + monthToday + "-" + dayToday);
            } else if (rtnFormmat.equals("YYYYMMDD-HHMISS")) {
                szReturn = new String(yearToday + monthToday + dayToday
                                        + "-" + hourToday + minuteToday + secondToday);
            } else if (rtnFormmat.equals("YYYYMMDDHHMISS")) {
                szReturn = new String(yearToday + monthToday + dayToday
                                        + hourToday + minuteToday + secondToday);
            } else if (rtnFormmat.equals("YYYYMMDD")) {
                szReturn = new String(yearToday + monthToday + dayToday);
            } else if (rtnFormmat.equals("HH:MI:SS")) {
                szReturn = new String(hourToday + ":" + minuteToday + ":" + secondToday);
            } else if (rtnFormmat.equals("HHMISS")) {
                szReturn = new String(hourToday + minuteToday + secondToday);
            } else if (rtnFormmat.equals("YYYY")) {
                szReturn = new String(yearToday);
            } else if (rtnFormmat.equals("MM")) {
                szReturn = new String(monthToday);
            } else if (rtnFormmat.equals("DD")) {
                szReturn = new String(dayToday);
            } else if (rtnFormmat.equals("HHMI")) {
                szReturn = new String(hourToday + minuteToday);
            } else if (rtnFormmat.equals("HH:MI")) {
                szReturn = new String(hourToday + ":" + minuteToday);
            } else if (rtnFormmat.equals("dafault")) {
                szReturn = new String(yearToday + StrDelimittoken + monthToday + StrDelimittoken + dayToday);
            } else {
                szReturn = new String(yearToday + StrDelimittoken + monthToday + StrDelimittoken + dayToday);
            }
        } catch (Exception e) {
            szReturn = "";
        }

        return szReturn;
    }

    /**
     * 현재 날짜와 비교. <br>
     * 값이 작으면 -1, 같으면 0, 크면 1을 반환한다.
     * @param objDate
     * @return -1, 0, 1
     * @since 1.00
     * @see
     */
    public static int getCurrentDateCompare(Object objDate) {
        if (objDate == null || "".equals(objDate.toString())) {
            return -2;
        }

        String strCompareDate   = CommonUtil.nvl(objDate);
        String strCurDate       = getCurrentDate();

        return strCompareDate.compareTo(strCurDate);
    }

    /**
     * Throws : IOException <br>
     * Parameters : 1) String startDate : 시작일자 YYYYMMDD <br>
     * 2) String endDate : 종료일자 YYYYMMDD Return Value : int <br>
     * 내용 : 날짜사이의 기간을 일수로 리턴한다 <br>
     */
    public static int getPeriodByDay(String startDate, String endDate) throws IOException {
        long endTimeStamp   = Long.parseLong(getTimeStamp(endDate));
        long startTimeStamp = Long.parseLong(getTimeStamp(startDate));
        long periodBySecond = endTimeStamp - startTimeStamp;
        double fPeriodByDay = (periodBySecond / (60 * 60 * 24));

        return (int) Math.ceil(fPeriodByDay);
    }

    /** 1970년 1월 1일 0시 0분 0초 부터 지정된 날짜까지의 초를 가져온다. */
    private static String getTimeStamp(String endDate) throws IOException {
        int endYear		= Integer.parseInt(endDate.substring(0, 4));
        int endMonth	= Integer.parseInt(endDate.substring(4, 6));
        int endDay		= Integer.parseInt(endDate.substring(6, 8));

        Calendar cal = Calendar.getInstance();
        //cal.set(endYear, endMonth, endDay, 0, 0, 0);
        cal.set(endYear, endMonth-1, endDay, 0, 0, 0);

        String strTimeStamp = "";

        long timeStamp  = 0L;
        long stdYear    = 1970L;

        long passedYear = (cal.get(Calendar.YEAR) - stdYear) * 365 * 24 * 60 * 60;
        long passedDay  = cal.get(Calendar.DAY_OF_YEAR) * 24 * 60 * 60;
        long passedTime = (cal.get(Calendar.HOUR) * 60 * 60) + (cal.get(Calendar.MINUTE) * 60)
                            + cal.get(Calendar.SECOND);

        timeStamp = passedYear + passedDay + passedTime;
        strTimeStamp = Long.toString(timeStamp);

        while (strTimeStamp.length() < 12) {
            strTimeStamp = "0" + strTimeStamp;
        }

        return strTimeStamp;
    }

    /**
     * Parameters : srcDate - 기준일자 <br>
     * cnt - 카운터 <br>
     * Return Value : String <br>
     * 내용 : 기준일에 해달 카운터 만큼의 월수를 증가 시킨다. <br>
     */
    public static String addMonth(String srcDate, int cnt) {
        String rtnData = null;

        try {
            String year = srcDate.substring(0, 4);
            String month = srcDate.substring(4, 6);
            String day = srcDate.substring(6, 8);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            formatter.getCalendar().set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day),
                    0, 0, 0);
            formatter.getCalendar().add(Calendar.MONTH, cnt);

            Date chkDay = formatter.getCalendar().getTime();

            rtnData = (String) formatter.format(chkDay);

            year = rtnData.substring(0, 4);
            month = rtnData.substring(5, 7);
            day = rtnData.substring(8, 10);

            rtnData = year + month + day;
        } catch (Exception e) {
            e.printStackTrace();
            rtnData = "";
        }

        return rtnData;
    }

    /**
     * Parameters : srcDate - 기준일자 <br>
     * cnt - 카운터 <br>
     * Return Value : String <br>
     * 내용 : 기준일에 해달 카운터 만큼의 일자를 증가시킨다. <br>
     */
    public static String addDay(String srcDate, int cnt) {
        String rtnData = null;

        try {
            String year = srcDate.substring(0, 4);
            String month = srcDate.substring(4, 6);
            String day = srcDate.substring(6, 8);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            formatter.getCalendar().set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day),
                    0, 0, 0);
            formatter.getCalendar().add(Calendar.DATE, cnt);

            Date chkDay = formatter.getCalendar().getTime();

            rtnData = (String) formatter.format(chkDay);

            year = rtnData.substring(0, 4);
            month = rtnData.substring(5, 7);
            day = rtnData.substring(8, 10);

            rtnData = year + month + day;
        } catch (Exception e) {
            e.printStackTrace();
            rtnData = "";
        }

        return rtnData;
    }

    /**
     * Parameters : String day, String delim <br>
     * Return Value : String <br>
     * 내용 : Date Format 변경. <br>
     */
    public static String getDateFormat(Object objDay) {
        return getDateFormat(objDay, "/");
    }

    /**
     * Parameters : String day, String delim <br>
     * Return Value : String <br>
     * 내용 : Date Format 변경. <br>
     */
    public static String getDateFormat(Object objDay, String delim) {
        String tmp = "";

        if (objDay == null)
            return tmp;
        String day = objDay.toString();

        day = day.replace("/", "");
        day = day.replace("-", "");
        day = day.replace(".", "");
        
        if (day == null || day.equals("") || delim == null) {
            tmp = "";
        } else if (day.length() == 6) {
            tmp = day.substring(0, 4) + delim + day.substring(4, 6);
        } else if (day.length() < 8) {
            tmp = day;
        } else if (delim.equals("MD")) {
            tmp = day.substring(4, 6) + "월 " + day.substring(6) + "일";
        }  else if (delim.equals("YMD")) {
            tmp = day.substring(0, 4) + "년 " + day.substring(4, 6) + "월 " + day.substring(6) + "일";
        } else {
            tmp = day.substring(0, 4) + delim + day.substring(4, 6) + delim + day.substring(6);
        }

        return tmp;
    }

    /**
     * Parameters : String day, String delim <br>
     * Return Value : String <br>
     * 내용 : Date Format 변경. <br>
     */
    public static String getDateTimeFormat(Object objDay) {
 	   String day = objDay.toString();

 	   day = day.replaceAll("-", "");
 	   day = day.replaceAll("//.", "");

 	   if (day.length() < 12) {
 	   	  return objDay.toString();
 	   }

        return getDateFormat(day, "-");
    }

    /**
     * 요일을 얻음. <br>
     * @param strDay
     * @return 요일
     * @since 1.00
     * @see
     */
    public static String getWeekdayName(String strDay) {
        if (strDay == null || "".equals(strDay)) {
            return "";
        }

        int year    = Integer.parseInt(strDay.substring(0, 4));
        int month   = Integer.parseInt(strDay.substring(4, 6));
        int day     = Integer.parseInt(strDay.substring(6, 8));

        if (month == 1 || month == 2) {
            year--;
        }

        month = (month + 9) % 12 + 1;

        int yRest   = year % 100;
        int century = year / 100;
        int week    = ((13 * month - 1) / 5 + day + yRest + yRest / 4 + century / 4 - 2 * century) % 7;

        if (week < 0) {
            week = (week + 7) % 7;
        }

        String strWeek = "";

        switch (week) {
        case 0:
            strWeek = "일";
            break;
        case 1:
            strWeek = "월";
            break;
        case 2:
            strWeek = "화";
            break;
        case 3:
            strWeek = "수";
            break;
        case 4:
            strWeek = "목";
            break;
        case 5:
            strWeek = "금";
            break;
        case 6:
            strWeek = "토";
        }

        return strWeek;
    }

    /**
     * 요일 번호를 얻음.(0:일요일) <br>
     * @param strDay
     * @return 요일
     * @since 1.00
     * @see
     */
    public static int getWeekdayNum(String strDay) {
        if (strDay == null || "".equals(strDay)) {
            return 0;
        }

        int year    = Integer.parseInt(strDay.substring(0, 4));
        int month   = Integer.parseInt(strDay.substring(4, 6));
        int day     = Integer.parseInt(strDay.substring(6, 8));

        int restYear    = 0;
        int century     = 0;
        int week        = 0;

        if (month == 1 || month == 2) {
            year--;
        }

        month = (month + 9) % 12 + 1;

        restYear    = year % 100;
        century     = year / 100;
        week        = ((13 * month - 1) / 5 + day + restYear + restYear / 4 + century / 4 - 2 * century) % 7;

        if (week < 0) {
            week = (week + 7) % 7;
        }

        return week;
    }

    /**
     * 1 ~ 4분기 구하기
     * @param strDay
     * @return description
     * @since 1.00
     * @see
     */
    public static String getSettle(String strDay) {
        String  strRetVal   = "";
        int     iMonth      = 0;

        if ("".equals(strDay)) {
            strDay = getCurrentDate();
        } else {
            iMonth = Integer.parseInt(strDay.substring(4, 6));

            if (iMonth <= 3) {
                strRetVal = "1";
            } else if (iMonth <= 6) {
                strRetVal = "2";
            } else if (iMonth <= 9) {
                strRetVal = "3";
            } else if (iMonth <= 12) {
                strRetVal = "4";
            }
        }

        return strRetVal;
    }

    /**
     * 전달 받은 Date 객체에 전달 받은 날짜 패턴 형태로 표현된 날짜 문자열을 반환한다.
     * @param pm_oDate formatting 대상이 되는 Date 객체
     * @param pm_sDatePattern formatting에 사용할 날짜 패턴
     * @return formatting 날짜 문자열
     */
    public static String getDateString(Date pm_oDate, String pm_sDatePattern) {
        SimpleDateFormat lm_oFormat = new SimpleDateFormat(pm_sDatePattern);
        return lm_oFormat.format(pm_oDate);
    }

    public static String getTimeFormat(Object objTime, String strFormat) {
        String strTime      = "";
        String strRetTime   = "";
        String strAmPm      = "";

        int iHour = 0;

        strFormat = strFormat.toUpperCase();

        try {
            if (objTime == null || "".equals(objTime.toString())) {
                strRetTime = "";
            } else {
                strTime = objTime.toString();

                if ("".equals(strFormat)) {
                    strRetTime = strTime.substring(0, 2) + ":" + strTime.substring(2, 4);
                } else if ("HH24:MM".equals(strFormat)) {
                    strRetTime = strTime.substring(0, 2) + ":" + strTime.substring(2, 4);
                } else if ("PM HH:MM".equals(strFormat)) {
                    iHour       = Integer.parseInt(strTime.substring(0, 2));
                    strAmPm     = (iHour <= 12) ? "오전 " : "오후";
                    strRetTime  = Integer.toString((iHour > 12) ? iHour - 12 : iHour);

                    if (strRetTime.length() == 1) {
                        strRetTime = "0" + strRetTime;
                    }

                    strRetTime = strAmPm + " " + strRetTime + ":" + strTime.substring(2, 4);
                }
            }
        } catch (Exception e) {
            //System.out.println("Error CommonUtil getTimeFormat() " + e.toString());
        }

        return strRetTime;
    }

    public static String[] getArrayTime(String strTm) {
        String[] retTm = { "", "", "" };

        if (strTm.length() == 4) {
            String strHour = strTm.substring(0, 2);

            int iHour = Integer.parseInt(strHour);

            if (iHour > 12) {
                retTm[0] = "12";
                iHour -= 12;
            } else if (iHour == 0) {
                retTm[0] = "12";
                iHour = 12;
            } else {
                retTm[0] = "0";
            }

            retTm[1] = CommonUtil.getRight("0" + iHour, 2);
            retTm[2] = strTm.substring(2);
        }

        return retTm;
    }
    
    @SuppressWarnings("static-access")
    public static String getNow() {
        String now      = "";
        String nows[]   = new String[3];
        int date[]      = new int[3];

        Calendar cal    = Calendar.getInstance();

        date[0] = cal.get(cal.MONTH) + 1;
        date[1] = cal.get(cal.DATE);
        date[2] = cal.get(cal.HOUR_OF_DAY);

        for (int i = 0; i < date.length; i++) {
            if (date[i] < 10) {
                nows[i] = "0" + new Integer(date[i]).toString();
            } else {
                nows[i] = new Integer(date[i]).toString();
            }
            now = now + nows[i];
        }

        return String.valueOf(cal.get(cal.YEAR)) + now;
    }

    public static int getLastDay(String days) {
        int ls      = 0;
        int year    = Integer.parseInt(days.substring(0, 4));
        int mon     = Integer.parseInt(days.substring(4, 6));

        switch (mon) {
        case 2:
                ls = getYoonMonth(year);
                break;
        case 4:
        case 6:
        case 9:
        case 11:
                ls = 30;
                break;
        default:
                ls = 31;
        }

        return ls;
    }

	/**
     * 윤달여부 Check. <br>
     * 해당년도가 윤달이 끼어있는 년도인지 판단하여 값을 반환한다.
     * @param name description
     * @return description
     * @throws name description
     * @since 1.00
     * @see
     */
    public static int getYoonMonth(int year) {
        int yn      = 0;
        int fyear   = (int) (year / 100);
        int byear   = year % 100;

        if (fyear % 4 == 0 && year % 4 == 0) {
            yn = 29;
        } else {
            if (byear != 0 && byear % 4 == 0) {
                yn = 29;
            } else {
                yn = 28;
            }
        }
        return yn;
    }

    /**<pre>
     * 년월일 사이에 구분자 sep를 첨가한다. 구분자가 "/"인 경우 "yyyymmdd" -> "yyyy/mm/dd"가 된다.
     *
     * @return java.lang.String
     * @param str 날짜(yyyymmdd)
     */
    public static String date(String str, String sep) {
      String    szTemp  = null;
      int       nLen    = 0;

      if (str == null) {
          szTemp = "";
      } else {
          nLen = str.length();

          if (nLen != 8) {
              szTemp = str;
          } else {
              if ( (str.equals("00000000")) || (str.equals("       0")) || (str.equals("        "))) {
                  szTemp = "";
              } else {
                  szTemp = str.substring(0, 4) + sep + str.substring(4, 6) + sep + str.substring(6, 8);
              }
          }
      }

      return szTemp;
    }

    /**
    * 입력받은 날짜에 월 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR><BR>
    * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
    * 분단위 더하기이다.
    *
    * 간단한 사용예는 다음과 같다.
    *
    * 사용예) LLog.debug.println( getFormattedDateAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6) );
    * 결과) 2001/09/23 15:54
    *
    * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
    *
    * @return java.lang.String
    * @param pIndate String
    * @param pInformat String
    * @param pOutformat String
    * @param pDay int
    * @param pHour int
    * @param pMin int
    */
    public static String getFormattedDateYearAdd(String pIndate, String pInformat, String pOutformat, int pYear) {


    	SimpleDateFormat pInformatter =  new SimpleDateFormat (pInformat, java.util.Locale.KOREA);
    	SimpleDateFormat pOutformatter =  new SimpleDateFormat (pOutformat, java.util.Locale.KOREA);
    	Calendar cal = Calendar.getInstance(Locale.getDefault()) ;
    	String rDateString = "";
    	Date vIndate = null;


    	try {
    		vIndate = pInformatter.parse(pIndate);
    		cal.setTime(vIndate) ;
    		cal.add(Calendar.YEAR, pYear) ;
    		rDateString = pOutformatter.format(cal.getTime());


    	} catch( Exception e ) {
    		rDateString = pIndate;
    	}


    	return rDateString;
    }

    /**
    * 입력받은 날짜에 월 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR><BR>
    * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
    * 분단위 더하기이다.
    *
    * 간단한 사용예는 다음과 같다.
    *
    * 사용예) LLog.debug.println( getFormattedDateAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6) );
    * 결과) 2001/09/23 15:54
    *
    * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
    *
    * @return java.lang.String
    * @param pIndate String
    * @param pInformat String
    * @param pOutformat String
    * @param pDay int
    * @param pHour int
    * @param pMin int
    */
    public static String getFormattedDateMonthAdd(String pIndate, String pInformat, String pOutformat, int pMonth) {


    	SimpleDateFormat pInformatter =  new SimpleDateFormat (pInformat, java.util.Locale.KOREA);
    	SimpleDateFormat pOutformatter =  new SimpleDateFormat (pOutformat, java.util.Locale.KOREA);
    	Calendar cal = Calendar.getInstance(Locale.getDefault()) ;
    	String rDateString = "";
    	Date vIndate = null;


    	try {
    		vIndate = pInformatter.parse(pIndate);
    		cal.setTime(vIndate) ;
    		cal.add(Calendar.MONTH, pMonth) ;
    		rDateString = pOutformatter.format(cal.getTime());


    	} catch( Exception e ) {
    		rDateString = pIndate;
    	}


    	return rDateString;
    }

    /**
    * 입력받은 날짜에 일 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR><BR>
    * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
    * 분단위 더하기이다.
    *
    * 간단한 사용예는 다음과 같다.
    *
    * 사용예) LLog.debug.println( getFormattedDateAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6) );
    * 결과) 2001/09/23 15:54
    *
    * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
    *
    * @return java.lang.String
    * @param pIndate String
    * @param pInformat String
    * @param pOutformat String
    * @param pDay int
    * @param pHour int
    * @param pMin int
    */
    public static String getFormattedDateDayAdd(String pIndate, String pInformat, String pOutformat, int pDay) {


    	SimpleDateFormat pInformatter =  new SimpleDateFormat (pInformat, java.util.Locale.KOREA);
    	SimpleDateFormat pOutformatter =  new SimpleDateFormat (pOutformat, java.util.Locale.KOREA);
    	Calendar cal = Calendar.getInstance(Locale.getDefault()) ;
    	String rDateString = "";
    	Date vIndate = null;


    	try {
    		vIndate = pInformatter.parse(pIndate);
    		cal.setTime(vIndate) ;
    		cal.add(Calendar.DAY_OF_YEAR, pDay) ;
    		rDateString = pOutformatter.format(cal.getTime());


    	} catch( Exception e ) {
    		rDateString = pIndate;
    	}


    	return rDateString;
    }

    public static float getDateDiffDay(String date) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
        float returnDay = 0;

        try {
        	Date d1 = myFormat.parse(date.replaceAll("-", ""));
        	Date d2 = new Date();

            long diff = d1.getTime() - d2.getTime();

            returnDay = (diff / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnDay;
    }

    public  static boolean  isDate(String  strDate){

        try{
        	
        	 if ( strDate == null || "".equals(strDate) ) {
        		 return true;
        	 }
        	 
        	 if ( strDate.length() < 8 ) {
        		 return false;
        	 }
        	
        	 strDate = strDate.substring(0, 8);
        	 
             SimpleDateFormat  dateFormat = new  SimpleDateFormat("yyyyMMdd");

             dateFormat.setLenient(false);
             dateFormat.parse(strDate);
             return  true;

          }catch (ParseException  e){
            return  false;
          }

     }    
    
}