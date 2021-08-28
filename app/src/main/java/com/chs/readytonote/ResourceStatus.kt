package com.chs.readytonote

sealed class ResourceStatus<T>(
    val data: T?,
    val msg: String?
) {
    class Success<T>(data: T?) : ResourceStatus<T>(data, null)
    class Failed<T>(msg: String) : ResourceStatus<T>(null, msg)
}
