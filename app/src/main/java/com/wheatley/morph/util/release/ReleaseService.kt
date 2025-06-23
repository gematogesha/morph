package com.wheatley.morph.util.release

interface ReleaseService {
    suspend fun latest(arguments: GetApplicationRelease.Arguments): Release?
}