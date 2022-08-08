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
package tests.jour.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import net.sf.jour.filter.TimeListFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.jour.util.TimeUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO Add docs
 *
 * Created on 08.12.2004
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$)  $Date$
 */
public class TimeFilterTest  {

    private static final Logger log = LoggerFactory.getLogger(TimeFilterTest.class);

    public static String date2text(Calendar c) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(c.getTime());
    }

    public static String today() {
        Calendar now = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.format(now.getTime());
    }

    public static Date txt2date(String textDate) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            return dateFormat.parse(textDate);
        } catch (ParseException e) {
            log.error("Wrong date" + textDate);
            throw e;
        }
    }

	private void verify(String pattern, String text, boolean expect) throws Exception {
		log.debug("verify [" + pattern + "] [" + text + "]");
		verify(pattern, txt2date(text).getTime(), expect);
	}

	private void verify(String pattern, double time, boolean expect) throws Exception {
		TimeListFilter f = new TimeListFilter();
		f.addPatterns(pattern);
		if (TimeUtil.debug && log.isDebugEnabled()) {
			log.debug("for " + (long)time);
			f.debug();
		}
		assertEquals(expect, f.match(time), pattern + " for {" + TimeUtil.timeStamp2dateString(time) + "}");
	}

    @Test
	public void testCurentTime() throws Exception {
	    Calendar now = new GregorianCalendar();
	    double t1 = System.currentTimeMillis();
	    now.add(Calendar.MINUTE, -2);
	    StringBuffer ptr = new StringBuffer();
	    ptr.append(date2text(now));
	    ptr.append(" - ");
	    now.add(Calendar.MINUTE, 2);
	    ptr.append(date2text(now));
	    log.debug("NativeTime:" + TimeUtil.timeStamp2dateString(t1));
	    verify(ptr.toString(), t1, true);
	    now.add(Calendar.MINUTE, -1);

	    now = new GregorianCalendar();
	    t1 = System.currentTimeMillis();
	    now.add(Calendar.SECOND, -2);

	    ptr = new StringBuffer();
	    ptr.append(date2text(now));
	    ptr.append(" - ");
	    now.add(Calendar.SECOND, +4);
	    ptr.append(date2text(now));
	    log.debug("NativeTime:" + TimeUtil.timeStamp2dateString(t1));
	    verify(ptr.toString(), t1, true);
	}

    @Test
	public void testTimePatterns() throws Exception {
	    verify("00:00:00.000 - 24:00:00.000", System.currentTimeMillis(), true);
	    verify("00:00:00 - 24:00:00", System.currentTimeMillis(), true);
	    verify("00:00:00-01:00:00;01:00:00-24:00:00", System.currentTimeMillis(), true);
	    verify("00:00:00-23:00:00;23:00:00-24:00:00", System.currentTimeMillis(), true);

	    String today = today();
	    verify("00:01:00 - 00:01:50", today + " 00:01:01", true);
	    verify("00:01:00 - 00:01:50", today + " 00:01:51", false);

	    verify("00:00:00.000 - 00:00:01.009", today + " 00:00:01", true);

	    // One day
	    //"MM-dd-yyyy",
	    verify("12-08-2004", "2004-12-08 00:00:01", true);
	    //"yyyy-MM-dd",
	    verify("2004-12-08", "2004-12-08 23:59:59", true);
		//"MM/dd/yyyy",
	    verify("12/08/2004", "2004-12-08 05:00:01", true);

	    verify("12-31-2004", "2004-12-08 00:00:01", false);

	    verify("12-08-2004 00:00:00 - 12-08-2004 23:59:59", "2004-12-08 05:00:01", true);
	}
}
