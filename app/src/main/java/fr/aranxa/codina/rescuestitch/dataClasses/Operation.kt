package fr.aranxa.codina.rescuestitch.dataClasses

enum class ElementType {
    button,
    switch,
    shake
}

enum class ElementValueType {
    color,
    string,
    int,
    float,
}

enum class OperationRoleType {
    intructor,
    operator,
    spectactor
}

enum class ButtonResultOrderType{
    ordered,
    random
}

data class Operation(
    val turn: Int,
    val role: String,
    val id: String?,
    val description: String?,
    val duration: Int?,
    val elements: List<Element>?,
    val result: Result?,
)

data class OperationTemplate(
    val description: String,
    val duration: Int,
    val elements: List<Element>,
    val result: Result,
)

data class OperationsTemplate(
    val operations: List<OperationTemplate>
)

data class Element(
    val type: String,
    val id: Int,
    val valueType: String,
    val value: String,
)

data class Result(
    val buttons: ButtonResult,
    val switch: List<Int>,
    val was_shaken: Boolean,
)

data class ButtonResult(
    val order: String,
    val ids: List<Int>
)

data class RegisteredEvents(
    val buttons:MutableList<Int> = mutableListOf<Int>(),
    val switches:MutableList<Int> = mutableListOf<Int>(),
    var was_shaken:Boolean = false
)



