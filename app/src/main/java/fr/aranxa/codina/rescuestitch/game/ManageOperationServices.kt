package fr.aranxa.codina.rescuestitch.game

import android.util.Log
import fr.aranxa.codina.rescuestitch.dataClasses.*

class ManageOperationsService(
    var nbplayers: Int = 0,
) {

    fun getOperations(turn: Int, operationsTemplate: OperationsTemplate?): List<Operation> {
        val operations: MutableList<Operation> = mutableListOf()
        if (nbplayers == 0) {
            return emptyList()
        }

        val playersIsOdd = nbplayers % 2 != 0
        if (playersIsOdd) {
            operations.add(getSpectatorOperation(turn))
            nbplayers--
        }
        for (i in 1..nbplayers / 2) {
            val operationTemplate = operationsTemplate?.operations?.random()
            if (operationTemplate != null) {
                val operatorId = getOperatorId()
                operations.add(
                    getPlayerOperation(
                        turn,
                        operationTemplate,
                        operatorId,
                        OperationRoleType.operator.toString()
                    )
                )
                operations.add(
                    getPlayerOperation(
                        turn,
                        operationTemplate,
                        operatorId,
                        OperationRoleType.intructor.toString()
                    )
                )
            }
        }
        return operations
    }

    private fun getOperatorId(): String {
        return List(2) { (('A'..'Z')).random() }.joinToString("") + "-" +
                List(2) { (('0'..'9')).random() }.joinToString("")
    }

    private fun getSpectatorOperation(turn: Int): Operation {
        return Operation(
            turn = turn,
            role = OperationRoleType.spectactor.toString(),
            id = null,
            duration = null,
            description = null,
            elements = null,
            result = null
        )
    }

    private fun getPlayerOperation(
        turn: Int,
        operationTemplate: OperationTemplate,
        operatorId: String,
        role: String
    ): Operation {

        return Operation(
            turn = turn,
            role = role,
            id = operatorId,
            duration = operationTemplate.duration,
            description = operationTemplate.description,
            elements = operationTemplate.elements,
            result = operationTemplate.result
        )
    }
}

class ManageResultService(
    val registeredEvents: RegisteredEvents,
    val attemptResult: Result
) {
    fun operationIsSuccessful(): Boolean {
        var operationIsSuccessful = true

//        check if attempt result for switches is correct bother about order of elements
        if (!(registeredEvents.switches.containsAll(attemptResult.switch) &&
                    attemptResult.switch.containsAll(registeredEvents.switches))
        ) {
            operationIsSuccessful = false
            return operationIsSuccessful
        }

        if (attemptResult.buttons.order == ButtonResultOrderType.ordered.toString()) {
//        check if attempt result correspond exactly of result buttons ids
            if (registeredEvents.buttons != attemptResult.buttons.ids) {
                operationIsSuccessful = false
                return operationIsSuccessful
            } else {
 //        check if attempt result for buttons is correct bother about order of elements
                if (!(registeredEvents.buttons.containsAll(attemptResult.buttons.ids) &&
                            attemptResult.buttons.ids.containsAll(registeredEvents.buttons))
                ) {
                    operationIsSuccessful = false
                    return operationIsSuccessful
                }
            }

        }
 //        check if the phone should be shaken
        if(!registeredEvents.was_shaken && attemptResult.was_shaken){
            operationIsSuccessful = false
            return operationIsSuccessful
        }


        return operationIsSuccessful
    }
}