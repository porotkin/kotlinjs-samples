import com.example.Derived
import junit.framework.TestCase
import org.junit.Ignore

@Ignore("For manual testing")
class DerivedObjectTest : TestCase() {
    fun test() {
        assertNotNull(Derived)
    }
}
