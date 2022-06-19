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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * TODO Add docs Created on 04.12.2004
 *
 * Contributing Author(s):
 *
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date: 2006-11-19 16:52:09 -0500
 *          (Sun, 19 Nov 2006) $
 */
public class MatchListFilter extends BasicFilter {

	protected static final Logger log = LoggerFactory.getLogger(MatchListFilter.class);

	private List<MatchFilter> include;

	private List<MatchFilter> exclude;

	protected boolean matchAllInList = false;

	protected boolean emptyListMath = false;

	protected MatchFilter lastMatch;

	public MatchListFilter() {
		include = null;
		exclude = null;
	}

	public void reset() {
		include = null;
		exclude = null;
	}

	public boolean isEmptyInclude() {
		return ((include == null) || (include.size() == 0));
	}

	public boolean isEmptyExclude() {
		return ((exclude == null) || (exclude.size() == 0));
	}

	public boolean isEmpty() {
		return isEmptyInclude() && isEmptyExclude();
	}

	public int size() {
		int rc = 0;
		if (include != null) {
			rc += include.size();
		}
		if (exclude != null) {
			rc += exclude.size();
		}
		return rc;
	}

	public boolean addInclude(final MatchFilter pattern) {
		if (pattern == null) {
			return false;
		}
		if (include == null) {
			include = new Vector<>();
		}
		include.add(pattern);
		return true;
	}

	public boolean addExclude(final MatchFilter pattern) {
		if (pattern == null) {
			return false;
		}
		if (this.exclude == null) {
			this.exclude = new Vector<>();
		}
		exclude.add(pattern);
		return true;
	}

	public int matchList(final Object obj, final List<MatchFilter> list) {
		if (list == null) {
			return MATCH_NO;
		}
		if (this.matchAllInList) {
			return matchListAll(obj, list);
		} else {
			return matchListAny(obj, list);
		}
	}

	protected int matchListAny(final Object obj, final List<MatchFilter> list) {
		int rc = MATCH_DONT_KNOW;
		search:
		// for (Iterator i = list.iterator(); i.hasNext();) {
		// MatchFilter f = (MatchFilter) i.next();
		// This works faster
		for (int i = 0; i < list.size(); i++) {
			final MatchFilter f = list.get(i);
			int rrc = f.matchState(obj);
			switch (rrc) {
			case MATCH_NO:
				rc = rrc;
				break;
			case MATCH_DONT_KNOW:
				break;
			case MATCH_YES:
			case MATCH_EXACT:
				rc = rrc;
				this.lastMatch = f;
				break search;
			case MATCH_EXCLUDE:
				rc = rrc;
				break search;
			}
		}
		return rc;
	}

	protected int matchListAll(final Object obj, final List<MatchFilter> list) {
		// for (Iterator i = list.iterator(); i.hasNext();) {
		// MatchFilter f = (MatchFilter) i.next();
		// This works faster
		for (int i = 0; i < list.size(); i++) {
			final MatchFilter f = list.get(i);
			int rrc = f.matchState(obj);
			switch (rrc) {
			case MATCH_NO:
				return MATCH_NO;
			case MATCH_EXCLUDE:
				return MATCH_EXCLUDE;
			}
		}
		return MATCH_YES;
	}

	public int matchState(Object obj) {
		this.lastMatch = null;
		if (emptyListMath && isEmpty()) {
			return MATCH_YES;
		}
		int rc = matchList(obj, include);
		if (debug) {
			log.debug("include->" + match2String(rc) + " while testing [" + obj + "] at " + this.getClass().getName()
					+ "-" + this.name);
		}
		if ((rc == MATCH_DONT_KNOW) && emptyListMath) {
			rc = MATCH_YES;
		}

		if ((rc == MATCH_YES) && (isMatch(matchList(obj, exclude)))) {
			rc = MATCH_EXCLUDE;
			if (debug) {
				log.debug("exclude->" + match2String(rc) + " while testing [" + obj + "] at "
						+ this.getClass().getName() + "-" + this.name);
			}
		}
		return rc;
	}

	/**
     * @return Returns the lastMatch.
     */
	public MatchFilter getLastMatch() {
		return lastMatch;
	}

	public boolean match(Object obj) {
		return isMatch(matchState(obj));
	}

	public void merge(MatchListFilter list) {
		if (list.include != null) {
			if (this.include == null) {
				this.include = new Vector<>();
			}
			this.include.addAll(list.include);
		}
		if (list.exclude != null) {
			if (this.exclude == null) {
				this.exclude = new Vector<>();
			}
			this.exclude.addAll(list.exclude);
		}
	}

	/**
     *
     * @return Iterator over include patterns
     */
	public Iterator<MatchFilter> iterator() {
		if (include == null) {
			// Empty Iterator
			return new Vector<MatchFilter>().iterator();
		} else {
			return include.iterator();
		}
	}

	public void debug(final List<MatchFilter> list) {
		for (Iterator<MatchFilter> i = list.iterator(); i.hasNext();) {
			final MatchFilter f = i.next();
			f.debug();
		}
	}

	public void debug() {
		if (!log.isDebugEnabled()) {
			return;
		}
		log.debug("start debug:" + this.getClass().getName() + "-" + this.name);
		log.debug("isEmpty " + isEmpty());
		log.debug("matchAllInList " + matchAllInList);
		if (include != null) {
			log.debug("include.size " + include.size());
			debug(include);
		} else {
			log.debug("include = null");
		}

		if (exclude != null) {
			log.debug("exclude.size " + exclude.size());
			debug(exclude);
		} else {
			log.debug("exclude = null");
		}
		log.debug("end   debug:" + this.getClass().getName() + "-" + this.name);
	}

	/**
     * Class needs to overrite this method to read context.
     */
	public boolean readContext(Object o) {
		return false;
	}

	/**
     * List Iterator delegate object constuction to child readContext function.
     */
	public void readConfig(List list) {
		if (list == null) {
			return;
		}
		for (Iterator i = list.iterator(); i.hasNext();) {
			Object o = i.next();
			if (!readContext(o)) {
				log.error("Wrong class type " + o.getClass().getName() + " while loading " + this.getClass().getName());
				throw new Error("Wrong class type " + o.getClass().getName());
			}
		}
		log.debug(this.getClass().getName() + " loaded.");

		if (include != null) {
			log.debug("include.size " + include.size());
		}

		if (exclude != null) {
			log.debug("exclude.size " + exclude.size());
		}
	}

}
