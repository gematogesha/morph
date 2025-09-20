package com.wheatley.morph.core.date

import com.wheatley.morph.domain.model.Time

fun Time.format(): String = String.format("%02d:%02d", hour, minute)
