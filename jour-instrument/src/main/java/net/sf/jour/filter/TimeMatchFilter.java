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
package net.sf.jour.filter;

import net.sf.jour.util.TimeUtil;

import org.apache.log4j.Logger;

public class TimeMatchFilter extends MatchFilter {

	protected static final Logger log = Logger.getLogger(TimeMatchFilter.class);

	double timeFrom;

	double timeTo;

	boolean enabled = true;

	TimeMatchFilter(String comp) {
		double time[] = TimeUtil.string2TimeInterval(comp);
		if (time != null) {
			this.timeFrom = time[0];
			this.timeTo = time[1];
		}
	}

	TimeMatchFilter(String sFrom, String sTo, String comp) {
		this.timeFrom = TimeUtil.string2TimeStamp(sFrom);
		this.timeTo = TimeUtil.string2TimeStamp(sTo);
		if (!isValid()) {
			double time[] = TimeUtil.string2TimeInterval(comp);
			if (time != null) {
				this.timeFrom = time[0];
				this.timeTo = time[1];
			}
		}
	}

	public boolean isValid() {
		return (this.timeFrom > 0) && (this.timeTo > 0) && (this.timeFrom <= this.timeTo);
	}

	public void debug() {
		log.debug("time:" + TimeUtil.timeStamp2string(timeFrom) + " - " + TimeUtil.timeStamp2string(timeTo) + " "
				+ enabled);
		log.debug("time:" + TimeUtil.timeStamp2dateString(timeFrom) + " - " + TimeUtil.timeStamp2dateString(timeTo)
				+ " " + enabled);
	}

	public int matchState(Object obj) {
		if (!this.enabled) {
			return MATCH_DONT_KNOW;
		}
		// Single thread optimization to avoid new object creation.
		// double eventTime = ((Double) obj).doubleValue();
		double eventTime = ((TimeListFilter) obj).doubleValue();
		if ((eventTime >= timeFrom) && (eventTime < timeTo)) {
			return MATCH_YES;
		} else {
			return MATCH_NO;
		}
	}

	/**
     * @return Returns the enabled.
     */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
     * @param enabled
     *            The enabled to set.
     */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}