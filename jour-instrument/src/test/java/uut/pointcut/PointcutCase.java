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
package uut.pointcut;

import java.util.List;

/**
 *
 * Created on 05.12.2004
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$)  $Date$
 */
public class PointcutCase implements PointcutCaseInterface {

    String foo;
    
    List bar;
    
    public static void main(String[] args) {
    }

    public int doFoo(String foo, List bar) {
        return 0;
    }
    
    public int getint() {
        return 0;
    }
    
    private int getintprivate() {
        return 0;
    }
    
    final int getintfinal() {
        return 0;
    }
    
	synchronized private int getintprivatesyn() {
        return 0;
    }
	
	synchronized static private int getintprivatesynstat() {
        return 0;
    }
	
    public void setint(int i) {
    }

    
    public String getString() {
        return "";
    }

    public String[] getStringArray() {
        return new String[1];
    }
    
    public boolean getboolean() {
        return true;
    }
    
    /**
     * @return Returns the foo.
     */
    public String getFoo() {
        return foo;
    }
    
    /**
     * @return Returns the foo.
     */
    public String getFooBar(int bar) {
        return foo;
    }
    
    /**
     * @param foo The foo to set.
     */
    public void setFoo(String foo) {
        this.foo = foo;
    }
    
    /**
     * @return Returns the bar.
     */
    public List getBar() {
        return bar;
    }
    
    public List getBarList() {
        return bar;
    }
    
    /**
     * @param bar The bar to set.
     */
    public void setBar(List bar) {
        this.bar = bar;
    }
}
