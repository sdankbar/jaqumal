import QtQuick 2.0
import QtTest 1.2

TestCase {
    name: "Test1"

    function test_string() {
        compare("A"+"B", "AB")
    }
}