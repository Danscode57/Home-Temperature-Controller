package io.dev.temperature.verticles

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime


class ScheduleVerticleTest {

    @Test fun shouldFoo(){
        println(LocalDateTime.now().dayOfWeek)
        assertEquals(2, LocalDateTime.now().dayOfWeek.value)
    }

}