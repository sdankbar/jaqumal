import QtQuick 2.11
import QtQuick.Controls 6.5

Rectangle {
    id: root
    property string text

    border.color: "black"
    width: 150
    height: parent.height

    Text {
        id: textObj
        x: 5
        text: root.text
        textFormat: Text.PlainText
    }

    TextField {
        id: input
        anchors.fill: parent
        visible: false

        onAccepted: {
            input.visible = false
            textObj.visible = true
            // TODO update value
        }
    }

    MouseArea {
        anchors.fill: parent
        onClicked: {
            input.text = root.text
            textObj.visible = false
            input.visible = true
            input.forceActiveFocus()
            input.selectAll()
        }
    }


}
