import QtQuick 2.0
import QtTest 1.2
import com.github.sdankbar.jaqumal 0.4

TestCase {
    name: "Test1"

    EventBuilder {
      id: eventing
    }

    function test_string() {
        eventing.fireEvent("Event1")
        log.info("Log Info")
        compare("A"+"B", "AB")
    }
}