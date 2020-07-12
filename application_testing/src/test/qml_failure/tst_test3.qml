import QtQuick 2.0
import QtTest 1.2

TestCase {
    name: "Test3"

    function test_fail() {
        compare(1, 2)
    }
}
