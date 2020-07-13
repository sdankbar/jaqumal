import QtQuick 2.0
import QtTest 1.2
import JaqumalTests 1.0
import com.github.sdankbar.jaqumal 0.4

TestCase {
    name: "Test3"

    QMLToTest {
        id: obj
    }

    function test_QMLToTest_function() {
        compare(5, obj.add(2, 3))
    }
}
