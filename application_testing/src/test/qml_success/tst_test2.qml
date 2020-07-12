import QtQuick 2.0
import QtTest 1.2

TestCase {
    name: "Test2"

	

    function benchmark_addition() {
        var sum = 0;
        for (var i = 0; i < 1000; ++i) {
            sum += 1
        }
        compare(sum, 1000)
    }
}