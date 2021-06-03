package monkey.util;

import java.util.Comparator;

/**
 * Operations on Object. This class tries to handle null input gracefully. An
 * exception will generally not be thrown for a null input. Each method
 * documents its behavior in more detail.
 * 
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class ObjectUtils {
    /**
     * Null safe comparison of Comparables.
     *
     * @param <C> Type of the elements to compare.
     * @param c1  The first comparable, may be null.
     * @param c2  The second comparable, may be null.
     * @return
     *         <ul>
     *         <li>If both objects are non-null and equal, c1.</li>
     *         <li>If one of the comparables is null, the non-null object.</li>
     *         <li>If both the comparables are null, null is returned.</li>
     *         </ul>
     * @author Gaia Clerici
     * @version 1.0
     * @since 1.0
     */
    public <C extends Comparable<C>> C max(C c1, C c2) {
        if (c1 == null)
            return c2;
        if (c2 == null)
            return c1;
        return c1.compareTo(c2) >= 0 ? c1 : c2;
    }

    /**
     * Null safe comparison of Comparables.
     *
     * @param <C>  Type of the elements to compare.
     * @param c1   The first comparable, may be null.
     * @param c2   The second comparable, may be null.
     * @param comp A comparator to compare c1 and c2.
     * @return
     *         <ul>
     *         <li>If both objects are non-null and equal, c1.</li>
     *         <li>If one of the comparables is null, the non-null object.</li>
     *         <li>If both the comparables are null, null is returned.</li>
     *         </ul>
     * @author Gaia Clerici
     * @version 1.0
     * @since 1.0
     */
    public <C> C max(C c1, C c2, Comparator<C> comp) {
        if (c1 == null)
            return c2;
        if (c2 == null)
            return c1;
        return comp.compare(c1, c2) >= 0 ? c1 : c2;
    }

    /**
     * Null safe comparison of Comparables.
     *
     * @param <C> Type of the elements to compare.
     * @param c1  The first comparable, may be null.
     * @param c2  The second comparable, may be null.
     * @return
     *         <ul>
     *         <li>If both objects are non-null and equal, c1.</li>
     *         <li>If one of the comparables is null, the non-null object.</li>
     *         <li>If both the comparables are null, null is returned.</li>
     *         </ul>
     * @author Gaia Clerici
     * @version 1.0
     * @since 1.0
     */
    public <C extends Comparable<C>> C min(C c1, C c2) {
        if (c1 == null)
            return c2;
        if (c2 == null)
            return c1;
        return c1.compareTo(c2) <= 0 ? c1 : c2;
    }

    /**
     * Null safe comparison of Comparables.
     *
     * @param <C>  Type of the elements to compare.
     * @param c1   The first comparable, may be null.
     * @param c2   The second comparable, may be null.
     * @param comp A comparator to compare c1 and c2.
     * @return
     *         <ul>
     *         <li>If both objects are non-null and equal, c1.</li>
     *         <li>If one of the comparables is null, the non-null object.</li>
     *         <li>If both the comparables are null, null is returned.</li>
     *         </ul>
     * @author Gaia Clerici
     * @version 1.0
     * @since 1.0
     */
    public <C> C min(C c1, C c2, Comparator<C> comp) {
        if (c1 == null)
            return c2;
        if (c2 == null)
            return c1;
        return comp.compare(c1, c2) <= 0 ? c1 : c2;
    }

}
