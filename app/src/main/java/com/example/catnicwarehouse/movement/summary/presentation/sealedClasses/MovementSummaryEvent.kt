package com.example.catnicwarehouse.movement.summary.presentation.sealedClasses

import com.example.shared.repository.movements.DropOffRequestModel

sealed class MovementSummaryEvent {
    data class DropOff(val id: String?,val dropOffRequestModel: DropOffRequestModel) : MovementSummaryEvent()
    object Reset: MovementSummaryEvent()
}