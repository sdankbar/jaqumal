import QtQuick 2.0
import QtTest 1.2

TestCase {
    name: "Test1"

    function test_string() {
        log.info("Log Info")
        compare("A"+"B", "AB")
    }
}