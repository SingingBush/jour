/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2004 Jour team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */
package net.sf.jour.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add docs
 * Created on 04.12.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */
public class TimeUtil {

    private static final Logger log = LoggerFactory.getLogger(TimeUtil.class);

    public static final boolean debug = true;

	public static int string2TimeSec(String str) {
		if (str.length() != 8) {
			return -1;
		}
		int h = Integer.parseInt(str.substring(0, 2));
		int m = Integer.parseInt(str.substring(3, 5));
		int s = Integer.parseInt(str.substring(6, 8));
		return s + m * 60 + h * 60 * 60;
	}

	public static double string2MTimeSec(String str) {
		if (str.length() != 12) {
			return string2TimeSec(str);
		}
		int h = Integer.parseInt(str.substring(0, 2));
		int m = Integer.parseInt(str.substring(3, 5));
		int s = Integer.parseInt(str.substring(6, 8));
		double ms = Integer.parseInt(str.substring(9, 12));
		return s + m * 60 + h * 60 * 60 + (ms / 1000.0);
	}

	public static String timeSec2string(long sec) {
		//hh:mm:ss
		long h = sec / (60 * 60);
		long m = (sec - h * 60 * 60) / 60;
		long s = (sec - h * 60 * 60 - m * 60);

		DecimalFormat nf = new DecimalFormat("00");

		return nf.format(h) + ":" + nf.format(m) + ":" + nf.format(s);
	}

	public static String formatDurationHHMM(double milliseconds) {
	    double hours = milliseconds / (1000D * 60D * 60D);
        int h = (int) Math.floor(hours);
        int m = (int) Math.ceil((hours - (double) h) * 60D);
        if (m == 60) {
            h++;
            m = 0;
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        return nf.format(h) + ":" + nf.format(m);
    }

	public static String formatDurationHHMMSS(double milliseconds) {
        return timeSec2string((long)(milliseconds / 1000D));
    }

	public static void trunc(Calendar calendar) {
	    dayStart(calendar);
	}

	public static void dayStart(Calendar calendar) {
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
	}

	public static void dayEnd(Calendar calendar) {
	    dayStart(calendar);
	    calendar.add(Calendar.DATE, 1);
	}

	public static double string2TimeStamp(String str) {
		if (str == null) {
			return 0;
		}
		String sTime = str.trim();
		double sec = string2MTimeSec(sTime);
		if (sec != -1) {
			Calendar calendar = new GregorianCalendar();
			trunc(calendar);
			int s = (int)Math.ceil(sec);
			calendar.add(Calendar.SECOND, s);
			return calendar.getTime().getTime() + (s - sec);
		}
		try {
            sec = Double.parseDouble(str);
            if (debug) {
                log.debug(str + "->" + timeStamp2dateString(sec));
            }
            return sec;
        } catch (NumberFormatException e) {
        }

		return detectTimeformat(str);
	}

    private static final String TIMESTAMP_FORMAT = "0000000000000.000";
    private static final DecimalFormat TIMESTAMP_FORMATER = new DecimalFormat(TIMESTAMP_FORMAT);

	public static String timeStamp2string(double timeStamp) {
		return TIMESTAMP_FORMATER.format(timeStamp);
	}

	private static final String TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static String timeStamp2dateString(double timeStamp) {
	    return new SimpleDateFormat(TIMESTAMP_DATE_FORMAT).format(timeStamp2calendar(timeStamp).getTime());
	}

	public static Calendar timeStamp2calendar(double timeStamp) {
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(new Date((long) timeStamp));
		return calendar;
	}

	public static double calendar2timeStamp(Calendar calendar) {
		return calendar.getTime().getTime();
	}


	private static double detectTimeformat(String str) {
		final String[] formats = {
		    "yyyy-MM-dd HH:mm:ss.SSS",
		    "yyyy-MM-dd HH:mm:ss",
		    "yyyy-MM-dd HH:mm",
			// We are North America afer all.
		    "MM-dd-yyyy HH:mm:ss.SSS",
		    "MM-dd-yyyy HH:mm:ss",
		    "MM-dd-yyyy HH:mm",
		    "MM/dd/yyyy HH:mm:ss.SSS",
		    "MM/dd/yyyy HH:mm:ss",
		    "MM/dd/yyyy HH:mm",
			"MM-dd-yyyy",
		    "yyyy-MM-dd",
			"MM/dd/yyyy",
		};

		boolean addOneSecond = true;
		if (str.endsWith("24:00:00")) {
			int idx = str.indexOf("24:00:00");
			str = str.substring(0, idx) + "23:59:59";
			addOneSecond = true;
		} else if (str.endsWith("24:00")) {
			int idx = str.indexOf("24:00");
			str = str.substring(0, idx) + "23:59:59";
			addOneSecond = true;
		}

        for (final String format : formats) {
            try {
                SimpleDateFormat aFormat = new SimpleDateFormat(format);
                Date dateObj = aFormat.parse(str);
                if (!str.equals(aFormat.format(dateObj))) {
                    continue;
                }
                if (debug) {
                    log.debug("date  :" + str);
                    log.debug("format:" + format + " " + aFormat.format(dateObj));
                }
                if (addOneSecond) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateObj);
                    calendar.add(Calendar.SECOND, 1);
                    return calendar.getTime().getTime();
                }
                return dateObj.getTime();
            } catch (Exception ignore) {
                // ignore
            }
        }
        log.warn("undetected date format [" + str + "]");
		return 0;
	}

    private static final String dateRE = "((\\d\\d-\\d\\d-\\d\\d\\d\\d)|(\\d\\d\\d\\d-\\d\\d-\\d\\d)|(\\d\\d/\\d\\d/\\d\\d\\d\\d))";
    private static final String intervalOneDayPattern = "^" + dateRE  + "$";

    //private static String tsRE = "\\d{12,}";
	//private static String tsintervalPattern = "^(" + tsRE + ")\\s*-\\s*(" + tsRE + ")$";

	private static final String secRE = "\\d\\d:\\d\\d:\\d\\d";
	private static final String intervalPattern = "^(" + secRE + ")\\s*-\\s*(" + secRE + ")$";

	private static final String msecRE = "\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d";
	private static final String intervalPattern1 = "^(" + msecRE + ")\\s*-\\s*(" + msecRE + ")$";

    public static double[] string2TimeInterval(String comp) {
        if (comp == null) {
            return null;
        }
        comp = comp.trim();
        String[] fullDay = RegExUtil.match(comp, intervalOneDayPattern);
        if (fullDay.length >= 1) {
            double day = string2TimeStamp(fullDay[0]);
            Calendar dc = timeStamp2calendar(day);
            double[] rc = new double[2];
            dayStart(dc);
            rc[0] = calendar2timeStamp(dc);
            dayEnd(dc);
            rc[1] = calendar2timeStamp(dc);
            return rc;
        }

        String[] result = RegExUtil.match(comp, intervalPattern);
        if (result.length < 2) {
            result = RegExUtil.match(comp, intervalPattern1);
        }

        if (result.length >= 2) {
            if (debug) {
                log.debug(comp + "->["  + result[0] + "]-[" + result[1] + "]" + result.length);
            }
            double[] rc = new double[2];
            rc[0] = string2TimeStamp(result[0]);
            rc[1] = string2TimeStamp(result[1]);
            return rc;
        } else {
            int idx = comp.indexOf(" - ");
            if (idx > 0) {
                double[] rc = new double[2];
                rc[0] = string2TimeStamp(comp.substring(0, idx));
                rc[1] = string2TimeStamp(comp.substring(idx + 3));
                return rc;
            }
            return null;
        }

    }
}
