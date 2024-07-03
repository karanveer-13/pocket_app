package com.example.pocketmoney.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip

class CustomFlowCombiner {
    fun <T1, T2, R> combine(
        flow1: Flow<List<T1>>,
        flow2: Flow<List<T2>>,
        transform: (List<T1>, List<T2>) -> List<R>
    ): Flow<List<R>> {
        return flow {
            flow1.zip(flow2) { list1, list2 ->
                emit(transform(list1, list2))
            }.collect()
        }
    }
}
