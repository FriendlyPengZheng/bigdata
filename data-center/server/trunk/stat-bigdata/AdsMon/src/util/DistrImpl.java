package util;
import java.util.*;

/**
 * @class DistrImpl
 * @brief accept distribution points number (positive), 
        give low & high border including this value
 */
public class DistrImpl {
    private TreeSet<Integer> m_points = new TreeSet<Integer>();
    public DistrImpl() {}
    public DistrImpl(String points) {  init(points);   }
    public void init(String points) {
        m_points.clear();
        if (points == null) { return; }
        String[] items = points.split(",");
        try { 
            m_points.add(0);
            m_points.add(Integer.MAX_VALUE);
            for (String i : items) {
                Integer p = Integer.parseInt(i);
                m_points.add(p);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            m_points.clear();
            return;
        }
    }

    public int getLow(int value) {
        Integer x = m_points.lower(value);
        return x != null ? x : 0;
    }
    public int getLowInclude(int value) {
        Integer x = m_points.floor(value);
        return x != null ? x : 0;
    }

    public int getHigh(int value) {
        Integer x = m_points.higher(value);
        return x != null ? x : Integer.MAX_VALUE;
    }
    public int getHighInclude(int value) {
        Integer x = m_points.ceiling(value);
        return x != null ? x : Integer.MAX_VALUE;
    }
}
