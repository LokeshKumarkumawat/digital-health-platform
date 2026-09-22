package com.digitalhealth.platform.appointment.dto;

import com.digitalhealth.platform.common.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentStatusUpdateRequest {

    @NotNull
    private AppointmentStatus status;
}

//Doctor completes appointment
//Patient cancels
//System marks NO_SHOW